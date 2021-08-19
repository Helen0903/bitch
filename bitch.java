import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class bitch {
    public static void main(String[] args) {
        Program program = new Program(new char[0]);
        
        for(int x = 0; x < args.length-1; x++) {
            if(args[x].substring(0,1).equals("-")) {
                program.useChar = args[x].contains("c");
                program.debug = args[x].contains("d");
            }
        }
        
        {
            File f = new File(args[args.length-1]);
            FileReader r;
            try { r = new FileReader(f); } catch(FileNotFoundException e) { System.err.println("Codefile not found."); return; }

            String code = "";
            try { for(int temp = -1; (temp = r.read()) != -1;) { code += (char) temp; } } catch(IOException e) { System.err.println("Invalid codefile."); return; }
            try { r.close(); } catch(IOException e) { System.err.println("Error whilst closing the file."); return; }

            program.program = code.toCharArray();
        }

        program.conclude();
    }
}

class Program {
    public char[] program;
    public int opCounter;

    public long currentValue;
    public long storage;

    public boolean useChar;
    public boolean debug;
    
    public int startPoint;

    public Program(char[] program) { this(program, 0, 0, 0, 0); }
    public Program(char[] program, long currentValue, long storage, int opCounter, int startPoint) {
        this.program = new char[program.length];
        for(int x = 0; x < this.program.length; x++) { this.program[x] = program[x]; }

        this.currentValue = currentValue;
        this.storage = storage;

        this.opCounter = opCounter;
        this.startPoint = startPoint;
    }

    public static final char[] fsChars = { '\\', '/', '>', '<', '.', '~' };
    public static final char[] conjChars = { '#', '|', '^', '&', ']', ':', ';', '[' };
    public static final char[] numberChars = { '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final Scanner scanner = new Scanner(System.in);
    public void conclude() { while(this.nextIteration()); }
    public boolean nextIteration() {
        String debug = "";
        if(this.debug) {
            String accumulator = "";
            {
                accumulator = Long.toBinaryString(this.currentValue);
                for(int x = accumulator.length(); x < 64; x++) { accumulator = "0" + accumulator; }

                char[] accum = accumulator.toCharArray();
                accumulator = "|Accumulator: ";

                for(int x = 0; x < 64; x++) { accumulator += accum[x] + ((x+1)%8==0?" ":"") + ((x+1)%32==0?"| ":""); }
                accumulator = accumulator.substring(0, 87);
            }

            String storage = "";
            {
                storage = Long.toBinaryString(this.storage);
                for(int x = storage.length(); x < 64; x++) { storage = "0" + storage; }

                char[] stor = storage.toCharArray();
                storage = "|Storage    : ";

                for(int x = 0; x < 64; x++) { storage += stor[x] + ((x+1)%8==0?" ":"") + ((x+1)%32==0?"| ":""); }
                storage = storage.substring(0, 87);
            }
            
            String loopMarker = "";
            {
                loopMarker = "|Loop Marker: ";
                for(int x = 0; x < this.startPoint; x++) { loopMarker += " "; }
                loopMarker += "V";
            }

            String fullCode = "|Full Code  : " + new String(this.program);

            String currentOp = "";
            {
                currentOp = "|Current    : ";
                for(int x = 0; x < this.opCounter; x++) { currentOp += " "; }
                currentOp += "^";
            }

            String dash = "";
            {
                int codeLength = fullCode.length();
                int optimalLength = codeLength > 88 ? codeLength : 88;

                for(int x = 87; x < optimalLength; x++) { accumulator += " "; } accumulator += "|";
                for(int x = 87; x < optimalLength; x++) { storage += " "; } storage += "|";
                for(int x = loopMarker.length(); x < optimalLength; x++) { loopMarker += " "; } loopMarker += "|";
                for(int x = codeLength; x < optimalLength; x++) { fullCode += " "; } fullCode += "|";
                for(int x = currentOp.length(); x < optimalLength; x++) { currentOp += " "; } currentOp += "|";

                for(int x = 0; x < optimalLength-1; x++) { dash += "-"; }
            }

            debug = "/" + dash + "\\" + "\n" +
                    accumulator + "\n" +
                    storage + "\n" +
                    loopMarker + "\n" +
                    fullCode + "\n" + 
                    currentOp + "\n" + 
                    "\\" + dash + "/";
        }

        char[] op = new char[] { '.' };
        
        {
            String nextOp = "";
            boolean nFlag = false;
            for(int x = opCounter; x < this.program.length; x++) {
                if(!nFlag && contains(conjChars, program[x])) { nextOp += program[x]; }
                else if(!nFlag && contains(fsChars, program[x])) { nextOp += program[x]; break; }
                else if(contains(numberChars, program[x])) { nFlag = true; nextOp += program[x]; }
                else { break; }
            }

            op = nextOp.toCharArray();
        }
        if(op.length == 0) return false;

        if(this.debug) System.out.println(debug);

        switch(op[0]) {
            case '\\': try { this.currentValue = this.useChar ? (long) scanner.findInLine(".").toCharArray()[0] : new Long(scanner.findInLine("[^\\s]+")); }
                       catch(NullPointerException|NumberFormatException e) { this.currentValue = -1; } this.storage = 0; break;
            case '/': if(this.useChar) System.out.print((char) this.currentValue); else System.out.println(this.currentValue); break;
            case '#': this.currentValue = evaluate(substring(op, 1), this.currentValue, this.storage); this.storage = 0; break;
            
            case '>': this.startPoint = this.opCounter; break;
            case '<': this.opCounter = this.startPoint; return true;
            case '.': return false;

            case ':': if(this.currentValue == 0) opCounter -= op.length - 1; break;
            case ';': if(this.currentValue != 0) opCounter -= op.length - 1; break;
            
            case '|': this.currentValue |= evaluate(substring(op, 1), this.currentValue, this.storage); break;
            case '^': this.currentValue ^= evaluate(substring(op, 1), this.currentValue, this.storage); break;
            case '&': this.currentValue &= evaluate(substring(op, 1), this.currentValue, this.storage); break;
            case '~': this.currentValue = ~this.currentValue; break;
            case ']': this.rightShift(evaluate(substring(op, 1), this.currentValue, this.storage)); break;
            case '[': this.leftShift(evaluate(substring(op, 1), this.currentValue, this.storage)); break;
        }

        this.opCounter += op.length;

        return true;
    }

    public static long evaluate(char[] program) { return evaluate(program, 0, 0); }
    public static long evaluate(char[] program, long startValue, long storage) {
        if(contains(numberChars, program[0])) return new Long(new String(program));

        Program p = new Program(program, startValue, storage, 0, 0);
        while(p.nextIteration());

        return p.currentValue;
    }
    
    private void rightShift(long n) {
        for(int x = 0; x < n; x++) {
            this.storage = (this.storage << 1) | (this.currentValue & 1);
            this.currentValue >>>= 1;
        }
    }

    private void leftShift(long n) {
        for(int x = 0; x < n; x++) {
            this.currentValue = (this.currentValue << 1) | (this.storage & 1);
            this.storage >>>= 1;
        }
    }

    private static boolean contains(char[] charset, char... chars) {
        boolean contains = true;
        
        outer:
        for(char c : chars) {
            for(char d : charset) { if(c == d) continue outer; }
            contains = false;
        }
        
        return contains;
    }

    private static char[] substring(char[] array, int start) { return substring(array, start, array.length); }
    private static char[] substring(char[] array, int start, int end) {
        char[] output = new char[end-start];

        for(int x = start; x < end; x++) output[x-start] = array[x];

        return output;
    }
}
