
import java.util.*;

public class Main {

    private static void countRep(String line, int[] res) {
        int[] count = new int[26];
        for (int i = 0; i < line.length(); i++) {
            int ordinal = line.charAt(i) - 'a';
            count[ordinal] += 1;
        }

        for (int i = 0; i < count.length; i++) {
            if (count[i] == 2) {
                res[0] += 1;
                break;
            }
        }

        for (int i = 0; i < count.length; i++) {
            if (count[i] == 3) {
                res[1] += 1;
                break;
            }
        }
    }

    private static int dist(String a, String b) {
        if (a.length() != b.length()) {
            throw new RuntimeException("The two strings are not equal length");
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                diff += 1;
            }
        }
        return diff;
    }

    private static List<String> differByOne(List<String> list) {
        List<String> diffByOne = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                String a = list.get(i);
                String b = list.get(j);
                if (dist(a, b) == 1) {
                    diffByOne.add(a);
                    diffByOne.add(b);
                }
            }
        }
        return diffByOne;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        while (s.hasNextLine()) {
            String line = s.nextLine().trim().toLowerCase();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }

        // idx 0 two count; idx 1 three count
        int[] counts = new int[2];
        for (String line : lines) {
            countRep(line, counts);
        }

        System.out.println("PRODUCT " + (counts[0] * counts[1]));

        // Show the strings for manual editing
        List<String> res = differByOne(lines);
        for (String t : res) {
            System.out.println(t);
        }
    }
}
