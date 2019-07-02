package server_client.server.threads.message_queues.third_stage;

import server_client.model.Message;
import server_client.server.MessageServer;
import server_client.server.StartPrimitives;
import server_client.server.database.LogFile;
import server_client.server.threads.handlers.MessageData;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;

public class LogThread implements Runnable {

    private BlockingDeque<Message> fila2;

    public LogThread(BlockingDeque<Message> fila2) {
        this.fila2 = fila2;
    }

    @Override
    public void run() {
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
