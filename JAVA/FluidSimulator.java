import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FluidSimulator extends JPanel implements ActionListener {
    private final Timer timer;
    private final ArrayList<Particle> particles;
    private final int width = 800;
    private final int height = 600;
    private final int particleCount = 666;

    public FluidSimulator() {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        particles = new ArrayList<>();

        // Initialize particles
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(Math.random() * width, Math.random() * height));
        }

        timer = new Timer(16, this); // Approx. 60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.CYAN);

        for (Particle particle : particles) {
            g2d.fillOval((int) particle.x, (int) particle.y, 5, 5);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Particle particle : particles) {
            particle.update(width, height);
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fluid Simulator");
        FluidSimulator simulator = new FluidSimulator();
        frame.add(simulator);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static class Particle {
        double x, y;
        double vx, vy;
        double radius = 5;

        public Particle(double x, double y) {
            this.x = x;
            this.y = y;
            this.vx = Math.random() * 2 - 1; // Random velocity between -1 and 1
            this.vy = Math.random() * 2 - 1;
        }

        public void update(int width, int height) {
            x += vx;
            y += vy;

            // Bounce off walls
            if (x < 0 || x > width) {
                vx *= -1;
            }
            if (y < 0 || y > height) {
                vy *= -1;
            }
        }
    }
}
