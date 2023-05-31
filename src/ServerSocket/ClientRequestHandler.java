package ServerSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
            this.clientUsername = inputStream.readUTF();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat! ");
        } catch (IOException e) {
            // closeEverything(socket, inputStream, outputStream);
        }
    }

    private void broadcastMessage(String messageToSend){
        for (ClientRequestHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)) { //biar ga ngirim ke diri sendiri
                    clientHandler.outputStream.writeUTF(messageToSend);
                    clientHandler.outputStream.flush();
                }
            }
            catch(IOException e){
                closeEverything(socket, inputStream, outputStream);
                
            }
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        
        while (socket.isConnected()) {
            try {
                messageFromClient = inputStream.readUTF();
//              Sout the message to server (sebagai pembeda antara yang dienkripsi dan belum dienkripsi)
                System.out.println("CLEAR MESSAGE: " + messageFromClient);
                broadcastMessage(messageFromClient);
            }
            catch (IOException e) {
                closeEverything(socket, inputStream, outputStream);
                break;
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER:" +clientUsername +" has left the chat!");
        
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
