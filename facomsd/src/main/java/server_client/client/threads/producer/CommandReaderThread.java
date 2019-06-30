package server_client.client.threads.producer;

import server_client.client.view.TerminalView;
import server_client.model.Message;
import server_client.server.threads.handlers.ClientHandler;

import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class CommandReaderThread implements Runnable{

    protected final static Logger LOGGER = Logger.getLogger(CommandReaderThread.class.getName());

    protected volatile boolean exit = false;

    protected Semaphore semaphore;
    protected Boolean producerIsOver;
    protected ObjectOutputStream objectOutputStream;
    protected ClientHandler clientHandler;

    protected TerminalView view;

    public CommandReaderThread(Boolean producerIsOver, ObjectOutputStream objectOutputStream, TerminalView view) {

        this.producerIsOver = producerIsOver;
        this.objectOutputStream = objectOutputStream;
        this.view = view;
    }

    public CommandReaderThread() {

    }

    public void sendMessage(Message message) {

        try {
            // send object
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            this.shutdown();
        }
    }

    public void shutdown() {
        this.exit = true;
        this.producerIsOver = true;
    }

    @Override
    public void run() {

        while (!this.exit && !Thread.interrupted()) {

            Message message = view.startReadMessage();

            this.sendMessage(message);

            if (message.getLastOption() == 5) {
                this.shutdown();
            }

        }

    }
}
