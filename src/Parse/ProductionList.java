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

    public ProductionList() {
        productions = new int[][]{{0},
        {4, 0, 43, 42, 41},
        {3, 44, 1, 42},
        {1, 42},
        {5, 44, 3, 45, 2, 44},
        {1, 44},
        {3, 47, 46, 45},
        {3, 48, 4, 47},
        {1, 47},
        {2, 49, 46},
        {2, 50, 46},
        {2, 5, 46},
        {2, 6, 49},
        {3, 51, 7, 49},
        {4, 10, 9, 8, 51},
        {3, 51, 11, 50},
        {3, 51, 12, 50},
        {5, 3, 14, 52, 13, 43},
        {3, 3, 15, 52},
        {2, 53, 52},
        {2, 54, 53},
        {3, 54, 53, 53},
        {3, 3, 55, 54},
        {2, 43, 54},
        {6, 3, 10, 2, 8, 16, 54},
        {6, 3, 10, 2, 8, 17, 54},
        {3, 3, 18, 54},
        {3, 3, 2, 19},
        {9, 3, 20, 14, 54, 21, 56, 13, 20, 54},
        {8, 3, 23, 14, 54, 23, 56, 22, 54},
        {4, 48, 4, 2, 55},
        {2, 56, 48},
        {4, 24, 25, 24, 48},
        {4, 24, 26, 24, 48},
        {5, 10, 56, 8, 57, 48},
        {2, 46, 57},
        {2, 59, 56},
        {4, 59, 60, 59, 56},
        {2, 61, 59},
        {4, 61, 62, 59, 59},
        {2, 63, 61},
        {4, 63, 64, 61, 61},
        {2, 2, 63},
        {2, 9, 63},
        {2, 27, 63},
        {2, 28, 63},
        {2, 15, 63},
        {3, 63, 29, 63},
        {2, 30, 60},
        {2, 31, 60},
        {2, 32, 60},
        {2, 33, 60},
        {2, 34, 60},
        {2, 35, 60},
        {2, 36, 62},
        {2, 37, 62},
        {2, 38, 64},
        {2, 39, 64},
        {2, 40, 64}};
    }

    public int[] reduce(int prod) {
        int[] production = new int[productions[prod][0] + 1];
        for (int x = 0; x < production.length; x++) {
            production[x] = productions[prod][x];
        }
        return production;
    }

}
