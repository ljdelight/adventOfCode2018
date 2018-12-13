
import java.util.*;

public class Main {
    private static int nodeId = 1;
    public static class Node {
        public final int id;
        public final List<Node> children;
        public final List<Integer> metadata;

        public Node(int id) {
            this.id = id;
            this.metadata = new ArrayList<Integer>();
            this.children = new ArrayList<Node>();
        }

        public long value() {
            if (children.isEmpty()) {
                return metadata.stream().mapToInt(Integer::intValue).sum();
            }

            long v = 0;
            for (Integer index : metadata) {
                if (index == 0) {
                    continue;
                }
                if (index <= children.size()) {
                    v += children.get(index-1).value();
                }
            }
            return v;
        }

        @Override
        public String toString() {
            String ret = "id=" + id + " children=[";
            for (Node child : children) {
                ret += " " + child.id;
            }
            ret += " ]";

            ret += " metadata=[";
            for (Integer meta : metadata) {
                ret += " " + meta;
            }
            ret += " ]";

            return ret;
        }
    }

    private static Node readInput(Scanner s, Map<Integer, Node> graph, Node parent) {
        final int id = nodeId++;
        final int childrenCount = s.nextInt();
        final int metadataCount = s.nextInt();

        Node node = new Node(id);
        for (int i = 0; i < childrenCount; i++) {
            readInput(s, graph, node);
        }
        for (int i = 0; i < metadataCount; i++) {
            node.metadata.add(s.nextInt());
        }
        if (parent != null) {
            parent.children.add(node);
        }

        return node;
    }

    private static long sumOfMetadata(Node node) {
        long sum = 0; 
        for (Integer metadataEntry : node.metadata) {
            sum += metadataEntry;
        }
        for (Node child : node.children) {
            sum += sumOfMetadata(child);
        }
        return sum;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        Map<Integer, Node> graph = new HashMap<>();
        Node root = readInput(s, graph, null);
        long metadataSum = sumOfMetadata(root);
        System.out.println("Sum of metadata: " + metadataSum);
        System.out.println("Value of root: " + root.value());

        // System.out.println(root);


    }
}