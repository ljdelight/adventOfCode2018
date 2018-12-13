//
// Run the app and save the output to 'res.txt'.
// Get the solution using cat res.txt | sed -e 's/power=//' | sort -n
//
import java.util.*;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
import java.util.concurrent.CountDownLatch;


public class Main {

    private static int EMPTY = Integer.MAX_VALUE;
    private static int[][] T = new int[301][302];
    private static int getPowerLevel(int x, int y, int serial) {
        if (T[x][y] != EMPTY) {
            return T[x][y];
        }
        int rackId = x + 10;
        int powerLevel = rackId * y;
        powerLevel += serial;
        powerLevel *= rackId;
        if (powerLevel >= 100) {
            powerLevel = (powerLevel / 100) % 10;
        } else {
            powerLevel = 0;
        }
        powerLevel -= 5;
        T[x][y] = powerLevel;
        return powerLevel;
    }

    private static int getPowerLevelAtGrid(int x, int y, int serial, int dial) {
        int powerLevel = 0;
        for (int i = 0; i < dial; i++) {
            for (int j = 0; j < dial; j++) {
                powerLevel += getPowerLevel(x + i, y + j, serial);
            }
        }
        return powerLevel;
    }

    private static int getLargestPower(int serial, int dial) {
        int maxPower = Integer.MIN_VALUE;
        int maxX = -1;
        int maxY = -1;
        for (int x = 1; x <= 300 - dial + 1; x++) {
            for (int y = 1; y <= 300 - dial + 1; y++) {
                int p = getPowerLevelAtGrid(x, y, serial, dial);
                if (p > maxPower) {
                    maxPower = p;
                    maxX = x;
                    maxY = y;
                }
            }
        }
        System.out.println("power=" + maxPower + " x=" + maxX + " y=" + maxY + " dial=" + dial);
        return maxPower;
    }


    private static void test() {
        if (getPowerLevel(3, 5, 8) != 4) {
            throw new RuntimeException("invalid power level");
        }
        if (getPowerLevel(122, 79, 57) != -5) {
            throw new RuntimeException("invalid power level");
        }
        if (getPowerLevel(217, 196, 39) != 0) {
            throw new RuntimeException("invalid power level");
        }
        if (getPowerLevel(101, 153, 71) != 4) {
            throw new RuntimeException("invalid power level");
        }
        if (getPowerLevelAtGrid(33, 45, 18, 3) != 29) {
            throw new RuntimeException("invalid grid power level");
        }
        if (getPowerLevelAtGrid(21, 61, 42, 3) != 30) {
            throw new RuntimeException("invalid grid power level");
        }
        if (getLargestPower(18, 3) != 29) {
            throw new RuntimeException("wrong answer");
        }
        if (getLargestPower(42, 3) != 30) {
            throw new RuntimeException("wrong answer");
        }
        if (getLargestPower(18, 16) != 113) {
            throw new RuntimeException("wrong answer");
        }
    }

    public static void main(String[] args) {
        //test();
        
        for (int i = 0; i < T.length; i++) {
            for (int j = 0; j < T[0].length; j++) {
                T[i][j] = EMPTY;
            }
        }
        int MAX_DIALS = 300;
        CountDownLatch latch = new CountDownLatch(MAX_DIALS);

        ExecutorService pool = Executors.newFixedThreadPool(10);   
        for (int dial = 1; dial <= MAX_DIALS; dial++) {
            final int d = dial;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    getLargestPower(1788, d);
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch(Exception e) {

        }
        pool.shutdown();


    }
}
