package Runtime;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * Lookup table of reserved word values to be used by the TokenScanner to feed
 * into the parser.
 */

public class ReservedWords {
    String[] rWords = new String[41];
    
    public ReservedWords(){
        rWords[0] = "\\";
        rWords[1] = "DECLARE";
        rWords[2] = "identifier";
        rWords[3] = ";";
        rWords[4] = ":=";
        rWords[5] = "BOOLEAN";
        rWords[6] = "CHAR";
        rWords[7] = "VARCHAR2";
        rWords[8] = "(";
        rWords[9] = "num";
        rWords[10] = ")";
        rWords[11] = "NUMBER";
        rWords[12] = "POSITIVE";
        rWords[13] = "BEGIN";
        rWords[14] = "END";
        rWords[15] = "NULL";
        rWords[16] = "DBMS_OUTPUT.PUT_LINE";
        rWords[17] = "DBMS_OUTPUT.PUT";
        rWords[18] = "DBMS_OUTPUT.NEW_LINE";
        rWords[19] = "&";
        rWords[20] = "IF";
        rWords[21] = "THEN";
        rWords[22] = "WHILE";
        rWords[23] = "LOOP";
        rWords[24] = "\'";
        rWords[25] = "stringliteral";
        rWords[26] = "c";
        rWords[27] = "TRUE";
        rWords[28] = "FALSE";
        rWords[29] = "NOT";
        rWords[30] = ">";
        rWords[31] = ">=";
        rWords[32] = "==";
        rWords[33] = "<=";
        rWords[34] = "<";
        rWords[35] = "<>";
        rWords[36] = "+";
        rWords[37] = "-";
        rWords[38] = "*";
        rWords[39] = "/";
        rWords[40] = "%";
    }
    
    public int getVal(String tk){
        for(int x = 0; x < rWords.length; x++){
            if(tk.equals(rWords[x])){
                return x;
            }
        }
        return -1;
    }
    
    public String getName(int i){
    	return rWords[i];
    }
}
