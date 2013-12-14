package Generation;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * This stack keeps track of value data passed from the parser or token scanner
 * to be used for code generation.
 */
import java.util.Stack;
public class GenStack {
    Stack<String> gStack;
    
    public GenStack(){
        gStack = new Stack();
    }
    
    public void addT(String s){
        gStack.push(s);
    }
    
    public String getT(){
        return gStack.pop();
    }
    
}
