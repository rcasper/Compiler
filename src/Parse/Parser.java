package Parse;

/**
 *
 * @author Ryan Kasprzyk
 * 
 * The Parser class uses a large number of rules and codes to determine whether
 * the structure of the code is syntactically correct. The parser is fed a
 * number of attributed grammar tokens by the TokenScanner class, and performs
 * pushes, (recursive) reductions, accepts, or errors out based on the rules
 * specified in the parse table.
 */
import java.util.Stack;
import Generation.Generator;

public class Parser {

    ParseTable pt;
    ProductionList pl;
    int cState;
    int nextAct;
    int[] reduction;
    Stack<ParseToken> pStack;
    ParseToken stackTop;
    Generator gs;

    public Parser(Generator g) {
        pStack = new Stack();
        pStack.push(new ParseToken(-1));
        pStack.push(new ParseToken(0));
        cState = 0;
        pl = new ProductionList();
        pt = new ParseTable();
        nextAct = 0;
        gs = g;
    }

    public int checkSymbol(ParseToken token) {
        nextAct = pt.parseSymbol(cState, token.getNum());
        if (nextAct > 0 && nextAct < 999) {
            pStack.push(token);
            pStack.push(new ParseToken(nextAct));
            cState = nextAct;
            return 1;
        } else if (nextAct < 0) {
            reduce();
            checkSymbol(token);
        } else if (nextAct == 9999) {
            return 9999;
        }

        if (cState == 6) {
            return 0;
        }
        return 1;
    }

    public void reduce() {
        nextAct = Math.abs(nextAct);
        reduction = pl.reduce(nextAct);
        for (int x = 1; x < reduction[0]; x++) {
            pStack.pop();
            stackTop = pStack.pop();
            if (reduction[0] > 2 && stackTop.getVal() != null) {
                gs.add(stackTop.getVal());
            }
            if (stackTop.getNum() != reduction[x]) {
                System.out.println("False");
                System.exit(-1);
            }
        }
        cState = (pStack.peek()).getNum();
        if (reduction[0] == 2) {
            pStack.push(new ParseToken
                    (reduction[reduction[0]], stackTop.getVal()));
        } else if (nextAct == 39 || nextAct == 41) {
            pStack.push(new ParseToken
                    (reduction[reduction[0]], gs.reduce(nextAct)));
        } else if (nextAct == 24 || nextAct == 25 || nextAct == 26 ||
                nextAct == 27 || nextAct == 28 || nextAct == 29 ||
                nextAct == 30 || nextAct == 34 || nextAct == 37) {
            gs.reduce(nextAct);
            pStack.push(new ParseToken(reduction[reduction[0]]));
        } else {
            pStack.push(new ParseToken(reduction[reduction[0]]));
        }

        // If the current state and new top of stack have a valid GOTO,
        // cState = new state and new State is pushed onto the stack
        if ((nextAct = pt.parseSymbol
                (cState, (pStack.peek()).getNum())) != 9999) {
            cState = nextAct;
            pStack.push(new ParseToken(cState));
        } else if (nextAct == 0) {
            System.out.println("True");
            System.exit(0);
        }
    }
}
