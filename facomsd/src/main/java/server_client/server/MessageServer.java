package server_client.server;

import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
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

    public static void main(String[] args) {

        int myId = Integer.parseInt(args[0]);
        List<Address> addresses = new LinkedList<>();

        Serializer serializer = Serializer.using(Namespace.builder()
                .register(Namespaces.BASIC)
                .register(MemberId.class)
                .register(Message.class)
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

         MultiRaftProtocol protocolMap = MultiRaftProtocol.builder()
                .withReadConsistency(ReadConsistency.LINEARIZABLE)
                .build();

         DistributedMap<BigInteger, String> mapDatabase = atomix.<BigInteger, String>mapBuilder("my-map-db")
                .withProtocol(protocolMap)
                .withKeyType(BigInteger.class)
                .withValueType(String.class)
                .withCacheEnabled()
                .withCacheSize(1000)
                .build();

//        DistributedMap<BigInteger, String> mapDatabase = atomix.<BigInteger,String>mapBuilder("my-map-db")
//                .withCacheEnabled()
//                .build();

        System.out.println("Database prepared");

        atomix.getCommunicationService().subscribe("test", serializer::decode, message -> {

            Message receivedMessageFromClient = (Message) message;

            LOGGER.info("Mensagem obtida: " + receivedMessageFromClient);

            if (receivedMessageFromClient == null || receivedMessageFromClient.getLastOption() < 1 || receivedMessageFromClient.getLastOption() > 4) {
                LOGGER.info(StringsConstants.ERR_INVALID_OPTION.toString());
                return CompletableFuture.completedFuture(new Message(StringsConstants.ERR_INVALID_OPTION.toString()));
            }

            MessageService messageService = new MessageServiceImpl(mapDatabase);
            Message answerToClient = messageService.processMessage(receivedMessageFromClient);

            return CompletableFuture.completedFuture(answerToClient);
        }, serializer::encode);

    }
}
