package Parse;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * The ParseToken object defines an attributed grammar entry. For example, a
 * number is processed by the parser as the value 9, however 9 is generally
 * not the value of number token. In order to preserve this information
 * for the code generator, the certain tokens which are place-holders for data
 * read from the source code will store that data as a value field where 
 * appropriate.
 */
public class ParseToken {
    private int tValue;
    private String rValue;
    
    public ParseToken(int v){
        tValue = v;
        rValue = null;
    }
    
    public ParseToken(int v, String s){
        tValue = v;
        rValue = s;
    }
    
    public int getNum(){
        return tValue;
    }
    
    public String getVal(){
        return rValue;
    }
    
}
