import io.netty.handler.codec.base64.Base64Encoder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;

public class Trusted {
    static ServerSocket server;
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;

    static final double timeToLive = 7200000; //2 hours

    static byte[] Kut = "Lk89njhH89aIr7uc".getBytes();
    static byte[] Kst = "NjJ989KJkla0poU3".getBytes();
    static List<User> users = new ArrayList<User>();
    static HashMap<String, Classification> resources = new HashMap<String, Classification>();

    static List<List<List<Permission>>> hasPermission = Arrays.asList(
                                                            Arrays.asList(
                                                                Arrays.asList(Permission.READ, Permission.WRITE),
                                                                Arrays.asList(Permission.NONE),
                                                                Arrays.asList(Permission.NONE)
                                                            ),
                                                            Arrays.asList(
                                                                    Arrays.asList(Permission.READ),
                                                                    Arrays.asList(Permission.READ, Permission.WRITE),
                                                                    Arrays.asList(Permission.NONE)
                                                            ),
                                                            Arrays.asList(
                                                                    Arrays.asList(Permission.READ),
                                                                    Arrays.asList(Permission.READ),
                                                                    Arrays.asList(Permission.READ, Permission.WRITE)
                                                            )
                                                        );

    public static void main(String args[]) {
        initSocket();
        loadUsers();
        loadResources();
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
            server = new ServerSocket(1112);
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

    static void closeConnection(){
        try {
            out.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void serviceRequestResponse(){
        try {
            while(!in.ready()){}
            String data[] =  in.readLine().split(",");
            String username = data[0];
            String service = data[1];
            String nonce = data[2];
            data = service.split("\\s");
            Permission permission = Permission.getPermission(data[0]);
            String resource = data[1];
            User user = findUser(username);
        //    List<List<Permission>> l1 = hasPermission.get(user.getType().ordinal());
       //     Classification c = resources.get(resource);
         //   List<Permission> l2 = l1.get(c.ordinal());

            if( (user != null) && ((hasPermission.get(user.getType().ordinal())).get((resources.get(resource)).ordinal()).contains(permission)) ){
                StringBuilder response = new StringBuilder();
                KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
                keyGen.init(112);
                SecretKey secretKey = keyGen.generateKey();
                response.append(Base64.encodeBase64String(secretKey.getEncoded()) + ",");
                response.append(nonce + ",");
                response.append(timeToLive + ",");
                response.append(service);
                out.println(encryptResponse(response.toString(), Kut));
                response = new StringBuilder();
                response.append(Base64.encodeBase64String(secretKey.getEncoded()) + ",");
                response.append(username + ",");
                response.append(timeToLive);
                out.println(encryptResponse(response.toString(), Kst));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadUsers(){

        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Razvan\\Desktop\\Facultate\\IS\\users.csv"));
            String line = null;
            while((line = reader.readLine()) != null){
                String[] data = line.split(",");
                User u = new User(data[0], data[1], Type.getType(data[2]));
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadResources(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Razvan\\Desktop\\Facultate\\IS\\resources.csv"));
            String line = null;
            while((line = reader.readLine()) != null){
                String[] data = line.split(",");
                resources.put(data[0], Classification.getClassification(data[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static User findUser(String username){
        for(User u : users){
            if(u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    static String encryptResponse(String message, byte[] secretKey){
        String encryptedText = null;
        try {
            Key key = new SecretKeySpec(secretKey, "AES");
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte encryption[] = c.doFinal(message.getBytes());
            encryptedText = Base64.encodeBase64String(encryption);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }
}
