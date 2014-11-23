import io.netty.handler.codec.base64.Base64Encoder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;

public class Client {
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;
    static Random rand = new Random();
    static byte[] Kut = "Lk89njhH89aIr7uc".getBytes();
    static String DESkey;
    static User user;
    static String nonce;
    static String requestedService;

    public static void main(String args[]) {
        try {
            connectToTrusted();
            user = new User("razvan", "abcd");
            requestService(user, "READ file1.txt");
            recieveServiceRequestResponse();
            recieveServiceResponse();
            closeConnection();
        }
        catch(Exception e) {
            System.out.println("[Client]Error!\n");
        }
    }

    static void connectToTrusted(){
        try {
            socket = new Socket("localhost", 1112);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void connectToService(){
        try {
            socket = new Socket("localhost", 1113);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void closeConnection(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    static void requestService(User user, String service){
        requestedService = service;
        nonce = "" + rand.nextInt((999999 - 100000) + 1) + 100000;
        out.println(user.getUsername() + "," + service + "," + nonce);
    }

    static void recieveServiceRequestResponse(){
        try {
            while(!in.ready()){}
            String userMessage = in.readLine();
            while(!in.ready()){}
            String serviceMessage = in.readLine();
            connectToService();
            userMessage = AESdecryption(userMessage, Kut);
            String[] data = userMessage.split(",");
            DESkey = new String(Base64.decodeBase64(data[0]));
            String receivedNonce = data[1];
            String expirationTime = data[2];
            String service = data[3];
            if(!service.equals(requestedService) && nonce.equals(receivedNonce)){
                System.out.println("Authenthication error.");
                return;
            }
            StringBuilder message = new StringBuilder();
            message.append(user.getUsername() + ",");
            message.append(System.currentTimeMillis() + ",");
            message.append(expirationTime);
            String encryptedMessage = DESencrypt(message.toString(), DESkey);
            out.println(serviceMessage);
            out.println(encryptedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void recieveServiceResponse(){
        try {
            while(!in.ready()){}
            String message = in.readLine();
            message = DESdecrypt(message, DESkey);
            String[] data = message.split(",");
            System.out.println(data[0] + "  " + data[1]);

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
