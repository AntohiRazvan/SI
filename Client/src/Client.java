import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;
    static Random rand = new Random();

    static byte[] Kut = "Lk89njhH89aI".getBytes();
    static String nonce;

    public static void main(String args[]) {
        try {
            initNet();
            User u = new User("Razvan", "abcd");

            byte b[] = new byte[] {0x21, 0x34};
            out.println(b);
            out.println(u.getUsername() + " " + u.getHashedPassword());

            while (!in.ready()) {}
            System.out.println(in.readLine());


            closeConnection();
        }
        catch(Exception e) {
            System.out.println("[Client]Error!\n");
        }
    }

    static void initNet(){
        try {
            socket = new Socket("localhost", 1234);
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
        nonce = "" + rand.nextInt((999999 - 100000) + 1) + 100000;
        out.println(user + "," + service + "," + nonce);
    }
}
