package server_client.server.threads.message_queues.third_stage;

import server_client.server.database.LogFile;
import server_client.server.threads.ServerThread;

public class LogThread implements Runnable {

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                LogFile.saveOperationLog(ServerThread.getFila2().take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
