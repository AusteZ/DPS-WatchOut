package Extensions;

public class LongExtension {
    public static Long tryParseLong(String str){
        try {
            return Long.parseLong(str);
        } catch(NumberFormatException e){
            System.out.println(str + " is not a valid long value");
            return null;
        }
    }
}