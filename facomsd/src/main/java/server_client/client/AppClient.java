package server_client.client;

import server_client.client.threads.ClientThread;

import java.util.logging.Logger;

public class AppClient {

    public static void main(String[] args) {

        final Logger LOGGER = Logger.getLogger(AppClient.class.getName());

        Thread client = new Thread(new ClientThread());
        client.start();

        try {
            // Wait for the server to shutdown
            client.join();
            LOGGER.info("Completed shutdown.");
        } catch (InterruptedException e) {
            // Exit with an error condition
            LOGGER.severe("Interrupted before accept thread completed.");
            System.exit(1);
        }
    }
}
