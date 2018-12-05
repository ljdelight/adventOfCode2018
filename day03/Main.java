
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static int MAP_SIZE = 1000;

    public static void main(String[] args) {
        // Example input:
        // #1 @ 100,366: 24x27
        // #2 @ 726,271: 11x15
        Scanner s = new Scanner(System.in);
        s.useDelimiter("#| @ |,|: |x|\\n");
        List<FabricBounds> fabricBounds = readInput(s);

        int part1 = countOverlapping(fabricBounds);
        System.out.println("P1: Squares within two or more claims: " + part1);

        FabricBounds noOverlap = getNoConflictBound(fabricBounds);
        System.out.println("P2: No conflicts for " + noOverlap);
    }

    private static int countOverlapping(List<FabricBounds> fabricBounds) {
        int[][] map = new int[MAP_SIZE][MAP_SIZE];
        for (FabricBounds f : fabricBounds) {
            for (int i = f.x_start; i < f.x_end; i++) {
                for (int j = f.y_start; j < f.y_end; j++) {
                    map[i][j] += 1;
                }
            }
        }

        int count = 0;
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (map[i][j] > 1) {
                    count += 1;
                }
            }
        }
        return count;

    }

    private static FabricBounds getNoConflictBound(List<FabricBounds> fabricBounds) {
        for (int i = 0; i < fabricBounds.size(); i++) {
            boolean hasConflicts = false;
            for (int j = 0; j < fabricBounds.size(); j++) {
                if (i != j && fabricBounds.get(i).overlaps(fabricBounds.get(j))) {
                    hasConflicts = true;
                }
            }
            if (!hasConflicts) {
                return fabricBounds.get(i);
            }
        }
        throw new RuntimeException("there should have been a fabric bound without conflict");
    }

    private static List<FabricBounds> readInput(Scanner s) {
        List<FabricBounds> fabricBounds = new ArrayList<>();
        while (s.hasNext()) {
            int id = s.nextInt();
            int lhsAdj = s.nextInt();
            int topAdj = s.nextInt();
            int w = s.nextInt();
            int h = s.nextInt();
            if (s.hasNext()) {
                s.next();
            }
            FabricBounds f = new FabricBounds(id, lhsAdj, topAdj, w, h);
            fabricBounds.add(f);
            // System.out.println(f);
        }
        return fabricBounds;
    }

    private static class FabricBounds {
        public int id;
        public int x_start;
        public int x_end;
        public int y_start;
        public int y_end;

        public FabricBounds(int id, int lhsAdj, int topAdj, int w, int h) {
            this.id = id;
            this.x_start = topAdj;
            this.x_end = x_start + h;
            this.y_start = lhsAdj;
            this.y_end = y_start + w;
        }

        public boolean overlaps(FabricBounds other) {
            if (this.x_start < other.x_end && other.x_start < this.x_end) {
                if (this.y_start < other.y_end && other.y_start < this.y_end) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "id=" + id + " lhsAdj=" + y_start + " topAdj=" + x_start;
        }
    }
}
