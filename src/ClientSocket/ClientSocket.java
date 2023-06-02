package ClientSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
// import java.net.UnknownHostException;
import java.util.Scanner;
import org.zaza.Helper.HelperTools;
import org.zaza.Helper.TLVWrapper;

/**
 *
 * @author Zaza
 */
public class ClientSocket {
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private String username; 
    private final char DATA_IN_CLEARTEXT = 'S';
    private final char DATA_IN_BINARY = 'B';
    
    
    public ClientSocket(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.getMessage();
            closeEverything(socket, inputStream, outputStream);
        } 
        // catch (UnknownHostException u) {
        //     System.out.println(u);
        //     return;
        // }

    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        TLVWrapper tlv = HelperTools.readStream2TLV(inputStream);
                        System.out.println(tlv.getValueAsString());
                    } catch (IOException e) {
                        closeEverything(socket, inputStream, outputStream);
                    }
                }
            }
        }).start();
    }

    public void sendMessage() {
        try {
//          send username for the first time
            // System.out.println("To get PUBLIC KEY of other client: LIST_PUBLIC_KEY" );
            // System.out.println("To send message to specific user: ENCRYPT destination message" );
            byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
            System.out.println("username length in bytes: " + usernameInBytes.length);
            HelperTools.sendTLV2Stream(DATA_IN_CLEARTEXT, usernameInBytes.length, usernameInBytes, outputStream);
            
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();// di sini panggil fungsi encrypt 
                outputStream.writeUTF(username + ": " + messageToSend);
                // outputStream.writeUTF("Dalam kode biner");
                outputStream.write(messageToSend.getBytes());
                outputStream.flush();
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
        Socket socket = new Socket("localhost", 8000);
        ClientSocket client = new ClientSocket(socket, username); //generate keypair for client
        client.listenForMessage();
        client.sendMessage();
        scanner.close();
    }
}
