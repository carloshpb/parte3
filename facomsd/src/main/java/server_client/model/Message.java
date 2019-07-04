package server_client.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/*
    Classe modelo da mensagem.
    Ela será do tipo Serializable, implementando a sua interface
    para que o objeto possa ser transformado em um monte de bytes e enviado entre
    cliente e servidor via Stream.
 */

//implements Serializable

public class Message implements Serializable{

    private static final long serialVersionUID = 2627984335773148702L;
    private int lastOption;
    private BigInteger id;
    private String message;

    public Message(int option, BigInteger id, String message) {

        this.lastOption = option;
        this.id = id;
        this.message = message;
    }

    public Message(int option, String message) {

        this.lastOption = option;
        this.id = BigInteger.valueOf(-1);
        this.message = message;
    }

    public Message(int option, BigInteger id) {

        this.lastOption = option;
        this.id = id;
        this.message = null;
    }

    public Message(BigInteger id, String message) {
        this.lastOption = -1;
        this.id = id;
        this.message = message;
    }

    public Message(String message) {
        this.lastOption = -1;
        this.id = BigInteger.valueOf(-1);
        this.message = message;
    }

    public Message() {
        this.lastOption = -1;
        this.id = BigInteger.valueOf(-1);
        this.message = null;
    }

    public int getLastOption() {

        return lastOption;
    }

    public void setLastOption(int option) {

        this.lastOption = option;
    }

    public BigInteger getId() {

        return id;
    }

    public void setId(BigInteger id) {

        this.id = id;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return id == message1.getId() &&
               message.equals(message1.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastOption, id, message);
    }

    @Override
    public String toString() {

        if ((this.id.compareTo(BigInteger.valueOf(-1)) == 0) && this.message != null && this.message.trim().isEmpty()) {
            return "(" + this.lastOption + ",)";
        } else if (this.id.compareTo(BigInteger.valueOf(-1)) == 0) {
            return "(" + this.lastOption + "," + this.message + ")";
        } else if (this.message == null) {
            return "(" + this.lastOption + "," + this.id + ")";
        } else if (this.message.trim().isEmpty()){
            return "(" + this.lastOption + "," + this.id + ",)";
        } else {
            return "(" + this.lastOption + "," + this.id + "," + this.message + ")";
        }
    }
}
