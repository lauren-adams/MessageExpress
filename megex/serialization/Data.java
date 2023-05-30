/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import java.util.Arrays;
import java.util.Objects;

import static megex.serialization.SerializationUtility.*;

/**
 * This class specifies the methods for the Data message object
 */
public class Data extends Message{

    /**
     * Private method attributes of Data object
     * represents whether there is end of data
     */
    private boolean isEnd;
    /**
     * records data stored in Data object
     */
    private byte[] data;

    /**
     * Creates Data message from given values
     * @param streamID stream ID
     * @param isEnd true if last data message
     * @param data bytes of application data
     * @throws BadAttributeException if attribute invalid (see protocol spec)
     */
    public Data(int streamID,
                boolean isEnd,
                byte[] data)
            throws BadAttributeException {
        super(streamID);
        setEnd(isEnd);
        setData(data);
    }


    /**
     * return end value
     *
     * @return end value
     */
    public boolean isEnd(){
        return  isEnd;
    }

    /**
     * set end value
     * @param end end value
     */
    public void setEnd(boolean end){
        this.isEnd = end;
    }

    /**
     * set data
     *
     * @param data data to set
     * @throws BadAttributeException if invalid
     */
    public void setData(byte[] data)
            throws BadAttributeException{
        if (data == null){
            throw new BadAttributeException("invalid data", "null");
        }
        if (data.length > DATAMAXLEN){
            throw new BadAttributeException("invalid data to big", String.valueOf(data.length));
        }
        this.data = data;
    }

    /**
     *Return Data's data
     *
     * @return data
     */
    public byte[] getData(){
        return  data;
    }


    /**
     * Returns string of the form
     * Data: StreamID=streamId isEnd=end data=length
     *
     * For example
     *  Data: StreamID=5 isEnd=true data=5
     * @return string
     */
    public String toString(){
        return "Data: StreamID=" + getStreamID() + " isEnd=" + isEnd + " data=" + getData().length;
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
        Data data1 = (Data) o;
        return isEnd == data1.isEnd && Arrays.equals(data, data1.data);
    }


    /**
     * creates a hashcode with elements of the message
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), isEnd);
        result = 31 * result + Arrays.hashCode(data);
        return result;
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
     * Gets Data code
     * @return code associated with Data
     */
    @Override
    public byte getCode() {
        return DATATYPE;
    }
}
