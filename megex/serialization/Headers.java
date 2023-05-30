/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 2
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import java.util.*;
import java.util.stream.Collectors;

import static megex.serialization.SerializationUtility.HEADERSTYPE;

/**
 * This class specifies the methods for the Data message object
 */
public class Headers extends Message {
    /**
     * true if Headers is at end
     */
    private boolean isEnd;

    /**
     * map to store key value pairs
     */
    private Map<String, String> map;

    /**
     * array of valid deliminating characters
     */
    private static final char[] delims =  {'(',')',',','/',';','<','=','>','?','@','[','\\',']','{','}'};

    /**
     * Creates Headers message from given values.
     *
     * @param streamID the stream ID
     * @param isEnd true if last header
     *
     * @throws BadAttributeException if attribute is invalid
     */
    public Headers(int streamID, boolean isEnd) throws BadAttributeException {
        super(streamID);
        setEnd(isEnd);
        map = new LinkedHashMap<>();
    }

    /**
     * Return the end value.
     *
     * @return end value
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Set end value.
     *
     * @param end end value
     */
    public void setEnd(boolean end) {
       isEnd = end;
    }

    /**
     * Returns a string of the form "Headers: StreamID=streamID isEnd=end ([name = value]...[name = value])".
     * The name/value pairs should be output in sorted (natural) order by name. For example:
     * Headers: StreamID=5 isEnd=false ([color=blue][method=GET])
     *
     * @return a string representation of this Headers message
     */
    @Override
    public String toString() {
        // implementation goes here
        return "Headers: StreamID=" + getStreamID() + " isEnd=" + isEnd + " (" + map.entrySet().stream()
                .map(entry -> "[" + entry.getKey() + "=" + entry.getValue() + "]")
                .collect(Collectors.joining("", "", "")) + ")";
    }

    /**
     * Get the Headers value associated with the given name.
     *
     * @param name the name for which to find the associated value
     *
     * @return the value associated with the name or null if the association cannot be found
     *         (e.g., no such name, invalid name, etc.)
     */
    public String getValue(String name) {
        return map.get(name);
    }

    /**
     * Get set of names in Headers
     *
     * @return a non-null set of names in sort order
     */
    public Set<String> getNames() {
        return map.keySet();
    }

    /**
     * Add name/value pair to header. If the name is already contained in the header, the corresponding
     * value is replaced by the new value.
     *
     * @param name  name to add
     * @param value value to add/replace
     *
     * @throws BadAttributeException if invalid name or value
     */
    public void addValue(String name, String value) throws BadAttributeException {
        if (name == null || value == null){
            throw new BadAttributeException("invalid value", "null");
        }
        if (name.isEmpty() || value.isEmpty()) {
            throw new BadAttributeException("invalid value", "");
        }
        if (nameCheck(name) && vcharCheck(value)) {
            map.remove(name);
            map.put(name, value);
        } else {
            throw new BadAttributeException("invalid value", value);
        }
    }

    /**
     * checks if value is a valid vischar
     * @param value string being checked
     * @return true if valid
     */
    private boolean vischarCheck(String value) {
        //between 21 and 7E
        return value.chars()
                .mapToObj(c -> (byte) c)
                .allMatch(c -> (c >= 0x21 && c <= 0x7E));
    }

    /**
     * checks if value is a valid delim
     * @param value string being checked
     * @return true if valid
     */
    private boolean delimCheck(String value) {
        return value.chars()
                .mapToObj(c -> (char) c)
                .allMatch(c -> new String(delims).indexOf(c) != -1);
    }

    /**
     * checks if value is a valid vChar
     * @param value string being checked
     * @return true if valid
     */
    private boolean vcharCheck(String value) {
        return value.chars().allMatch(c -> (c >= 0x21 && c <= 0x7E) || c == 0x20 || c == 0x9);
    }

    /**
     * checks if value is a valid name
     * @param value string being checked
     * @return true if valid
     */
    private boolean nameCheck(String value) {
        return ncharCheck(value) && value.toLowerCase().equals(value);
    }

    /**
     * checks if value is a valid nChar
     * @param value string being checked
     * @return true if valid
     */
    private boolean ncharCheck(String value) {
        return vischarCheck(value) && !(value.chars()
                .mapToObj(c -> (char) c)
                .anyMatch(c -> new String(delims).indexOf(c) != -1));
    }

    /**
     * creates a hashcode with elements of the message
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isEnd, map);
    }

    /**
     * set equality for a Data object
     * @param o object being compared
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Headers headers = (Headers) o;
        return isEnd == headers.isEnd && Objects.equals(map, headers.map);
    }


    /**
     * enforces that streamId cannot be 0
     * @param streamId new stream id value
     * @throws BadAttributeException if streamID is 0
     */
    @Override
    public int validateStreamID(int streamId) throws BadAttributeException {
        if (streamId < 1){
            throw new BadAttributeException("stream id cannot be 0", String.valueOf(streamId));
        }
        return streamId;
    }

    /**
     * Gets Headers code
     * @return code associated with Headers
     */
    @Override
    public byte getCode() {
        return HEADERSTYPE;
    }
}