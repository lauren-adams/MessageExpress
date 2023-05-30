/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 4
 * Class: Data Communications 4321
 *
 ************************************************/
package jack.serialization;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static jack.serialization.JackUtility.CHARENC;
import static jack.serialization.JackUtility.OPACK;

/**
 * ACK message.
 */
public class ACK extends Message {

    private Service s;

    /**
     * Create an ACK message from given values
     *
     * @param host the host ID
     * @param port the port
     * @throws IllegalArgumentException any validation problem
     */
    public ACK(String host, int port) throws IllegalArgumentException {
        s = new Service(host, port);
    }

    /**
     * Get host
     *
     * @return host
     */
    public String getHost() {
        return s.getHost();
    }

    /**
     * Set host
     *
     * @param host the new host
     * @return this object with new val
     */
    public ACK setHost(String host) {
        s.setHost(host);
        return this;
    }

    /**
     * Get port
     *
     * @return port
     */
    public int getPort() {
        return s.getPort();
    }

    /**
     * Set port
     *
     * @param port new port val
     * @return this object with new val
     */
    public ACK setPort(int port) {
        s.setPort(port);
        return this;
    }

    /**
     * Returns string of the form
     * ACK [name:port]
     * For example: ACK [google.com:8080]
     *
     * @return the string rep of this ACK
     */
    @Override
    public String toString() {
        return "ACK [" + s.toString() + "]";
    }

    /**
     * Gets operation
     * @return operation of class
     */
    @Override
    public String getOperation() {
        return String.valueOf(OPACK);
    }

    /**
     * gets ecoding for object
     * @return byte representation of object
     */
    @Override
    public byte[] encode(){
        String ret = "A " + s.toString();
        return ret.getBytes(CHARENC);

    }

    /**
     * compare if two ACKS are equal
     * @param o   the reference object with which to compare.
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ACK ack = (ACK) o;
        return Objects.equals(s, ack.s);
    }

    /**
     * hashCode equivalent of code
     * @return int rep of code
     */
    @Override
    public int hashCode() {
        return Objects.hash(s);
    }
}
