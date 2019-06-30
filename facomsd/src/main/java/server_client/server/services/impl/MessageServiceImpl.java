package server_client.server.services.impl;

import server_client.model.Message;
import server_client.server.repository.MessageRepository;
import server_client.server.repository.impl.MessageRepositoryMemory;
import server_client.server.services.MessageService;
import server_client.constants.StringsConstants;
import server_client.server.threads.handlers.MessageData;

import java.util.logging.Logger;

public class MessageServiceImpl implements MessageService {

    private final static Logger LOGGER = Logger.getLogger(MessageServiceImpl.class.getName());

    private MessageRepository messageRepository = new MessageRepositoryMemory();

    @Override
    public synchronized void processMessage(MessageData messageData) {

        LOGGER.info("Mensagem " + messageData.getMessage() + " será processada para o banco de dados.");

        Message serverAnswer = null;

        switch (messageData.getMessage().getLastOption()) {
            case 1:
                serverAnswer = this.createMessage(messageData.getMessage());
                break;
            case 2:
                serverAnswer = this.readMessage(messageData.getMessage());
                break;
            case 3:
                serverAnswer = this.updateMessage(messageData.getMessage());
                break;
            case 4:
                serverAnswer = this.deleteMessage(messageData.getMessage());
                break;
            default:
                serverAnswer = new Message(StringsConstants.ERR_INVALID_OPTION.toString());
                break;
        }

        if (messageData.getAnswerQueue() != null) {
            try {
                messageData.getAnswerQueue().put(serverAnswer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Message createMessage(Message message) {
        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            return new Message(1, StringsConstants.ERR_EMPTY_SAVE_MESSAGE.toString());
        }

        return this.messageRepository.create(message);
    }

    @Override
    public Message readMessage(Message message) {

        return this.messageRepository.read(message);
    }

    @Override
    public Message updateMessage(Message message) {

        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            return new Message(3, StringsConstants.ERR_EMPTY_UPDATE_MESSAGE.toString());
        }

        return this.messageRepository.update(message);
    }

    @Override
    public Message deleteMessage(Message message) {

        return this.messageRepository.delete(message);
    }

}
