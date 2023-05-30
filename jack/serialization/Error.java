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
 * Error message
 */
public class Error extends Message {

    /**
     * Message contained by error
     */
    private String errorMessage;

    /**
     * Create an Error message from values
     * @param errorMessage error message
     * @throws IllegalArgumentException if validation problem with errorMessage
     */
    public Error(String errorMessage) throws IllegalArgumentException {
        setErrorMessage(errorMessage);
    }

    /**
     * Get error message
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message
     * @param errorMessage error message
     * @return this object with new val
     * @throws IllegalArgumentException if validation fails
     */
    public final Error setErrorMessage(String errorMessage)
            throws IllegalArgumentException {
        if (errorMessage == null || errorMessage.length() < 1 ||
                errorMessage.length() > UDPMAX ||
                !errorMessage.chars().allMatch(c -> c >= 32 && c <= 127)){
            throw new IllegalArgumentException("Invalid error Message");
        } else {
            this.errorMessage = errorMessage;
        }
        return this;
    }

    /**
     * Returns string of the form ERROR message
     * For example ERROR Bad stuff
     * @return string representation of the error message
     */
    public String toString() {
        return "ERROR " + errorMessage;
    }


    /**
     * Gets operation
     * @return operation of class
     */
    @Override
    public String getOperation() {
        return String.valueOf(OPERR);
    }

    /**
     * gets ecoding for object
     * @return byte representation of object
     */
    @Override
    public byte[] encode(){
        String ret = "E " + this.errorMessage;
        return ret.getBytes(CHARENC);
    }

    /**
     * compare if two Errors are equal
     * @param o   the reference object with which to compare.
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return Objects.equals(errorMessage, error.errorMessage);
    }

    /**
     * hashCode equivalent of code
     * @return int rep of code
     */
    @Override
    public int hashCode() {
        return Objects.hash(errorMessage);
    }
}

