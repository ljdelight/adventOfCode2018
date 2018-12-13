
import java.util.*;
import java.text.MessageFormat;
public class Main {
    private static class Node {
        public int x;
        public int y;
        public final int vx;
        public final int vy;

        public Node(int x, int y, int vx, int vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        public void tick() {
            x += vx;
            y += vy;
        }
    }

    private static class Display {
        private int xMin = Integer.MAX_VALUE;
        private int xMax = Integer.MIN_VALUE;
        private int yMin = Integer.MAX_VALUE;
        private int yMax = Integer.MIN_VALUE;
        private List<Node> nodes = new ArrayList<Node>();

        public Display() {

        }

        private void display() {
            System.out.println(MessageFormat.format("BOUNDING RECTANGLE: xMin={0} xMax={1}   yMin={2} yMax={3}", xMin, xMax, yMin, yMax));

            int scale = 2000;
            boolean[][] matrix = new boolean[10000][10000];
            for (Node n : nodes) {
                matrix[n.x + scale][n.y + scale] = true;
            }

            StringBuilder buff = new StringBuilder();
            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    if (matrix[x + scale][y + scale]) {
                        buff.append("#");
                    } else {
                        buff.append(".");
                    }
                }
                System.out.println(buff.toString());
                buff.setLength(0);
            }
        }

        public void addNode(Node node) {
            if (node.x < xMin) {
                xMin = node.x;
            }
            if (node.x > xMax) {
                xMax = node.x;
            }

            if (node.y < yMin) {
                yMin = node.y;
            }
            if (node.y > yMax) {
                yMax = node.y;
            }
            nodes.add(node);
        }

        public void tick() {
            xMin = Integer.MAX_VALUE;
            xMax = Integer.MIN_VALUE;
            yMin = Integer.MAX_VALUE;
            yMax = Integer.MIN_VALUE;
            for (Node node : nodes) {
                node.tick();

                if (node.x < xMin) {
                    xMin = node.x;
                }
                if (node.x > xMax) {
                    xMax = node.x;
                }
    
                if (node.y < yMin) {
                    yMin = node.y;
                }
                if (node.y > yMax) {
                    yMax = node.y;
                }
            }
        }

        public long getBoundingRectangleSize() {
            //System.out.println(MessageFormat.format("BOUNDING RECTANGLE: xMin={0} xMax={1}   yMin={2} yMax={3}", xMin, xMax, yMin, yMax));
            return ((long)Math.abs(xMax - xMin)) * Math.abs(yMax - yMin);
        }
    }

    private static Display readInput(List<String> lines) {
        Display display = new Display();
        for (String line : lines) {
            String[] split = line.split("<\\s?|, |>");
            int x = Integer.parseInt(split[1].trim());
            int y = Integer.parseInt(split[2].trim());
            int vx = Integer.parseInt(split[4].trim());
            int vy = Integer.parseInt(split[5].trim());
            // System.out.println(MessageFormat.format("position={0},{1} velocity={2},{3}", x, y, vx, vy));
            display.addNode(new Node(y, x, vy, vx));
        }
        return display;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        List<String> lines = new ArrayList<>();

        while (s.hasNextLine()) {
            String line = s.nextLine();
            lines.add(line);
        }

        Display display = readInput(lines);

        int tickMin = 0;
        long tickMinRect = display.getBoundingRectangleSize();
        for (int tick = 0; tick < 10634; tick++) {

            display.tick();
            if (display.getBoundingRectangleSize() < tickMinRect) {
                tickMin = tick;
                tickMinRect = display.getBoundingRectangleSize();
            }
            
            // display.tickAll();
            // System.out.println("\n\n\n");
        }

        System.out.println("Found minimum rectangle at tick(seconds)=" + (1+tickMin) + " rect=" + tickMinRect);
        
        display = readInput(lines);
        for (int tick = 0; tick <= tickMin; tick++) {
            display.tick();
        }

        display.display();

    }
}
