package server_client.server.threads.message_queues.third_stage;

import io.atomix.core.Atomix;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import server_client.model.Message;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.database.LogFile;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class LogThread implements Runnable {

//    private final Atomix atomix;
    private BlockingQueue<Message> fila2;

//    public LogThread(Atomix atomix) {
//        this.atomix = atomix;
//    }

    public LogThread(BlockingQueue<Message> fila2) {
        this.fila2 = fila2;
    }

    @Override
    public void run() {

//        MultiRaftProtocol protocolQueue = MultiRaftProtocol.builder()
//                .withReadConsistency(ReadConsistency.LINEARIZABLE)
//                .build();
//
//        this.fila2 = atomix.<Message>queueBuilder("fila-2")
//                .withProtocol(protocolQueue)
//                .build();

        while (!Thread.interrupted()) {

//            LogFile.saveOperationLog(fila2.poll());

            try {
                LogFile.saveOperationLog(fila2.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
