/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 3
 * Class: Data Communications
 *
 ************************************************/

package megex.app.server;

import megex.serialization.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static megex.serialization.SerializationUtility.*;


/**
 * Runnable class for handling a client connection
 */
public class clientProtocol implements Runnable{

    /**
     * Correct Preface used to connect
     */
    protected static final String PREFACE = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";

    /**
     * Maximum amount of data to read
     */
    private int MAXDATASIZE;

    /**
     * MAx server can be open before disconnect
     */
    private static final int TIMEOUTTIME = 40000;

    /**
     * Minimum interval between each read in milliseconds per stream
     */
    private int MINDATAINTERVAL;

    /**
     * Socket object representing the client connection
     */
    private final Socket client;

    /**
     * Logger instance for logging received data
     */
    private final Logger logger;

    /**
     * Directory Server is given
     */
    private final String directory;


    /**
     * Creates a new handleClient instance with the provided
     * Socket, Logger, and directory objects.
     * @param client the client Socket object
     * @param logger the Logger instance to log received data
     * @param dir the directory to save files received from the client
     * @param maxdata max size set by user
     * @param minint min size
     */
    public clientProtocol(Socket client, Logger logger, String dir, Integer maxdata, Integer minint){
        this.client = client;
        this.logger = logger;
        this.directory = dir;
        this.MAXDATASIZE = maxdata;
        this.MINDATAINTERVAL = minint;
    }

    /**
     * runs the handleClient method
     */
    public void run(){
        handleClient(client, logger, directory, MAXDATASIZE, MINDATAINTERVAL);
    }

    /**
     * Handles a client connection on a separate thread.
     * @param client The Socket object representing the client connection.
     * @param logger The Logger object used for logging.
     * @param directory The directory from which to serve files.
     * @param MAXDATASIZE the biggest it can be
     * @param MINDATAINTERVAL the min data interval
     */
    public static void handleClient(Socket client, Logger logger,
                                    String directory, int MAXDATASIZE, int MINDATAINTERVAL) {

    logger.info("Client " + client.getRemoteSocketAddress()
            + " connected");
    try {
        // Set timeout to 40 seconds
        client.setSoTimeout(MINDATAINTERVAL);

        //initalialize needed sending compnents
        MessageFactory mf = new MessageFactory();
        Framer f = new Framer(client.getOutputStream());
        Deframer d = new Deframer(client.getInputStream());

        //check if preface is okay otherwise shut down
        if (!checkPreface(f, d, logger, client, mf)) {
            client.close();
            return;
        }

        //set up components used for data recieveing and processing
        ArrayList<Headers> headers = new ArrayList<>();
        Map<Integer, FileInputStream> map = new HashMap<>();

        //used for loop control
        int countTime = 0;
        boolean exit = false;

        do {
            try {
                // read in initial message and send any data if available
                Message m = mf.decode(d.getFrame());
                countTime = 0;

                switch (m.getCode()) {
                    case DATATYPE -> {
                        logger.log(Level.INFO, "Unexpected message: "
                                + (Data) m);
                    }
                    case HEADERSTYPE -> {
                        logger.log(Level.INFO, "Received: " +
                                ((Headers) m).toString());
                        //check is Id is not duplicate or invalid
                        if (goodID((Headers) m, headers, logger)) {
                            if (sendHeader(((Headers) m).getStreamID(),
                                    f, mf, logger,
                                    goodPath(((Headers) m).getValue(":path"),
                                            directory))) {
                                //makes a file from the parameters and
                                File file = new File(directory +
                                        ((Headers) m).getValue(":path"));
                                loadMap(map, file,
                                        ((Headers) m).getStreamID(), logger,
                                        mf, f);
                            }
                        }

                    }

                    default -> {
                        logger.log(Level.INFO, "Received message: " + m);
                    }
                }
            } catch (SocketTimeoutException e) {
                //test if blocking has exceeded limit
                countTime += MINDATAINTERVAL;
                 if (countTime >= TIMEOUTTIME || client.getSoTimeout() == TIMEOUTTIME) {
                     logger.log(Level.WARNING, "Server Timeout");
                     exit = true;
                }
                 // send data when there is nothing to read
                sendData(map, logger, f, mf, MAXDATASIZE, MINDATAINTERVAL);
            } catch (BadAttributeException e) {
                // catch incorrect frames
                logger.log(Level.WARNING, "Unable to parse: ", e.toString());
            } catch (EOFException e) {
                //catch error thrown by reading end of file
                client.setSoTimeout(TIMEOUTTIME);
                exit = true;
            } catch (IOException e){
                logger.log(Level.WARNING, "Unable to parse: ", e.getMessage());
            }
        } while (!exit);
    }catch (Exception e){
        // catch fatal errors and shut down
        logger.log(Level.WARNING, "Error: ", e);
    }

    logger.info("Client " + client.getRemoteSocketAddress()
            + " handling completed");
    try {
        client.close();
    } catch (IOException ignored) {

    }

    }

    /**
     * Loads a map with stream ID and FileInputStream if non-zero length
     * If the file is empty, encodes and sends an empty Data message
     * @param map the Map to load
     * @param file the File to load from
     * @param streamID the stream ID to use
     * @param logger the Logger to use for logging
     * @param mf the MessageFactory to use for message encoding
     * @param f the Framer to use for sending frames
     * @throws IOException if an IO error occurs while loading the file
     * @throws BadAttributeException if failure when encoding
     */
    private static void loadMap(Map<Integer, FileInputStream> map, File file,
                                Integer streamID, Logger logger,
                                MessageFactory mf, Framer f)
            throws IOException, BadAttributeException {
        if (file.length() != 0) {
            FileInputStream fis = new FileInputStream(file);
            map.put(streamID, fis);
        } else {
            //if no data in file just send empty data message
            f.putFrame(mf.encode(new Data(streamID, true, new byte[]{})));
            logger.log(Level.INFO, "Sent message: " + new Data(streamID,
                    true, new byte[]{}));
        }
    }


    /**
     * Sends a header message with stream ID and status code to the Framer
     * Logs the event and returns whether the status code was empty or not
     * @param streamID the stream ID to use
     * @param f the Framer to use for sending frames
     * @param mf the MessageFactory to use for message encoding
     * @param logger the Logger to use for logging
     * @param check the status code to use, or an empty string if none
     * @return true if the status code was empty, false otherwise
     * @throws BadAttributeException if failure while encoding a message
     * @throws IOException if an IO error occurs while sending the message
     */
    private static boolean sendHeader(int streamID, Framer f, MessageFactory mf,
                                      Logger logger, String check)
            throws BadAttributeException, IOException {
        Headers h = new Headers(streamID, false);
        boolean good;
        if (check.length() == 0) {
            h.addValue(":status", "200");
            good = true;
        } else {
            logger.log(Level.WARNING, check);
            h.addValue(":status", check);
            good = false;
        }
        // send frame with correct status code
        f.putFrame(mf.encode(h));
        logger.log(Level.INFO, "Sent message: " + h);
        return good;
    }

    /**
     * checks preface and returns false if invalid
     * @param f the Framer to use for sending frames
     * @param mf the MessageFactory to use for message encoding
     * @param logger the Logger to use for logging
     * @param client the socket we are using
     * @return true preface is valid
     */
    private static boolean checkPreface(Framer f, Deframer d, Logger logger,
                                        Socket client, MessageFactory mf) {
        //check for correct preface string and returns false otherwise
        byte[] b = new byte[24];
        try {
            client.getInputStream().read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String byteString = new String(b, StandardCharsets.US_ASCII);
        if (!byteString.equals(PREFACE)) {
            logger.log(Level.SEVERE, "Bad preface: " + byteString);
            return false;
        }
        //check for correct preface frame
        try {
            Message settings = mf.decode(d.getFrame());
            logger.log(Level.INFO, "Received message: " + settings);
            if (settings.getCode() != SETTINGSTYPE) {
                logger.log(Level.SEVERE, "Bad preface: ");
                return false;
            }
            //sends server side of preface exchange
            f.putFrame(mf.encode(new Settings()));
            logger.log(Level.INFO, "Sent message: " + new Settings());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Bad preface: ");
            return false;
        }
        return true;
    }


    /**
     * Returns whether the specified file path is valid.
     *
     * @param path the file path to check
     * @param dir the directory containing the file
     * @return a whether the specified file path is valid
     */
    private static String goodPath(String path, String dir) {
        if (path == null || path.trim().isEmpty()) {
            return "400";
        }
        // files used to test the strings for validity
        File file = new File(dir + path);
        File directory = new File(dir);

        if (!directory.exists() || !directory.isDirectory() ||
                !directory.canRead()) {
            return "403";
        }

        if (!file.exists() || !file.canRead()) {
            return "404";
        }

        return "";
    }

    /**
     * Determines whether the specified request ID is valid.
     *
     * @param h the Headers with the ID
     * @param headers the list of previously received headers
     * @param logger the logger to use for warnings
     * @return true if the request ID is valid
     */
    private static boolean goodID(Headers h, List<Headers> headers,
                                  Logger logger) {
        if (h.getStreamID() % 2 == 0 || h.getStreamID() == 0) {
            logger.log(Level.WARNING, "Illegal stream ID: " + h);
            return false;
        }

        if (headers.stream().anyMatch(temp -> temp.getStreamID() ==
                h.getStreamID())) {
            logger.log(Level.WARNING, "Duplicate request: " + h);
            return false;
        }

        if (headers.size() > 0 && h.getStreamID()
                <= headers.get(headers.size() - 1).getStreamID()) {
            logger.log(Level.WARNING, "Illegal stream ID: " + h);
            return false;
        }
        headers.add(h);
        return true;
    }

    /**
     * Trims the specified byte array to the specified length.
     *
     * @param bytes the byte array to trim
     * @param length the length to trim the byte array to
     * @return the trimmed byte array
     */
    private static byte[] trim(byte[] bytes, int length) {
        byte[] trimmedBytes = new byte[length];
        System.arraycopy(bytes, 0, trimmedBytes, 0, length);
        return trimmedBytes;
    }

    /**
     * Splits the data in the specified map into frames and sends them.
     *
     * @param map the map of stream IDs to input streams containing data
     * @param logger the logger to use for logging
     * @param f the framer to use for framing the data
     * @param mf the message factory to use for encoding the data
     * @throws RuntimeException if there is an error processing the data
     * @throws IOException if there is an I/O error
     * @throws BadAttributeException if the data contains bad attributes
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    private static void sendData(Map<Integer, FileInputStream> map,
                                  Logger logger, Framer f, MessageFactory mf, Integer MAXDATASIZE, Integer MINDATAINTERVAL)
            throws RuntimeException, IOException, BadAttributeException {
        //components used tp read in data
        List<Integer> invalid = new ArrayList<>();
        byte[] bytes = new byte[MAXDATASIZE];

        for (Map.Entry<Integer, FileInputStream> entry : map.entrySet()) {
            int bytesRead = entry.getValue().read(bytes);

            if (bytesRead <= 0) {
                invalid.add(entry.getKey());
            } else {
                if (bytesRead < MAXDATASIZE) {
                    bytes = trim(bytes, bytesRead);
                }
                f.putFrame(mf.encode(new Data(entry.getKey(),
                        entry.getValue().available() == 0, bytes)));
                logger.log(Level.INFO, "Sent message: " +
                        new Data(entry.getKey(),
                                entry.getValue().available() == 0, bytes));
            }

        }

        //remove from map once end of file is reached
        for (Integer i: invalid){
            map.remove(i);
        }

    }

}
