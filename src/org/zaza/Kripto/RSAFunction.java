package org.zaza.Kripto;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.interfaces.RSAPublicKey;

public class RSAFunction {
    
    public static SecretKey generateKey(int size, String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(size);
        SecretKey key = keyGenerator.generateKey();
        return key;    
    }
    
    public static byte[] encryptRSA(byte[] plaintextInBytes, PublicKey pubkey) 
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, pubkey);
        byte[] encryptedText = encryptCipher.doFinal(plaintextInBytes);
        return encryptedText;
    }

    public static byte[] decryptRSA(byte[] cipherText, PrivateKey privkey)
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, privkey);
        byte[] decryptedMessage = decryptCipher.doFinal(cipherText);
        return decryptedMessage;
    }

     
}
