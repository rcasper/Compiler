package Parse;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * The parse table for this language was created by feeding the original grammar
 * provided through GNU-Bison, the open-source equivalent to the YACC parse 
 * table creator. As is the case with the production list, this output has
 * been translated into a 112x65 entry table, where the columns refer to the
 * current token or stack symbol and the rows refer to the given state of the
 * parser. In the parse table, positive values represent a shift operation,
 * negative values represent a reduce operation, the number 0 represents an
 * accepting state, and the number 9999 represents an error, which causes the
 * parser to reject the code and terminate execution.
 */
import java.io.*;
public class ParseTable {
    int[][] table;
    String line, value;
    String[] values;
    int row;
    
    public ParseTable(){
        table = new int[112][65];
        row = 0;
        try{
            BufferedReader in = new BufferedReader
                    (new FileReader("ParseTable.csv"));
            while((line = in.readLine()) != null){
                values = line.split(",");
                for(int x = 0; x < values.length; x++){
                    value = values[x].trim();
                    table[row][x] = Integer.parseInt(value);
                }
                row++;
            }
            System.out.println("Done");
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public int parseSymbol(int state, int token){
        if(state==9999 || token == 9999){
            return 9999;
        }
        return table[state][token];
    }
    
}
