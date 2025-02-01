import javax.swing.*;
import java.awt.*;

public class CocktailSortVisualizer extends JPanel {
    private int[] array;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BAR_WIDTH = 5;

    public CocktailSortVisualizer(int[] array) {
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

    public void cocktailSort() throws InterruptedException {
        boolean swapped = true;
        int start = 0;
        int end = array.length - 1;

        while (swapped) {
            swapped = false;

            // Traverse from left to right
            for (int i = start; i < end; i++) {
                if (array[i] > array[i + 1]) {
                    // Swap elements
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;

                    swapped = true;

                    // Repaint to visualize the swap
                    repaint();
                    Thread.sleep(50); // Pause for visualization effect
                }
            }

            // If nothing was swapped, the array is sorted
            if (!swapped) break;

            swapped = false;

            // Move the end point one step back
            end--;

            // Traverse from right to left
            for (int i = end - 1; i >= start; i--) {
                if (array[i] > array[i + 1]) {
                    // Swap elements
                    int temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;

                    swapped = true;

                    // Repaint to visualize the swap
                    repaint();
                    Thread.sleep(50); // Pause for visualization effect
                }
            }

            // Move the starting point one step forward
            start++;
        }
    }

    public static void main(String[] args) {
        int size = WIDTH / BAR_WIDTH;
        int[] data = new int[size];

        // Create a symmetrical array
        for (int i = 0; i < size / 2; i++) {
            int height = (int) (Math.random() * (HEIGHT - 10)) + 10; // Ensure minimum height of 10
            data[i] = height;
            data[size - 1 - i] = height; // Mirror the height
        }

        JFrame frame = new JFrame("Cocktail Sort Visualization");
        CocktailSortVisualizer panel = new CocktailSortVisualizer(data);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.add(panel);
        frame.setVisible(true);

        // Start sorting in a separate thread to keep the UI responsive
        new Thread(() -> {
            try {
                panel.cocktailSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
