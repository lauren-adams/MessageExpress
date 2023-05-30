/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 5
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.app.client;

import jack.serialization.*;
import jack.serialization.Error;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Represents a client for jack
 */
public class Client {

    /**
     * sets timeout in 3 seconds
     */
    private static final int TIMEOUT = 3000;
    /**
     * sets max times to try
     */
    private static final int MAXTRIES = 3;
    /**
     * sets max length of a payload
     */
    private static final int MAXUDP = 65514;

    /**
     * sets min length of arguments
     */
    private static final int MINARGS = 3;

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
     * creates client object
     */
    public Client() {
    }

    /**
     * main functionality of client
     * @param args server port op payload
     * @throws IOException if it is interrupted
     */
    public static void main(String[] args) throws IOException {

        // validate length
        if ((args.length < MINARGS) || (!args[2].equals(OPRES)
                && args.length < MINARGS + 1)) {
            System.err.println("Parameter(s): <Server> <port> " +
                    "<Op> <payload>");
            return;
        }

        // initialize client from provided variables
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);
        DatagramPacket receivePacket = new DatagramPacket(new byte[MAXUDP],
                MAXUDP);

        InetAddress serverAddress;
        int servPort;
        try {
            servPort = Integer.parseInt(args[1]);
            serverAddress = InetAddress.getByName(args[0]);
        } catch (Exception e){
            System.err.println("Bad parameters: " + e.getMessage());
            return;
        }


        int tries = 0;
        Message recMessage = null;
        boolean receivedResponse = false;

        // make message from params
        Message toSend;
        try {
            toSend = Message.decode(getMessage(args).getBytes(CHARENC));
        } catch (IllegalArgumentException e) {
            System.err.println("Bad parameters: " + e.getMessage());
            return;
        }


        do {



            //send packet
            try {
                DatagramPacket sendPacket = new DatagramPacket(toSend.encode(),
                        toSend.encode().length, serverAddress, servPort);
                socket.send(sendPacket);
            } catch (Exception e){
                System.err.println("Bad parameters: " + e.getMessage());
                return;
            }

            try {
                //receive packet
                socket.receive(receivePacket);
                byte[] rData = Arrays.copyOfRange(receivePacket.getData(), 0,
                        receivePacket.getLength());

                //make sure it is from correct source
                if (!receivePacket.getAddress().equals(serverAddress) ||
                        receivePacket.getPort() != servPort) {
                    System.err.println("Unexpected message source: " +
                            receivePacket.getAddress());
                } else {

                    try {
                        recMessage = Message.decode(rData);


                        // respond to specific message types
                        switch (recMessage.getOperation()) {
                            case OPERR -> {
                                String mes = ((Error) recMessage).getErrorMessage();
                                System.err.println(mes);
                                return;
                            }
                            case OPRES -> {
                                receivedResponse = handleResponse(toSend,
                                        (Response) recMessage);
                            }
                            case OPACK -> {
                                receivedResponse = handleAck(toSend,
                                        (ACK) recMessage);
                            }
                            default -> {
                                System.err.println("Unexpected message type");
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid message: " + e.getMessage());
                    }
                }
                //increase tries and retry
                tries++;

            } catch (SocketTimeoutException e) {
                tries++;
            } catch (Exception e) {
                System.err.println("Communication Problems: " + e.getMessage());
                return;
            }
        } while (!receivedResponse && tries < MAXTRIES);

        if (!receivedResponse){
            System.err.println("Three failed attempts - client terminated");
        }

        socket.close();
    }

    /**
     * returns message from parameters
     * @param args commandline params
     * @return string form of message
     */
    private static String getMessage(String[] args){
        StringBuilder message = new StringBuilder();
        if (args[2].equals(OPERR) || args[2].equals(OPRES)){
            for (int i = 2; i < args.length; i++){
                message.append(args[i]).append(" ");
            }
            return message.toString();
        } else {
             return args[2] + " " + args[3];
        }
    }

    /**
     * handle response type object
     * @param toSend the message sent from client
     * @param recMessage message returned to client
     * @return true if response is value
     */
    private static Boolean handleResponse(Message toSend, Response recMessage){
        if (toSend.getOperation().equals(OPQUERY)) {
            System.out.println(recMessage.toString());
            return true;
        } else {
            System.err.println("Unexpected Response");
            return false;
        }
    }

    /**
     * handle ack type object
     * @param toSend the message sent from client
     * @param recMessage message returned to client
     * @return true if ack is value
     */
    private static Boolean handleAck(Message toSend, ACK recMessage){
        if (toSend.getOperation().equals(OPNEW) &&
                recMessage.getPort() == ((New) toSend).getPort() &&
                recMessage.getHost().equals(((New) toSend).getHost())) {
            System.out.println(recMessage);
            return true;
        } else {
            System.err.println("Unexpected ACK");
            return false;
        }
    }


}
