/**
 *
 * @author Ryan Kasprzyk
 */
import Runtime.TokenScanner;
import java.io.File;
import java.util.Scanner;

public class Start {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.print("Specify source file: ");
        String f = scan.nextLine();
        File in = new File(f);
        TokenScanner s = new TokenScanner(in);
        System.out.println("Processing");
        s.scan();
        System.out.println("Output generated, shutting down");
        }
    }
    
