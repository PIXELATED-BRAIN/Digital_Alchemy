import javax.swing.*;
import java.awt.*;

public class BubbleSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public BubbleSortVisualizer(int[] array) {
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

    public void bubbleSort() throws InterruptedException {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Swap elements
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;

                    // Repaint after each swap to visualize
                    repaint();
                    Thread.sleep(50); // Pause for visualization effect
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] data = new int[WIDTH / BAR_WIDTH];
        for (int i = 0; i < data.length; i++) {
            data[i] = (int) (Math.random() * HEIGHT);
        }

        JFrame frame = new JFrame("Bubble Sort Visualization");
        BubbleSortVisualizer panel = new BubbleSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.bubbleSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
