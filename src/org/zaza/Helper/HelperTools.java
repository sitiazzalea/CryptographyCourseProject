package org.zaza.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Zaza
 */
public class HelperTools {
    
    public static final String COMMAND_LIST_PUBLIC_KEY = "LIST_PK"; //buat nge-list public key semua user 
    public static final String COMMAND_ENCRYPT = "ENCRYPT";  
        
    public static KeyPair generateKeyPair(int size, String algorithm) throws NoSuchAlgorithmException {
        //Bikin keypair
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(size);
        KeyPair keypair = generator.generateKeyPair();
        return keypair;
    }

    public static SecretKey generateAESKey(int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(size);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }    
 
    public static byte[] generateRandom(int size) {
        byte[] rand = new byte[size];
        new SecureRandom().nextBytes(rand);
        return rand;
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

    public static byte[] serializeToArray(Serializable objToSerialize) {
        byte[] serializedObject = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            // Write the object to the output stream
            objectOutputStream.writeObject(objToSerialize);
            objectOutputStream.flush();

            // Get the serialized object as a byte array
            serializedObject = byteArrayOutputStream.toByteArray();

            // Close the streams
            objectOutputStream.close();
            byteArrayOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }

    public static Serializable deserializeFromArray(byte[] arr) {
        Serializable result = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arr);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            // Read the object from the input stream
            result = (Serializable) objectInputStream.readObject();

            // Close the streams
            objectInputStream.close();
            byteArrayInputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static byte[] serializeMap(Map<String, PublicKey> map) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(map);
            objectOutputStream.flush();

            byte[] serializedMap = byteArrayOutputStream.toByteArray();

            objectOutputStream.close();
            byteArrayOutputStream.close();

            return serializedMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, PublicKey> deserializeMap(byte[] serializedMap) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedMap);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            Map<String, PublicKey> map = (Map<String, PublicKey>) objectInputStream.readObject();

            objectInputStream.close();
            byteArrayInputStream.close();

            return map;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
