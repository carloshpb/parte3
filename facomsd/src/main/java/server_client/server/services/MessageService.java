package server_client.server.services;

import server_client.model.Message;

public interface MessageService {
    Message processMessage(Message messageData);
    Message createMessage(Message message);
    Message readMessage(Message message);
    Message updateMessage(Message message);
    Message deleteMessage(Message message);
}
