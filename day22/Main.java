
import java.util.*;

public class Main {
    private static final Map<String, Integer> T = new HashMap<>();
    private static final int[][] DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    enum RegionType {
        UNKNOWN, ROCKY, WET, NARROW
    }

    public static class Node implements Comparable {
        public int x;
        public int y;
        public int t;
        public int d;

        public Node(final int x, final int y, final int t, final int d) {
            this.x = x;
            this.y = y;
            // Nothing, Torch, Climbing Gear
            this.t = t;
            this.d = d;
        }

        @Override
        public int compareTo(Object o) {
            if ((o != null) && (o instanceof Node)) {
                return Integer.compare(this.d, ((Node) o).d);
            }
            return -1;
        }
    }

    private static RegionType getErosionType(int x, int y, int dstX, int dstY, int caveDepth) {
        switch (getErosionLevel(x, y, dstX, dstY, caveDepth) % 3) {
        case 0:
            return RegionType.ROCKY;
        case 1:
            return RegionType.WET;
        case 2:
            return RegionType.NARROW;
        }
        throw new RuntimeException("impossible state");
    }

    private static int getErosionLevel(int x, int y, int dstX, int dstY, int caveDepth) {
        return (getGeologicIndex(x, y, dstX, dstY, caveDepth) + caveDepth) % 20183;
    }

    private static int getGeologicIndex(int x, int y, int dstX, int dstY, int caveDepth) {
        String key = getK(x, y);
        if (T.containsKey(key)) {
            return T.get(key);
        }

        int ret;
        if (x == 0 && y == 0 || x == dstX && y == dstY) {
            ret = 0;
        } else if (y == 0) {
            ret = x * 16807;
        } else if (x == 0) {
            ret = y * 48271;
        } else {
            ret = ((getGeologicIndex(x - 1, y, dstX, dstY, caveDepth) + caveDepth) % 20183)
                    * ((getGeologicIndex(x, y - 1, dstX, dstY, caveDepth) + caveDepth) % 20183);
        }

        T.put(key, ret);
        return ret;
    }

    private static RegionType[][] buildMap(int dstX, int dstY, int caveDepth) {
        RegionType[][] res = new RegionType[2 * dstX + 1][2 * dstY + 1];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = getErosionType(i, j, dstX, dstY, caveDepth);
            }
        }
        return res;
    }

    private static int getRiskLevel(RegionType[][] map, int dstX, int dstY) {
        int risk = 0;
        for (int i = 0; i <= dstX; i++) {
            for (int j = 0; j <= dstY; j++) {
                if (map[i][j] == RegionType.ROCKY) {
                    risk += 0;
                } else if (map[i][j] == RegionType.WET) {
                    risk += 1;
                } else if (map[i][j] == RegionType.NARROW) {
                    risk += 2;
                }
            }
        }
        return risk;
    }

    public static void main(String args[]) {
        // test();
        int depth = 7305;
        int dstX = 13;
        int dstY = 734;

        // int depth = 510;
        // int dstX = 10;
        // int dstY = 10;
        RegionType[][] map = buildMap(dstX, dstY, depth);
        //printMap(map);
        System.out.println("RISK " + getRiskLevel(map, dstX, dstY));

        // x, y, region type -> distance
        int[][][] dist = new int[2 * dstX][2 * dstY][3];
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[0].length; j++) {
                for (int k = 0; k < dist[0][0].length; k++) {
                    dist[i][j][k] = Integer.MAX_VALUE - 1;
                }
            }
        }

        Queue<Node> q = new PriorityQueue<>();
        // Start at 0,0 with the torch (1) and distance 0.
        dist[0][0][1] = 0;
        q.add(new Node(0, 0, 1, 0));

        while (!q.isEmpty()) {
            Node current = q.remove();
            if (current.d > dist[current.x][current.y][current.t]) {
                continue;
            }
            for (int tools = 0; tools < 3; tools++) {
                if (tools == current.t) {
                    continue;
                }
                int newDist = current.d + 7;
                if (dist[current.x][current.y][tools] > newDist) {
                    dist[current.x][current.y][tools] = newDist;
                    q.add(new Node(current.x, current.y, tools, newDist));
                }
            }

            // check neighbors
            for (int[] dir : DIRECTIONS) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                if (newX < 0 || newY < 0 || newX >= dist.length || newY >= dist[0].length) {
                    continue;
                }
                for (int tools = 0; tools < 3; tools++) {
                    // TODO: this is hacked and nasty
                    if (tools == map[newX][newY].ordinal() - 1 || tools == map[current.x][current.y].ordinal() - 1) {
                        continue;
                    }
                    // move a step and account for changing tools
                    int newDist = current.d + 1 + (tools == current.t ? 0 : 7);
                    if (dist[newX][newY][tools] > newDist) {
                        dist[newX][newY][tools] = newDist;
                        q.add(new Node(newX, newY, tools, newDist));
                    }
                }
            }
        }
        System.out.println("MIN: " + dist[dstX][dstY][1]);
    }

    private static void printMap(RegionType[][] map) {
        for (int j = 0; j < map[0].length; j++) {
            for (int i = 0; i < map.length; i++) {
                char c = 'F';
                if (map[i][j] == RegionType.ROCKY) {
                    c = '.';
                } else if (map[i][j] == RegionType.WET) {
                    c = '=';
                } else if (map[i][j] == RegionType.NARROW) {
                    c = '|';
                } else if (map[i][j] == RegionType.UNKNOWN) {
                    c = '?';
                } else {
                    throw new RuntimeException("impossible state");
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static String getK(int x, int y) {
        return x + "." + y;
    }

    private static void test() {
        if (getGeologicIndex(0, 0, 10, 10, 510) != 0) {
            throw new RuntimeException("invalid");
        }
        if (getGeologicIndex(5, 5, 5, 5, 510) != 0) {
            throw new RuntimeException("invalid");
        }
        if (getGeologicIndex(5, 0, 1, 1, 510) != 5 * 16807) {
            throw new RuntimeException("invalid");
        }
        if (getGeologicIndex(0, 8, 1, 1, 510) != 8 * 48271) {
            throw new RuntimeException("invalid");
        }

        if (getErosionLevel(0, 0, 10, 10, 510) != 510) {
            throw new RuntimeException("invalid");
        }
        if (getErosionType(0, 0, 10, 10, 510) != RegionType.ROCKY) {
            throw new RuntimeException("invalid");
        }

        if (getErosionLevel(1, 0, 10, 10, 510) != 17317) {
            throw new RuntimeException("invalid");
        }
        if (getErosionType(1, 0, 10, 10, 510) != RegionType.WET) {
            throw new RuntimeException("invalid");
        }

        if (getErosionLevel(0, 1, 10, 10, 510) != 8415) {
            throw new RuntimeException("invalid");
        }
        if (getErosionType(0, 1, 10, 10, 510) != RegionType.ROCKY) {
            throw new RuntimeException("invalid");
        }

        if (getErosionLevel(1, 1, 10, 10, 510) != 1805) {
            throw new RuntimeException("invalid");
        }
        if (getErosionType(1, 1, 10, 10, 510) != RegionType.NARROW) {
            throw new RuntimeException("invalid");
        }

    }
}
