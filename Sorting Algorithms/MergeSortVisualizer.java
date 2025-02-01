import javax.swing.*;
import java.awt.*;

public class MergeSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public MergeSortVisualizer(int[] array) {
        this.array = array;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < array.length; i++) {
            int barHeight = array[i];
            g.setColor(Color.RED);
            g.fillRect(i * BAR_WIDTH, HEIGHT - barHeight, BAR_WIDTH, barHeight);
        }
    }

    public void mergeSort(int[] array, int left, int right) throws InterruptedException {
        if (left < right) {
            int mid = (left + right) / 2;

            // Sort first and second halves
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);

            // Merge the sorted halves
            merge(array, left, mid, right);

            // Repaint to visualize the current state
            repaint();
            Thread.sleep(50); // Pause for visualization
        }
    }

    private void merge(int[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        // Copy data to temporary arrays
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        // Merge the temporary arrays
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of leftArray[]
        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        // Copy remaining elements of rightArray[]
        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }

    public static void main(String[] args) {
        int[] data = new int[WIDTH / BAR_WIDTH];
        for (int i = 0; i < data.length; i++) {
            data[i] = (int) (Math.random() * (HEIGHT - 10)) + 10; // Ensure minimum height of 10
        }

        JFrame frame = new JFrame("Merge Sort Visualization");
        MergeSortVisualizer panel = new MergeSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.mergeSort(data, 0, data.length - 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
