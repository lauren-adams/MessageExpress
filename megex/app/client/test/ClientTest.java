package megex.app.client.test;

import megex.app.client.Client;
import megex.serialization.*;

import java.io.ByteArrayOutputStream;



import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * implement methods for testing client
 */
public class ClientTest {

    /**
     * basic host used to test
     */
    private static final String LOCALHOST = "localhost";
    /**
     * basic port used to test
     */
    private static final int PORT = 8080;

    /**
     * used to make client test object
     */
    public ClientTest() {
    }

    /**
     * tests normal run of getting one file
     * @throws Exception if error in client
     */
    @Test
    public void testClientGood() throws Exception {
        // Set up test server
        TestServer testServer = new TestServer();
        testServer.setMode(1);
        testServer.start();

        // arguments
        String[] args = new String[]{LOCALHOST, Integer.toString(PORT), "/index.html"};
        Headers h = new Headers(1, false);
        h.addValue(":method", "GET");
        h.addValue(":path", "/index.html");
        h.addValue(":authority", LOCALHOST);
        h.addValue(":scheme", "https");
        h.addValue(":status", "200");

        String expectedOutput = "Received message: " + new Settings() + (char)0x0D + (char)0x0A +
                "Received message: " + h + (char)0x0D + (char)0x0A +
                "Received message: " + new Data(1, true, new byte[]{72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33}) + (char)0x0D + (char)0x0A;

        //test
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
        Client.main(args);
        assertEquals(expectedOutput, outputStream.toString());

        //Shut down
        testServer.stop();
    }

    /**
     * test if no correct settings passed
     * @throws Exception if error
     */
    @Test
    public void testNoSettings() throws Exception{
        // Set up test server
        TestServer testServer = new TestServer();
        testServer.setMode(2);
        testServer.start();

        // Set up test arguments
        String[] args = new String[]{LOCALHOST, Integer.toString(PORT), "/index.html"};

        //test
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        Client.main(args);
        assertEquals("Received message: Window_Update: StreamID=3 increment=7" + (char)0x0D + (char)0x0A, outputStream.toString());

        //Shut down
        testServer.stop();
    }


    /**
     * test online with duckduckgo
     * @throws Exception if fails in client
     */
    @Test
    public void testDDG() throws Exception{
        // Set up test server
        TestServer testServer = new TestServer();
        testServer.setMode(2);
        testServer.start();

        // Set up test arguments
        String[] args = new String[]{"duckduckgo.com", "443", "/tl5.js", "/ti5.js"};

        //test
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
        Client.main(args);
        String output = "Received message: Settings: StreamID=0"+ (char)0x0D + (char)0x0A +
                "Received message: Window_Update: StreamID=0 increment=2147418112" + (char)0x0D + (char)0x0A +
                "Received message: Headers: StreamID=1 isEnd=false ([:status = 200][server = nginx][date = Mon, 13 Mar 2023 23:21:44 GMT][content-type = application/x-javascript][content-length = 103][last-modified = Tue, 19 Oct 2021 20:21:18 GMT][vary = Accept-Encoding][etag = \"616f28be-67\"][strict-transport-security = max-age=31536000][permissions-policy = interest-cohort=()])" + (char)0x0D + (char)0x0A +
                "Received message: Data: StreamID=1 isEnd=true data=103" + (char)0x0D + (char)0x0A +
                "Received message: Headers: StreamID=3 isEnd=false ([:status = 200][server = nginx][date = Mon, 13 Mar 2023 23:21:44 GMT][content-type = application/x-javascript][content-length = 534][last-modified = Tue, 19 Oct 2021 20:21:18 GMT][vary = Accept-Encoding][etag = \"616f28be-216\"][strict-transport-security = max-age=31536000][permissions-policy = interest-cohort=()])" + (char)0x0D + (char)0x0A +
                "Received message: Data: StreamID=3 isEnd=true data=534" + (char)0x0D + (char)0x0A;
        String[] array1 = output.split("Received message:");
        String[] array2 = outputStream.toString().split("Received message:");
        assertEquals(array1[0], array2[0]);
        assertEquals(array1[1], array2[1]);
        assertEquals(array1[2], array2[2]);
        assertEquals(array1[4], array2[4]);



        //Shut down
        testServer.stop();
    }

    /**
     * tests proper error for invalid status
     * @throws Exception if error in client
     */
    @Test
    public void testBadStatus() throws Exception{
        // Set up test server
        TestServer testServer = new TestServer();
        testServer.setMode(3);
        testServer.start();

        // Set up test arguments
        String[] args = new String[]{LOCALHOST, Integer.toString(PORT), "/index.html"};
        Headers h = new Headers(1, false);
        h.addValue(":method", "GET");
        h.addValue(":path", "/index.html");
        h.addValue(":authority", LOCALHOST);
        h.addValue(":scheme", "https");
        h.addValue(":status", "200");

        //expected
        String expectedOutput = "Received message: Settings: StreamID=0" + (char)0x0D + (char)0x0A +
        "Received message: Headers: StreamID=1 isEnd=false ([:method = GET][:path = /index.html][:authority = localhost][:scheme = https][:status = 404 File not found])" + (char)0x0D + (char)0x0A;

        //test
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
        Client.main(args);
        assertEquals(expectedOutput, outputStream.toString());

        //Shut down
        testServer.stop();
    }

}




