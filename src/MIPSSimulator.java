import java.util.HashMap;
import java.util.Map;

public class MIPSSimulator {

    private static final int MEMORY_SIZE = 1024;
    private static final int REGISTER_COUNT = 32;

    private int[] memory = new int[MEMORY_SIZE];
    private int[] registers = new int[REGISTER_COUNT];
    private int pc = 0;

    private static final Map<String, Integer> registerMap = new HashMap<>();

    static {
        registerMap.put("$zero", 0);    
        registerMap.put("$t0", 8);
        registerMap.put("$t1", 9);
        registerMap.put("$t2", 10);
        registerMap.put("$t3", 11);
        registerMap.put("$t4", 12);
        registerMap.put("$t5", 13);
        registerMap.put("$t6", 14);
        registerMap.put("$t7", 15); 
        registerMap.put("$s0", 16);
        registerMap.put("$s1", 17);
        registerMap.put("$s2", 18);
        registerMap.put("$s3", 19);
    }

    public void execute(String[] instructions) {
        while (pc < instructions.length) {
            String[] parts = instructions[pc].trim().split("\\s+");
            //String[] parts = instructions[pc].split(" ");
            String opcode = parts[0];
            switch (opcode) {
                case "add":
                    executeRType(parts, "add"); break;
                case "sub":
                    executeRType(parts, "sub"); break;
                case "lw":
                    executeIType(parts, "lw"); break;
                case "sw": 
                    executeIType(parts, "sw"); break;
                case "beq":
                    executeIType(parts, "beq"); break;
                case "li":
                    executeLIType(parts); break;
                default:
                     throw new IllegalArgumentException("Unsupported instruction: " + opcode);
            }
            pc++;
        }
    }

    private void executeLIType (String[] parts) {
        int rd = registerMap.get(parts[1]);
        registers[rd] = Integer.parseInt(parts[2]);
    }
    private void executeRType(String[] parts, String type) {
        int rd = registerMap.get(parts[1]);
        int rs = registerMap.get(parts[2]);
        int rt = registerMap.get(parts[3]);
        if (type.equals("add")) {
            registers[rd] = registers[rs] + registers[rt];
        } else if (type.equals("sub")) {
            registers[rd] = registers[rs] - registers[rt];
        }
    }
    private void executeIType(String[] parts, String type) {
        int rt = registerMap.get(parts  [1]);
        int offset = 0;
        int rs = 0;

        if (type.equals("lw") || type.equals("sw")) {
            offset = Integer.parseInt(parts[2].substring(0, parts[2].indexOf('(')));
            rs = registerMap.get(parts[2].substring(parts[2].indexOf('(') + 1, parts[2].indexOf(')')));
        }
        else {
            offset = Integer.parseInt(parts[3]);
            rs = registerMap.get(parts[2]);
        }
        if (type.equals("lw")) {
            registers[rt] = memory[registers[rs] + offset];
        } else if (type.equals("sw")) {
            memory[registers[rs] + offset] = registers[rt];
        } else if (type.equals("beq")) {
            if (registers[rt] == registers[rs]) {
                pc += offset;
            }
        }
    }

    public int[] getMemory() {
        return memory;
    }

    public int[] getRegisters() {
        return registers;
    }

    public int getPC() {
        return pc;
    }

    public void reset() {
        memory = new int[MEMORY_SIZE];
        registers = new int[REGISTER_COUNT];
        pc = 0;
    }

    public static void main(String[] args) {
        MIPSSimulator simulator = new MIPSSimulator();
        String[] instructions = {
                "add $t0 $t1 $t2",
                "sub $t3 $t4 $t5",
                "lw $t0 0($t1)",
                "li $t0 15",
                "sw $t0 4($t1)",
                "beq $t0 $t1 10",
                "add $t0 $t0 $t0"
        };
        simulator.execute(instructions);
        System.out.println("Program counter: " + simulator.getPC());

        System.out.println("Register layout: ");
        int[] registers = simulator.getRegisters();
        for (int register = 0; register < registers.length; register++)
            System.out.println("#" + register +  ": " + registers[register]);

        System.out.println("Memory layout: ");
        int[] memory = simulator.getMemory();
        int memorySize = 12; 
        for (int memoryCell = 0; memoryCell < memorySize; memoryCell++) 
            System.out.println("#" + memoryCell + ": " + memory[memoryCell]);
    }
}