/**
 * Created by Razvan on 22.11.2014.
 */
public enum Type {
    GUEST,
    USER,
    ADMIN;

    public static Type getType(String s){
        if(s.equals("GUEST"))
            return Type.GUEST;
        else if(s.equals("USER"))
            return Type.USER;
        else if(s.equals("ADMIN"))
            return Type.ADMIN;
        return Type.GUEST;
    }
}

