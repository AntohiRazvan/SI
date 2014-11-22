import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Trusted {
    static ServerSocket server;
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;

    static final double timeToLive = 7200000; //2 hours

    static byte[] Kut = "Lk89njhH89aI".getBytes();
    static byte[] Kst = "NjJ989KJkla0".getBytes();
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
        loadUsers();
        loadResources();
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DESede");
            keyGen.init(112);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            for (byte b : encoded)
            {
                System.out.printf("%2X ",b);
            }
            System.out.println("");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        try {

            waitForClient();


            while(!in.ready()){}
            String data =  in.readLine();
            out.println(data);
        }
        catch(Exception e) {
            System.out.println("[Trusted]Error!\n");
        }
    }

    static void waitForClient(){
        try {
            server = new ServerSocket(1234);
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
            if( (user != null) && ((hasPermission.get(user.getType().ordinal())).get((resources.get(resource)).ordinal()).contains(permission)) ){
                StringBuilder response = new StringBuilder();
                double expirationTime = System.currentTimeMillis() + timeToLive;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadUsers(){

        try {
            BufferedReader reader = new BufferedReader(new FileReader("users.csv"));
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
            BufferedReader reader = new BufferedReader(new FileReader("resources.csv"));
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

    static String encryptResponse(String message){
        String encryption = null;
        try {
            Key key = new SecretKeySpec(Kut, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            encryption = new String(c.doFinal(message.getBytes()), "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryption;
    }
}
