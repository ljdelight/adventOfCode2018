import java.util.*;
import java.util.regex.Matcher;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.text.MessageFormat;
import java.util.stream.Collectors;



public class Main {

    private static class Node {
        public final String id;
        public int ttl;
        public final HashSet<String> out_neighbors;
        public final HashSet<String> in_neighbors;

        public Node(String id) {
            this.id = id;
            this.ttl = 0;
            this.out_neighbors = new HashSet<>();
            this.in_neighbors = new HashSet<>();
        }
        @Override
        public String toString() {
            return MessageFormat.format("[id={0} ttl={1} in=[{2}] out=[{3}]]", id, ttl, String.join(" ", in_neighbors), String.join(" ", out_neighbors));
        }
    }

    private static void graphAddEdge(Map<String, Node> graph, String src, String dst) {
        src = src.trim();
        dst = dst.trim();
        if (!graph.containsKey(src)) {
            graph.put(src, new Node(src));
        }
        if (!graph.containsKey(dst)) {
            graph.put(dst, new Node(dst));
        }
        graph.get(src).out_neighbors.add(dst);
        graph.get(dst).in_neighbors.add(src);
    }


    private static String getNextFreeNode(Map<String, Node> graph, Queue<String> queue, Set<String> visited, Set<String> pending, Set<String> complete) {
        System.out.println(" Queued:  " + String.join(" ", queue));
        System.out.println(" Visited: " + String.join(" ", visited));
        System.out.println(" Pending: " + String.join(" ", pending));
        Iterator<String> it = pending.iterator();
        while (it.hasNext()) {
            String pend = it.next();
            //
            // if (visited.containsAll(graph.get(pend).in_neighbors) && graph.get(pend).in_neighbors.stream().allMatch(n -> graph.get(n).ttl <= 0)) {
            if (graph.get(pend).in_neighbors.stream().allMatch(n -> visited.contains(n)&& complete.contains(n)) ) {
                it.remove();
                if (!queue.contains(pend)) {
                    queue.add(pend);
                }
                System.out.println(" Back to queue " + pend);
            }
        }


        if (queue.isEmpty()) {
            throw new RuntimeException("cannot get nodes from an empty queue");
        }

        String k = queue.remove();
        System.out.println("Traversing node " + k);

        // sanity checks
        if (pending.contains(k)) {
            throw new RuntimeException("key cannot be in pending state");
        }
        if (visited.contains(k)) {
            throw new RuntimeException("key cannot be in visited state");
        }
        if (!visited.containsAll(graph.get(k).in_neighbors)) {
            throw new RuntimeException("cannot legally visit node");
        }

        visited.add(k);
        System.out.println("Visited " + graph.get(k));
        for (String neighbor : graph.get(k).out_neighbors) {
            System.out.println(" inspecting neighbor " + graph.get(neighbor));
            if (!visited.contains(neighbor)) {
                System.out.println(" add pending " + graph.get(neighbor));
                pending.add(neighbor);
            }
        }


        return k;
    }

    private static String p1(Map<String, Node> graph) {
        List<String> solution = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> pending = new HashSet<>();
        Set<String> complete = new HashSet<>();
        Queue<String> queue = new PriorityQueue<>();
        for (String root : graph.keySet()) {
            Node rootNode = graph.get(root);
            if (rootNode.in_neighbors.isEmpty()) {
                queue.add(root);
            }
        }
        while (!queue.isEmpty() || !pending.isEmpty()) {
            String r = getNextFreeNode(graph, queue, visited, pending, complete);
            solution.add(r);
            complete.add(r);
        }
        return String.join("", solution);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Pattern pattern = Pattern.compile("Step (.*) must be finished before step (.*) can begin.");
        Map<String, Node> graph = new HashMap<>();

        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            Matcher matcher = pattern.matcher(line);
            matcher.find();

            String src = matcher.group(1);
            String dst = matcher.group(2);
            graphAddEdge(graph, src, dst);
        }

        String p1solution = p1(graph);
        System.out.println("P1: visited all nodes: " + p1solution);

        // System.exit(1);
        // update graph for P2.
        // for (Map.Entry<String, Node> entry : graph.entrySet()) {
        //     entry.getValue().ttl = entry.getKey().charAt(0) - 'A' + 1;
        // }
        
        List<String> solution = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> pending = new HashSet<>();
        Set<String> complete = new HashSet<>();
        Queue<String> queue = new PriorityQueue<>();
        for (String root : graph.keySet()) {
            Node rootNode = graph.get(root);
            if (rootNode.in_neighbors.isEmpty()) {
                // System.out.println("Queued " + root);
                queue.add(root);
            }
        }

        Map<String, Integer> activeJobs = new LinkedHashMap<String, Integer>();
        final int nWorkers = 5;
        final int nWorkTimeExtra = 60;

        int totalTime = 0;
        int[] jobInProgressTimeRemaining = new int[nWorkers];

        // while (!activeJobs.isEmpty() || !queue.isEmpty()) {
        while (true) {
            System.out.println(" Active:  " + activeJobs);
            System.out.println(" Queued:  " + String.join(" ", queue));
            System.out.println(" Visited: " + String.join(" ", visited));
            System.out.println(" Pending: " + String.join(" ", pending));
            boolean startedNewJob = false;
            while (activeJobs.size() < nWorkers) {
                try {
                    String key = getNextFreeNode(graph, queue, visited, pending, complete);
                    graph.get(key).ttl = nWorkTimeExtra + key.charAt(0) - 'A' + 1;
                    Integer value = graph.get(key).ttl;
                    System.out.println("T=" + totalTime + " nActiveJobs=" + activeJobs.size() + " STARTING JOB " + key + " with timeout " + value);
                    activeJobs.put(key, value);
                    startedNewJob = true;
                } catch (Exception e) {
                    break;
                }
            }

            System.out.print("***T=" + totalTime + " ");
            System.out.print(" workers=" + String.join("", activeJobs.keySet()));
            System.out.println(" done=" + String.join("", solution));
            System.out.println("T=" + totalTime + " Done: " + String.join("", solution));

            // SUBTRACT 1 tick from all jobs and then remove expired jobs
            System.out.println("Reducing time of all nodes");
            activeJobs.replaceAll((k, v) -> v - 1);
            for (Iterator<Map.Entry<String, Integer>> it = activeJobs.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Integer> entry = it.next();
                graph.get(entry.getKey()).ttl -= 1;
                System.out.println(" Reduced " + entry + " " + graph.get(entry.getKey()));
                if (entry.getValue() <= 0) {
                    solution.add(entry.getKey());
                    complete.add(entry.getKey());
                    System.out.println("T=" + totalTime + " TTL reached for job entry " + entry);
                    it.remove();
                }
            }

            totalTime += 1;
            if (activeJobs.isEmpty() && queue.isEmpty() && pending.isEmpty()) {
                break;
            }
        }

        System.out.println(MessageFormat.format("P2: parallelWorkers={0} defaultDelay={1} totalTime={2}", nWorkers, nWorkTimeExtra, totalTime));
        for (String t : solution) {
            System.out.print(t);
        }
        System.out.println();

        pending.clear();
        solution.clear();
        visited.clear();
        queue.clear();
    }
}
