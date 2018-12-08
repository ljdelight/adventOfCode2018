
import java.text.MessageFormat;
import java.util.*;

public class Main {
    private static int UNASSIGNED = 0;
    private static int NODEID_TIED = -1;
    private static int DIST_UNBOUNDED = -2;
    private static int DIST_ORIGINAL = -3;

    private static int manhattanDistance(int x1, int y1, int x2, int y2) {
        int res = Math.abs(x1 - x2) + Math.abs(y1 - y2);
        // System.out.println(MessageFormat.format("mhd(({0}, {1}), ({2}, {3})", x1, y1, x2, y2));
        return res;
    }

    private static class Point {
        public final Integer id;
        public final int x;
        public final int y;
        private int value;

        public Point(Integer id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.value = 0;
        }

        public int getValue() {
            return value;
        }

        public void increaseValue() {
            value += 1;
        }

        public void decreaseValue() {
            if (value == 0) {
                throw new RuntimeException("value cannot be negative");
            }
            value -= 1;
        }

        public int getManhattanDistance(Point q) {
            return manhattanDistance(this.x, this.y, q.x, q.y);
        }

        @Override
        public String toString() {
            return MessageFormat.format("P[id={0} x={1} y={2} value={3}]", id, x, y, value);
        }
    }

    private static List<Point> getNeighbors(final Point root, int[][] distances, int[][] ownerIds, int xMin, int xMax, int yMin, int yMax) {
        final List<Point> ret = new ArrayList<>();

        // up
        ret.add(new Point(root.id, root.x - 1, root.y));
        // down
        ret.add(new Point(root.id, root.x + 1, root.y));
        // left
        ret.add(new Point(root.id, root.x, root.y - 1));
        // right
        ret.add(new Point(root.id, root.x, root.y + 1));

        Iterator<Point> it = ret.iterator();
        while (it.hasNext()) {
            Point p = it.next();
            // System.out.println(" Inspecting neighbor for use... " + p);
            // Ignore if out of the bounding box
            if (p.x < xMin || p.x > xMax || p.y < yMin || p.y > yMax) {
                // System.out.println("  Neighbor is out of bounds");
                distances[p.x][p.y] = DIST_UNBOUNDED;
                it.remove();
            } else if (ownerIds[p.x][p.y] == p.id || distances[p.x][p.y] == DIST_ORIGINAL || distances[p.x][p.y] == DIST_UNBOUNDED
                    || ownerIds[p.x][p.y] == NODEID_TIED) {
                // System.out.println("  Neighbor is orig, unbounded, or tied");
                it.remove();
            }
        }
        // System.out.println(" Queued up " + ret.size() + " neighbors");
        return ret;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        s.useDelimiter("\\s+|,");

        Map<Integer, Point> originals = new HashMap<>();

        // Start ID at 1 to avoid matrix defaults of '0'
        for (int id = 1; s.hasNextInt(); id++) {
            int x = s.nextInt();
            s.next();
            int y = s.nextInt();

            Point p = new Point(id, x, y);
            originals.put(id, p);
        }

        // Find the bounding box
        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMin = xMin;
        int yMax = xMax;
        for (Point p : originals.values()) {
            int x = p.x;
            int y = p.y;
            if (x < xMin) {
                xMin = x;
            } else if (x > xMax) {
                xMax = x;
            }
            if (y < yMin) {
                yMin = y;
            } else if (y > yMax) {
                yMax = y;
            }
        }
        System.out.println(
                MessageFormat.format("Bounding box: xMin={0} xMax={1} yMin={2} yMax={3}", xMin, xMax, yMin, yMax));

        // Setup the queue. BFS rooted at each origin node.
        Queue<Point> q = new ArrayDeque<>();
        for (Point p : originals.values()) {
            q.add(p);
        }

        // matrix with the NODE ID as the value, 0 is unassigned
        int[][] ownerIds = new int[1000][1000];
        int[][] distances = new int[ownerIds.length][ownerIds[0].length];

        for (Point p : originals.values()) {
            ownerIds[p.x][p.y] = p.id;
            distances[p.x][p.y] = DIST_ORIGINAL;
        }

        while (!q.isEmpty()) {
            Point p = q.remove();
            // System.out.println("PROCESSING " + p);
            if (ownerIds[p.x][p.y] == NODEID_TIED || distances[p.x][p.y] == DIST_UNBOUNDED) {
                // System.out.println(" CONTESTED or UNBOUNDED or owned by same-id");
                continue;
            } else if (p.id == ownerIds[p.x][p.y] && distances[p.x][p.y] != DIST_ORIGINAL) {
                // The queued node doesn't need to re-obtain a same-id node. This happens if a neighbor was available and it was in the queue.
                // System.out.println(" Node is already owned by the same id");
                continue;
            }

            if (ownerIds[p.x][p.y] == p.id && distances[p.x][p.y] == DIST_ORIGINAL) {
                // First time for this origin node!
                // System.out.println("First time for origin node " + p);
                originals.get(p.id).increaseValue();
            } else if (ownerIds[p.x][p.y] == 0 && distances[p.x][p.y] == 0) {
                // System.out.println("Unclaimed location " + p);
                ownerIds[p.x][p.y] = p.id;
                distances[p.x][p.y] = p.getManhattanDistance(originals.get(p.id));
                originals.get(p.id).increaseValue();
            } else if (p.getManhattanDistance(originals.get(p.id)) == distances[p.x][p.y]) {
                // System.out.println("Location " + p + " is CONTESTED between " + originals.get(p.id) + " and "
                //         + originals.get(ownerIds[p.x][p.y]));
                originals.get(ownerIds[p.x][p.y]).decreaseValue();
                ownerIds[p.x][p.y] = NODEID_TIED;
                continue;
            } else if (p.getManhattanDistance(originals.get(p.id)) > distances[p.x][p.y]) {
                // System.out.println("Location " + p + " has NO change owner=" + originals.get(ownerIds[p.x][p.y])
                //         + " verse=" + originals.get(p.id));
                continue;
            } else if (p.getManhattanDistance(originals.get(p.id)) < distances[p.x][p.y]) {
                // System.out.println("Location " + p + " has TRANSITIONED OWNERS from "
                //         + originals.get(ownerIds[p.x][p.y]) + " to " + originals.get(p.id));
                originals.get(p.id).increaseValue();
                originals.get(ownerIds[p.x][p.y]).decreaseValue();
                ownerIds[p.x][p.y] = p.id;
            } else {
                throw new RuntimeException("Illegal state");
            }

            q.addAll(getNeighbors(p, distances, ownerIds, xMin, xMax, yMin, yMax));
        }

        Point maxP = null;
        for (Point p : originals.values()) {
            // System.out.println(p);
            if (maxP == null || distances[p.x][p.y] != DIST_UNBOUNDED && p.getValue() > maxP.getValue()) {
                maxP = p;
            }
        }

        System.out.println("P1: Point responsible for the largest sprawl is " + maxP);

        // for (int i = xMin; i <= xMax; i++) {
        //     for (int j = yMin; j <= yMax; j++) {
        //         System.out.printf("%2d ", ownerIds[i][j]);
        //     }
        //     System.out.println();
        // }

        int regionSize = 0;
        for (int i = xMin; i <= xMax; i++) {
            for (int j = yMin; j <= yMax; j++) {
                if (isWithinRegion(i, j, originals.values(), 10000)) {
                    regionSize += 1;
                }
            }
        }

        System.out.println("P2: Region size is " + regionSize);

    }



    private static boolean isWithinRegion(int x, int y, Collection<Point> originals, int max) {
        int totalDistance = 0;
        for (Point src : originals) {
            totalDistance += manhattanDistance(x, y, src.x, src.y);
            if (totalDistance >= max) {
                return false;
            }
        }
        return true;
    }
}
