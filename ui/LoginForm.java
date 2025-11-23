package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        nameTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameTextField.setOpaque(true);
        rightPanel.add(nameTextField);

        // Age Field
        JLabel ageLabel = new JLabel("Age: ");
        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        ageLabel.setBounds(20, 230, 200, 20);
        rightPanel.add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(20, 255, 400, 30);
        ageField.setForeground(Color.BLACK);
        ageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ageField.setOpaque(true);
        rightPanel.add(ageField);

        // Contact Number Field
        JLabel contactLabel = new JLabel("Contact Number: ");
        contactLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contactLabel.setBounds(20, 290, 200, 20);
        rightPanel.add(contactLabel);

        JTextField contaField = new JTextField();
        contaField.setBounds(20, 315, 400, 30);
        contaField.setForeground(Color.BLACK);
        contaField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        contaField.setOpaque(true);
        rightPanel.add(contaField);

        // Passenger Types
        JLabel passengerLabel = new JLabel("Passenger Type: ");
        passengerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passengerLabel.setBounds(20, 350, 200, 20);
        rightPanel.add(passengerLabel);

        String passengerTypes[] = { "Child", "Teenager", "Adult", "Student" };
        JComboBox<String> passengerComboBox = new JComboBox<>(passengerTypes);
        passengerComboBox.setBounds(20, 375, 400, 30);
        passengerComboBox.setFont(new Font("SansSerif", Font.BOLD, 16));
        rightPanel.add(passengerComboBox);

        // Destination
        JLabel destinationLabel = new JLabel("Destination: ");
        destinationLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        destinationLabel.setBounds(20, 410, 200, 20);
        rightPanel.add(destinationLabel);

        JTextField destinationField = new JTextField();
        destinationField.setBounds(20, 435, 400, 30);
        destinationField.setForeground(Color.BLACK);
        destinationField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        destinationField.setOpaque(true);
        rightPanel.add(destinationField);

        // To Pay
        JLabel topayLabel = new JLabel("Payment [Cash]: ");
        topayLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topayLabel.setBounds(20, 470, 200, 20);
        rightPanel.add(topayLabel);

        JTextField topayField = new JTextField();
        topayField.setBounds(20, 495, 400, 30);
        topayField.setForeground(Color.BLACK);
        topayField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        topayField.setOpaque(true);
        rightPanel.add(topayField);

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(234, 127, 55));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusable(false);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.setBounds(165, 545, 100, 40);
        sendButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        rightPanel.add(sendButton);

        JLabel clickAdmin = new JLabel("Are you a Station Attendant?");
        clickAdmin.setBounds(130, 600, 200, 30);
        clickAdmin.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clickAdmin.setForeground(new Color(82, 181, 247));
        addUnderline(clickAdmin);
        rightPanel.add(clickAdmin);

        sendButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    backgroundPanel, // Parent component
                    "Are you sure you want to send?", // Message
                    "Confirmation", // Title
                    JOptionPane.OK_CANCEL_OPTION // Option type
            );

            if (result == JOptionPane.OK_OPTION) {
                String passengerName = nameTextField.getText();
                String passengerAge = ageField.getText();
                String passengerContact = contaField.getText();
                String passengerDestination = destinationField.getText();
                String passengerCash = topayField.getText();

                // ERROR MESSAGES
                if (passengerName.isEmpty() || passengerAge.isEmpty() || passengerContact.isEmpty()
                        || passengerDestination.isEmpty()) {
                    JOptionPane.showMessageDialog(backgroundPanel, "Please fill out all fields!", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int passengerAge1;
                try {
                    passengerAge1 = Integer.parseInt(ageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(backgroundPanel, "Invalid age format. Please enter a number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!passengerContact.matches("\\d{10,15}")) {
                    JOptionPane.showMessageDialog(backgroundPanel, "Invalid contact number format. Use only digits.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(backgroundPanel, "Cancel or dialog closed. No action taken.");
            }
        });

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

    private void addUnderline(JLabel label) {
        if (label == null) {
            return;
        }

        Font font = label.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());

        // Set the underline attribute to ON
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

        // Create a new font with the added attribute and set it to the label
        label.setFont(font.deriveFont(attributes));
    }
}
