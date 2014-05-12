package Runtime;

/**
 *
 * @author Ryan Kasprzyk
 *
 * The token scanner serves as the front end of the compiler, and can process a
 * wide variety of input formats. As with any compiler, the token scanner
 * disregards all whitespace characters (except those in stringliterals) and
 * uses a series of regular expressions to identify system reserved words,
 * identifiers, and operation symbols to be passed to the parser and code
 * generation modules.
 */
import Errors.ErrorHandler;
import Generation.Generator;
import Parse.ParseToken;
import Parse.Parser;
import java.io.*;

public class TokenScanner {

    private File inputFile;
    String[] lineTokens;
    String line, tkn, n, t, l, v;
    int curToken, lineN, cNum;
    ReservedWords rw = new ReservedWords();
    TokenStack ts = new TokenStack();
    SymbolTable symT = new SymbolTable();
    
    /*
     * Flags for code generation, symbol table modification, multi-line comments,
     * and stringliterals, size setting, value setting, and negation.
     */
    boolean symFlag, cFlag, sFlag, setSize, setVal, neg, casting;
    Parser p;
    String idRegex = "[a-z0-9#$_]";
    String rwRegex = "[A-Z0-9_.]";
    int len;
    String[] cBuf = new String[2];
    String lit;
    int x;
    ErrorHandler err = new ErrorHandler(ts);
    Generator g = new Generator(symT, err);

    public TokenScanner(File inFile) {
        inputFile = inFile;
        p = new Parser(g);
        n = t = l = v = "";
    }

    /**
     * The scan method passes the source file line by line into the token
     * processor to recognize language tokens. When the end of the file is
     * reached, the scanner submits an end of line character to the parser and
     * signals the code generator to begin writing the intermediate code to an
     * output file.
     */
    public void scan() {
        symFlag = cFlag = sFlag = setSize = setVal = neg = casting = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader(inputFile));
            lineN = 0;
            lit = "";
            while ((line = in.readLine()) != null) {
                process(line);
                lineN++;
            }
            if (p.checkSymbol(new ParseToken(0)) == 0) {
                System.out.println("Accepted");
            }
            g.reduce(0);
        } catch (IOException e) {
        }
    }

    /**
     * The string processor takes a line from the scanner method and runs
     * through character by character to determine what tokens it is looking at.
     * The method is essentially built from a number of cases under which the
     * terminal values of this language have been organized, such as reserved
     * words, identifiers, operators, and comments.
     *
     * @param line
     */
    public void process(String line) {
        String token = "";
        String s;
        int sym;
        x = 0;
        while (x < line.length()) {
            s = getS(line.charAt(x));

            /*
             * If we are continuing a comment from the previous line
             */
            if (cFlag == true) {
                while (!cBuf[0].matches("\\*") && !cBuf[1].matches("/")
                        && x < line.length()) {
                    s = getS(line.charAt(x));
                    cBuf[0] = cBuf[1];
                    cBuf[1] = s;
                    x++;
                }
                if (x < line.length()) {
                    cFlag = false;
                } else {
                    // Still in comment, read next line
                }
            } /*
             * If we are continuing a string from the previous line
             */ else if (sFlag == true) {
                while (!(s = getS(line.charAt(x))).matches("\'")
                        && x < line.length()) {
                    lit += s;
                    len++;
                    x++;
                }
                if (x < line.length()) {
                    sFlag = false;
                    if (len == 1) {
                        parseSymbol(new ParseToken(26, lit));
                        ts.add(new StackListing(26, lineN, lit));
                        lit = "";
                    } else if (len > 1) {
                        ts.add(new StackListing(25, lineN, lit));
                        parseSymbol(new ParseToken(25, lit));
                        lit = "";
                    }
                    ts.add(new StackListing(24, lineN));
                    parseSymbol(new ParseToken(24));
                    len = 0;
                    x++;
                } else {
                    // Still in string
                }
            } /*
             * Whitespace
             */ else if (s.matches("\\s")) {
                x++;
            } /*
             * Reserved word
             */ else if (s.matches("[A-Z]")) {
                token += s;
                x++;
                while ((s = getS(line.charAt(x))).matches(rwRegex)) {
                    token += s;
                    x++;
                    if (x == line.length()) {
                        break;
                    }
                }
                // We are seeing the declarations section, start writing to 
                // symbol table
                if (token.equals("DECLARE")) {
                    symFlag = true;
                } else if (token.equals("BEGIN") && symFlag == true) {
                    symFlag = false;
                } else if (token.equals("NOT")) {
                    neg = true;
                } else if (setVal == true) {
                    if (neg == true && token.matches("TRUE")) {
                        v = "FALSE";
                    } else if (neg == true && token.matches("FALSE")) {
                        v = "TRUE";
                    }else{
                    v = token;
                    }
                } else {
                    t = token;
                }
                sym = rw.getVal(token);
                if (isType(token) == true && symFlag == false) {
                    parseSymbol(new ParseToken(sym, token));
                } else if (sym == 27 || sym == 28) {
                    if (neg == true && token.matches("TRUE")) {
                        token = "FALSE";
                        neg = false;
                    } else if (neg == true && token.matches("FALSE")) {
                        token = "TRUE";
                        neg = false;
                    }
                    parseSymbol(new ParseToken(sym, token));
                } else {
                    parseSymbol(new ParseToken(sym));
                }
                ts.add(new StackListing(sym, lineN));
                token = "";
            } /*
             * Identifier
             */ else if (s.matches("[a-z]")) {
                token += s;
                x++;
                while ((s = getS(line.charAt(x))).matches(idRegex)) {
                    token += s;
                    x++;
                    if (x == line.length()) {
                        break;
                    }
                }
                if(token.length() > 20){
                    lineBreak();
                    err.DisplayError(5);
                }
                ts.add(new StackListing(2, lineN, token));
                parseSymbol(new ParseToken(2, token));
                if (symFlag == true) {
                    n = token;
                }
                if (symFlag == false && symT.isPresent(token) == false) {
                    System.out.println("Identifier " + token
                            + " has not been defined.");
                    lineBreak();
                    err.DisplayError(4);
                }
                token = "";
            } /*
             * Number
             */ else if (s.matches("[0-9]")) {
                token += s;
                x++;
                while ((s = getS(line.charAt(x))).matches("[0-9]")) {
                    token += s;
                    x++;
                    if (x == line.length()) {
                        break;
                    }
                }
                if (neg == true) {
                    token = "-" + token;
                    neg = false;
                }
                ts.add(new StackListing(9, lineN, token));
                parseSymbol(new ParseToken(9, token));
                if (setVal == true) {
                    v = token;
                    setVal = false;
                } else if (setSize == true) {
                    l = token;
                    setSize = false;
                }
                token = "";
            } /*
             * Beginning of string or char
             */ else if (s.matches("\'")) {
                ts.add(new StackListing(24, lineN));
                parseSymbol(new ParseToken(24));
                x++;
                len = 0;
                sFlag = true;
                while (!(s = getS(line.charAt(x))).matches("\'")
                        && x < line.length()) {
                    lit += s;
                    len++;
                    x++;
                }
                if (x < line.length()) {
                    v = lit;
                    sFlag = false;
                    if (len == 1) {
                        ts.add(new StackListing(26, lineN, lit));
                        parseSymbol(new ParseToken(26, lit));
                        lit = "";
                    } else if (len > 1) {
                        ts.add(new StackListing(25, lineN, lit));
                        parseSymbol(new ParseToken(25, lit));
                        lit = "";
                    }
                    ts.add(new StackListing(24, lineN));
                    parseSymbol(new ParseToken(24));
                    len = 0;
                    x++;
                } else {
                    // Still in string
                }
            } /*
             * Open parenthesis
             */ else if (s.matches("\\(")) {
                ts.add(new StackListing(8, lineN));
                parseSymbol(new ParseToken(8));
                if (symFlag == true) {
                    setSize = true;
                }
                x++;
            } /*
             * Close parenthesis
             */ else if (s.matches("\\)")) {
                ts.add(new StackListing(10, lineN));
                parseSymbol(new ParseToken(10));
                x++;
            } /*
             * Semicolon
             */ else if (s.matches(";")) {
                ts.add(new StackListing(3, lineN));
                parseSymbol(new ParseToken(3));
                if (symFlag == true) {
                    if (t.equals("CHAR") && v.length() > 1) {
                        lineBreak();
                        System.out.println("Cannot convert VARCHAR2 to "
                                + "CHAR by implicit conversion.");
                        err.DisplayError(1);
                    } else if (t.equals("VARCHAR2") && v.length()
                            > Integer.parseInt(l)) {
                        lineBreak();
                        System.out.println("Cannot wrap " + v.length()
                                + "-character string into " + l
                                + "-character string construct.");
                        err.DisplayError(3);
                    } else if (t.equals("NUMBER") && v.length()
                            > Integer.parseInt(l)) {
                        lineBreak();
                        System.out.println("Cannot wrap " + v.length()
                                + "-digit numbers into " + l
                                + "-digit numeric constructs.");
                        err.DisplayError(3);
                    } else if (t.equals("POSITIVE") && v.length()
                            > Integer.parseInt(l)) {
                        lineBreak();
                        System.out.println("Cannot wrap " + v.length()
                                + "-digit numbers into " + l
                                + "-digit numeric constructs.");
                        err.DisplayError(3);
                    } else if (t.equals("POSITIVE") && v.matches("[-][0-9]+")) {
                        lineBreak();
                        System.out.println("Cannot wrap negative values "
                                + "into POSITIVE objects.");
                        err.DisplayError(1);
                    }
                    if (!v.equals("")) {
                        g.add(n);
                        g.add(v);
                        g.reduce(4);
                    }
                    symT.insert(new Symbol(n, t, l, v));
                    n = t = l = v = "";
                    neg = false;
                    setVal = false;
                }
                x++;
            } /*
             * End of program symbol
             */ else if (s.matches("\\\\")) {
                ts.add(new StackListing(0, lineN));
                parseSymbol(new ParseToken(0));
                x++;
            } /*
             * Multi-line comment or division
             */ else if (s.matches("[/]")) {
                token += s;
                x++;
                if ((s = getS(line.charAt(x))).matches("\\*")) {
                    x++;
                    cBuf[0] = "/";
                    cBuf[1] = "\\*";
                    cFlag = true;
                    while (!cBuf[0].matches("\\*") && !cBuf[1].matches("/")
                            && x < line.length()) {
                        s = getS(line.charAt(x));
                        cBuf[0] = cBuf[1];
                        cBuf[1] = s;
                        x++;
                    }
                    if (x < line.length()) {
                        cFlag = false;
                    } else {
                        // Do nothing
                    }
                } else {
                    ts.add(new StackListing(39, lineN));
                    parseSymbol(new ParseToken(39));
                }
            } /*
             * Single line comment or subtraction
             */ else if (s.matches("-")) {
                x++;
                if ((s = getS(line.charAt(x))).matches("-")) {
                    return;
                } else {
                    ts.add(new StackListing(37, lineN));
                    parseSymbol(new ParseToken(37, "-"));
                }
            } /*
             * Less than, not equal, less than or equal
             */ else if (s.matches("<")) {
                x++;
                if ((s = getS(line.charAt(x))).matches(">")) {
                    ts.add(new StackListing(35, lineN));
                    parseSymbol(new ParseToken(35, "<>"));
                    x++;
                } else if ((s = getS(line.charAt(x))).matches("=")) {
                    ts.add(new StackListing(33, lineN));
                    parseSymbol(new ParseToken(33, ">="));
                    x++;
                } else {
                    ts.add(new StackListing(34, lineN));
                    parseSymbol(new ParseToken(34, "<"));
                }
            } /*
             * Greater than, greater than or equal
             */ else if (s.matches(">")) {
                x++;
                if ((s = getS(line.charAt(x))).matches("=")) {
                    ts.add(new StackListing(33, lineN));
                    parseSymbol(new ParseToken(33, ">="));
                    x++;
                } else {
                    ts.add(new StackListing(30, lineN));
                    parseSymbol(new ParseToken(30, ">"));
                }
            } /*
             * Addition
             */ else if (s.matches("\\+")) {
                ts.add(new StackListing(36, lineN));
                parseSymbol(new ParseToken(36, "+"));
                x++;
            } /*
             * Multiplication
             */ else if (s.matches("\\*")) {
                ts.add(new StackListing(38, lineN));
                parseSymbol(new ParseToken(38, "*"));
                x++;
            } /*
             * Modulus
             */ else if (s.matches("\\%")) {
                ts.add(new StackListing(40, lineN));
                parseSymbol(new ParseToken(40, "%"));
                x++;
            } /*
             * Assignment
             */ else if (s.matches(":")) {
                x++;
                if ((s = getS(line.charAt(x))).matches("\\=")) {
                    ts.add(new StackListing(4, lineN));
                    parseSymbol(new ParseToken(4));
                    if (symFlag == true) {
                        setVal = true;
                    }
                    x++;
                } else {
                    lineBreak();
                    err.DisplayError(2);
                }
            } /*
             * Equality
             */ else if (s.matches("\\=")) {
                x++;
                if ((s = getS(line.charAt(x))).matches("\\=")) {
                    ts.add(new StackListing(32, lineN));
                    parseSymbol(new ParseToken(32, "=="));
                    x++;
                } else {
                    lineBreak();
                    err.DisplayError(2);
                }
            } /*
             * &
             */ else if (s.matches("\\&")) {
                ts.add(new StackListing(19, lineN));
                parseSymbol(new ParseToken(19));
                x++;
            } /*
             * Symbol not recognized
             */ else {
                lineBreak();
                err.DisplayError(2);
            }
        }

    }

    public String getS(char c) {
        String s = "";
        s = s + c;
        return s;
    }

    public void parseSymbol(ParseToken i) {
        int q = p.checkSymbol(i);
        if (q == 9999) {
            lineBreak();
            err.DisplayError(0);
        } else if (q == 0) {
            System.out.println("Accepted");
            System.exit(0);
        }
    }

    /*
     * The linebreak method is used to print the line and column where an 
     * exception occured as well as the current contents of the token stack
     * in order to help the programmer find and diagnose issues in their code
     */
    public void lineBreak() {
        System.out.println("Encountered exception at " + lineN + ":" + x);
    }

    /**
     * Check if the current reserved word token is a type.
     *
     * @param token
     * @return
     */
    public boolean isType(String token) {
        if (token.matches("VARCHAR2")) {
            return true;
        } else if (token.matches("CHAR")) {
            return true;
        } else if (token.matches("NUMBER")) {
            return true;
        } else if (token.matches("POSITIVE")) {
            return true;
        } else if (token.matches("BOOLEAN")) {
            return true;
        } else {
            return false;
        }
    }
}
