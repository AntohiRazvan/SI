import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    String username;
    String hashedPassword;
    Type type;
    final public byte[] Kut = "tn3oa9ht01dit5hgopre93dc".getBytes();
    final public byte[] Kst = "p94hnvg8ufh3dsipr4thd4cb".getBytes();

    User(String username, String password, Type type){
        this.username = username;
        hashedPassword = generateHash(password);
        this.type = type;
    }

    public String getUsername(){
        return username;
    }

    public String getHashedPassword(){
        return hashedPassword;
    }

    public Type getType(){
        return type;
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
