package server_client.server.services.impl;

import server_client.model.Message;
import server_client.server.repository.MessageRepository;
import server_client.server.repository.impl.MessageRepositoryMemory;
import server_client.server.services.MessageService;
import server_client.constants.StringsConstants;

import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;

public class MessageServiceImpl implements MessageService {

    private final static Logger LOGGER = Logger.getLogger(MessageServiceImpl.class.getName());

    private MessageRepository messageRepository;

    public MessageServiceImpl(Map<BigInteger, String> mapDB) {
        this.messageRepository =  new MessageRepositoryMemory(mapDB);
    }

    // Método que agirá de forma síncrona no servidor (apenas uma thread pode usar este método por vez)
    // com o objetivo de escolher o respectivo método que usará para a mensagem sobre o banco de dados
    // (create, read, update, delete)
    // Este é o único método que deve ser utilizado desta classe
    @Override
    public synchronized Message processMessage(Message message) {

        LOGGER.info("Mensagem " + message.getMessage() + " será processada para o banco de dados.");

        Message serverAnswer = null;

        switch (message.getLastOption()) {
            case 1:
                serverAnswer = this.createMessage(message);
                break;
            case 2:
                serverAnswer = this.readMessage(message);
                break;
            case 3:
                serverAnswer = this.updateMessage(message);
                break;
            case 4:
                serverAnswer = this.deleteMessage(message);
                break;
            default:
                serverAnswer = new Message(StringsConstants.ERR_INVALID_OPTION.toString());
                break;
        }

        return serverAnswer;
    }

    @Override
    public Message createMessage(Message message) {
        // Se a mensagem for vazia ou Null , retornará mensagem de erro
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
        // Se a mensagem for vazia ou Null , retornará mensagem de erro
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
