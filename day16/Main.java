import java.util.*;

public class Main {

    private static final Map<Integer, OpCode> INSTRUCTIONID_TO_OPCODE = new HashMap<>();
    private static final Map<Integer, Integer> OPCODEID_TO_INSTRUCTIONID = new HashMap<>();

    enum OpCode {
        // Add
        addr, addi,
        // multiply
        mulr, muli,
        // bitwise AND
        banr, bani,
        // bitwise OR
        borr, bori,
        // assignment
        setr, seti,
        // greater than
        gtir, gtri, gtrr,
        // equality
        eqir, eqri, eqrr
    }

    private static int[] exec(Instruction instruction, int[] registers) {
        return exec(INSTRUCTIONID_TO_OPCODE.get(instruction.instructionId), registers, instruction.asArray());
    }

    private static int[] exec(OpCode code, int[] registers, int[] instruction) {
        int[] res = Arrays.copyOf(registers, registers.length);
        switch (code) {
        case addr:
            res[instruction[3]] = registers[instruction[1]] + registers[instruction[2]];
            break;
        case addi:
            res[instruction[3]] = registers[instruction[1]] + instruction[2];
            break;
        // multiply
        case mulr:
            res[instruction[3]] = registers[instruction[1]] * registers[instruction[2]];
            break;
        case muli:
            res[instruction[3]] = registers[instruction[1]] * instruction[2];
            break;
        // bitwise AND
        case banr:
            res[instruction[3]] = registers[instruction[1]] & registers[instruction[2]];
            break;
        case bani:
            res[instruction[3]] = registers[instruction[1]] & instruction[2];
            break;
        // bitwise OR
        case borr:
            res[instruction[3]] = registers[instruction[1]] | registers[instruction[2]];
            break;
        case bori:
            res[instruction[3]] = registers[instruction[1]] | instruction[2];
            break;
        // assignment
        case setr:
            res[instruction[3]] = registers[instruction[1]];
            break;
        case seti:
            res[instruction[3]] = instruction[1];
            break;
        // greater than
        case gtir:
            res[instruction[3]] = instruction[1] > registers[instruction[2]] ? 1 : 0;
            break;
        case gtri:
            res[instruction[3]] = registers[instruction[1]] > instruction[2] ? 1 : 0;
            break;
        case gtrr:
            res[instruction[3]] = registers[instruction[1]] > registers[instruction[2]] ? 1 : 0;
            break;
        // equality
        case eqir:
            res[instruction[3]] = instruction[1] == registers[instruction[2]] ? 1 : 0;
            break;
        case eqri:
            res[instruction[3]] = registers[instruction[1]] == instruction[2] ? 1 : 0;
            break;
        case eqrr:
            res[instruction[3]] = registers[instruction[1]] == registers[instruction[2]] ? 1 : 0;
            break;
        default:
            throw new RuntimeException("invalid opcode");
        }
        return res;
    }

    private static int behavesLike(Capture capture) {
        int matches = 0;
        for (OpCode opcode : OpCode.values()) {
            int[] resultingRegisters = exec(opcode, capture.before, capture.instruction.asArray());
            if (Arrays.equals(capture.after, resultingRegisters)) {
                // System.out.println("Found match using opcode " + opcode.name() + " (" +
                // capture.instruction.instructionId + ") " + capture);
                matches += 1;
            }
        }
        if (matches == 1) {
            // System.out.println("EXACT MATCH: " + capture);
        }
        return matches;
    }

    private static class Instruction {
        private final int instructionId;
        private final int inputA;
        private final int inputB;
        private final int outputRegister;

        public Instruction(int[] instruction) {
            this(instruction[0], instruction[1], instruction[2], instruction[3]);
        }

        public Instruction(int instructionId, int inputA, int inputB, int outputRegister) {
            this.instructionId = instructionId;
            this.inputA = inputA;
            this.inputB = inputB;
            this.outputRegister = outputRegister;
        }

        public int[] asArray() {
            int[] res = { instructionId, inputA, inputB, outputRegister };
            return res;
        }

        @Override
        public String toString() {
            return "[" + instructionId + " " + inputA + " " + inputB + " " + outputRegister + "]";
        }
    }

    private static class Capture {
        private final int[] before;
        private final int[] after;
        private final Instruction instruction;

        public Capture(int[] before, int[] after, Instruction instruction) {
            this.before = Arrays.copyOf(before, before.length);
            this.after = Arrays.copyOf(after, after.length);
            this.instruction = instruction;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("before=[");
            for (int i = 0; i < before.length; i++) {
                b.append(" ");
                b.append(before[i]);
            }
            b.append(" ] after=[");
            for (int i = 0; i < after.length; i++) {
                b.append(" ");
                b.append(after[i]);
            }
            b.append(" ] " + instruction);
            return b.toString();
        }

    }

    private static void resolveMappings(List<Capture> captures) {
        Set<Integer> pending = new HashSet<>();
        for (OpCode opcode : OpCode.values()) {
            pending.add(opcode.ordinal());
        }

        while (!pending.isEmpty()) {
            for (Capture capture : captures) {
                if (INSTRUCTIONID_TO_OPCODE.containsKey(capture.instruction.instructionId)) {
                    continue;
                }

                int matches = 0;
                OpCode lastMatch = null;
                for (OpCode opcode : OpCode.values()) {
                    if (OPCODEID_TO_INSTRUCTIONID.containsKey(opcode.ordinal())) {
                        continue;
                    }

                    int[] resultingRegisters = exec(opcode, capture.before, capture.instruction.asArray());
                    if (Arrays.equals(capture.after, resultingRegisters)) {
                        // System.out.println("Found match using opcode " + opcode.name() + " (" +
                        // capture.instruction.instructionId + ") " + capture);
                        lastMatch = opcode;
                        matches += 1;
                    }
                }
                if (matches == 1) {
                    // System.out.println("EXACT MATCH: " + capture);
                    OPCODEID_TO_INSTRUCTIONID.put(lastMatch.ordinal(), capture.instruction.instructionId);
                    INSTRUCTIONID_TO_OPCODE.put(capture.instruction.instructionId, lastMatch);
                    pending.remove(lastMatch.ordinal());
                }
            }
        }
    }

    public static void main(String[] args) {
        // INSTRUCTIONID_TO_OPCODE.put(2, OpCode.bori);
        // OPCODEID_TO_INSTRUCTIONID.put(OpCode.bori.ordinal(), 2);

        String REGEX = ":\\s+\\[|\\s+|]|,\\s*";
        Scanner s = new Scanner(System.in);
        List<Capture> captures = new ArrayList<>();
        List<Instruction> instructions = new ArrayList<>();

        {
            String line;
            String[] toks;
            int[] before = new int[4];
            int[] after = new int[4];
            int[] instruction = new int[4];

            while (s.hasNextLine()) {
                line = s.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                toks = line.split(REGEX);
                if ("Before".equals(toks[0])) {
                    for (int i = 0; i < 4; i++) {
                        before[i] = Integer.parseInt(toks[i + 1]);
                    }

                    line = s.nextLine().trim();
                    toks = line.split(REGEX);
                    for (int i = 0; i < 4; i++) {
                        instruction[i] = Integer.parseInt(toks[i]);
                    }

                    line = s.nextLine().trim();
                    toks = line.split(REGEX);
                    if (!"After".equals(toks[0])) {
                        throw new RuntimeException("expected 'after'");
                    }
                    for (int i = 0; i < 4; i++) {
                        after[i] = Integer.parseInt(toks[i + 1]);
                    }

                    captures.add(new Capture(before, after, new Instruction(instruction)));
                } else {
                    for (int i = 0; i < 4; i++) {
                        instruction[i] = Integer.parseInt(toks[i]);
                    }
                    instructions.add(new Instruction(instruction));
                }
            }
        }

        // Part 1
        int threeOrHigher = 0;
        for (Capture capture : captures) {
            if (behavesLike(capture) >= 3) {
                threeOrHigher += 1;
            }
        }
        System.out.println("P1: " + threeOrHigher);

        // Part 2
        resolveMappings(captures);
        // for (Map.Entry<Integer, OpCode> entry : INSTRUCTIONID_TO_OPCODE.entrySet()) {
        // System.out.println("instructionId=" + entry.getKey() + " code=" +
        // entry.getValue());
        // }

        int[] registers = new int[4];
        for (Instruction instruction : instructions) {
            registers = exec(instruction, registers);
        }
        System.out.println("P2: " + registers[0]);
    }
}
