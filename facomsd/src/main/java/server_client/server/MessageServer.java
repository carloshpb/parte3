package server_client.server;

import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.utils.net.Address;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;
import io.atomix.utils.serializer.Serializer;
import server_client.constants.StringsConstants;
import server_client.model.Message;
import server_client.server.services.MessageService;
import server_client.server.services.impl.MessageServiceImpl;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class MessageServer {


    private final static Logger LOGGER = Logger.getLogger(MessageServer.class.getName());

    // Ex :
    // args -> 0 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002
    //        (myId) (sequência de endereços dos servidores)

    public static void main(String[] args) {

        int myId = Integer.parseInt(args[0]);
        List<Address> addresses = new LinkedList<>();

        // O Serializer será necessário de se usar, caso vá usar uma classe customizada (e usaremos a Message)
        // Será necessário registrar todas as classes das informações que serão passadas para o server pela conexão
        // Não precisa registrar String ou primitivos (int, long, etc)
        Serializer serializer = Serializer.using(Namespace.builder()
                .register(Namespaces.BASIC)
                .register(MemberId.class)
                .register(Message.class)
                .register(BigInteger.class)
                .build());

        for(int i = 1; i <args.length; i+=2)
        {
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
            addresses.add(address);
        }

        AtomixBuilder builder = Atomix.builder();

        Atomix atomix = builder.withMemberId("member-"+myId)
                .withAddress(addresses.get(myId))
                .withMembershipProvider(BootstrapDiscoveryProvider.builder()
                        .withNodes( Node.builder()
                                        .withId("member-0")
                                        .withAddress(addresses.get(0))
                                        .build(),
                                Node.builder()
                                        .withId("member-1")
                                        .withAddress(addresses.get(1))
                                        .build(),
                                Node.builder()
                                        .withId("member-2")
                                        .withAddress(addresses.get(2))
                                        .build())
                        .build())
                .withProfiles(ConsensusProfile.builder().withDataPath("/tmp/member-"+myId).withMembers("member-1", "member-2", "member-3").build())
                .build();

        atomix.start().join();

        System.out.println("Cluster joined");

        atomix.getMembershipService().addListener(event -> {
            switch (event.type()) {
                case MEMBER_ADDED:
                    System.out.println(event.subject().id() + " joined the cluster");
                    break;
                case MEMBER_REMOVED:
                    System.out.println(event.subject().id() + " left the cluster");
                    break;
            }
        });

        // Database que usaremos, a qual é visivel por todos os nodes do cluster do Atomix e ainda é thread-safe
        DistributedMap<BigInteger, String> mapDatabase = atomix.<BigInteger,String>mapBuilder("map-database")
                .withCacheEnabled()
                .build();

        System.out.println("Database prepared");

        // O método .subscribe() receberá a mensagem enviada pelo método .send() no cliente.
        // Será necessário colocar o decoder e encoder do Serializer, caso esteja usando classe customizada.
        // Como visto abaixo, será retornado um CompletableFuture, a qual o cliente receberá e executará uma função
        // sobre a mensagem pega com o .thenAccept() no cliente.
        atomix.getCommunicationService().subscribe("mensagem-big", serializer::decode, message -> {

            Message receivedMessageFromClient = (Message) message;

            LOGGER.info("Mensagem obtida: " + receivedMessageFromClient);

            // Verificando se não foi colocado uma opção inválida
            if (receivedMessageFromClient == null || receivedMessageFromClient.getLastOption() < 1 || receivedMessageFromClient.getLastOption() > 4) {
                LOGGER.info(StringsConstants.ERR_INVALID_OPTION.toString());
                return CompletableFuture.completedFuture(new Message(StringsConstants.ERR_INVALID_OPTION.toString()));
            }

            // Enviará o mapDatabase para a instância de MessaService, onde verificará a mensagem para ter
            // certeza de que ela não está inválida.
            MessageService messageService = new MessageServiceImpl(mapDatabase);
            Message answerToClient = messageService.processMessage(receivedMessageFromClient);

            return CompletableFuture.completedFuture(answerToClient);
        }, serializer::encode);

    }
}
