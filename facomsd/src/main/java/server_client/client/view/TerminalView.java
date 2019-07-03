package server_client.client.view;

import server_client.constants.StringsConstants;
import server_client.model.Message;

import java.math.BigInteger;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

/*
    Terminal View
    - Esta classe executa em sequência, tanto que o único método público desta classe é o startReadMessage().
    - Para entender, basta ler a ordem de chamada dos métodos que estão dentro do startReadMessage().
 */

public class TerminalView {

    private final static Logger LOGGER = Logger.getLogger(TerminalView.class.getName());

    private Scanner scanner = new Scanner(System.in);

    private int option;
    private BigInteger id;
    private String message;

    public TerminalView() {
    }

    public Message startReadMessage() {

        this.beginIntro();
        this.chooseOption();
        Message answer = this.writeMessage();
        return answer;
    }

    private void beginIntro() {

        System.out.println(StringsConstants.TITLE.toString());
        System.out.println(StringsConstants.MENU_OPTIONS.toString());

        this.option = -1;
        this.id = BigInteger.valueOf(-1);
        this.message = null;
    }

    private void chooseOption() {

        while (true) {
            try {
                if (this.option == -1) {
                    this.option = scanner.nextInt();
                }

                if (this.option < 1 || this.option > 5) {
                    throw new InputMismatchException();
                }
                break;
            } catch (InputMismatchException | NumberFormatException ex) {
                LOGGER.severe(StringsConstants.ERR_CHOOSE_SINGLE_OPTION.toString());
                this.option = -1;
            }
        }
    }

    private Message writeMessage() {

        switch (this.option) {

            case 1:
            case 3:
                this.typeId();
                this.typeMessage();
                break;


            case 2:
            case 4:
                this.typeId();
                break;

            case 5:
                break;

        } // end switch

        return new Message(this.option, this.id, this.message);
    }

    private void typeMessage() {
        do {
            System.out.println(StringsConstants.TYPE_MESSAGE.toString());
            try {
                this.message = scanner.nextLine();

                if (this.message.trim().isEmpty()) {
                    throw new Exception(StringsConstants.ERR_EMPTY_SAVE_MESSAGE.toString());
                }

            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
                this.message = null;
            }
        } while (this.message == null);
    }

    private void typeId() {
        while (this.id.compareTo(BigInteger.valueOf(-1)) == 0) {
            System.out.println(StringsConstants.TYPE_ID.toString());

            try {
                this.id = scanner.nextBigInteger();
                //Próximo nextLine serve pra limpar o \n inserido com o número no nextBigInteger anterior
                scanner.nextLine();
            } catch (InputMismatchException | NumberFormatException e) {
                LOGGER.severe(StringsConstants.ERR_NON_INT.toString());
                this.id = BigInteger.valueOf(-1);
            }
        }
    }
}
