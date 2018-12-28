
import java.util.*;

public class Main {
    private static final char S_OPEN = '.';
    private static final char S_TREES = '|';
    private static final char S_LUMBER = '#';
    private static final int ITERATIONS = 1_000_000_000;

    // Up, Down, Left, Right, Top-Left, Top-Right, Bottom-Left, Bottom-Right
    private static final int[][] DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 },
            { 1, -1 }, { 1, 1 } };

    private static class PointStats {
        final int x;
        final int y;
        final char type;
        final int open;
        final int trees;
        final int lumber;

        public PointStats(char[][] map, final int x, final int y) {
            this.x = x;
            this.y = y;
            this.type = map[x][y];

            int open = 0;
            int trees = 0;
            int lumber = 0;
            for (int[] dir : DIRECTIONS) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (!(nx >= 0 && nx < map.length) || !(ny >= 0 && ny < map[0].length)) {
                    continue;
                }

                switch (map[nx][ny]) {
                case S_OPEN:
                    open += 1;
                    break;
                case S_TREES:
                    trees += 1;
                    break;
                case S_LUMBER:
                    lumber += 1;
                    break;
                default:
                    throw new RuntimeException("invalid type on the map");
                }
            }

            this.open = open;
            this.trees = trees;
            this.lumber = lumber;
        }

        @Override
        public String toString() {
            return "x=" + x + " y=" + y + " open=" + open + " trees=" + trees + " lumber=" + lumber;
        }
    }

    private static void printMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    private static String mapToString(char[][] map) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                b.append(map[i][j]);
            }
        }
        return b.toString();
    }

    private static char[][] readInput(Scanner s) {
        int size = -1;
        char[][] map = null;
        int row = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (size == -1) {
                size = line.length();
                map = new char[size][size];
            }
            for (int i = 0; i < line.length(); i++) {
                map[row][i] = line.charAt(i);
            }
            row += 1;
        }

        return map;
    }

    private static char[][] getMapAfterTick(char[][] map) {
        boolean atLeastOneConversion = false;
        char[][] updated = new char[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                PointStats stats = new PointStats(map, i, j);
                if (stats.type == S_OPEN && stats.trees >= 3) {
                    atLeastOneConversion = true;
                    updated[i][j] = S_TREES;
                } else if (stats.type == S_TREES && stats.lumber >= 3) {
                    atLeastOneConversion = true;
                    updated[i][j] = S_LUMBER;
                } else if (stats.type == S_LUMBER) {
                    if (stats.lumber >= 1 && stats.trees >= 1) {
                        // remain a lumberyard
                        updated[i][j] = stats.type;
                    } else {
                        atLeastOneConversion = true;
                        updated[i][j] = S_OPEN;
                    }
                } else {
                    updated[i][j] = stats.type;
                }
            }
        }
        return updated;
    }

    private static int getScore(char[][] map) {
        int trees = 0;
        int lumber = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == S_TREES) {
                    trees += 1;
                } else if (map[i][j] == S_LUMBER) {
                    lumber += 1;
                }
            }
        }

        return trees * lumber;
    }

    public static void main(String[] args) {
        final Scanner s = new Scanner(System.in);
        char[][] map = readInput(s);

        // printMap(map);
        // for (int i = 0; i < map.length; i++) {
        // for (int j = 0; j < map[0].length; j++) {
        // PointStats stats = new PointStats(map, i, j);
        // System.out.println(stats);
        // }
        // }

        Map<String, Integer> cache = new HashMap<>();

        for (int i = 0; i < ITERATIONS; i++) {
            map = getMapAfterTick(map);
            if (i == 9) {
                System.out.println("P1: SCORE: " + getScore(map));
            }
            String key = mapToString(map);
            if (cache.containsKey(key)) {
                System.out.println("Iteration " + i + " matches iteration " + cache.get(key) + " (cycle length " + (i - cache.get(key)) + ")");
                int skip = (ITERATIONS - i) / (i - cache.get(key));
                i += skip * (i - cache.get(key));
            }
            cache.put(key, i);
            // System.out.println("\nUpdated map:");
            // printMap(map);

        }
        System.out.println("P2: SCORE: " + getScore(map));
    }
}
