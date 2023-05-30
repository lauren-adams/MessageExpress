/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import java.util.Objects;

/**
 * describes methods of BadAttributeException class used to throw errors
 * for framer objects
 */
public class BadAttributeException extends Exception{


    /**
     * UID is defined here
     * */
    private static final long serialVersionUID = 127L;

    /**
     * describes attribute associated with the error
     */
    private final String attribute;

    /**
     * Constructs a BadAttributeException with given message, attribute,
     * and cause
     *
     * @param message detail message
     * @param attribute attribute related to problem
     * @param cause underlying cause (null is permitted and indicates
     *              no or unknown cause)
     * @throws NullPointerException if message or attribute is null
     */
    public BadAttributeException(String message,
                                 String attribute,
                                 Throwable cause){
        super(message, cause);
        Objects.requireNonNull(message);
        Objects.requireNonNull(attribute);
        this.attribute = attribute;
    }

    /**
     * Takes message and attribute and throws exception
     *
     * @param message detail message
     * @param attribute attribute related to problem
     * @throws NullPointerException if message or attribute is null
     */
    public BadAttributeException(String message,
                                 String attribute){
        super(message);
        Objects.requireNonNull(message);
        Objects.requireNonNull(attribute);
        this.attribute = attribute;
    }

    /**
     * Return attribute related to problem
     *
     * @return String
     */
    public String getAttribute(){
        return this.attribute;
    }

}
