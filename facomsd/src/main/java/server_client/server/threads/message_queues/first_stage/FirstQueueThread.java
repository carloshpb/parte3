package server_client.server.threads.message_queues.first_stage;

import io.atomix.core.Atomix;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class FirstQueueThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(FirstQueueThread.class.getName());
//    private final Atomix atomix;

    private volatile MessageData message;
    private BlockingQueue<MessageData> fila1;

//    public FirstQueueThread(MessageData message, Atomix atomix) {
//        this.message = message;
//        this.atomix = atomix;
//    }

    public FirstQueueThread(MessageData message, BlockingQueue<MessageData> fila1) {
        this.message = message;
        this.fila1 = fila1;
    }

    @Override
    public void run() {

//        MultiRaftProtocol protocolQueue = MultiRaftProtocol.builder()
//                .withReadConsistency(ReadConsistency.LINEARIZABLE)
//                .build();
//
//        fila1 = atomix.<MessageData>queueBuilder("fila-1")
//                .withProtocol(protocolQueue)
//                .build();

//        LOGGER.info("Mensagem " + this.message.getMessage() + " será colocada na Fila1.");
//        fila1.add(message);

        try {
            LOGGER.info("Mensagem " + this.message.getMessage() + " será colocada na Fila1.");
            fila1.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
