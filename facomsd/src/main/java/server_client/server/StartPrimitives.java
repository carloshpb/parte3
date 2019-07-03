package server_client.server;

import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.map.AtomicMap;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import io.atomix.utils.net.Address;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;
import io.atomix.utils.serializer.Serializer;
import server_client.model.Message;
import server_client.server.database.MemoryDB;
import server_client.server.threads.handlers.MessageData;
import server_client.server.threads.message_queues.second_stage.SecondThirdQueueThread;
import server_client.server.threads.message_queues.third_stage.DatabaseProcessingThread;
import server_client.server.threads.message_queues.third_stage.LogThread;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class StartPrimitives {

    private final static Logger LOGGER = Logger.getLogger(StartPrimitives.class.getName());

    private static volatile BlockingQueue<MessageData> fila1;
    private static volatile BlockingQueue<Message> fila2;
    private static volatile BlockingQueue<MessageData> fila3;

    private volatile boolean exit = false;

    private static volatile ExecutorService queueThreadPool = Executors.newFixedThreadPool(3);

    private static volatile SecondThirdQueueThread secondThirdQueueThread;
    private static volatile DatabaseProcessingThread databaseProcessingThread;
    private static volatile LogThread logThread;

    public synchronized static BlockingQueue<MessageData> getFila1() {
        return fila1;
    }

    public static BlockingQueue<Message> getFila2() {
        return fila2;
    }

    public static BlockingQueue<MessageData> getFila3() {
        return fila3;
    }

    // args - starter 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002 127.0.0.1 5003

    public static void main(String[] args) {

        int myId = Integer.parseInt(args[0]);
        List<Address> addresses = new LinkedList<>();

        for(int i = 1; i <args.length; i+=2)
        {
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
            addresses.add(address);
        }

        AtomixBuilder builder = Atomix.builder();

        Atomix atomix = builder.withMemberId("starter")
                .withAddress(addresses.get(myId))
                .withMembershipProvider(BootstrapDiscoveryProvider.builder()
                        .withNodes(
                                Node.builder()
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
                                        .build(),
                                Node.builder()
                                        .withId("starter")
                                        .withAddress(addresses.get(3))
                                        .build()
                                )
                        .build())
                .withProfiles(ConsensusProfile.builder().withDataPath("/tmp/starter").withMembers("member-1", "member-2", "member-3", "starter").build())
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

//        List<Address> addresses = new LinkedList<>();
//
//        for(int i = 1; i <args.length; i+=2)
//        {
//            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
//            addresses.add(address);
//        }
//
//        AtomixBuilder builder = Atomix.builder();
//
//        Atomix atomix = builder.withMemberId("starter")
//                .withAddress(addresses.get(0))
//                .withMembershipProvider(BootstrapDiscoveryProvider.builder()
//                        .withNodes( Node.builder()
//                                        .withId("starter")
//                                        .withAddress(addresses.get(0))
//                                        .build(),
//                                Node.builder()
//                                        .withId("member-0")
//                                        .withAddress(addresses.get(1))
//                                        .build(),
//                                Node.builder()
//                                        .withId("member-1")
//                                        .withAddress(addresses.get(2))
//                                        .build(),
//                                Node.builder()
//                                        .withId("member-2")
//                                        .withAddress(addresses.get(3))
//                                        .build())
//                        .build())
//                .withProfiles(ConsensusProfile.builder().withDataPath("/tmp/starter").withMembers("starter", "member-1", "member-2", "member-3").build())
//                .build();

//        fila1 = new LinkedBlockingDeque<>();
//        fila2 = new LinkedBlockingDeque<>();
//        fila3 = new LinkedBlockingDeque<>();
//
//        if (secondThirdQueueThread == null) {
//            secondThirdQueueThread = new SecondThirdQueueThread();
//            queueThreadPool.submit(secondThirdQueueThread);
//        }
//
//        if (databaseProcessingThread == null) {
//            databaseProcessingThread = new DatabaseProcessingThread();
//            queueThreadPool.submit(databaseProcessingThread);
//        }
//
//        if (logThread == null) {
//            logThread = new LogThread();
//            queueThreadPool.submit(logThread);
//        }

//        MultiRaftProtocol protocol = MultiRaftProtocol.builder()
//                .withReadConsistency(ReadConsistency.LINEARIZABLE)
//                .build();
//
//        atomix.start().join();
//
//        AtomicMap<BigInteger, String> map = atomix.<BigInteger, String>atomicMapBuilder("my-atomic-map")
//                .withProtocol(protocol)
//                .withKeyType(BigInteger.class)
//                .withValueType(String.class)
//                .withCacheEnabled()
//                .withCacheSize(1000)
//                .build();

//        MemoryDB.startDB(map);

//        atomix.getMembershipService().addListener(event -> {
//            switch (event.type()) {
//                case MEMBER_ADDED:
//                    System.out.println(event.subject().id() + " joined the cluster");
//                    break;
//                case MEMBER_REMOVED:
//                    System.out.println(event.subject().id() + " left the cluster");
//                    break;
//            }
//        });
//
//        System.out.println("Cluster joined");

//        MemoryDB.startDB(new ConcurrentHashMap<>());
    }

    private static void startPrimitives() {

    }
}
