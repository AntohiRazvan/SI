/**
 * Created by Razvan on 22.11.2014.
 */
public enum Permission {
    NONE,
    READ,
    WRITE;

    public static Permission getPermission(String p){
        if(p.equals("READ"))
            return Permission.READ;
        else if(p.equals("WRITE"))
            return Permission.WRITE;
        return Permission.NONE;
    }
}
