/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 4
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.serialization;

import java.util.Objects;

import static jack.serialization.JackUtility.PORTMAX;

/**
 * Identifies a service by host and port
 */
public class Service {

    /** The service host */
    private String host;

    /** The service port */
    private int port;

    /**
     * Create a service ID
     * @param host service host
     * @param port service port
     * @throws IllegalArgumentException if invalid host or port
     */
    public Service(String host, int port) throws IllegalArgumentException {
        setHost(host);
        setPort(port);
    }

    /**
     * Get service host
     * @return service host
     */
    public String getHost() {
        return host;
    }

    /**
     * Get service port
     * @return service port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set service host
     * @param host service host
     * @return this object with new value
     * @throws IllegalArgumentException if invalid host
     */
    public Service setHost(String host) throws IllegalArgumentException {
        if (host != null && host.chars().allMatch(c -> Character.isLetterOrDigit(c)
                || c == '.' || c == '-' ) && host.length() > 0) {
            this.host = host;
        } else {
            throw new IllegalArgumentException("Invalid char: " + host);
        }
        return this;
    }

    /**
     * Set service port
     * @param port given service port
     * @throws IllegalArgumentException if invalid port
     */
    public void setPort(int port) throws IllegalArgumentException {
        if ((port < 1) || (port > PORTMAX)){
            throw new IllegalArgumentException("Invalid port: " + port);
        } else {
            this.port = port;
        }
    }

    /**
     * Returns string of the form name:port
     * @return string of the form name:port
     * For example, google.com:8080
     */
    public String toString() {
        return getHost() + ":" + getPort();
    }


    /**
     * compares if services are equal
     * @param o service to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return port == service.port && Objects.equals(host, service.host);
    }



    /**
     * hash representation of object
     * @return int representation of object
     */
    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
