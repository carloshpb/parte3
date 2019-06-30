package server_client.server.threads.handlers;

import server_client.constants.StringsConstants;
import server_client.model.Message;
import server_client.server.threads.message_queues.first_stage.FirstQueueThread;
import server_client.server.threads.message_queues.second_stage.SecondThirdQueueThread;
import server_client.server.threads.message_queues.third_stage.DatabaseProcessingThread;
import server_client.server.threads.message_queues.third_stage.LogThread;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private volatile boolean exit = false;

    private Socket socket = null;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    private MessageData messageData;
    private volatile BlockingQueue<Message> answerQueue;

    private final static Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    private void connect() {
        try {
            if (socket.getRemoteSocketAddress() != null) {
                LOGGER.info("Cliente " + socket.getRemoteSocketAddress() + " conectado ao servidor...");
            } else {
                LOGGER.info("Cliente " + socket.toString() + " conectado ao servidor...");
            }

            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            LOGGER.info("Streams preparados para cliente " + socket.getRemoteSocketAddress());

            this.receiveMessage();

        } catch (Exception e) {
            e.printStackTrace();
            this.disconnect();
        }
    }

    private void receiveMessage() throws Exception {
        LOGGER.info("Start receive message");
        while (!exit && !Thread.interrupted()) {
            // get data from client
            Message message = null;
            try {
                message = (Message) objectInputStream.readObject();
            } catch (EOFException e) {
                this.disconnect();
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                this.disconnect();
                continue;
            }

            LOGGER.info("Mensagem obtida: " + message);

            if (message == null) {
                continue;
            }

            if (message.getLastOption() < 1 || message.getLastOption() > 5) {
                LOGGER.info(StringsConstants.ERR_INVALID_OPTION.toString());
                this.sendAnswer(new Message(StringsConstants.ERR_INVALID_OPTION.toString()));
                this.disconnect();
                continue;
            }

            if (message.getLastOption() == 5) {
                this.disconnect();
                this.sendAnswer(message);
                continue;
            }

            this.messageData = new MessageData(message, this.answerQueue);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new FirstQueueThread(this.messageData));

            this.sendAnswer(this.answerQueue.take());
        }
    }

    private void sendAnswer(Message answer) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(answer);
        objectOutputStream.flush();
    }

    public synchronized void disconnect() {
        try {
            this.exit = true;
            LOGGER.info("Cliente " + socket.getRemoteSocketAddress() + " desconectado do servidor...");
        } catch (Exception e) {
            LOGGER.severe("Error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        this.answerQueue = new LinkedBlockingDeque<>();
        this.connect();
    }
}
