package Extensions;

public class IntegerExtension {
    public static Integer tryParseInt(String str){
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e){
            System.out.println(str + " is not a valid integer value");
            return null;
        }
    }
}
