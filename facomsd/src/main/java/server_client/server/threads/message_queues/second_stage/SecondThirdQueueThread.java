package server_client.server.threads.message_queues.second_stage;

import io.atomix.core.Atomix;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import server_client.model.Message;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SecondThirdQueueThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(SecondThirdQueueThread.class.getName());

    private BlockingQueue<MessageData> fila1;
    private BlockingQueue<Message> fila2;
    private BlockingQueue<MessageData> fila3;

//    private final Atomix atomix;

//    public SecondThirdQueueThread(Atomix atomix) {
//        this.atomix = atomix;
//    }

    public SecondThirdQueueThread(BlockingQueue<MessageData> fila1, BlockingQueue<Message> fila2, BlockingQueue<MessageData> fila3) {
        this.fila1 = fila1;
        this.fila2 = fila2;
        this.fila3 = fila3;
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
//
//        fila2 = atomix.<Message>queueBuilder("fila-2")
//                .withProtocol(protocolQueue)
//                .build();
//
//        fila3 = atomix.<MessageData>queueBuilder("fila-3")
//                .withProtocol(protocolQueue)
//                .build();

        while (!Thread.interrupted()) {
            MessageData messageData = null;

            do {

//                messageData = fila1.poll();

                try {
                    messageData = fila1.take();
                    LOGGER.info("Mensagem " + messageData.getMessage() + " pega da Fila1.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (messageData == null);

            LOGGER.info("Mensagem " + messageData.getMessage() + " pega da Fila1.");

            Message messageDB = messageData.getMessage();
            messageDB = new Message(messageDB.getLastOption(), messageDB.getId(), messageDB.getMessage());
            messageData.setMessage(messageDB);

//            LOGGER.info("Mensagem " + messageData.getMessage() + " será colocada na Fila3.");
//            fila3.add(messageData);

            try {
                LOGGER.info("Mensagem " + messageData.getMessage() + " será colocada na Fila3.");
                fila3.put(messageData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (messageData.getMessage().getLastOption() != 2) {
                Message messageLog = messageData.getMessage();
                messageLog = new Message(messageLog.getLastOption(), messageLog.getId(), messageLog.getMessage());

//                LOGGER.info("Mensagem " + messageLog + " será colocada na Fila2.");
//                fila2.add(messageLog);

                try {
                    LOGGER.info("Mensagem " + messageLog + " será colocada na Fila2.");
                    fila2.put(messageLog);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
