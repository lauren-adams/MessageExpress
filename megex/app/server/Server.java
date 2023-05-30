/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 3
 * Class: Data Communications
 *
 ************************************************/

package megex.app.server;

import java.net.*;
import java.io.*;

import java.util.logging.*;

import static tls.TLSFactory.getServerConnectedSocket;
import static tls.TLSFactory.getServerListeningSocket;

/**
 * The Server class listens for incoming client connections
 * and handles them concurrently using a thread pool
 */
public class Server {

    /**
     * Maximum amount of data to read
     */
    private static final int MAXDATASIZE = 8000;


    /**
     * Minimum interval between each read in milliseconds per stream
     */
    private static final int MINDATAINTERVAL = 500;

    /**
     * creates server class
     */
    public Server() {
    }

    /**
     * Initialize the server socket, start the thread pool, and handle clients
     *
     * @param args the command-line arguments
     * @throws Exception error while initializing the server or accepting
     * client connections
     */
    public static void main(String[] args) throws Exception {

        // Configure the logger
        Logger logger = configureLogger();

        // Test for correct number of arguments
        if (args.length != 3) {
            logger.log(Level.SEVERE,
                    "Parameter(s): <Port> <pool-size> <path>");
            return;
        }

        // Parse the server port and thread pool size from the command-line
        int servPort;
        int threadPoolSize;
        try {
            servPort = Integer.parseInt(args[0]);
            threadPoolSize = Integer.parseInt(args[1]);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error invalid parameters", e);
            return;
        }

        // Create a server socket to accept client connection requests
        // src//main//java//keystore
        ServerSocket servSocket;
        try {
            servSocket = getServerListeningSocket(servPort,
                    "keystore", "password");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failure to create server", e);
            return;
        }

        // Start the thread pool to handle incoming client connections
        for (int i = 0; i < threadPoolSize; i++) {
            Thread thread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            // Accept a client connection
                            Socket client = getServerConnectedSocket(servSocket);
                            // Handle the client connection with handleClient
                            clientProtocol.handleClient(client, logger, args[2], MAXDATASIZE, MINDATAINTERVAL);
                        } catch (Exception ex) {
                            logger.log(Level.WARNING, "Client accept failed",
                                    ex);
                        }
                    }
                }
            };
            thread.start();
        }

    }

    /**
     * Configures the logger for the server
     *
     * @return the configured logger
     */
    public static Logger configureLogger() {
        Logger logger = Logger.getLogger("server.log");
        logger.setUseParentHandlers(false);
        try {
            // Initialize a file handler for the logger
            FileHandler fileHandler = new FileHandler("server.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logger", e);
        }
        logger.setLevel(Level.ALL);
        return logger;
    }

}
