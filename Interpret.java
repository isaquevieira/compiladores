import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Interpret
{
    public static void main(String argv[])
    {
        Interpreter aci = new Interpreter();
        String str;
        System.out.println("Calculadora Avancada (digite 's' para sair)");
        System.out.println("Exemplos de expressoes: ");
        System.out.println("- 2+5-3*4");
        System.out.println("- 3*(1+4)");
        System.out.println("- def abs(n) = if n>=0 then n else -n");
        System.out.println("- def fat(n) = if n=0 then 1 else n*fat(n-1)");
        System.out.println("- fat(5) + abs(-10)\n");
        while (true) {
            try {
                System.out.print("? ");
                BufferedReader bufferRead = new
                BufferedReader(new InputStreamReader(System.in));
                str = bufferRead.readLine();
                
                if (str.equalsIgnoreCase("s"))
                    System.exit(0);

                InputStream stream = new
                ByteArrayInputStream(str.getBytes("UTF-8"));
                Scanner s = new Scanner(stream);
                Parser p = new Parser(s);
                p.setInterpreter(aci);
                p.Parse();

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
