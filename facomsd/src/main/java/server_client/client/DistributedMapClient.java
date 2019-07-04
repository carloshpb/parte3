package server_client.client;

import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.utils.net.Address;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;
import io.atomix.utils.serializer.Serializer;
import server_client.client.view.TerminalView;
import server_client.model.Message;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class DistributedMapClient {

    // Ex :
    // args -> 0 0 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002
    //        (myId) (id do server) (sequência de endereços dos servidores)
    public static void main(String[] args) {
        // ----------------------------------------------
        // Inicialização do servidor no cluster do Atomix
        // ----------------------------------------------
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

        for(int i = 2; i <args.length; i+=2)
        {
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
            addresses.add(address);
        }

        AtomixBuilder builder = Atomix.builder();

        Atomix atomix = builder.withMemberId("client-"+myId)
                .withAddress(new Address("127.0.0.1", (6000 + myId)))
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
                .build();

        atomix.start().join();

        System.out.println("Cluster joined");


        // ----------------------------------------------
        //  Criação do objeto do View (textos que aparecerão no terminal do respectivo Sistema Operacional)
        // ----------------------------------------------
        final TerminalView terminalView = new TerminalView();


        // ----------------------------------------------
        //  Loop da chamada do método que inicia a sequência do View, para obter a mensagem do usuário e enviar ao server
        // ----------------------------------------------
        boolean exit = false;
        while (!exit) {
            // Pede mensagem ao usuário
            Message message = terminalView.startReadMessage();

            switch (message.getLastOption()) {
                case 1:
                case 2:
                case 3:
                case 4:
                    // Serviço de comunicação do Atomix, responsavel para enviar a mensagem ao server do MemberId
                    // Recebe um CompletableFuture com a mensagem do server, a qual será pega pelo .thenAccept() e
                    // rodará o println sobre a mensagem (ela chega como Object, tem que fazer cast nela de Message)
                    atomix.getCommunicationService().send("mensagem-big", message, serializer::encode, serializer::decode, MemberId.from("member-" + args[1])).thenAccept(response -> {
                        System.out.println(((Message)response).getMessage());
                    });
                    break;
                default:
                    // Qualquer outro valor além de 1,2,3 e 4, vai fechar a conexão com o server e fechar o cliente.
                    exit = true;
                    atomix.stop();
                    break;
            }
        }
    }
}

