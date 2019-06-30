package server_client.client.producer;

import server_client.client.threads.producer.CommandReaderThread;
import server_client.model.Message;

import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

public class CommandReaderThreadTest extends CommandReaderThread {

    private volatile BlockingQueue<Message> novasMensagens;

    public CommandReaderThreadTest(Boolean producerIsOver, ObjectOutputStream objectOutputStream, BlockingQueue<Message> novasMensagens) {
        super();
        this.producerIsOver = producerIsOver;
        this.objectOutputStream = objectOutputStream;
        this.novasMensagens = novasMensagens;
    }

    @Override
    public void run() {

        while (!this.exit && !Thread.interrupted()) {

            Message message = null;
            try {
                message = this.novasMensagens.take();
            } catch (InterruptedException e) {
                LOGGER.info(e.getMessage());
                this.shutdown();
                continue;
            }

            this.sendMessage(message);

            if (message.getLastOption() == 5) {
                this.shutdown();
            }

        }

    }

}
