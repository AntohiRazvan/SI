/**
 * Created by Razvan on 22.11.2014.
 */
public enum Classification{
    RESTRICTED,
    CONFIDENTIAL,
    SECRET,
    TOPSECRET;

   public static Classification getClassification(String c){
       if(c.equals("RESTRICTED"))
           return Classification.RESTRICTED;
       else if(c.equals("CONFIDENTIAL"))
           return Classification.CONFIDENTIAL;
       else if(c.equals("SECRET"))
           return Classification.SECRET;
       else if(c.equals("TOPSECRET"))
           return Classification.TOPSECRET;
       return Classification.TOPSECRET;
   }
}
