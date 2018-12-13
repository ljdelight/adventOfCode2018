
import java.util.*;

public class Main {

    private static int goForwardTwo(int index, int size) {
        return index += 2;
        // for (int i = 0; i < 2; i++) {
        //     index += 1;
        //     if (index > size) {
        //         index = 0;
        //     }
        // }
        // return index;
    }
    private static int goBackwardsSeven(int index, int size) {
        for (int i = 0; i < 7; i++) {
            index -= 1;
            if (index < 0) {
                index = size - 1;
            }
        }
        return index;
    }

    private static int solve(String input) {
        String[] split = input.split(" ");
        int nPlayers = Integer.parseInt(split[0]);
        int lastMarbleValue = Integer.parseInt(split[6]);


        int[] players = new int[nPlayers];
        List<Integer> marbles = new LinkedList<>();
        int playerIdx = 0;
        int currentMarbleIdx = 0;

        marbles.add(0);

        for (int marble = 1; marble <= lastMarbleValue; marble++) {


            if (marble % 23 == 0) {
                // System.out.println("Marble is a multiple of 23");
                // Current player keeps the marble they would have placed.
                // Current player gets marble at 7 counter-clockwise.
                // currentMarbleIdx becomes "marble located immediately clockwise of the marble that was removed"
                players[playerIdx] += marble;
                int newIndex = goBackwardsSeven(currentMarbleIdx, marbles.size());
                // System.out.println("GO BACK 7: from=" + currentMarbleIdx + " to=" + newIndex);
                currentMarbleIdx = newIndex;
                players[playerIdx] += marbles.remove(currentMarbleIdx);
            } else {
                int newIndex = goForwardTwo(currentMarbleIdx, marbles.size());
                // System.out.println("BEFORE " + currentMarbleIdx + " " + marbles + " newIdx=" + newIndex);
                if (newIndex == marbles.size()+1) {
                    marbles.add(1, marble);
                    currentMarbleIdx = 1;
                }
                else if (newIndex == marbles.size()) {
                    // System.out.println(" Appending to the list");
                    marbles.add(marble);
                    currentMarbleIdx = marbles.size() - 1;
                } else {
                    // System.out.println(" Inserting at " + newIndex);
                    marbles.add(newIndex, marble);
                    currentMarbleIdx = newIndex ;
                }
                // System.out.println("AFTER " + currentMarbleIdx + " " + marbles); 
            }

            // System.out.print("[" + (playerIdx+1) + "]"); // " + currentMarbleIdx + " " + marbles);
            // for (int i = 0; i < marbles.size(); i++) {
            //     if (i == currentMarbleIdx) {
            //         System.out.print("*");
            //     } else {
            //         System.out.print(" ");
            //     }
            //     System.out.print(marbles.get(i));
            //     if (i == currentMarbleIdx) {
            //         System.out.print("*");
            //     } else {
            //         System.out.print(" ");
            //     }
            // }
            // System.out.println();

            playerIdx = (playerIdx + 1) % players.length;

        }

        int max = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i] > max) {
                max = players[i];
            }
        }
        return max;
    }

    private static void test() {
        if (solve("9 players; last marble is worth 25 points") != 32) {
            throw new RuntimeException("WRONG ANSWER");
        };
        if (solve("10 players; last marble is worth 1618 points") != 8317) {
            throw new RuntimeException("WRONG ANSWER");
        };
        if (solve("13 players; last marble is worth 7999 points") != 146373) {
            throw new RuntimeException("WRONG ANSWER");
        };
        if (solve("17 players; last marble is worth 1104 points") != 2764) {
            throw new RuntimeException("WRONG ANSWER");
        };
        if (solve("21 players; last marble is worth 6111 points") != 54718) {
            throw new RuntimeException("WRONG ANSWER");
        };
        if (solve("30 players; last marble is worth 5807 points") != 37305) {
            throw new RuntimeException("WRONG ANSWER");
        };    
    }

    public static void main(String[] args) {
        // test();
        Scanner s = new Scanner(System.in);
        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            System.out.println("Solving " + line);
            int res = solve(line);
            System.out.println(res);
        }
        // Map<Integer, Node> graph = new HashMap<>();
        // Node root = readInput(s, graph, null);
        // long metadataSum = sumOfMetadata(root);
        // System.out.println("Sum of metadata: " + metadataSum);
        // System.out.println("Value of root: " + root.value());

        // System.out.println(root);


    }
}


      
        

    