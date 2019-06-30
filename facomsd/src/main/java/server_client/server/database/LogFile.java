package server_client.server.database;

import server_client.model.Message;
import server_client.server.services.MessageService;
import server_client.server.services.impl.MessageServiceImpl;
import server_client.server.threads.handlers.MessageData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Classe interna do LogFile
 * Criada para ficar mais separado as funções que lidarão mais com o logFile do que com o DB */
public class LogFile {

    /* Diretório do arquivo de log */
    private static final Path logFile = Paths.get("./logs/db.log");

    /* Objetos buffers, responsáveis para ler e escrever no logFile */
    private static BufferedReader reader;
    private static BufferedWriter writer;

    /* Padrõe regex para entender as strings retiradas do logFile e dividi-las,
     * podendo recuperar a informação e transforma-las em objetos  */

    /* Message(option, message) - ex: (1,"olá") */
    private static final String regex1 = "^\\(([0-9]+),([\\s\\S]+)\\)$";
    private static final Pattern pattern1 = Pattern.compile(regex1);

    /* Message(option, id, message) - ex: (3,1,"olá mundo") */
    private static final String regex2 = "^\\(([0-9]+),([0-9]+),([\\s\\S]+)\\)$";
    private static final Pattern pattern2 = Pattern.compile(regex2);

    public synchronized static void startLog() {
        LogFile.openLog();
        LogFile.readLog();
    }

    /* Método usado para abrir/criar o logFile no programa */
    private synchronized static void openLog() {

        try {
            if (Files.notExists(logFile)) {
                Files.createFile(logFile);
            }
            reader = Files.newBufferedReader(logFile);
            writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            closeLog();
        }
    }

    /* Método usado para fechar as conexões com o logFile - IMPORTANTISSIMO ou o logFile perderá informações */
    public synchronized static void closeLog() {

        try {
            writer.flush();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Método que irá ler linha por linha do logFile, descobrirá qual dos dois padrões que é, e pegará as informações
     * uma por uma, criando assim um objeto Message novo. */
    public synchronized static void readLog() {

        System.out.println("Recuperando o BD através do log");

        openLog();

        MessageService service = new MessageServiceImpl();

        try {

            String line = null;

            while ((line = reader.readLine()) != null) {

                Matcher matcher1 = pattern1.matcher(line);
                Matcher matcher2 = pattern2.matcher(line);

                Message backupMsg = null;

                if (matcher2.find()) {
                    backupMsg = new Message(Integer.parseInt(matcher2.group(1)), Long.parseLong(matcher2.group(2)), matcher2.group(3));
                } else if (matcher1.find()) {
                    if (Integer.parseInt(matcher1.group(1)) == 1) {
                        backupMsg = new Message(Integer.parseInt(matcher1.group(1)), matcher1.group(2));
                    } else if (Integer.parseInt(matcher1.group(1)) == 4) {
                        backupMsg = new Message(Integer.parseInt(matcher1.group(1)), Long.parseLong(matcher1.group(2)));
                    } else if (Integer.parseInt(matcher1.group(1)) == 3) {
                        try {
                            backupMsg = new Message(Integer.parseInt(matcher1.group(1)), Long.parseLong(matcher1.group(2)));
                        } catch (NumberFormatException e) {
                            backupMsg = new Message(Integer.parseInt(matcher1.group(1)), matcher1.group(2));
                        }
                    }
                }

                service.processMessage(new MessageData(backupMsg, null));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeLog();
        }
    }

    /* Método usado para salvar uma nova operação no logFile*/
    public static synchronized void saveOperationLog(Message message) {

        openLog();

        try {
            writer.write(message.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeLog();
        }
    }

    public static void deleteLogFile() {
        File file = logFile.toFile();
        if (file.delete()) {
            System.out.println("LogFile deletado.");
        } else {
            System.out.println("LogFile não existe.");
        }
    }
}
