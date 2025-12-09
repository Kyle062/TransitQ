package ui;

import javax.swing.*;

import models.Passenger;
import models.TransitQManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginForm extends JFrame {

    TransitQManager manager = new TransitQManager();
    private TransitQGUI mainGUI;

    public LoginForm() {
        this(null);
    }

    public LoginForm(TransitQGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializeUI();
    }

    private void initializeUI() {
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
        leftPanel.setBounds(150, 80, 450, 670);
        leftPanel.setLayout(null);
        leftPanel.setOpaque(false);
        backgroundPanel.add(leftPanel);

        // Right panel
        JPanel rightPanel = createRoundedPanel(40, 40);
        rightPanel.setBounds(630, 80, 450, 670);
        rightPanel.setOpaque(false);
        rightPanel.setLayout(null);
        backgroundPanel.add(rightPanel);

        // Right panel Components
        BufferedImage rightTopImage = loadImage("images/Logo3.png");
        ImageIcon rightTopIcon = new ImageIcon(rightTopImage);

        JLabel imageLabel = new JLabel(rightTopIcon);
        imageLabel.setBounds(150, 5, 150, 150);
        rightPanel.add(imageLabel);

        JLabel welcomeLabel = new JLabel("Welcome to TransitQ", SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(55, 79, 114));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setBounds(30, 110, 400, 40);
        rightPanel.add(welcomeLabel);

        JLabel welcomeLabel2 = new JLabel("Please fill the fields below to register as passenger",
                SwingConstants.CENTER);
        welcomeLabel2.setForeground(Color.black);
        welcomeLabel2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        welcomeLabel2.setBounds(30, 135, 400, 40);
        rightPanel.add(welcomeLabel2);

        // Name Field
        JLabel nameLabel = new JLabel("Username: ");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setBounds(55, 180, 200, 20);
        rightPanel.add(nameLabel);

        JTextField nameTextField = new JTextField();
        nameTextField.setBounds(55, 205, 350, 30);
        nameTextField.setForeground(Color.BLACK);
        nameTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameTextField.setOpaque(true);
        rightPanel.add(nameTextField);

        // Age Field
        JLabel ageLabel = new JLabel("Age: ");
        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        ageLabel.setBounds(55, 240, 200, 20);
        rightPanel.add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(55, 265, 350, 30);
        ageField.setForeground(Color.BLACK);
        ageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ageField.setOpaque(true);
        rightPanel.add(ageField);

        // Contact Number Field
        JLabel contactLabel = new JLabel("Contact Number: ");
        contactLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contactLabel.setBounds(55, 300, 200, 20);
        rightPanel.add(contactLabel);

        JTextField contaField = new JTextField();
        contaField.setBounds(55, 325, 350, 30);
        contaField.setForeground(Color.BLACK);
        contaField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        contaField.setOpaque(true);
        rightPanel.add(contaField);

        // Passenger Types
        JLabel passengerLabel = new JLabel("Ticket Type: ");
        passengerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passengerLabel.setBounds(55, 360, 200, 20);
        rightPanel.add(passengerLabel);

        String passengerTypes[] = { "Standard", "Discounted", "Vip" };
        JComboBox<String> passengerComboBox = new JComboBox<>(passengerTypes);
        passengerComboBox.setBounds(55, 385, 350, 30);
        passengerComboBox.setFont(new Font("SansSerif", Font.BOLD, 16));
        rightPanel.add(passengerComboBox);

        // Destination
        JLabel destinationLabel = new JLabel("Destination: ");
        destinationLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        destinationLabel.setBounds(55, 420, 200, 20);
        rightPanel.add(destinationLabel);

        JTextField destinationField = new JTextField();
        destinationField.setBounds(55, 445, 350, 30);
        destinationField.setForeground(Color.BLACK);
        destinationField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        destinationField.setOpaque(true);
        rightPanel.add(destinationField);

        // To Pay
        JLabel topayLabel = new JLabel("Payment [Cash]: ");
        topayLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topayLabel.setBounds(55, 480, 350, 20);
        rightPanel.add(topayLabel);

        JTextField topayField = new JTextField();
        topayField.setBounds(55, 505, 350, 30);
        topayField.setForeground(Color.BLACK);
        topayField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        topayField.setOpaque(true);
        rightPanel.add(topayField);

        // Send Button
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(234, 127, 55));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusable(false);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.setBounds(165, 555, 100, 40);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        rightPanel.add(sendButton);

        JLabel clickAdmin = new JLabel("Are you a Station Attendant?");
        clickAdmin.setBounds(130, 610, 200, 30);
        clickAdmin.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clickAdmin.setForeground(new Color(82, 181, 247));
        addUnderline(clickAdmin);
        rightPanel.add(clickAdmin);
        clickAdmin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open station attendant login with reference to main GUI
                new LoginStationAttendant(mainGUI).setVisible(true);
                dispose();
            }
        });
        sendButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    backgroundPanel,
                    "Are you sure you want to send?",
                    "Confirmation",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String passengerName = nameTextField.getText();
                String passengerAge = ageField.getText();
                String passengerContact = contaField.getText();
                String passengerDestination = destinationField.getText();
                String passengerCash = topayField.getText();
                Object selectedItemObject = passengerComboBox.getSelectedItem();
                String selectedPassengerType = (String) selectedItemObject;

                // ERROR MESSAGES
                if (passengerName.isEmpty() || passengerAge.isEmpty() || passengerContact.isEmpty()
                        || passengerDestination.isEmpty() || passengerCash.isEmpty()) {
                    JOptionPane.showMessageDialog(backgroundPanel, "Please fill out all fields!", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (!passengerContact.matches("\\d{10,15}")) {
                    JOptionPane.showMessageDialog(backgroundPanel, "Invalid contact number format. Use only digits.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
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

                if (!passengerName.isEmpty() && !passengerAge.isEmpty() && !passengerContact.isEmpty()
                        && !passengerDestination.isEmpty() && !passengerCash.isEmpty()) {

                    // Create passenger
                    Passenger p = new Passenger(passengerName, passengerDestination, selectedPassengerType, "Cash",
                            passengerCash);

                    if (mainGUI != null) {
                        // Add passenger to main GUI's ticket area
                        String logMessage = mainGUI.getManager().addPassengerToTicketArea(p);
                        mainGUI.logOperation(logMessage);
                        mainGUI.updateVisuals();

                        JOptionPane.showMessageDialog(backgroundPanel,
                                "Welcome Passenger " + passengerName + "!\nYou have been added to the ticket area.",
                                "Registration Successful!",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Clear form after successful submission
                        nameTextField.setText("");
                        ageField.setText("");
                        contaField.setText("");
                        destinationField.setText("");
                        topayField.setText("");
                        passengerComboBox.setSelectedIndex(0);

                    } else {
                        // Fallback: use local manager
                        manager.addPassengerToTicketArea(p);
                        JOptionPane.showMessageDialog(backgroundPanel,
                                "Welcome Passenger " + passengerName,
                                "Registration Successful!",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(backgroundPanel, "Registration cancelled.");
            }
        });

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

                RoundRectangle2D.Float clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
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

                RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
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
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        label.setFont(font.deriveFont(attributes));
    }
}