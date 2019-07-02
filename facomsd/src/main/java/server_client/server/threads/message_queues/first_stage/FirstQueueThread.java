package server_client.server.threads.message_queues.first_stage;

import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class FirstQueueThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(FirstQueueThread.class.getName());

    private volatile MessageData message;
    private BlockingDeque<MessageData> fila1;

    public FirstQueueThread(MessageData message, BlockingDeque<MessageData> fila1) {
        this.message = message;
        this.fila1 = fila1;
    }

    @Override
    public void run() {

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
