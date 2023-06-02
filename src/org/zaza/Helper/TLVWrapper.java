package org.zaza.Helper;

import java.util.Arrays;

/**
 *
 * @author Zaza
 */
public class TLVWrapper {
    public final char DATA_IN_CLEARTEXT = 'S';
    public final char DATA_IN_BINARY = 'B';
    
    private char type;
    private int length;
    private byte[] value = null;
    
    public TLVWrapper(char type, int length, byte[] value) {
        this.type = type;
        this.length = length;
        this.value = Arrays.copyOf(value, length);
    }
    
    public char getType() {
        return this.type;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public byte[] getValueAsBinary() {
        return this.value;
    }
    
    public String getValueAsString() {
        if (type == DATA_IN_CLEARTEXT) {
            return HelperTools.convBin2Str(value);
        }
        else
            return null; //nanti untuk hasil dekrip
    }
    
}
