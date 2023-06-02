package org.zaza.Helper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Zaza
 */
public class HelperTools {
        
    public static KeyPair generateKeyPair(int size, String algorithm) throws NoSuchAlgorithmException {
        //Bikin keypair
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(size);
        KeyPair keypair = generator.generateKeyPair();
        return keypair;
    }
    
    public static byte[] convStr2Bin(String s) {
        return s.getBytes();
    }
    
    public static String convBin2Str(byte[] data) {
        return new String(data) ;
    }

    public static String convBin2Str(byte[] data, int length) {
        return new String(data, 0, length) ;
    }

    public static String convBin2Hex(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (byte b : data) {
            result.append(String.format("%02x ", b));
        }
        return result.toString();       
    }
    
    public static void sendTLV2Stream(char type, int length,  byte[] value, DataOutputStream os) throws IOException{
        os.writeChar(type);
        os.writeInt(length);
        os.write(value);
        os.flush();
        
    }
    
    public static TLVWrapper readStream2TLV(DataInputStream is) throws IOException {
        char dataType = is.readChar();      //Type
        int length = is.readInt();         //Length
        byte[] value = new byte[length];    
        int byteCount = is.read(value);    //Value
        TLVWrapper result = new TLVWrapper(dataType, length, value);
        return result;
    }

}
