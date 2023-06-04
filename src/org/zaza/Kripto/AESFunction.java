package org.zaza.Kripto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 *
 * @author Zaza
 */
public class AESFunction {
        
    //encrypt with GCM, iv must be 12 bytes
    public static byte[] encryptAESGCM(byte[] additionalData, byte[] plainText, SecretKey key, byte[] iv)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
    BadPaddingException, IllegalBlockSizeException {
        final int tagLength = 128; //dalam bit, artinya 16 byte
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); //factory method (new ada di dalam getInstance)
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(tagLength, iv)); //inisialisasi
        cipher.updateAAD(additionalData); //nambahin aad
        byte[] cipherText = cipher.doFinal(plainText); //finishing encryption process
        return cipherText;
    }
    
        
    public static byte[] decryptAESGCM(byte[] additionalData, byte[] cipherText, SecretKey key, byte[] iv) 
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
    BadPaddingException, IllegalBlockSizeException {
        final int tagLength = 128; //dalam bit, artinya 16 byte
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(tagLength, iv));
        cipher.updateAAD(additionalData);
        byte[] plainText = cipher.doFinal(cipherText);
        return plainText;
    }
}
