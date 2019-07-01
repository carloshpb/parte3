package server_client.server.threads.handlers;

import io.atomix.core.Atomix;
import server_client.model.Message;

import java.util.concurrent.BlockingQueue;

public class MessageData {

    private Message message;
    private BlockingQueue<Message> answerQueue;
    private Atomix atomix;

    public MessageData(Message message, BlockingQueue<Message> answerQueue, Atomix atomix) {
        this.message = message;
        this.answerQueue = answerQueue;
        this.atomix = atomix;
    }

    public MessageData(Message message, BlockingQueue<Message> answerQueue) {
        this.message = message;
        this.answerQueue = answerQueue;
    }

    public synchronized Message getMessage() {
        return message;
    }

    public synchronized void setMessage(Message message) {
        this.message = message;
    }

    public synchronized BlockingQueue<Message> getAnswerQueue() {
        return answerQueue;
    }

    public synchronized Atomix getAtomix() {
        return atomix;
    }
}
