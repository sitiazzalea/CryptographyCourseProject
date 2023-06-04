package org.zaza.Helper;

import java.security.PublicKey;
import java.util.Arrays;

/**
 *
 * @author Zaza
 */
public class TLVWrapper {
    public static final char TYPE_CLEAR_DATA = 'S';
    public static final char TYPE_BINARY_DATA = 'B';
    public static final char TYPE_PUBLIC_KEY = 'P';
    public static final char TYPE_GET_ALL_PUBLIC_KEYS = 'G';
    public static final char TYPE_PUBLIC_KEY_LIST = 'L';
    public static final char TYPE_RSA_ENCRYPTED_DATA = 'R';
    
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
        if (type == TYPE_CLEAR_DATA) {
            return HelperTools.convBin2Str(value);
        }
        else
            return null; //nanti untuk hasil dekrip
    }
    
    public PublicKey getValueAsPublicKey() {
        if (type == TYPE_PUBLIC_KEY) {
            return (PublicKey)HelperTools.deserializeFromArray(value);
        }
        else
            return null; //nanti untuk hasil dekrip
    }
}
