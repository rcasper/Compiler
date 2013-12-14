package Runtime;


/**
 * @author Ryan Kasprzyk
 * 
 * This class houses a stack of tokens read in from the token scanner. This stack
 * is used in conjunction with row and column information to help a user identify
 * where errors in their source code are identified during compilation
 */
import java.util.Stack;
public class TokenStack {
	Stack<StackListing> tStack;
	StackListing s;
	ReservedWords rw = new ReservedWords();
	
	public TokenStack(){
		tStack = new Stack();
	}
	
	public void add(StackListing n){
		tStack.push(n);
	}
	
	public void remove(){
		tStack.pop();
	}
	
	public void printStackTrace(){
		while(!tStack.isEmpty()){
			s = tStack.pop();
			System.out.print("LINE " + s.getLine() + " : " + rw.getName(s.getName()));
			if(!s.getValue().matches(""))
				System.out.println(", " + s.getValue());
			else
				System.out.println();
		}
	}

}
