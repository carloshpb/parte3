package server_client.server.services;

import server_client.model.Message;
import server_client.server.threads.handlers.MessageData;

public interface MessageService {
    void processMessage(MessageData messageData);
    Message createMessage(Message message);
    Message readMessage(Message message);
    Message updateMessage(Message message);
    Message deleteMessage(Message message);
}
