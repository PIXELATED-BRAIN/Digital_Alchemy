import javax.swing.*;
import java.awt.*;

public class InsertionSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public InsertionSortVisualizer(int[] array) {
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

    public void insertionSort() throws InterruptedException {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;

            // Move elements of the array that are greater than the key
            // to one position ahead of their current position
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;

                // Repaint to visualize the movement
                repaint();
                Thread.sleep(50); // Pause for visualization effect
            }

            array[j + 1] = key;

            // Repaint after inserting the key
            repaint();
            Thread.sleep(50);
        }
    }

    public static void main(String[] args) {
        int size = WIDTH / BAR_WIDTH;
        int[] data = new int[size];

        // Initialize the array with random values
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * HEIGHT);
        }

        JFrame frame = new JFrame("Insertion Sort Visualization");
        InsertionSortVisualizer panel = new InsertionSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.insertionSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
