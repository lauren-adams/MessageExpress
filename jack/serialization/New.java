/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 4
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.serialization;
import java.util.Objects;

import static jack.serialization.JackUtility.CHARENC;
import static jack.serialization.JackUtility.OPNEW;

/**
 * Represents a New message
 */
public class New extends Message {

    /**
     * service contained by New
     */
    private Service s;

    /**
     * Creates a new instance of the New
     *
     * @param host host ID
     * @param port port num
     * @throws IllegalArgumentException any validation problem
     */
    public New(String host, int port) throws IllegalArgumentException {
        s = new Service(host, port);
    }

    /**
     * Get the host ID of this New
     *
     * @return the host ID
     */
    public String getHost() {
        return s.getHost();
    }

    /**
     * Set the host ID
     *
     * @param host the new host ID
     * @return this object with new val
     */
    public New setHost(String host) {
        s.setHost(host);
        return this;
    }

    /**
     * Get the port number
     *
     * @return the port number
     */
    public int getPort() {
        return s.getPort();
    }

    /**
     * Set the port number
     *
     * @param port the new port num
     * @return this object with new val
     */
    public New setPort(int port) {
        s.setPort(port);
        return this;
    }

    /**
     * Returns a string representation
     *
     * @return a string of the form NEW [name:port], for example:
     * "NEW [google.com:8080]"
     */
    @Override
    public String toString() {
        return "NEW [" + s.toString() + "]";
    }

    /**
     * Gets operation
     * @return operation of class
     */
    @Override
    public String getOperation() {
        return String.valueOf(OPNEW);
    }

    /**
     * gets ecoding for object
     * @return byte representation of object
     */
    @Override
    public byte[] encode(){
        String ret = "N " + s.toString();
        return ret.getBytes(CHARENC);
    }

    /**
     * compares if News are equal
     *
     * @param o   the reference object with which to compare.
     * @return true if equivalent
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        New aNew = (New) o;
        return Objects.equals(s, aNew.s);
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
