package org.zaza.Helper;

import java.io.Serializable;

/**
 *
 * @author Zaza
 */
public class CipherSendToWrapper implements Serializable{

    private static final long serialVersionUID = 1L;
    private String senderUser;
    private String destinationUser;    
    private byte[] cipherText;
    
    public CipherSendToWrapper(String sender, String destUser, byte[] cipherText) {
        this.senderUser = sender;
        this.destinationUser = destUser;
        this.cipherText = cipherText;
    }
    
    public String getSenderUsername() {
        return this.senderUser;
    }
    
    public String getDestinationUsername() {
        return this.destinationUser;
    }
    
    public byte[] getCipherText() {
        return this.cipherText;
    }
    
}
