
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.MessageFormat;

public class Main {

  private static class Guard {
    String id;
    public int[] timeSleepingAtMinute;
    List<Integer> transitions;
    int totalTimeAsleep;
    int mostFrequentSleepMinute;
    boolean isAwake;

    public Guard(final String id) {
      this.id = id;
      this.isAwake = true;
      this.totalTimeAsleep = 0;
      this.mostFrequentSleepMinute = 0;
      this.transitions = new ArrayList<>();
      this.timeSleepingAtMinute = new int[60];
    }

    public void transition(final String mmStr) {
      final int mm = Integer.parseInt(mmStr);
      // Transitioning from SLEEPING to AWAKE
      if (!isAwake) {
        int sleepStart = transitions.get(transitions.size() - 1);
        for (int i = sleepStart; i < mm; i++) {
          timeSleepingAtMinute[i] += 1;
          totalTimeAsleep += 1;
          if (timeSleepingAtMinute[mostFrequentSleepMinute] <
              timeSleepingAtMinute[i]) {
            mostFrequentSleepMinute = i;
          }
        }
      }
      this.transitions.add(mm);
      this.isAwake = !this.isAwake;
    }

    public int getId() { return Integer.parseInt(id); }
    public int getTotalMinutesSleeping() { return totalTimeAsleep; }

    public int getMostFrequentSleepMinute() { return mostFrequentSleepMinute; }

    public int getMaxSleepCountInSingleMinuteCount() {
      return timeSleepingAtMinute[mostFrequentSleepMinute];
    }

    @Override
    public String toString() {
      return MessageFormat.format(
          "[id={0} totalTimeSleeping={1} mostFrequentSleepMinute={2} isAwake={3} ttlTransitions={4}]",
          id, totalTimeAsleep, mostFrequentSleepMinute, isAwake,
          transitions.size());
    }
  }

  private static void part1(Map<String, Guard> guards) {
    Guard maxSleeping =
        guards.values()
            .stream()
            .max(Comparator.comparing(it -> it.getTotalMinutesSleeping()))
            .get();
    int res = maxSleeping.getId() * maxSleeping.getMostFrequentSleepMinute();
    System.out.println("P1=" + res + " " + maxSleeping);
  }

  public static void main(String[] args) {
    // ASSUME SOMEONE FORMATTED INPUT USING 'sort' COMMAND LINE TOOL
    // Example input:
    // [1518-01-12 23:57] Guard #3209 begins shift
    // [1518-01-13 00:13] falls asleep
    // [1518-01-13 00:21] wakes up
    Scanner s = new Scanner(System.in);
    Map<String, Guard> guards = readInput(s);

    part1(guards);

    Guard mfmin = null;
    for (Guard g : guards.values()) {
      if (mfmin == null || g.getMaxSleepCountInSingleMinuteCount() >
                               mfmin.getMaxSleepCountInSingleMinuteCount()) {
        mfmin = g;
      }
    }
    System.out.println("P2=" + (mfmin.getId() * mfmin.getMostFrequentSleepMinute()) + " " + mfmin + " ");
  }

  private static Map<String, Guard> readInput(Scanner s) {

    Map<String, Guard> guards = new HashMap<>();
    Pattern linePattern =
        Pattern.compile("\\[(\\d+-\\d+-\\d+) (\\d+):(\\d+)] (.*)");
    Pattern guardAction = Pattern.compile("Guard #(\\d+) begins shift");
    String guardId = null;
    while (s.hasNextLine()) {
      String line = s.nextLine().trim();
      Matcher matcher = linePattern.matcher(line);
      matcher.find();

      String yyyymmdd = matcher.group(1);
      String hh = matcher.group(2);
      String mm = matcher.group(3);
      String action = matcher.group(4);

      if ("falls asleep".equals(action)) {
        guards.get(guardId).transition(mm);
        // System.out.println(MessageFormat.format(" awake->sleep guard={0} {1}
        // {2}:{3}", guardId, yyyymmdd, hh, mm));
      } else if ("wakes up".equals(action)) {
        guards.get(guardId).transition(mm);
        // System.out.println(MessageFormat.format(" sleep->awake guard={0} {1}
        // {2}:{3}", guardId, yyyymmdd, hh, mm));
      } else {
        // System.out.println(MessageFormat.format("NEW GUARD: {0} {1}:{2}
        // \"{3}\"", yyyymmdd, hh, mm, action));

        Matcher g = guardAction.matcher(action);
        g.find();
        guardId = g.group(1);
        if (!guards.containsKey(guardId)) {
          guards.put(guardId, new Guard(guardId));
        }
      }
      // System.out.println(MessageFormat.format(" Guard {0} has slept for {1}",
      // guardId, guards.get(guardId).getTotalMinutesSleeping()));
    }
    return guards;
  }
}
