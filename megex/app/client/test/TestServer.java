package megex.app.client.test;
import megex.serialization.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static tls.TLSFactory.*;

/**
 * fakes server functionality for testing purposes
 */
public class TestServer implements Runnable {

    /**
     * default port
     */
    private static final int PORT = 8080;

    /**
     * Serve socket for test server
     */
    private final ServerSocket serverSocket;
    /**
     * list of client sockets used for multiple connections
     */
    private List<Socket> clients;
    /**
     * thread used to implement test server
     */
    private Thread thread;
    /**
     * used to implment multiple server functionality tests on one server
     */
    int mode;

    /**
     * Sets the mode of the server.
     * @param mode The mode to set.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Creates a new instance of the TestServer class with the default port and mode.
     * @throws Exception If there is an error starting the server.
     */
    public TestServer() throws Exception {
        this(PORT, 0);
    }

    /**
     * Creates a new instance of the TestServer class with the specified mode and the default port.
     * @param mode The mode of the server.
     * @throws Exception If there is an error starting the server.
     */
    public TestServer(int mode) throws Exception {
        this(PORT, mode);
    }

    /**
     * Creates a new instance of the TestServer class with the specified port and mode.
     * @param port The port to listen on.
     * @param mode The mode of the server.
     * @throws Exception If there is an error starting the server.
     */
    public TestServer(int port, int mode) throws Exception {
        try {
            serverSocket = getServerListeningSocket(port, "src/main/keystore", "password");
            // ServerSocket(port);
            clients = new ArrayList<>();
            thread = new Thread(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start test server", e);
        }
    }

    /**
     * makes server start
     */
    public void start() {
        thread.start();
    }

    /**
     * turns off server
     */
    public void stop() {
        try {
            serverSocket.close();
            for (Socket client : clients) {
                client.close();
            }
            thread.join();
        } catch (Exception e) {
            System.err.println("Error: Server fails stop");
        }
    }

    /**
     * access port of server
     * @return server port
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * runs server
     */
    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket client = getServerConnectedSocket(serverSocket);
                ;
                clients.add(client);
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            System.err.println("Error: Server fails to run");
        }
    }

    /**
     * This class handles client threads
     */
    private class ClientHandler implements Runnable {
        /**
         * instance of socket for client
         */
        private Socket client;

        /**
         * sets client manually
         * @param client socket to be set for client
         */
        public ClientHandler(Socket client) {
            this.client = client;
        }

        /**
         * decides what server funcitonality to implment
         */
        @Override
        public void run() {
            if (mode == 1) {
                test1();
            } else if (mode == 2) {
                test2();
            } else if (mode == 3) {
                test3();
            }

        }

        /**
         * tests straightforwards run
         */
        public void test1() {
            try (InputStream in = client.getInputStream();
                 OutputStream out = client.getOutputStream()) {

                // Read and discard client request

                MessageFactory mf = new MessageFactory();
                Framer f = new Framer(out);
                Deframer d = new Deframer(in);

                // Send initial connection preface
                 f.putFrame(mf.encode(new Settings()));
                Headers h = new Headers(1, false);
                h.addValue(":method", "GET");
                h.addValue(":path", "/index.html");
                h.addValue(":authority", "localhost");
                h.addValue(":scheme", "https");
                h.addValue(":status", "200");

                f.putFrame(mf.encode(h));
                Data dat = new Data(1, true, new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33});
                f.putFrame(mf.encode(dat));
                out.flush();


                while (in.available() == 0) {
                    Thread.sleep(10);
                }
                while (in.available() > 0) {
                    in.read();
                }


            } catch (Exception e) {
                System.err.println("Error: Server failure");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Error: Server failure");
                }
                clients.remove(client);
            }
        }

        /**
         * tests if settings is not first should stop
         */
        public void test2() {
            try (InputStream in = client.getInputStream();
                 OutputStream out = client.getOutputStream()) {

                // Read and discard client request

                MessageFactory mf = new MessageFactory();
                Framer f = new Framer(out);
                Deframer d = new Deframer(in);

                f.putFrame(mf.encode(new Window_Update(3, 7)));


                while (in.available() == 0) {
                    Thread.sleep(10);
                }
                while (in.available() > 0) {
                    in.read();
                }


            } catch (Exception e) {
                System.err.println("Error: Server failure");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Error: Server failure");
                }
                clients.remove(client);
            }
        }

        /**
         * tests if status is invalid
         */
        public void test3() {
            try (InputStream in = client.getInputStream()) {
                OutputStream out = client.getOutputStream();

                // Read and discard client request

                MessageFactory mf = new MessageFactory();
                Framer f = new Framer(out);
                Deframer d = new Deframer(in);

                // Send initial connection preface
                 f.putFrame(mf.encode(new Settings()));
                Headers h = new Headers(1, false);
                h.addValue(":method", "GET");
                h.addValue(":path", "/index.html");
                h.addValue(":authority", "localhost");
                h.addValue(":scheme", "https");
                h.addValue(":status", "404 File not found");

                f.putFrame(mf.encode(h));
                Data dat = new Data(1, true, new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33});
                f.putFrame(mf.encode(dat));
                out.flush();


                while (in.available() == 0) {
                    Thread.sleep(10);
                }
                while (in.available() > 0) {
                    in.read();
                }


            } catch (Exception e) {
                System.err.println("Error: Server failure");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Error: Server failure");
                }
                clients.remove(client);
            }
        }
    }
}

