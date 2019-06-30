package server_client.client.consumer;

import server_client.client.threads.consumer.AnswerPresentationThread;
import server_client.model.Message;

import javax.swing.*;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class AnswerPresentationThreadTest extends AnswerPresentationThread {

    private volatile BlockingQueue<Message> novasRespostas;

    public AnswerPresentationThreadTest(Boolean producerIsOver, ObjectInputStream objectInputStream, BlockingQueue<Message> novasRespostas) {
        super();
        this.producerIsOver = producerIsOver;
        this.objectInputStream = objectInputStream;
        this.novasRespostas = novasRespostas;
    }

    @Override
    public void run() {

        Timer timer = null;
        Message answer = null;

        while (!this.exit && !Thread.interrupted()) {

            if (this.producerIsOver && timer == null) {
                final int FIVE_SECONDS = 5000;

                timer = new Timer(FIVE_SECONDS, event -> {
                    this.shutdown();
                });

                timer.start();
            }

            try {

                answer = (Message) objectInputStream.readObject();

                if (answer != null) {
                    if (answer.getLastOption() == 5) {
                        System.out.println("Shutting down.");
                        this.shutdown();
                    } else {
                        this.novasRespostas.put(answer);
                        answer = null;
                    }
                }

            } catch (EOFException e) {
                this.shutdown();
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                this.shutdown();
            }
        }
    }

}
