//package server_client.client;
//
//import server_client.client.consumer.AnswerPresentationThreadTest;
//import server_client.client.producer.CommandReaderThreadTest;
//import server_client.client.threads.ClientThread;
//import server_client.model.Message;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.util.concurrent.BlockingQueue;
//
//public class ClientThreadTest extends ClientThread {
//
//    private volatile BlockingQueue<Message> novasMensagens;
//    private volatile BlockingQueue<Message> novasRespostas;
//
//    public ClientThreadTest(BlockingQueue<Message> novasMensagens, BlockingQueue<Message> novasRespostas) {
//        this.novasMensagens = novasMensagens;
//        this.novasRespostas = novasRespostas;
//    }
//
//    public synchronized BlockingQueue<Message> getNovasMensagensQueue() {
//        return this.novasMensagens;
//    }
//
//    public synchronized BlockingQueue<Message> getNovasRespostasQueue() {
//        return this.novasRespostas;
//    }
//
//    @Override
//    protected void connect() {
//
//        int count = 0;
//        int maxTries = 3;
//
//        try {
//            socket = new Socket(serverName, serverPort);
//        } catch (IOException e) {
//            LOGGER.severe(e.getMessage());
//            return;
//        }
//
//        LOGGER.info("Conectado ao servidor " + socket.getRemoteSocketAddress());
//
//        try {
//            LOGGER.info("Iniciando as streams para conectar ao server ... - THREAD : " + Thread.currentThread().getName());
//            objectInputStream = new ObjectInputStream(socket.getInputStream());
//            LOGGER.info("Iniciado objectInputStream - THREAD : " + Thread.currentThread().getName());
//            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            LOGGER.info("Iniciado objectOutputStream - THREAD : " + Thread.currentThread().getName());
//            //objectOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            this.disconnect();
//        }
//
//        try {
//
//            LOGGER.info("Preparando threads consumer e producer do client ...");
//            Runnable commandReader = new CommandReaderThreadTest(this.producerIsOver, this.objectOutputStream, this.novasMensagens);
//            Runnable answerPresentation = new AnswerPresentationThreadTest(this.producerIsOver, this.objectInputStream, this.novasRespostas);
//            LOGGER.info("Threads consumer e producer prontas. Hora de executa-las.");
//
//            threadPool.execute(commandReader);
//            threadPool.execute(answerPresentation);
//
//        } catch (Exception e) {
//            LOGGER.severe(e.getMessage());
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException ex) {
//                LOGGER.severe(ex.getMessage());
//                this.disconnect();
//            }
//            if (++count == maxTries) {
//                this.disconnect();
//            }
//        }
//
//        this.awaitTerminationAfterShutdown();
//
//    }
//
//    @Override
//    public void run() {
//        this.connect();
//    }
//}
