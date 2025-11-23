package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Transit IQ Login");
        int frameWidth = 1250;
        int frameHeight = 900;

        setSize(frameWidth, frameHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        // Load background
        BufferedImage backgroundImage = loadImage("images/TransitQBackgroundpicture.jpg");

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, frameWidth, frameHeight);
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        BufferedImage leftImage = loadImage("images/leftPanel.jpg");
        JPanel leftPanel = createRoundedPanelWithImage(leftImage, 40, 40);
        leftPanel.setBounds(150, 100, 450, 650);
        leftPanel.setLayout(null); // if you want absolute positioning inside
        leftPanel.setOpaque(false); // important so corners outside rounded area show background
        backgroundPanel.add(leftPanel);

        // Right panel (white rounded background)
        JPanel rightPanel = createRoundedPanel(40, 40);
        rightPanel.setBounds(630, 100, 450, 650);
        rightPanel.setOpaque(false); // let rounded painting control the visible area
        rightPanel.setLayout(null); // you used setBounds on child components, so keep null
        backgroundPanel.add(rightPanel);

        // Right panel Components
        JLabel welcomeLabel = new JLabel("Welcome to TransitQ", SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(55, 79, 114));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setBounds(30, 80, 400, 40);
        rightPanel.add(welcomeLabel);

        JLabel welcomeLabel2 = new JLabel("Please fill the fields bellow to register as passenger",
                SwingConstants.CENTER);
        welcomeLabel2.setForeground(Color.black);
        welcomeLabel2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        welcomeLabel2.setBounds(30, 110, 400, 40);
        rightPanel.add(welcomeLabel2);

        // Name Field
        JLabel nameLabel = new JLabel("Username: ");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setBounds(20, 170, 200, 20);
        rightPanel.add(nameLabel);

        JTextField nameTextField = new JTextField();
        nameTextField.setBounds(20, 195, 400, 30);
        nameTextField.setForeground(Color.BLACK);
        rightPanel.add(nameTextField);

        // Age Field
        JLabel ageLabel = new JLabel("Age: ");
        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        ageLabel.setBounds(20, 230, 200, 20);
        rightPanel.add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(20, 255, 400, 30);
        ageField.setForeground(Color.BLACK);
        rightPanel.add(ageField);

        // Contact Number Field
        JLabel contactLabel = new JLabel("Contact Number: ");
        contactLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contactLabel.setBounds(20, 290, 200, 20);
        rightPanel.add(contactLabel);

        JTextField contaField = new JTextField();
        contaField.setBounds(20, 315, 400, 30);
        contaField.setForeground(Color.BLACK);
        rightPanel.add(contaField);

        // Passenger Types
        JLabel passengerLabel = new JLabel("Passenger Type: ");
        passengerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passengerLabel.setBounds(20, 350, 200, 20);
        rightPanel.add(passengerLabel);

        String passengerTypes[] = { "Child", "Adult", "Student" };
        JComboBox<String> passengerComboBox = new JComboBox<>(passengerTypes);
        passengerComboBox.setBounds(20, 375, 400, 30);
        passengerComboBox.setFont(new Font("SansSerif", Font.BOLD, 16));
        rightPanel.add(passengerComboBox);

        



        // show frame
        setVisible(true);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            return null;
        }
    }

    private JPanel createRoundedPanelWithImage(BufferedImage img, int arcW, int arcH) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
                g2.setClip(clip);

                // Fill with image (if present) or fallback to white
                if (img != null) {
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fill(clip);
                }

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f,
                        getWidth() - 3f, getHeight() - 3f, arcW, arcH));

                g2.dispose();
            }
        };
    }

    private JPanel createRoundedPanel(int arcW, int arcH) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
                g2.setColor(Color.WHITE);
                g2.fill(shape);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f,
                        getWidth() - 3f, getHeight() - 3f, arcW, arcH));

                g2.dispose();
            }
        };
    }

}
