package server_client.server.repository.impl;

import server_client.model.Message;
import server_client.server.repository.MessageRepository;
import server_client.constants.StringsConstants;

import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;

/*
    MessageRepositoryMemory
    - Classe responsável de validar a mensagem com o banco de dados e saber se há um erro ou não, em relação ao banco.
    - Atuará direto sobre o banco de dados
 */
public class MessageRepositoryMemory implements MessageRepository {

    private final static Logger LOGGER = Logger.getLogger(MessageRepositoryMemory.class.getName());

    // Banco de dados
    private Map<BigInteger, String> mapDatabase;

    public MessageRepositoryMemory(Map<BigInteger, String> mapDatabase) {
        this.mapDatabase = mapDatabase;
    }

    // Cria mensagem e salva no banco
    @Override
    public Message create(Message message) {

        LOGGER.info("Mensagem " + message.getMessage() + " será adicionada ao banco.");

        Message answer = null;

        // Verifica se o ID colocado na mensagem já existe. Se sim, retorna mensagem com texto de erro. Senão, retorna sucesso.
        if (this.mapDatabase.containsKey(message.getId())) {
            answer = new Message(1, BigInteger.valueOf(-1), StringsConstants.ERR_EXISTENT_ID.toString());
        } else {
            this.mapDatabase.put(message.getId(), message.getMessage());
            answer = new Message(1, message.getId(), StringsConstants.MESSAGE_CREATION_SUCCESS_ID.toString() + message.getId() + " -- " + message.getMessage());
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");
        return answer;
    }

    // Retorna mensagem do banco para o cliente
    @Override
    public Message read(Message message) {

        String messageString = this.mapDatabase.get(message.getId());

        Message answer = null;

        // Se não existe a mensagem (null), então o ID colocado para leitura não existe.
        if (messageString == null) {
            answer = new Message(2, StringsConstants.ERR_NON_EXISTENT_ID.toString());
        }

        // Se existe mensagem, então retorna ela com seu respectivo ID.
        else {
            answer = new Message(2, message.getId(), messageString);
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");
        return answer;
    }

    // Fará update do ID que já existe no banco
    @Override
    public Message update(Message message) {

        Message answer = null;
        String messageFromDB = this.mapDatabase.get(message.getId());

        // Se o ID não existe, retorna mensagem de erro para o cliente
        if (messageFromDB == null) {
            answer = new Message(3, message.getId(), StringsConstants.ERR_NON_EXISTENT_ID.toString());
        } else {
            // Se o ID existe, retorna mensagem de sucesso do update para o cliente
            this.mapDatabase.replace(message.getId(), message.getMessage());
            answer = new Message(3, message.getId(), StringsConstants.MESSAGE_UPDATE_SUCCESS.toString());
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");

        return answer;
    }

    // Método responsável de deletar a mensagem do banco de dados
    @Override
    public Message delete(Message message) {

        Message answer = null;

        // Remove a mensagem do banco.
        // Se ela existia, será deletada e retornará a mensagem
        // Se ela não existia no banco, será retornado Null
        String text = this.mapDatabase.remove(message.getId());

        if (text == null) {
            answer = new Message(4, StringsConstants.ERR_NON_EXISTENT_ID.toString());
        } else {
            answer = new Message(4, message.getId(), StringsConstants.MESSAGE_DELETE_SUCCESS.toString());
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");

        return answer;
    }

}
