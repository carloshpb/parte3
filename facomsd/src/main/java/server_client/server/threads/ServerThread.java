package server_client.server.threads;

import server_client.model.Message;
import server_client.server.database.MemoryDB;
import server_client.server.threads.handlers.ClientHandler;
import server_client.server.threads.handlers.MessageData;
import server_client.server.threads.message_queues.second_stage.SecondThirdQueueThread;
import server_client.server.threads.message_queues.third_stage.DatabaseProcessingThread;
import server_client.server.threads.message_queues.third_stage.LogThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class ServerThread implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(ServerThread.class.getName());

    private static volatile BlockingQueue<MessageData> fila1;
    private static volatile BlockingQueue<Message> fila2;
    private static volatile BlockingQueue<MessageData> fila3;

    private volatile boolean exit = false;

    private static int port;
    private ServerSocket serverSocket = null;
    private ExecutorService clientThreadPool = Executors.newFixedThreadPool(10);
    private ExecutorService queueThreadPool = Executors.newFixedThreadPool(3);

    static {
        fila1 = new LinkedBlockingDeque<>();
        fila2 = new LinkedBlockingDeque<>();
        fila3 = new LinkedBlockingDeque<>();
    }

    public ServerThread(int port) {
        ServerThread.port = port;
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Servidor iniciado na porta " + serverSocket.getLocalPort());
            LOGGER.info("A espera de um cliente conectar...");

            this.queueThreadPool.submit(new SecondThirdQueueThread());
            this.queueThreadPool.submit(new DatabaseProcessingThread());
            this.queueThreadPool.submit(new LogThread());

            this.getAndInsertClientConnection();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            try {
                this.stopServer();
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    private void getAndInsertClientConnection() throws IOException{
        while (!exit && !Thread.interrupted()) {

            ClientHandler clientConnection = new ClientHandler(this.serverSocket.accept());
            LOGGER.info("Conectou!");
            clientThreadPool.submit(clientConnection);
        }
    }

    public synchronized void stopServer() throws IOException{
        this.exit = true;
        if (this.serverSocket != null ) {
            this.serverSocket.close();
        }
    }

    public static BlockingQueue<MessageData> getFila1() {
        return fila1;
    }

    public static BlockingQueue<Message> getFila2() {
        return fila2;
    }

    public static BlockingQueue<MessageData> getFila3() {
        return fila3;
    }

    @Override
    public void run() {
        MemoryDB.getInstance();
        this.startServer();
    }
}
