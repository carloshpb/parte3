package server_client.server.threads.message_queues.third_stage;

import io.atomix.core.Atomix;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.services.MessageService;
import server_client.server.services.impl.MessageServiceImpl;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class DatabaseProcessingThread implements Runnable {

//    private final Atomix atomix;
    private static MessageService messageService;

    static {
        messageService = new MessageServiceImpl();
    }

    private BlockingQueue<MessageData> fila3;

    public DatabaseProcessingThread(BlockingQueue<MessageData> fila3) {
        this.fila3 = fila3;
    }

//    public DatabaseProcessingThread(Atomix atomix) {
//        this.atomix = atomix;
//    }

    @Override
    public void run() {

//        MultiRaftProtocol protocolQueue = MultiRaftProtocol.builder()
//                .withReadConsistency(ReadConsistency.LINEARIZABLE)
//                .build();
//
//        this.fila3 = atomix.<MessageData>queueBuilder("fila-3")
//                .withProtocol(protocolQueue)
//                .build();

        messageService = new MessageServiceImpl();

        while (!Thread.interrupted()) {

//            messageService.processMessage(fila3.poll());

            try {
                messageService.processMessage(fila3.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
