import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class bitch {
    public static void main(String[] args) {
        char[] program;

        useChars = args[0].equals("-c");

        {
            File f = new File(args[useChars ? 1 : 0]);
            FileReader r;
            try {
                r = new FileReader(f);
            } catch(FileNotFoundException e) { System.out.println("Error on finding file"); e.printStackTrace(); return; }
        
            int i = -1;
            String prgString = "";
            try {
                while((i = r.read()) != -1) {
                    prgString += (char) i;
                }
            } catch(IOException e) { System.out.println("Error on reading file"); e.printStackTrace(); return; }
            
            try {
                r.close();
            } catch(IOException e) { System.out.println("Error on closing file stream"); e.printStackTrace(); return; }

            program = prgString.toCharArray();
        }

        runProgram(program);
    }

    public static boolean useChars = false;
    public static Scanner s = new Scanner(System.in);
    public static int runProgram(char[] originalProgram) {
        if(useChars) { s.useDelimiter(""); }

        int opCounter = 0;
        char[] program = originalProgram.clone();
        int current = 0;
        
        ArrayList<Integer> blockPoints = new ArrayList<Integer>();

        loop:
        while(program.length > 0) {
            switch(program[0]) {
                case '\\': storage = ""; current = grabInput(); break;
                case '#': storage = ""; current = grabInteger(new String(program).substring(1).toCharArray()); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case '>': blockPoints.add(opCounter); break;
                case '<': opCounter = blockPoints.get(blockPoints.size()-1)-1; program = join(new char[] { 'a' }, new String(originalProgram).substring(opCounter+1).toCharArray()); blockPoints.remove(blockPoints.size()-1); break;
                case '&': current &= grabInteger(new String(program).substring(1).toCharArray()); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case '|': current |= grabInteger(new String(program).substring(1).toCharArray()); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case '~': current = ~current; break;
                case '^': current ^= grabInteger(new String(program).substring(1).toCharArray()); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case '[': current = lshift(current, grabInteger(new String(program).substring(1).toCharArray())); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case ']': current = rshift(current, grabInteger(new String(program).substring(1).toCharArray())); opCounter += ((Integer) current).toString().length(); program = new String(program).substring(((Integer) current).toString().length()).toCharArray(); break;
                case '.': break loop;
                case '/': System.out.print(useChars ? new Character((char) current).toString() : new Integer(current).toString()+"\n"); break;
                case ':': if(current != 0) { program = new String(program).substring(1).toCharArray(); opCounter++; } break;
                case ';': if(current == 0) { program = new String(program).substring(1).toCharArray(); opCounter++; } break;
            }

            program = new String(program).substring(1).toCharArray();
            opCounter++;

            // if(blockPoints.size() > 0) { System.out.print("Latest block point: "); System.out.println(blockPoints.get(blockPoints.size()-1)); }
            // System.out.print("Current code: "); System.out.println(program);
            // System.out.print("Current pointer: "); System.out.println(opCounter);
        }

        return current;
    }

    public static int grabInput() {
        return useChars ? (int) s.next().toCharArray()[0] : s.nextInt();
    }

    public static int grabInteger(char[] c) {
        if(c[0] == '\\') { return grabInput(); }

        String intS = "";
        
        for(char chr : c) {
            if(!new String(new char[] {chr}).matches("-?[0-9]?")) { break; }
            intS += chr;
        }

        int intV = new Integer(intS);

        return intV;
    }

    public static char[] join(char[] a, char[] b) {
        char[] r = new char[a.length + b.length];
        for(int x = 0; x < a.length; x++) { r[x] = a[x]; }
        for(int x = a.length; x < a.length+b.length; x++) { r[x] = b[x-a.length]; }

        return r;
    }

    public static String storage = "";
    public static int lshift(int n, int x) {
        int num = 0;
        {
            while(storage.length() < x) { storage += "0"; }

            String a = storage.substring(0, x);
            storage = storage.substring(x);

            String ns = Integer.toBinaryString(n) + a;
            char[] nc = ns.toCharArray();

            for(int c = 0; c < nc.length; c++) {
                num += new Integer(new String(new char[] { nc[nc.length-c-1] })) * Math.pow(2, c);
            }
        }

        return num;
    }

    public static int rshift(int n, int x) {
        int num = 0;
        {
            String ns = Integer.toBinaryString(n);
            while(ns.length() < x) { ns = "0" + ns; }

            storage = ns.substring(0, x) + storage;
            ns = ns.substring(0, ns.length()-x);

            char[] nc = ns.toCharArray();

            for(int c = 0; c < nc.length; c++) {
                num += new Integer(new String(new char[] { nc[nc.length-c-1] })) * Math.pow(2, c);
            }
        }

        return num;
    }
}
