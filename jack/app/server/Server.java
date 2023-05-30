/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 6
 * Class: Data Communications 4321
 *
 ************************************************/


package jack.app.server;

import jack.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;


/**
 * UDP server instance for MegEx class
 */
public class Server {

    /**
     * list of servers added by new calls
     */
    static ArrayList<Service> serverList = new ArrayList<>();

    /**
     * Max size of UDP payload
     */
    private static final int UDPMAX =  65514;

    /**
     * constant used for encode/decode setting charset
     */
    private static final Charset CHARENC = StandardCharsets.US_ASCII;

    /**
     * op code of ACK
     */
    public static final String OPACK = "A";

    /**
     * op code of Error
     */
    public static final String OPERR = "E";


    /**
     * op code of Response
     */
    public static final String OPRES = "R";

    /**
     * op code of New
     */
    public static final String OPNEW = "N";

    /**
     * op code of Query
     */
    public static final String OPQUERY = "Q";


    /**
     * Generate a server, necessary for JavDoc specifications
     */
    public Server() {
    }

    /**
     * main functionality of server
     * @param args port to be used
     * @throws IOException if reading fails
     */
    public static void main(String[] args) throws IOException {

        // Configure the logger
        Logger logger = configureLogger();

        // validate parameters
        int servPort;
        try {
            if (args.length != 1) { // Test for correct argument list
                throw new IOException("Incorrect number of parameters");
            }
            servPort = Integer.parseInt(args[0]);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Invalid parameters: " + args[0]);
            return;
        }

        // Initialize socket
        DatagramSocket socket = new DatagramSocket(servPort);
        DatagramPacket packet = new DatagramPacket(new byte[UDPMAX], UDPMAX);

        // Run forever, receiving and echoing datagrams
        while (true) {
            try {

                // Receive packet from client
                socket.receive(packet);
                byte[] rData = Arrays.copyOfRange(packet.getData(),
                        0, packet.getLength());

                //read in and validate message
                try {
                    Message r = Message.decode(rData);


                    //generate response
                    String replyMessage = "";
                    switch (r.getOperation()) {
                        case OPNEW -> {
                            Service temp = new Service(((New) r).getHost(),
                                    ((New) r).getPort());
                            if (!serverList.contains(temp)) {
                                serverList.add(temp);
                            }
                            replyMessage = OPACK + " " + ((New) r).getHost()
                                    + ":" + ((New) r).getPort();
                        }
                        case OPQUERY -> {
                            replyMessage = matchHost(serverList, ((Query) r).getSearchString());
                        }
                        default -> {
                            replyMessage = OPERR + " Unexpected message type: " + r.toString();
                        }

                    }
                    //send reply and log message
                    DatagramPacket reply = new DatagramPacket(replyMessage.getBytes(CHARENC),
                            replyMessage.getBytes(StandardCharsets.UTF_8).length,
                            packet.getAddress(), packet.getPort());
                    logger.log(Level.INFO, "Recieved: " + r.toString() +
                            "\n" + "Reply: " + replyMessage);

                    //Send the same packet back to client
                    socket.send(reply);
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "Invalid message: "
                            + e.getMessage());
                }
                //Reset length to avoid shrinking buffer
                packet.setLength(UDPMAX);
            } catch (Exception e){
                logger.log(Level.SEVERE, "Communication problem: " + e);
            }
        }

    }

    /**
     * returns string for Reply with hosts that contain q
     * @param services list of stored replies
     * @param str the search string to match
     * @return string representation of response
     */
    public static String matchHost(ArrayList<Service> services, String str) {
        return OPRES + " " + services.stream()
                .filter(service -> str.equals("*") ||
                        service.getHost().contains(str))
                .map(service -> service.getHost() + ":" + service.getPort())
                .collect(Collectors.joining(" "));
    }

    /**
     * Configures the logger for the server
     *
     * @return the configured logger
     */
    public static Logger configureLogger() {
        Logger logger = Logger.getLogger("jack.log");
        logger.setUseParentHandlers(false);
        try {
            // Initialize a file handler for the logger
            FileHandler fileHandler = new FileHandler("jack.log");
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





