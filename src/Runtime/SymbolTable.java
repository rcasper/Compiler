package Runtime;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * This symbol table is an array of Symbol objects, which are declared with at
 * least a name, type and size (where applicable) and also store the current
 * values of the symbols.
 */
public class SymbolTable {
    Symbol[] table;
    String name;
    int placement;
    
    public SymbolTable(){
        table = new Symbol[100];
    }
    
    /**
     * Insert a new Symbol object into the table. Insertions is done by using
     * the ASCII value of the identifier mod the size of the table to determine
     * the storage index.
     * @param newSym
     * @return 
     */
    public int insert(Symbol newSym){
        name = newSym.n;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        table[placement%table.length] = newSym;
        
        return 0;
    }
    
    public Symbol getSymbol(String id){
        name = id;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        return table[placement%table.length];
    }
    
    /**
     * Returns the current value of the given identifier
     * @param id
     * @return 
     */
    public String getValue(String id){
        name = id;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        return table[placement%table.length].v;  
    }
    
    /**
     * Get the current size of the given identifier
     * @param id
     * @return 
     */
    public String getSize(String id){
        name = id;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        return table[placement%table.length].l;  
    }
    
    /**
     * Return the type of the given identifier
     * @param id
     * @return 
     */
    public String getType(String id){
        name = id;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        return table[placement%table.length].t;
    }
    
    /**
     * Check if the given identifier has been declared
     * @param id
     * @return 
     */
    public boolean isPresent(String id){
        try{
            name = id;
        placement = 0;
        for(int x = 0; x < name.length(); x++){
            placement += (int)name.charAt(x);
        }
        String out = table[placement%table.length].t;
        return true;
        }catch(NullPointerException e){
            return false;
        }
    }
}