package server_client.server.threads.message_queues.third_stage;

import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.services.MessageService;
import server_client.server.services.impl.MessageServiceImpl;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;

public class DatabaseProcessingThread implements Runnable {

    private static MessageService messageService;

    static {
        messageService = new MessageServiceImpl();
    }

    private final BlockingDeque<MessageData> fila3;

    public DatabaseProcessingThread(BlockingDeque<MessageData> fila3) {
        this.fila3 = fila3;
    }

    @Override
    public void run() {
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
