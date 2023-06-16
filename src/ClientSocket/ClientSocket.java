package ClientSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
// import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.zaza.Helper.CipherSendToWrapper;
import org.zaza.Helper.HelperTools;
import org.zaza.Helper.TLVWrapper;
import org.zaza.Kripto.AESFunction;
import org.zaza.Kripto.RSAFunction;

/**
 *
 * @author Zaza
 */
public class ClientSocket {
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private String username; 
    private KeyPair keypair;
    private SecretKey AESkey;
    private Map<String, PublicKey> userPKMap = new HashMap<>();
    
    
    public ClientSocket(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.keypair = HelperTools.generateKeyPair(2048, "RSA");
            this.AESkey = HelperTools.generateAESKey(256);
        } catch (IOException e) {
            e.getMessage();
            closeEverything(socket, inputStream, outputStream);
        } 
         catch (NoSuchAlgorithmException u) {
         }

    }

    public void listenForMessage() throws NoSuchAlgorithmException, NoSuchPaddingException, 
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        TLVWrapper tlv = HelperTools.readStream2TLV(inputStream);
                        if (tlv.getType() == TLVWrapper.TYPE_PUBLIC_KEY_LIST) {
                            userPKMap.clear();
                            userPKMap = HelperTools.deserializeMap(tlv.getValueAsBinary());
                            System.out.println("LIST OF PUBLIC KEY");
                            System.out.println(userPKMap);
                        }
                        else if (tlv.getType() == TLVWrapper.TYPE_RSA_ENCRYPTED_DATA) {
                            CipherSendToWrapper data = (CipherSendToWrapper)HelperTools.deserializeFromArray(tlv.getValueAsBinary());
                            byte[] decryptedAESKey = RSAFunction.decryptRSA(data.getEncryptedAESKey(), keypair.getPrivate());
                            SecretKey originalAESKey = new SecretKeySpec(decryptedAESKey, 0, decryptedAESKey.length, "AES"); 
                            byte[] decryptedMsg = AESFunction.decryptAESGCM(data.getDestinationUsername().getBytes(), data.getCipherText(), originalAESKey, data.getIv());
                            System.out.println("Encrypted message from " + data.getSenderUsername()+ ": " + HelperTools.convBin2Str(decryptedMsg));
                        }
                        else {
                            System.out.println(tlv.getValueAsString());
                        }
                    } catch (IOException e) {
                        closeEverything(socket, inputStream, outputStream);
                    }
                    catch (Exception n) {}
                }
            }
        }).start();
    }

    public void sendMessage() throws NoSuchAlgorithmException, NoSuchPaddingException, 
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        try {
            System.out.println("To get PUBLIC KEY of other users: LIST_PK" );
            System.out.println("To send message to specific user: ENCRYPT destination message" );
             
            byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
            HelperTools.sendTLV2Stream(TLVWrapper.TYPE_CLEAR_DATA, usernameInBytes.length, usernameInBytes, outputStream);

            byte[] publicKeyInBytes = HelperTools.serializeToArray(keypair.getPublic());
            HelperTools.sendTLV2Stream(TLVWrapper.TYPE_PUBLIC_KEY, publicKeyInBytes.length, publicKeyInBytes, outputStream);
            
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                StringTokenizer st = new StringTokenizer(messageToSend, " ");
                String command = "";
                if (st.hasMoreElements()) {
                    command = st.nextToken();
                }

                if (HelperTools.COMMAND_LIST_PUBLIC_KEY.equals(messageToSend)) {
                    byte[] commandInBytes = HelperTools.COMMAND_LIST_PUBLIC_KEY.getBytes(StandardCharsets.UTF_8);
                    HelperTools.sendTLV2Stream(TLVWrapper.TYPE_GET_ALL_PUBLIC_KEYS, commandInBytes.length, commandInBytes, outputStream);
                }
                else if (HelperTools.COMMAND_ENCRYPT.equals(command)){
                    String destinationUsername = "";
                    String plainText = "";
                    
                    byte[] iv = HelperTools.generateRandom(12);
                    byte[] encodedAESKey = AESkey.getEncoded();
                    byte[] encryptedAESKey;
                    byte[] cipherText;
                    
                    if (st.hasMoreElements()) 
                        destinationUsername = st.nextToken();
                    StringBuilder sb = new StringBuilder();
                    while (st.hasMoreElements()) {
                        sb.append(st.nextToken());
                        sb.append(" ");
                    }
                    plainText = sb.toString();
                    
                    if (userPKMap.containsKey(destinationUsername)) {// TODO: check if message is empty 
                        encryptedAESKey = RSAFunction.encryptRSA(encodedAESKey, userPKMap.get(destinationUsername));
                        cipherText = AESFunction.encryptAESGCM(destinationUsername.getBytes(), plainText.getBytes(), AESkey, iv);
//                        1. pack username and ciphertext into CipherSendToWrapper object (immutable class): 
                        CipherSendToWrapper data = new CipherSendToWrapper(username,destinationUsername, encryptedAESKey, cipherText, iv);
//                        2. serialize CipherSendToWrapper in bytes.
                        byte[] serializedData = HelperTools.serializeToArray(data);
//                        3. sendTLV2Stream
                        HelperTools.sendTLV2Stream(TLVWrapper.TYPE_RSA_ENCRYPTED_DATA, serializedData.length, serializedData, outputStream);
                    }
                    else {
                        System.out.println(destinationUsername + " is not listed. Run LIST_PK to check.");
                    }
                }
                else{
                    byte[] messageToSendInBytes = messageToSend.getBytes(StandardCharsets.UTF_8);
                    HelperTools.sendTLV2Stream(TLVWrapper.TYPE_CLEAR_DATA, messageToSendInBytes.length, messageToSendInBytes, outputStream);                
                }
            }
            scanner.close();

        } catch (IOException e) {
            closeEverything(socket, inputStream, outputStream);
        }

    }    

    public void closeEverything(Socket socket, DataInputStream input, DataOutputStream output){
        try{
            if(input != null){
                input.close();
            }
            if(output != null){
                output.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        System.out.println("Enter server IP address (press enter for localhost): ");
        String serverIP = scanner.nextLine();
        if (serverIP.isEmpty()) {
            serverIP = "localhost";
        }
        Socket socket = new Socket(serverIP, 6666);
        ClientSocket client = new ClientSocket(socket, username); //generate keypair for client
        client.listenForMessage();
        client.sendMessage();
        scanner.close();
    }
}
