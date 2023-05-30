/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 4
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.serialization;
import java.util.*;

import static jack.serialization.JackUtility.*;

/**
 * Response message
 * The list of services from any method
 * must not have duplicates and be sorted by Java's default
 * order using host/port
 * as the primary/secondary sort key.
 * Note that Response deserialization will accept unordered
 * and/or duplicate host+port pairs
 */
public class Response extends Message {

    /**
     * Construct response with empty host:port list
     */
    private ArrayList<Service> services = new ArrayList<>();

    /**
     * generates default response object
     */
    public Response() {
    }

    /**
     * Add service to list The list of services sorted by Java's default
     * String order for the String representation of a service (name:port)
     * A duplicate host+port leaves the list unchanged
     *
     * @param host new service host
     * @param port new service port
     * @return this object with new value
     * @throws IllegalArgumentException if validation fails including null host
     */
    public final Response addService(String host, int port)
            throws IllegalArgumentException {
        Service s = new Service(host, port);
        if (!services.contains(s)) {
            services.add(s);
        }
        return this;
    }

    /**
     * Get the service (string representation) list where each service is
     * represented as name:port (e.g., google:8000)
     *
     * @return service list
     */
    public List<String> getServiceList() {
        ArrayList<String> list = new ArrayList<>();
        for (Service s : services) {
            list.add(s.toString());
        }
        Collections.sort(list, customComparator);
        return list;
    }

    /**
     * Returns string of the form RESPONSE [name:port]*
     *
     * @return response string
     */
    public String toString() {
        String res = "RESPONSE ";
        for (String s : this.getServiceList()) {
            res += "[" + s + "]";
        }
        return res;
    }

    /**
     * Gets operation
     *
     * @return operation of class
     */
    @Override
    public String getOperation() {
        return String.valueOf(OPRES);
    }

    /**
     * gets ecoding for object
     *
     * @return byte representation of object
     */
    @Override
    public byte[] encode() {
        String ret = "R ";
        for (String s : this.getServiceList()) {
            ret += s + " ";
        }
        return ret.getBytes(CHARENC);
    }

    /**
     * compares if two Responses are equal
     *
     * @param o the reference object with which to compare.
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return Objects.equals(services, response.services);
    }

    /**
     * hash representation of object
     *
     * @return int representation of hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(services);
    }

    /**
     * custom comparator used to properly sort the list elements
     */
    Comparator<String> customComparator = (s1, s2) -> {
        String[] s1Parts = s1.split(SPLITER);
        String[] s2Parts = s2.split(SPLITER);
        String s1Text = s1Parts[0];
        String s2Text = s2Parts[0];
        int s1Num = Integer.parseInt(s1Parts[1]);
        int s2Num = Integer.parseInt(s2Parts[1]);
        return s1Text.compareTo(s2Text) != 0 ? s1Text.compareTo(s2Text) : Integer.compare(s1Num, s2Num);
    };
}
