import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    public String username;
    public String hashedPassword;

    User(String username, String password){
        this.username = username;
        hashedPassword = generateHash(password);
    }

    public String generateHash(String password){
        String hash = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes(), 0, password.length());
            StringBuffer hexString = new StringBuffer();
            byte[] mdbytes = md.digest();
            for (int i=0;i<mdbytes.length;i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }

            hash = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
