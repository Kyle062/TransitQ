package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginStationAttendant extends JFrame {
    private TransitQGUI mainGUI; // Add this field

    // Modify constructor to accept TransitQGUI reference
    public LoginStationAttendant(TransitQGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Transit IQ Login - Station Attendant");
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

        // Right panel Components
        JLabel welcomeLabel = new JLabel("Attendant Login", SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(55, 79, 114));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setBounds(30, 150, 400, 40);
        rightPanel.add(welcomeLabel);

        JLabel welcomeLabel2 = new JLabel("Please enter your credentials to access the system",
                SwingConstants.CENTER);
        welcomeLabel2.setForeground(Color.black);
        welcomeLabel2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        welcomeLabel2.setBounds(30, 180, 400, 40);
        rightPanel.add(welcomeLabel2);

        // --- Station Attendant Login Fields ---
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        userLabel.setBounds(55, 260, 200, 20);
        rightPanel.add(userLabel);

        JTextField userTextField = new JTextField();
        userTextField.setBounds(55, 285, 350, 30);
        userTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        rightPanel.add(userTextField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passLabel.setBounds(55, 330, 200, 20);
        rightPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(55, 355, 350, 30);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        rightPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(234, 127, 55));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusable(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setBounds(165, 420, 120, 40);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        rightPanel.add(loginButton);

        JLabel clickPassenger = new JLabel("Are you a Passenger?");
        clickPassenger.setBounds(158, 480, 200, 30);
        clickPassenger.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // clickPassenger.setForeground(new Color(82, 181, 247));
        addUnderline(clickPassenger);
        rightPanel.add(clickPassenger);
        clickPassenger.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Go back to passenger login with the same mainGUI reference
                new LoginForm(mainGUI).setVisible(true);
                dispose();
            }
        });

        // Add action listener for the Login Button
        loginButton.addActionListener(e -> {
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(rightPanel, "Please enter username and password.", "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Authentication Logic
            if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin123")) {
                JOptionPane.showMessageDialog(rightPanel, "Login Successful! Welcome, Attendant.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Use the existing mainGUI instance instead of creating a new one
                if (mainGUI != null) {
                    mainGUI.setVisible(true);
                    mainGUI.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    dispose();
                } else {
                    // Fallback: create new instance if mainGUI is null
                    new TransitQGUI().setVisible(true);
                    dispose();
                }

            } else {
                JOptionPane.showMessageDialog(rightPanel, "Invalid username or password.", "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    // ... rest of your helper methods remain the same ...
    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
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

                java.awt.Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
                g2.setClip(clip);

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

                java.awt.Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcW, arcH);
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