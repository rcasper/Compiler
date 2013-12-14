package Generation;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * The Quad object represents a single line of intermediate code. The name Quad
 * refers to the code type we use, where we have an opcode and up to three 
 * operators defined. In the case of certain opcodes, some operator fields are 
 * not used. In cases like these, unused fields are populated with blank
 * spaces, which will still be comma-separated as the language dictates.
 */
public class Quad {
    int line;
    String op;
    String a1;
    String a2;
    String a3;
    
    public Quad(int l, String o, String le, String mi, String ri){
        line = l;
        op = o;
        a1 = le;
        a2 = mi;
        a3 = ri;
    }
    
}
