import java.lang.*;
import java.io.*;
import java.net.*;

public class Trusted {
    static ServerSocket server;
    static Socket socket;
    static BufferedReader in;
    static PrintWriter out;

    public static void main(String args[]) {
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
}
