import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkVisualization extends JPanel {

    private final List<List<Point>> layers = new ArrayList<>(); // Stores neuron positions layer by layer
    @SuppressWarnings("unused")
    private int[] layerStructure; 

    public NeuralNetworkVisualization(int[] layerStructure) {
        this.layerStructure = layerStructure;

        int panelWidth = 800;
        int panelHeight = 600;

        // Calculate neuron positions for each layer
        for (int i = 0; i < layerStructure.length; i++) {
            List<Point> layer = new ArrayList<>();
            int yOffset = panelHeight / (layerStructure[i] + 1);
            int xPosition = (i + 1) * panelWidth / (layerStructure.length + 1);

            for (int j = 0; j < layerStructure[i]; j++) {
                int yPosition = (j + 1) * yOffset;
                layer.add(new Point(xPosition, yPosition));
            }
            layers.add(layer);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw connections
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < layers.size() - 1; i++) {
            List<Point> currentLayer = layers.get(i);
            List<Point> nextLayer = layers.get(i + 1);

            for (Point from : currentLayer) {
                for (Point to : nextLayer) {
                    g2d.drawLine(from.x, from.y, to.x, to.y);
                }
            }
        }

        // Draw neurons
        int neuronRadius = 20;
        for (List<Point> layer : layers) {
            for (Point neuron : layer) {
                g2d.setColor(Color.BLUE);
                g2d.fillOval(neuron.x - neuronRadius / 2, neuron.y - neuronRadius / 2, neuronRadius, neuronRadius);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(neuron.x - neuronRadius / 2, neuron.y - neuronRadius / 2, neuronRadius, neuronRadius);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Neural Network Visualization");

        // Input panel for user to define the neural network structure
        JPanel inputPanel = new JPanel();
        JTextField structureInput = new JTextField(20);
        JButton submitButton = new JButton("Submit");
        submitButton.setFocusable(false);

        inputPanel.add(new JLabel());
        inputPanel.add(structureInput);
        inputPanel.add(submitButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);

        NeuralNetworkVisualization[] panel = new NeuralNetworkVisualization[1];

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] tokens = structureInput.getText().split(",");
                    int[] layerStructure = new int[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        layerStructure[i] = Integer.parseInt(tokens[i].trim());
                    }

                    if (panel[0] != null) {
                        frame.remove(panel[0]);
                    }

                    panel[0] = new NeuralNetworkVisualization(layerStructure);
                    frame.add(panel[0], BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter integers separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
