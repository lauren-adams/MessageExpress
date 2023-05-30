/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 2
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.app.client;

import megex.serialization.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static megex.serialization.SerializationUtility.*;
import static tls.TLSFactory.getClientSocket;

/**
 * This class manages connecting and gathering data from a server
 */
public class Client {

    /**
     * String of bytes used to connect to a server
     */
    protected static final String PREFACE = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";

    /**
     * increment used to get next odd
     */
    protected static final int ODDINC = 2;

    /**
     * value used to start streamIDs
     */
    protected static final int STARTSTREAMID = 1;

    /**
     * Creates a client object
     */
    public Client(){

    }

    /**
     * Connects to server and handles streams of data
     * @param args user provided server port and list of paths
     * @throws Exception if invalid
     */
    public static void main(String args[]) throws Exception {
        // Throws error if invalid number of arguments
        if (args.length < 2) {
            throw new IllegalArgumentException("Parameter(s): <Server> " +
                    "<Port> <list of paths>");
        }

        // Initialize Socket and classes used for data transfer
        try(Socket sock = getClientSocket(args[0],
                Integer.parseInt(args[1]))) {
            MessageFactory mf = new MessageFactory();
            Framer f = new Framer(sock.getOutputStream());
            Deframer d = new Deframer(sock.getInputStream());

            // Send initial connection preface
            sock.getOutputStream().write(
                    PREFACE.getBytes(StandardCharsets.US_ASCII));
            f.putFrame(mf.encode(new Settings()));


            // Initialize array of headers from args and send them to server
            ArrayList<Headers> headers = initHeaders(args);
            for (Headers h1 : headers) {
                f.putFrame(mf.encode(h1));
            }


            // Ensure settings object is read first
            Message settings;
            try {
                settings = mf.decode(d.getFrame());
                System.out.println("Received message: " + settings);
                if (settings.getCode() != SETTINGSTYPE) {
                    System.err.println("Error: invalid first frame");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error: invalid first frame");
                return;
            }


            // Initialize container for validated stream ids/associated files
            Map<Integer, File> validHeaders = new HashMap<>();


            do {
                try {
                    // Read in message from client
                    Message m = mf.decode(d.getFrame());

                    switch (m.getCode()) {
                        case DATATYPE -> {
                            // If Data is received checks its on valid list
                            Data data = (Data) m;
                            if (validHeaders.keySet().contains(
                                    m.getStreamID())) {
                                System.out.println("Received message: "+ data);

                                // Write payload of Data object to file
                                writeToFile(data.getData(), validHeaders.get(
                                        data.getStreamID()));


                                if (data.isEnd()) {
                                    // If data stream is concluded remove the
                                    // header stream from array
                                    headers.removeIf(h -> h.getStreamID() ==
                                            data.getStreamID());
                                    validHeaders.remove(data.getStreamID());
                                } else if (data.getData().length != 0) {
                                    // Sends appropriate Windows_Update if needed
                                    f.putFrame(mf.encode(new Window_Update
                                            (0, data.getData().length)));
                                    f.putFrame(mf.encode(new Window_Update
                                            (data.getStreamID(), data.getData()
                                                    .length)));
                                }
                            } else {
                                System.err.println("Unexpected stream ID: " +
                                        data);
                            }
                        }

                        case HEADERSTYPE -> {

                            // If headers is received make sure it is on list
                            Headers h = (Headers) m;
                            Headers header = getMatch(headers, h.getStreamID());
                            if (header != null) {
                                System.out.println("Received message: " + h);

                                // CHeck to make sure status is valid
                                String[] number = h.getValue(":status")
                                        .split(" ");
                                int status = Integer.parseInt(number[0]);
                                if ((status >= 200) && (status < 300)) {
                                    // If valid make a file to write to
                                    makeFile(header.getValue(":path"),
                                            h.getStreamID(), validHeaders);
                                } else {
                                    System.err.println("Bad status: " +
                                            h.getValue(":status"));
                                    headers.remove(header);
                                }
                            } else {
                                System.err.println("Unexpected stream ID: " +
                                        h);
                                headers.remove(h);
                            }
                        }

                        default -> System.out.println("Received message: " + m);
                    }
                } catch (BadAttributeException e) {
                    if (e.getMessage().equals("type is not valid")) {
                        System.err.println("Received unknown type: "
                                + e.getAttribute());
                    } else {
                        System.err.println("Invalid message: "
                                + e.getAttribute());
                    }
                } catch (Exception e) {
                    System.err.println("Unable to parse: " + e.getMessage());
                    return;
                }

            } while (!headers.isEmpty());
        } catch (Exception e) {
            System.err.println("Error: can't communicate with server");
            return;
        }
    }

    /**
     * Initializes the headers for the Megex request
     * @param args the command line arguments passed to method
     * @return ArrayList of Headers for the request
     * @throws BadAttributeException if there is an error in encoding
     */
    private static ArrayList<Headers> initHeaders(String[] args)
            throws BadAttributeException {
        ArrayList<Headers> headers = new ArrayList<>();
        int nextOdd = STARTSTREAMID;
        for (int i = 2; i < args.length; i++){
            Headers h = new Headers(nextOdd, true);
            h.addValue(":method", "GET");
            h.addValue(":path", args[i]);
            h.addValue(":authority", args[0]);
            h.addValue(":scheme", "https");
            headers.add(h);
            nextOdd += ODDINC;
        }
        return headers;
    }

    /**
     * Writes a byte array to a file
     * @param message the byte array to write to the file
     * @param f the file to write to
     * @throws IOException if there is an error with the file
     */
    private static void writeToFile(byte[] message, File f) throws IOException{
        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(message);
        fos.close();
    }


    /**
     * Creates a new file with the given name and associates it with StreamID
     * @param name the name of the file to create
     * @param StreamID the ID of the stream to associate with the file
     * @param validHeaders a map of StreamIDs to created files
     */
    private static void makeFile(String name, Integer StreamID,
                                 Map<Integer, File> validHeaders){
        File file = new File(name.replace('/', '-'));
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error failed file creation: "
                    + e.getMessage());
            System.exit(0);
        }
        validHeaders.put(StreamID, file);
    }

    /**
     * Finds the header with the given StreamID in the ArrayList of headers
     * @param headers the ArrayList of headers to search through
     * @param streamID the StreamID to search for
     * @return the header with the given StreamID, or null if not found
     */
    private static Headers getMatch(ArrayList<Headers> headers,
                                    Integer streamID){
        return headers.stream()
                .filter(h -> h.getStreamID() == streamID)
                .findFirst().orElse(null);
    }

}