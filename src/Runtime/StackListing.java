package Runtime;

/**
 * 
 * @author Ryan Kasprzyk
 * 
 * This class holds the attributes of an element of the TokenStack
 */
public class StackListing {
	int name, line;
	String value;
	
	public StackListing(int n, int l){
		name = n;
		line = l;
		value = "";
	}
	
	public StackListing(int n, int l, String v){
		name = n;
		line = l;
		value = v;
	}
	
	public int getName(){
		return name;
	}
	
	public int getLine(){
		return line;
	}
	
	public String getValue(){
		return value;
	}

}
