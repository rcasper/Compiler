package Errors;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * This class stores general error codes to to help the user located issues with
 * their code
 */
import Runtime.TokenStack;
public class ErrorHandler {

    String[] errCodes = new String[6];
    TokenStack tkn;
    
    public ErrorHandler(TokenStack t) {
        tkn = t;
        // Parser has reached an error state
        errCodes[0] = "GrammarException";
        // Attempted to assign the wrong kind of data to a variable
        errCodes[1] = "TypeMismatchException";
        // Invoking an invalid reserved word
        errCodes[2] = "InvalidTokenException";
        // Attempting to store more data than a variable can handle
        errCodes[3] = "SizeMismatchException";
        // Attempting to use a variable that has not been declared
        errCodes[4] = "UndeclaredIdentifierException";
        // Attemted to use and invalid identifier
        errCodes[5] = "InvalidIdentifierException";
    }
    
    public void DisplayError(int code){
        System.out.println(errCodes[code]);
        tkn.printStackTrace();
        System.exit(-1);
    }
}
