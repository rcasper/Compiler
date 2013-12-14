package Parse;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * This list of productions is used by the parser to pop and check entries to 
 * the parsing stack to ensure that the flow of tokens is syntactically valid.
 * The first entry is n, the number of elements in the production rule including
 * the left-hand side. Entries 1 to n-1 are the elements to be popped and from
 * the parse stack. Entry n is the element to be placed onto the stack.
 * 
 * Production rules are stored on a Comma-Separated Value file which is read in
 * at the start of the compilation process.
 */
import java.io.*;
public class ProductionList {
    int[][] productions;
    String line, value;
    String[] values;
    int row;
    
    public ProductionList(){
        productions = new int[59][10];
        try{
            row = 0;
            BufferedReader in = new BufferedReader
                    (new FileReader("Productions.csv"));
            while((line = in.readLine()) != null){
                values = line.split(",");
                for(int x = 0; x < values.length; x++){
                    value = values[x].trim();
                    productions[row][x] = Integer.parseInt(value);
                }
                row++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public int[] reduce(int prod){
        int[] production = new int[productions[prod][0] + 1];
        for (int x = 0; x < production.length; x++){
            production[x] = productions[prod][x];
        }
        return production;
    }
    
}
