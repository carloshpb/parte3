package server_client.client.threads;

import server_client.client.threads.consumer.AnswerPresentationThread;
import server_client.client.threads.producer.CommandReaderThread;
import server_client.client.view.TerminalView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientThread implements Runnable {

    protected final static Logger LOGGER = Logger.getLogger(ClientThread.class.getName());

    protected volatile Boolean producerIsOver = false;
    protected final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    protected volatile boolean exit = false;

    protected String serverName = "127.0.0.1";
    protected int serverPort = 8080;
    protected Socket socket = null;
    protected ObjectOutputStream objectOutputStream;
    protected ObjectInputStream objectInputStream;

    protected void connect() {

        int count = 0;
        int maxTries = 3;

        try {
            socket = new Socket(serverName, serverPort);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return;
        }

        LOGGER.info("Conectado ao servidor " + socket.getRemoteSocketAddress());

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.disconnect();
        }

        try {

            final TerminalView terminalView = new TerminalView();

            Runnable commandReader = new CommandReaderThread(this.producerIsOver, this.objectOutputStream, terminalView);
            Runnable answerPresentation = new AnswerPresentationThread(this.producerIsOver, this.objectInputStream, terminalView);

            threadPool.execute(commandReader);
            threadPool.execute(answerPresentation);

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                LOGGER.severe(ex.getMessage());
                this.disconnect();
            }
            if (++count == maxTries) {
                this.disconnect();
            }
        }

        this.awaitTerminationAfterShutdown();

    }

    public synchronized void awaitTerminationAfterShutdown() {
        this.threadPool.shutdown();
        try {
            if (!this.threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                this.threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            this.threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Acabou!");
        this.disconnect();
    }

    public synchronized void disconnect() {
        try {
            if (!this.socket.isInputShutdown()) {
                objectInputStream.close();
            }
            if (!this.socket.isOutputShutdown()) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }

            this.socket.close();
        } catch (Exception e) {
            LOGGER.severe("Error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        this.connect();
    }
}
