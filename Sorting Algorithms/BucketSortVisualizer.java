import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class BucketSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public BucketSortVisualizer(int[] array) {
        this.array = array;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < array.length; i++) {
            int barHeight = array[i];
            g.setColor(Color.BLUE);
            g.fillRect(i * BAR_WIDTH, HEIGHT - barHeight, BAR_WIDTH, barHeight);
        }
    }

    public void bucketSort() throws InterruptedException {
        int n = array.length;
        int max = Integer.MIN_VALUE;

        // Find the maximum value in the array
        for (int value : array) {
            if (value > max) max = value;
        }

        // Create buckets
        int bucketCount = (int) Math.sqrt(n);
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] buckets = new ArrayList[bucketCount];
        for (int i = 0; i < bucketCount; i++) {
            buckets[i] = new ArrayList<>();
        }

        // Distribute array elements into buckets
        for (int value : array) {
            int bucketIndex = (value * bucketCount) / (max + 1);
            buckets[bucketIndex].add(value);
            repaint();
            Thread.sleep(50); // Visualization of bucket distribution
        }

        // Sort each bucket and merge
        int index = 0;
        for (ArrayList<Integer> bucket : buckets) {
            Collections.sort(bucket);
            for (int value : bucket) {
                array[index++] = value;
                repaint();
                Thread.sleep(50); // Visualization of sorted merging
            }
        }
    }

    public static void main(String[] args) {
        int size = WIDTH / BAR_WIDTH;
        int[] data = new int[size];

        // Initialize the array with random values
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * HEIGHT);
        }

        JFrame frame = new JFrame("Bucket Sort Visualization");
        BucketSortVisualizer panel = new BucketSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.bucketSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
