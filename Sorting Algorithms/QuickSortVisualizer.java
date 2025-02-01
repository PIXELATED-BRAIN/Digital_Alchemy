import javax.swing.*;
import java.awt.*;

public class QuickSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public QuickSortVisualizer(int[] array) {
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

    public void quickSort() throws InterruptedException {
        quickSortHelper(0, array.length - 1);
    }

    private void quickSortHelper(int low, int high) throws InterruptedException {
        if (low < high) {
            int pi = partition(low, high);

            // Recursively sort the sub-arrays
            quickSortHelper(low, pi - 1);
            quickSortHelper(pi + 1, high);
        }
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = array[high];
        int i = (low - 1);  // Index of smaller element

        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                // Swap array[i] and array[j]
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;

                // Repaint to visualize the swap
                repaint();
                Thread.sleep(50); // Pause for visualization effect
            }
        }

        // Swap array[i + 1] and array[high] (pivot)
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        // Repaint to visualize the pivot swap
        repaint();
        Thread.sleep(50); // Pause for visualization effect

        return i + 1;
    }

    public static void main(String[] args) {
        int size = WIDTH / BAR_WIDTH;
        int[] data = new int[size];

        // Initialize the array with random values
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * HEIGHT);
        }

        JFrame frame = new JFrame("Quick Sort Visualization");
        QuickSortVisualizer panel = new QuickSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.quickSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
