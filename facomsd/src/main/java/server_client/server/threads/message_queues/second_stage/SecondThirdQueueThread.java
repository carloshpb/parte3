package server_client.server.threads.message_queues.second_stage;

import server_client.model.Message;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class SecondThirdQueueThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(SecondThirdQueueThread.class.getName());

    private final BlockingDeque<MessageData> fila1;
    private final BlockingDeque<Message> fila2;
    private final BlockingDeque<MessageData> fila3;

    public SecondThirdQueueThread(BlockingDeque<MessageData> fila1, BlockingDeque<Message> fila2, BlockingDeque<MessageData> fila3) {
        this.fila1 = fila1;
        this.fila2 = fila2;
        this.fila3 = fila3;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            MessageData messageData = null;

            while (messageData == null) {

//                messageData = fila1.poll();
//                LOGGER.info("Mensagem " + messageData.getMessage() + " pega da Fila1.");

                try {
                    messageData = fila1.take();
                    LOGGER.info("Mensagem " + messageData.getMessage() + " pega da Fila1.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Message messageDB = messageData.getMessage();
            messageDB = new Message(messageDB.getLastOption(), messageDB.getId(), messageDB.getMessage());
            messageData.setMessage(messageDB);
            try {
                LOGGER.info("Mensagem " + messageData.getMessage() + " será colocada na Fila3.");
                fila3.put(messageData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (messageData.getMessage().getLastOption() != 2) {
                Message messageLog = messageData.getMessage();
                messageLog = new Message(messageLog.getLastOption(), messageLog.getId(), messageLog.getMessage());
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
