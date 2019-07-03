package server_client.server.repository.impl;

import io.atomix.core.map.AtomicMap;
import server_client.model.Message;
import server_client.server.repository.MessageRepository;
import server_client.server.database.MemoryDB;
import server_client.constants.StringsConstants;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class MessageRepositoryMemory implements MessageRepository {

    private final static Logger LOGGER = Logger.getLogger(MessageRepositoryMemory.class.getName());

    private static volatile AtomicLong counterCreator = new AtomicLong(0);

    public static long getLastId() {
        return counterCreator.get();
    }

    public static synchronized void resetAtomicLongIdCreator() {
        counterCreator = new AtomicLong(0);
    }

//    private final Logger LOGGER = Logger.getLogger(MessageRepositoryMemory.class.getName());
//
//    private volatile AtomicLong counterCreator = new AtomicLong(0);
//
//    public long getLastId() {
//        return counterCreator.get();
//    }
//
//    public synchronized void resetAtomicLongIdCreator() {
//        counterCreator = new AtomicLong(0);
//    }

    public String getKey(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return BigInteger.valueOf(-1).toString();
    }

    @Override
    public Message create(Message message) {

        LOGGER.info("Mensagem " + message.getMessage() + " será adicionada ao banco.");

        Message answer = null;

        if (MemoryDB.getDatabase().containsValue(message.getMessage())) {
//            long id = getKey(MemoryDB.getDatabase(), message.getMessage()).longValue();
            answer = new Message(1, -1, StringsConstants.ERR_EXISTENT_MESSAGE.toString());
        } else {

            long newId = counterCreator.incrementAndGet();

            while(this.existId(newId)) {
                newId = counterCreator.incrementAndGet();
            }

            MemoryDB.getDatabase().put("generico", message.getMessage());
            answer = new Message(1, newId, StringsConstants.MESSAGE_CREATION_SUCCESS_ID.toString() + newId + " -- " + message.getMessage());
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");
        return answer;
    }

    @Override
    public Message read(Message message) {

        String messageString = (String) MemoryDB.getDatabase().get(message.getId());

        Message answer = null;

        if (messageString == null) {
            answer = new Message(2, StringsConstants.ERR_NON_EXISTENT_ID.toString());
        } else {
            answer = new Message(2, message.getId(), messageString);
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");
        return answer;
    }

    @Override
    public Message update(Message message) {

        Message answer = null;
        String messageFromDB = MemoryDB.getDatabase().get(BigInteger.valueOf(message.getId()));

        if (messageFromDB == null) {

            answer = new Message(3, message.getId(), StringsConstants.ERR_NON_EXISTENT_ID.toString());

        } else if (MemoryDB.getDatabase().containsValue(message.getMessage())) {

            String id = getKey(MemoryDB.getDatabase(), MemoryDB.getDatabase().get(message.getMessage()));
            answer = new Message(3, StringsConstants.ERR_EXISTENT_MESSAGE.toString());

        } else {

            MemoryDB.getDatabase().replace(message.getId()+"", message.getMessage());
            answer = new Message(3, message.getId(), StringsConstants.MESSAGE_UPDATE_SUCCESS.toString());

        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");

        return answer;
    }

    @Override
    public Message delete(Message message) {

        Message answer = null;

        String text = MemoryDB.getDatabase().remove(BigInteger.valueOf(message.getId()));

        if (text == null) {
            answer = new Message(4, StringsConstants.ERR_NON_EXISTENT_ID.toString());
        } else {
            answer = new Message(4, message.getId(), StringsConstants.MESSAGE_DELETE_SUCCESS.toString());
        }

        LOGGER.info("Resposta " + answer + " será será retornada ao cliente.");

        return answer;
    }

    @Override
    public boolean existId(long id) {
        return MemoryDB.getDatabase().containsKey(BigInteger.valueOf(id));
    }

}
