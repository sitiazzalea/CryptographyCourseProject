package ServerSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.zaza.Helper.HelperTools;
import org.zaza.Helper.TLVWrapper;

/**
 *
 * @author Zaza
 */
public class ClientRequestHandler implements Runnable {
    
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private String clientUsername; 
    public static ArrayList<ClientRequestHandler> clientHandlers = new ArrayList<>();


    public ClientRequestHandler(Socket socket) {
        try {
            this.socket = socket;
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.inputStream = new  DataInputStream(socket.getInputStream());

            TLVWrapper tlv = HelperTools.readStream2TLV(inputStream);
            this.clientUsername = tlv.getValueAsString();
            clientHandlers.add(this);
            String message = "SERVER: " + this.clientUsername + " has entered the chat!";
            broadcastTLVMessage(new TLVWrapper(tlv.getType(), message.length(), message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
             closeEverything(socket, inputStream, outputStream);
        }
    }

    private void broadcastTLVMessage(TLVWrapper tlv){
        for (ClientRequestHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)) { //biar ga ngirim ke diri sendiri
                    HelperTools.sendTLV2Stream(tlv.getType(), tlv.getType(), tlv.getValueAsBinary(), clientHandler.outputStream);
                }
            }
            catch(IOException e){
                closeEverything(socket, inputStream, outputStream);
                
            }
        }
    }
    
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                TLVWrapper tlv = HelperTools.readStream2TLV(inputStream);
                String message = this.clientUsername + ": " + tlv.getValueAsString();
                broadcastTLVMessage(new TLVWrapper(tlv.getType(), message.length(), message.getBytes(StandardCharsets.UTF_8)));
                System.out.println("CLEAR MESSAGE: " + tlv.getValueAsString());
            }
            catch (IOException e) {
                closeEverything(socket, inputStream, outputStream);
                break;
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        String message = "SERVER:" + clientUsername +" has left the chat!";
        broadcastTLVMessage(new TLVWrapper(TLVWrapper.DATA_IN_CLEARTEXT, message.length(), message.getBytes(StandardCharsets.UTF_8)));
    }

    public void closeEverything(Socket socket, DataInputStream inputStream, DataOutputStream outputStream){
        removeClientHandler();
        try{
            if (inputStream != null){
                inputStream.close();
            }
            if (outputStream != null){
                outputStream.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}
