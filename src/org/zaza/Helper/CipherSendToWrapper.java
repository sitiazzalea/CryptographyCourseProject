package org.zaza.Helper;

import java.io.Serializable;

/**
 *
 * @author Zaza
 */
public class CipherSendToWrapper implements Serializable{
    
//  bikin EncryptedAESKey dalam byte[]

    private static final long serialVersionUID = 1L;
    private String senderUser;
    private String destinationUser;    
    private byte[] cipherText;
    private byte[] encryptedAESKey;
    private byte[] iv;
    
    public CipherSendToWrapper(String sender, String destUser,byte[] encryptedAESKey, byte[] cipherText, byte[] iv) {
        this.senderUser = sender;
        this.destinationUser = destUser;
        this.encryptedAESKey = encryptedAESKey;
        this.cipherText = cipherText;
        this.iv = iv;
    }
    
    public String getSenderUsername() {
        return this.senderUser;
    }
    
    public String getDestinationUsername() {
        return this.destinationUser;
    }
    
    public byte[] getEncryptedAESKey() {
        return encryptedAESKey;
    }
    
    public byte[] getCipherText() {
        return this.cipherText;
    }
    
    public byte[] getIv() {
        return this.iv;
    }
    
}
