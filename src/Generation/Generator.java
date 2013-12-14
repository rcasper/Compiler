package Generation;

/**
 * @author Ryan Kasprzyk
 *
 *
 * This class is passed data by the parser and uses it to generate intermediate
 * code for use with the micE compiler. For every reduction of a statement, the
 * program generates a Quad with a line number, opcode, and up to 3 operators.
 *
 * When the source file ends with no errors, the generator will print the
 * generated lines in order, ending with a HALT command.
 */
import Runtime.SymbolTable;
import Runtime.Symbol;
import Errors.ErrorHandler;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class Generator {

    private GenStack gs;
    Quad[] output;
    Stack<Integer> needPatch;
    Stack<Integer> loopBack;
    int line, sum;
    File outFile = new File("output.asm");
    String cLine, op1, op2, op3, result, relop, results;
    int temps, i1, i2, code;
    SymbolTable sym;
    Symbol s;
    ErrorHandler err;

    public Generator(SymbolTable symbol, ErrorHandler er) {
        err = er;
        gs = new GenStack();
        line = 0;
        output = new Quad[100];
        needPatch = new Stack();
        loopBack = new Stack();
        temps = 0;
        sym = symbol;
        int sum = 0;
    }

    public void add(String s) {
        gs.addT(s);
    }

    public String reduce(int production) {
        result = null;
        if (line == output.length + 1) {
            Quad[] temp = new Quad[output.length * 2];
            System.arraycopy(output, line, temp, 0, line - 1);
            output = temp;
        }
        switch (production) {
            /*
             * This reduction signifies an EOF has been reached in the source
             * file with no errors being thrown by any of the compilers 
             * component parts. At this point the code generator will begin
             * iterating through the stack and printing the lines of code
             * generated to an output assembly file, which will be used in micE.
             */
            case 0:
                output[line] = new Quad(line, "HLT", "", "", "");
                line++;
                try {
                    PrintWriter out = new PrintWriter(outFile);
                    for (int x = 0; x < line; x++) {
                        cLine = x + " " + output[x].op + " ";
                        if (output[x].a1.matches("[-]*[0-9]+")) {
                            cLine += "#" + output[x].a1 + ",";
                        } else if (output[x].a1.matches("TRUE")) {
                            cLine += "#1" + ",";
                        } else if (output[x].a1.matches("FALSE")) {
                            cLine += "#0" + ",";
                        } else {
                            cLine += output[x].a1 + ",";
                        }
                        if (output[x].a2.matches("[-]*[0-9]+")) {
                            cLine += "#" + output[x].a2 + ",";
                        } else {
                            cLine += output[x].a2 + ",";
                        }
                        if (output[x].a3.matches("[-]*[0-9]+")) {
                            cLine += "#" + output[x].a3;
                        } else {
                            cLine += output[x].a3;
                        }
                        out.println(cLine);
                        cLine = "";
                    }
                    out.close();
                } catch (IOException e) {
                }
            /*
             * This case is for when an assignment is made in the 
             * declaration body of the code, which is the only time in the
             * declaration body where code is generated.
             */
            case 4:
                op1 = gs.getT();
                op3 = gs.getT();
                if (op1.matches("[a-z]")) {
                    char c = op1.charAt(0);
                    int ic = (int) c;
                    op1 = String.valueOf(ic);
                } else if (op1.matches("TRUE")) {
                    op1 = "1";
                } else if (op1.matches("FALSE")) {
                    op1 = "0";
                }
                output[line] = new Quad(line, "STO", op1, "", op3);
                line++;
                break;
            /*
             * This reduction generated two lines of code: one to print
             * the value of the given identifier, and one to move to a new
             * line.
             */
            case 24:
                if (sym.getType(op1).matches("CHAR")) {
                    char c = sym.getValue(op1).charAt(0);
                    int ic = (int) c;
                    op1 = String.valueOf(ic);
                    output[line] = new Quad(line, "SYS", "-2", op1, "");
                    line++;
                } else {
                    output[line] = new Quad(line, "SYS", "-1", gs.getT(), "");
                    line++;
                }
                output[line] = new Quad(line, "SYS", "0", "", "");
                line++;
                break;
            /*
             * This case simply prints the value of an identifier without
             * moving to a new line
             */
            case 25:
                op1 = gs.getT();
                if (sym.getType(op1).matches("CHAR")) {
                    char c = sym.getValue(op1).charAt(0);
                    int ic = (int) c;
                    op1 = String.valueOf(ic);
                    output[line] = new Quad(line, "SYS", "-2", op1, "");
                    line++;
                } else if (sym.getType(op1).matches("VARCHAR2")) {
                    // Deprecated
                } else {
                    output[line] = new Quad(line, "SYS", "-1", op1, "");
                    line++;
                }
                break;
            /*
             * This case simple moves to a new line
             */
            case 26:
                output[line] = new Quad(line, "SYS", "0", "", "");
                line++;
                break;
            /*
             * This case allows the program to read and store user input
             */
            case 27:
                output[line] = new Quad(line, "SYS", "1", "", gs.getT());
                line++;
                break;
            /*
             * This statement back-patches the start code of a conditional
             * statement for the line to jump to in order to skip the lines
             * that would be executed if the condition is false.
             */
            case 28:
                output[needPatch.pop()].a3 = String.valueOf(line);
                break;
            /*
             * This statement back-patches the start code of a looping
             * statement for the line to jump to in order to skip the lines
             * that would be executed if the condition is false.
             */
            case 29:
                output[line] = new Quad(line, "JMP", "", "", String.valueOf(loopBack.pop()));
                line++;
                output[needPatch.pop()].a3 = String.valueOf(line);
                break;
            /*
             * This statement handles assignment operations within the body
             * of the code.
             */
            case 30:
                // Assignment
                String vOp1 = "";
                op3 = gs.getT();
                op1 = gs.getT();
                vOp1 = gs.getT();
                if (temps > 0) {
                    op1 = "temp" + temps;
                    s = sym.getSymbol(op3);
                    code = s.changeValue(String.valueOf(sum));
                    if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                } else if (op1.matches("[0-9]+")) {
                    s = sym.getSymbol(op3);
                    code = s.changeValue(String.valueOf(op1));
                    if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                } else if (op1.matches("TRUE")) {
                    s = sym.getSymbol(op3);
                    code = s.changeValue(op1);
                    if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                    op1 = "1";
                } else if (op1.matches("FALSE")) {
                    s = sym.getSymbol(op3);
                    code = s.changeValue(op1);
                    if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                    op1 = "0";
                } else {
                    s = sym.getSymbol(op3);
                    code = s.changeValue(sym.getValue(op3));
                    if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                }
                output[line] = new Quad(line, "STO", op1, "", op3);
                temps = 0;
                sum = 0;
                line++;
                break;
            /*
             * This case manages type casting
             */
            case 34:
                String tar = gs.getT();
                String type = gs.getT();
                if (tar.matches("CHAR")) {
                    String temp = tar;
                    tar = type;
                    type = temp;
                }
                if (tar.matches("BOOLEAN")) {
                    err.DisplayError(5);
                }
                if (type.matches("VARCHAR2") || type.matches("POSITIVE")
                        || type.matches("NUMBER")) {
                    String size = gs.getT();
                    sym.getSymbol(tar).changeSize(size);
                }
                code = sym.getSymbol(tar).changeType(type);
                if(code != 0){
                    err.DisplayError(Math.abs(code));
                }
                gs.addT(tar);
                break;
            /*
             * This case generates relational operations, leaving a line of
             * code to back-patch after the number of lines within the 
             * conditional section have been determined.
             */
            case 37:
                op1 = gs.getT();
                op2 = gs.getT();
                if (op2.matches("[>]")) {
                    relop = "JGT";
                } else if (op2.matches("[>][=]")) {
                    relop = "JGE";
                } else if (op2.matches("[=][=]")) {
                    relop = "JEQ";
                } else if (op2.matches("[<][=]")) {
                    relop = "JLE";
                } else if (op2.matches("[<]")) {
                    relop = "JLT";
                } else if (op2.matches("[<][>]")) {
                    relop = "JNE";
                }
                op3 = gs.getT();
                if (op3.matches("TRUE")) {
                    op3 = "1";
                } else if (op3.matches("FALSE")) {
                    op3 = "0";
                }
                output[line] = new Quad(line, relop, op1, op3, String.valueOf(line + 3));
                loopBack.push(line);
                line++;
                output[line] = new Quad(line, "STO", "0", "", "t1");
                line++;
                output[line] = new Quad(line, "JMP", "", "", String.valueOf(line + 2));
                line++;
                output[line] = new Quad(line, "STO", "1", "", "t1");
                line++;
                output[line] = new Quad(line, "JNE", "1", "t1", "");
                needPatch.push(line);
                line++;
                break;
            /*
             * This case handles addition and subtraction operations
             */
            case 39:
                //First read operators and operand from stack
                op1 = gs.getT();
                op2 = gs.getT();
                op3 = gs.getT();
                // Check if op1 and op 3 are values or identifiers
                if (op1.matches("temp[0-9]+")) {
                    i1 = sum;
                } else if (op1.matches("[a-z][a-z0-9$_#]*")) {
                    i1 = Integer.parseInt(sym.getValue(op1));
                } else {
                    i1 = Integer.parseInt(op1);
                }
                if (op3.matches("temp[0-9]+")) {
                    i2 = sum;
                } else if (op3.matches("[a-z][a-z0-9$_#]*")) {
                    i2 = Integer.parseInt(sym.getValue(op3));
                } else {
                    i2 = Integer.parseInt(op3);
                }
                if (op2.matches("[+]")) {
                    temps++;
                    sum = i1 + i2;
                    result = "temp" + temps;
                    output[line] = new Quad(line, "ADD", op1, op3, "temp" + temps);
                } else if (op2.matches("[-]")) {
                    temps++;
                    sum = i1 + i2;
                    result = "temp" + temps;
                    output[line] = new Quad(line, "SUB", op1, op3, "temp" + temps);
                }
                line++;
                break;
            /*
             * This case handles multiplication, division, and modulus
             * operations
             */
            case 41:
                op1 = gs.getT();
                op2 = gs.getT();
                op3 = gs.getT();
                // Check if op1 and op 3 are values or identifiers
                if (op1.matches("temp[0-9]+")) {
                    i1 = sum;
                } else if (op1.matches("[a-z][a-z0-9$_#]*")) {
                    i1 = Integer.parseInt(sym.getValue(op1));
                } else {
                    i1 = Integer.parseInt(op1);
                }
                if (op3.matches("temp[0-9]+")) {
                    i2 = sum;
                } else if (op3.matches("[a-z][a-z0-9$_#]*")) {
                    i2 = Integer.parseInt(sym.getValue(op3));
                } else {
                    i2 = Integer.parseInt(op3);
                }
                if (op2.matches("[*]")) {
                    temps++;
                    sum = i1 * i2;
                    result = "temp" + temps;
                    output[line] = new Quad(line, "MUL", op1, op3, "temp" + temps);
                } else if (op2.matches("[/]")) {
                    temps++;
                    sum = i1 / i2;
                    result = "temp" + temps;
                    output[line] = new Quad(line, "DIV", op1, op3, "temp" + temps);
                } else if (op2.matches("[%]")) {
                    temps++;
                    sum = i1 % i2;
                    result = "temp" + temps;
                    output[line] = new Quad(line, "MOD", op1, op3, "temp" + temps);
                }
                line++;
                break;
            default:
                //Do nothing
                break;
        }
        return result;
    }

    public void backPatch(int i) {
        Quad old = output[i];
    }
}