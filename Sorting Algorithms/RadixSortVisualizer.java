import javax.swing.*;
import java.awt.*;

public class RadixSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public RadixSortVisualizer(int[] array) {
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

    public void radixSort() throws InterruptedException {
        int max = getMax(array);

        // Perform counting sort for every digit. The exp is 10^i where i is the digit number.
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(exp);
        }
    }

    private int getMax(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private void countingSortByDigit(int exp) throws InterruptedException {
        int[] output = new int[array.length];
        int[] count = new int[10];

        // Store count of occurrences in count[]
        for (int i = 0; i < array.length; i++) {
            int digit = (array[i] / exp) % 10;
            count[digit]++;
        }

        // Change count[i] so that it now contains the actual position of the digit in output[]
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build the output array
        for (int i = array.length - 1; i >= 0; i--) {
            int digit = (array[i] / exp) % 10;
            output[count[digit] - 1] = array[i];
            count[digit]--;
        }

        // Copy the output array to array[], so that array[] contains sorted numbers
        System.arraycopy(output, 0, array, 0, array.length);

        // Repaint after each pass to visualize the sorting process
        repaint();
        Thread.sleep(100); // Pause for visualization effect
    }

    public static void main(String[] args) {
        int size = WIDTH / BAR_WIDTH;
        int[] data = new int[size];

        // Initialize the array with random values
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * HEIGHT);
        }

        JFrame frame = new JFrame("Radix Sort Visualization");
        RadixSortVisualizer panel = new RadixSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.radixSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
