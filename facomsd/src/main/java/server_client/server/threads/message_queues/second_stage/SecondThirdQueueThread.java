package server_client.server.threads.message_queues.second_stage;

import server_client.model.Message;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.threads.handlers.MessageData;

import java.util.logging.Logger;

public class SecondThirdQueueThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(SecondThirdQueueThread.class.getName());

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            MessageData messageData = null;

            while (messageData == null) {
                try {
                    messageData = StartPrimitives.getFila1().take();
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
                StartPrimitives.getFila3().put(messageData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (messageData.getMessage().getLastOption() != 2) {
                Message messageLog = messageData.getMessage();
                messageLog = new Message(messageLog.getLastOption(), messageLog.getId(), messageLog.getMessage());
                try {
                    LOGGER.info("Mensagem " + messageLog + " será colocada na Fila2.");
                    StartPrimitives.getFila2().put(messageLog);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
