package server_client.client.view;

import server_client.constants.StringsConstants;
import server_client.model.Message;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

public class TerminalView {

    private final static Logger LOGGER = Logger.getLogger(TerminalView.class.getName());

    private Scanner scanner = new Scanner(System.in);

    private int option;
    private long id;
    private String message;

    private volatile boolean wait = false;

    public TerminalView() {
    }

    public Message startReadMessage() {

        while (this.wait) { }

        this.beginIntro();
        this.chooseOption();
        Message answer = this.writeMessage();
        this.wait = true;
        return answer;
    }

    private void beginIntro() {

        System.out.println(StringsConstants.TITLE.toString());
        System.out.println(StringsConstants.MENU_OPTIONS.toString());

        this.option = -1;
        this.id = -1;
        this.message = null;
    }

    private void chooseOption() {

        while (true) {
            try {
                if (this.option == -1) {
                    this.option = Integer.parseInt(scanner.nextLine());
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
                System.out.println(StringsConstants.TYPE_MESSAGE.toString());
                while (this.message == null) {
                    try {
                        this.message = scanner.nextLine();

                        if (this.message.trim().isEmpty()) {
                            throw new Exception(StringsConstants.ERR_EMPTY_SAVE_MESSAGE.toString());
                        }

                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                        this.message = null;
                    }
                }
                break;


            case 2:
            case 4:
                this.typeId();
                break;


            case 3:
                this.typeId();

                while (this.message == null) {
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
                }
                break;


            case 5:
                break;

        } // end switch

        return new Message(this.option, this.id, this.message);
    }

    private void typeId() {
        while (this.id == -1) {
            System.out.println(StringsConstants.TYPE_ID.toString());

            try {
                this.id = Integer.parseInt(scanner.nextLine());
            } catch (InputMismatchException | NumberFormatException e) {
                LOGGER.severe(StringsConstants.ERR_NON_INT.toString());
                this.id = -1;
            }
        }
    }

    public void readMessage(Message message) {
        System.out.println(message.getMessage());
        this.wait = false;
    }
}
