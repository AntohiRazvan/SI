import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    String username;
    String hashedPassword;

    User(String username, String password){
        this.username = username;
        hashedPassword = generateHash(password);
    }

    public String getUsername(){
        return username;
    }

    public String getHashedPassword(){
        return hashedPassword;
    }

    public String generateHash(String password){
        String hash = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes(), 0, password.length());
            StringBuffer hexString = new StringBuffer();
            byte[] mdbytes = md.digest();
            for (int i=0; i<mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }

            hash = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
