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

import static jack.serialization.JackUtility.*;

/**
 * Represents a Query message
 */
public class Query extends Message {

    /**
     * String contained by Query
     */
    private String searchString;


    /**
     * Creates a Query message from given values
     * @param searchString search string for query
     * @throws IllegalArgumentException any validation problem
     **/

    public Query(String searchString) throws IllegalArgumentException {
        setSearchString(searchString);
    }

    /**
     * Get search
     * @return search string
     */
    public String getSearchString() {
        return this.searchString;
    }

    /**
     * Set the search
     * @param host string to search for
     * @return this object with new value
     * @throws IllegalArgumentException if search string fails validation
     */
    public final Query setSearchString(String host)
            throws IllegalArgumentException {

        if (host != null && ((host.chars().allMatch(c -> Character.isLetterOrDigit(c)
                || c == '.' || c == '-')) || host.equals("*")) && host.length() > 0) {
            this.searchString = host;
        } else {
            throw new IllegalArgumentException("Invalid char: " + host);
        }
        return this;
    }

    /**
     * Returns string of the form QUERY query
     * For example QUERY win
     * @return string representation of the Query message
     */
    @Override
    public String toString() {
        return "QUERY " + searchString;
    }

    /**
     * Gets operation
     * @return operation of class
     */
    @Override
    public String getOperation() {
        return String.valueOf(OPQUERY);
    }

    /**
     * gets ecoding for object
     * @return byte representation of object
     */
    @Override
    public byte[] encode(){
        String ret = "Q " + this.searchString;
        return ret.getBytes(CHARENC);
    }



    /**
     * compares if Query are equal
     *
     * @param o   the reference object with which to compare.
     * @return true if equivalent
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return Objects.equals(searchString, query.searchString);
    }

    /**
     * hashCode equivalent of code
     * @return int rep of code
     */
    @Override
    public int hashCode() {
        return Objects.hash(searchString);
    }
}
