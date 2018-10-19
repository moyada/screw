package common;

import java.util.Arrays;
import java.util.Random;

/**
 * @author xueyikang
 * @create 2018-03-02 22:07
 */
public class Main {
    public static void main(String[] args) {
        a();
//        random();
    }

    private static void a() {
        int arraySize = 65536;
        Area data[] = new Area[arraySize];

        Random rnd = new Random(0);
        for (int c = 0; c < arraySize; ++c)
            data[c] = new Area(rnd.nextInt() % 256, rnd.nextInt() % 256);

        // !!! With this, the next loop runs faster
        Arrays.sort(data, (a, b) -> Integer.compare(a.getArea(), b.getArea()));

        // Test
        long start = System.nanoTime();
        long sum = 0;

        int total = 128 * 128;
        for (int i = 0; i < 10000; ++i)
        {
            // Primary loop
            for (int c = 0; c < arraySize; ++c)
            {
                if (data[c].getArea() >= total)
                    sum += data[c].getArea();
            }
        }

        System.out.println((System.nanoTime() - start) / 1000000000.0);
        System.out.println("sum = " + sum);
    }

    private static class Area {

        public Area(int width, int height) {
            this.width = width;
            this.height = height;
        }

        private int width;

        private int height;

        public int getArea() {
            return width*height;
        }
    }

    private static void random() {
        // Generate data
        int arraySize = 32768;
        int data[] = new int[arraySize];

        Random rnd = new Random(0);
        for (int c = 0; c < arraySize; ++c)
            data[c] = rnd.nextInt() % 256;

        // !!! With this, the next loop runs faster
        Arrays.sort(data);

        // Test
        long start = System.nanoTime();
        long sum = 0;

        for (int i = 0; i < 100000; ++i)
        {
            // Primary loop
            for (int c = 0; c < arraySize; ++c)
            {
                if (data[c] >= 128)
                    sum += data[c];
            }
        }

        System.out.println((System.nanoTime() - start) / 1000000000.0);
        System.out.println("sum = " + sum);
    }
}