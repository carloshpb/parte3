package server_client.server;

import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.map.AtomicMap;
import io.atomix.core.map.DistributedMap;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.core.queue.DistributedQueue;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import io.atomix.utils.net.Address;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;
import io.atomix.utils.serializer.Serializer;
import server_client.constants.StringsConstants;
import server_client.model.Message;
import server_client.server.database.MemoryDB;
import server_client.server.threads.handlers.MessageData;
import server_client.server.threads.message_queues.first_stage.FirstQueueThread;
import server_client.server.threads.message_queues.second_stage.SecondThirdQueueThread;
import server_client.server.threads.message_queues.third_stage.DatabaseProcessingThread;
import server_client.server.threads.message_queues.third_stage.LogThread;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class MessageServer {


    private final static Logger LOGGER = Logger.getLogger(MessageServer.class.getName());

    private static volatile BlockingDeque<MessageData> fila1;
    private static volatile BlockingDeque<Message> fila2;
    private static volatile BlockingDeque<MessageData> fila3;

    private static volatile ExecutorService queueThreadPool;

    private static volatile SecondThirdQueueThread secondThirdQueueThread;
    private static volatile DatabaseProcessingThread databaseProcessingThread;
    private static volatile LogThread logThread;

    public static BlockingDeque<MessageData> getFila1() {
        return fila1;
    }

    public static BlockingDeque<Message> getFila2() {
        return fila2;
    }

    public static BlockingDeque<MessageData> getFila3() {
        return fila3;
    }

    public static void main(String[] args) {

//        if (args[0].equals("starter")) {

//            List<Address> addresses = new LinkedList<>();
//
//            for(int i = 1; i <args.length; i+=2)
//            {
//                Address address = new Address(args[i], Integer.parseInt(args[i+1]));
//                addresses.add(address);
//            }
//
//            AtomixBuilder builder = Atomix.builder();
//
//            Atomix atomix = builder.withMemberId("starter")
//                    .withAddress(addresses.get(0))
//                    .withMembershipProvider(BootstrapDiscoveryProvider.builder()
//                            .withNodes( Node.builder()
//                                            .withId("starter")
//                                            .withAddress(addresses.get(0))
//                                            .build()
//                                    Node.builder()
//                                            .withId("member-0")
//                                            .withAddress(addresses.get(1))
//                                            .build(),
//                                    Node.builder()
//                                            .withId("member-1")
//                                            .withAddress(addresses.get(2))
//                                            .build(),
//                                    Node.builder()
//                                            .withId("member-2")
//                                            .withAddress(addresses.get(3))
//                                            .build())
//                            .build())
//                    .withProfiles(ConsensusProfile.builder().withDataPath("/tmp/starter").withMembers("starter", "member-1", "member-2", "member-3").build())
//                    .build();
//
//            fila1 = new LinkedBlockingDeque<>();
//            fila2 = new LinkedBlockingDeque<>();
//            fila3 = new LinkedBlockingDeque<>();
//
//            if (secondThirdQueueThread == null) {
//                secondThirdQueueThread = new SecondThirdQueueThread();
//                queueThreadPool.submit(secondThirdQueueThread);
//            }
//
//            if (databaseProcessingThread == null) {
//                databaseProcessingThread = new DatabaseProcessingThread();
//                queueThreadPool.submit(databaseProcessingThread);
//            }
//
//            if (logThread == null) {
//                logThread = new LogThread();
//                queueThreadPool.submit(logThread);
//            }
//
//            atomix.start().join();
//
//            System.out.println("Cluster joined");

//        } else {
//
//        }

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

//        int myId = Integer.parseInt(args[0]);
//        List<Address> addresses = new LinkedList<>();
//
//        Serializer serializer = Serializer.using(Namespace.builder()
//                .register(Namespaces.BASIC)
//                .register(MemberId.class)
//                .register(Message.class)
//                .build());
//
//        for(int i = 1; i <args.length; i+=2)
//        {
//            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
//            addresses.add(address);
//        }
//
//        AtomixBuilder builder = Atomix.builder();
//
//        Atomix atomix = builder.withMemberId("member-"+myId)
//                .withAddress(addresses.get(myId))
//                .withMembershipProvider(BootstrapDiscoveryProvider.builder()
//                        .withNodes(
//                                Node.builder()
//                                        .withId("member-0")
//                                        .withAddress(addresses.get(0))
//                                        .build(),
//                                Node.builder()
//                                        .withId("member-1")
//                                        .withAddress(addresses.get(1))
//                                        .build(),
//                                Node.builder()
//                                        .withId("member-2")
//                                        .withAddress(addresses.get(2))
//                                        .build())
//                        .build())
//                .withProfiles(ConsensusProfile.builder().withDataPath("/tmp/member-"+myId).withMembers("member-1", "member-2", "member-3").build())
//                .build();
//
//        atomix.start().join();
//
//        System.out.println("Cluster joined");

//        if (MemoryDB.getDatabase() == null)

//            if (atomix.getAtomicMap("my-map") == null) {
//                MultiRaftProtocol protocol = MultiRaftProtocol.builder()
//                        .withReadConsistency(ReadConsistency.LINEARIZABLE)
//                        .build();
//
//                AtomicMap<BigInteger, String> map = atomix.<BigInteger, String>atomicMapBuilder("my-atomic-map")
//                        .withProtocol(protocol)
//                        .withKeyType(BigInteger.class)
//                        .withValueType(String.class)
//                        .withCacheEnabled()
//                        .withCacheSize(1000)
//                        .build();
//
//                MemoryDB.startDB(map);
//            }
//
//            atomix.getMembershipService().addListener(event -> {
//                switch (event.type()) {
//                    case MEMBER_ADDED:
//                        System.out.println(event.subject().id() + " joined the cluster");
//                        break;
//                    case MEMBER_REMOVED:
//                        System.out.println(event.subject().id() + " left the cluster");
//                        break;
//                }
//            });



        if (myId == 0) {
            queueThreadPool = Executors.newFixedThreadPool(3);

//            fila1 = atomix.<MessageData>queueBuilder("fila1")
//                    .withProtocol(protocol)
//                    .withElementType(MessageData.class)
//                    .build();
//
//            fila2 = atomix.<Message>queueBuilder("fila2")
//                    .withProtocol(protocol)
//                    .withElementType(Message.class)
//                    .build();
//
//            fila3 = atomix.<MessageData>queueBuilder("fila2")
//                    .withProtocol(protocol)
//                    .withElementType(MessageData.class)
//                    .build();

            fila1 = new LinkedBlockingDeque<>();
            fila2 = new LinkedBlockingDeque<>();
            fila3 = new LinkedBlockingDeque<>();

            if (secondThirdQueueThread == null) {
                secondThirdQueueThread = new SecondThirdQueueThread(fila1, fila2, fila3);
                queueThreadPool.submit(secondThirdQueueThread);
            }

            if (databaseProcessingThread == null) {
                databaseProcessingThread = new DatabaseProcessingThread(fila3);
                queueThreadPool.submit(databaseProcessingThread);
            }

            if (logThread == null) {
                logThread = new LogThread(fila2);
                queueThreadPool.submit(logThread);
            }

            MultiRaftProtocol protocol = MultiRaftProtocol.builder()
                    .withReadConsistency(ReadConsistency.LINEARIZABLE)
                    .build();

            DistributedMap<BigInteger, String> map = atomix.<BigInteger, String>mapBuilder("my-map-db")
                    .withProtocol(protocol)
                    .withKeyType(BigInteger.class)
                    .withValueType(String.class)
                    .withCacheEnabled()
                    .withCacheSize(1000)
                    .build();

            MemoryDB.startDB(map);
        }



        BlockingQueue<Message> answerQueue = new LinkedBlockingDeque<>();

        atomix.getCommunicationService().subscribe("test", serializer::decode, message -> {

            Message receivedMessage = (Message) message;

            LOGGER.info("Mensagem obtida: " + receivedMessage);

            if (receivedMessage == null || receivedMessage.getLastOption() < 1 || receivedMessage.getLastOption() > 4) {
                LOGGER.info(StringsConstants.ERR_INVALID_OPTION.toString());
                return CompletableFuture.completedFuture(new Message(StringsConstants.ERR_INVALID_OPTION.toString()));
            }

            MessageData messageData = new MessageData(receivedMessage, answerQueue);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new FirstQueueThread(messageData, getFila1()));

            try {
                receivedMessage =  answerQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return CompletableFuture.completedFuture(new Message(e.getMessage()));
            }

            return CompletableFuture.completedFuture(receivedMessage);
        }, serializer::encode);


    }
}
