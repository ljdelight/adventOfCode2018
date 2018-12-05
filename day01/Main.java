
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static Long resultingFrequency(List<Long> itemsList) {
        long frequency = 0L;
        for (Long item : itemsList) {
            frequency += item;
        }
        return frequency;
    }

    private static Long firstDuplicateFrequency(List<Long> itemsList) {
        Set<Long> prevFrequencies = new HashSet<>();
        long frequency = 0L;
        while (true) {
            for (Long item : itemsList) {
                frequency += item;
                if (prevFrequencies.contains(frequency)) {
                    return frequency;
                } else {
                    prevFrequencies.add(frequency);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        List<Long> itemsList = new ArrayList<>();
        while (s.hasNextLong()) {
            itemsList.add(s.nextLong());
        }

        System.out.println("Resulting frequncy: " + resultingFrequency(itemsList));
        System.out.println("Duplicate frequncy: " + firstDuplicateFrequency(itemsList));

    }
}
