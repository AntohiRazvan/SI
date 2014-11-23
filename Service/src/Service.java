import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;

public class Service {
    static ServerSocket server;
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;
    static byte[] Kst = "NjJ989KJkla0poU3".getBytes();

    static public void main(String[] args){
        initSocket();
        try {
            while(true) {
               waitForClient();
               serviceRequestResponse();
            }
        }
        catch(Exception e) {
            System.out.println("[Trusted]Error!\n");
        }
    }

    static void initSocket(){
        try {
            server = new ServerSocket(1113);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void waitForClient(){
        try {
            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void serviceRequestResponse(){
        try {
            while(!in.ready()){}
            String trustedMessage = in.readLine();
            while(!in.ready()){}
            String clientMessage = in.readLine();

            String[] data = AESdecryption(trustedMessage, Kst).split(",");
            String plainKey = new String(Base64.decodeBase64(data[0]));
            String usernameTrusted = data[1];
            double timeToLiveTrusted = Double.parseDouble(data[2]);
            data = DESdecrypt(clientMessage, plainKey).split(",");
            String usernameClient = data[0];
            double timestamp = Double.parseDouble(data[1]);
            double timeToLiveUser = Double.parseDouble(data[2]);
            if(!usernameTrusted.equals(usernameClient) && ! !(timestamp < timeToLiveTrusted + System.currentTimeMillis())){
                return;
            }
            StringBuilder response = new StringBuilder();
            response.append(timestamp + ",");
            response.append(timeToLiveUser - 1);
            out.println(DESencrypt(response.toString(), plainKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static String AESdecryption(String message, byte[] secretKey){
        String decryptedMessage = null;
        try{
            byte[] decodedMessage = Base64.decodeBase64(message);
            Key key = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(decodedMessage);
            decryptedMessage = new String(decryptedBytes);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return decryptedMessage;
    }

    static String DESencrypt(String message, String  plainKey){
        String encryptedMessage = null;
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            Key key = new SecretKeySpec(plainKey.getBytes(), "DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainTextBytes = message.getBytes();
            byte[] cipherText = cipher.doFinal(plainTextBytes);
            encryptedMessage = Base64.encodeBase64String(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedMessage;
    }

    static String DESdecrypt(String message, String plainKey){
        String decryptedMessage = null;
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            Key key = new SecretKeySpec(plainKey.getBytes(), "DESede");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(message);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedMessage = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedMessage;
    }
}
