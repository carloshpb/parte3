package server_client.server.threads.message_queues.third_stage;

import server_client.server.services.MessageService;
import server_client.server.services.impl.MessageServiceImpl;
import server_client.server.threads.ServerThread;

public class DatabaseProcessingThread implements Runnable {

    private static MessageService messageService;

    static {
        messageService = new MessageServiceImpl();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                messageService.processMessage(ServerThread.getFila3().take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
