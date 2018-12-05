
import java.util.*;

public class Main {
  private static char EMPTY = '.';

  private static int part1(char[] polymer) {
    // System.out.println(" " + String.valueOf(polymer));
    int units = 0;
    for (int i = 0; i < polymer.length; i++) {
      if (polymer[i] != EMPTY) {
        units += 1;
      }
    }

    for (int i = 0; i < polymer.length;) {
      if (polymer[i] == EMPTY) {
        i += 1;
        continue;
      }
      int j = i + 1;
      while (j < polymer.length && polymer[j] == EMPTY) {
        j += 1;
      }
      if (j >= polymer.length) {
        i += 1;
        continue;
      }

      if (Character.toLowerCase(polymer[i]) ==
              Character.toLowerCase(polymer[j]) &&
          polymer[i] != polymer[j]) {
        polymer[i] = EMPTY;
        polymer[j] = EMPTY;
        units -= 2;

        while (i >= 0 && polymer[i] == EMPTY) {
          i -= 1;
        }
        if (i < 0) {
          i = j;
          while (i < polymer.length && polymer[i] == EMPTY) {
            i += 1;
          }
        }
      } else {
        i += 1;
      }
    }
    // System.out.println(" Reduced to " + String.valueOf(polymer));
    return units;
  }

  // Input polymer is 50000 characters. 5*10^4.
  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);

    String polymerStr = s.nextLine();
    // String polymerStr = "dabAcCaCBAcCcaDA";
    System.out.println("P1: " + part1(polymerStr.toCharArray()));

    // Setup unit types
    Set<Character> unitTypes = new HashSet<>();
    for (char c : polymerStr.toCharArray()) {
      unitTypes.add(Character.toLowerCase(c));
    }

    // Try removing all unit types to find a max
    int shortest = Integer.MAX_VALUE;
    for (char c : unitTypes) {
      // System.out.println("Removal of " + c);
      char[] reduced = polymerStr.toCharArray();
      for (int i = 0; i < reduced.length; i++) {
        if (Character.toLowerCase(c) == Character.toLowerCase(reduced[i])) {
          reduced[i] = EMPTY;
        }
      }
      int len = part1(reduced);
      // System.out.println("Results in " + len);
      if (len < shortest) {
        shortest = len;
      }
    }
    System.out.println("P2: " + shortest);
  }
}
