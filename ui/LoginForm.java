package ui;

import javax.swing.*;

import models.Passenger;
import models.TransitQManager;

import java.awt.*;
import java.awt.event.KeyEvent;
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
import java.util.regex.Pattern;

public class LoginForm extends JFrame {

    TransitQManager manager = new TransitQManager();
    private TransitQGUI mainGUI;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]+$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^\\d{10,15}$");
    private static final Pattern AGE_PATTERN = Pattern.compile("^\\d{1,3}$");
    private static final Pattern DESTINATION_PATTERN = Pattern.compile("^[A-Za-z\\s.,'-]+$");
    private static final Pattern CASH_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

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
        JLabel nameLabel = new JLabel("Username: *");
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
        JLabel ageLabel = new JLabel("Age: *");
        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        ageLabel.setBounds(55, 240, 200, 20);
        rightPanel.add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(55, 265, 350, 30);
        ageField.setForeground(Color.BLACK);
        ageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ageField.setOpaque(true);
        // Input verification for age field
        ageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        rightPanel.add(ageField);

        // Contact Number Field
        JLabel contactLabel = new JLabel("Contact Number: *");
        contactLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contactLabel.setBounds(55, 300, 200, 20);
        rightPanel.add(contactLabel);

        JTextField contaField = new JTextField();
        contaField.setBounds(55, 325, 350, 30);
        contaField.setForeground(Color.BLACK);
        contaField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        contaField.setOpaque(true);
        // Input verification for contact field
        contaField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        rightPanel.add(contaField);

        // Passenger Types
        JLabel passengerLabel = new JLabel("Ticket Type: ");
        passengerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passengerLabel.setBounds(55, 360, 200, 20);
        rightPanel.add(passengerLabel);

        // Add ticket price info button
        JButton priceInfoButton = new JButton("ℹ️ Price Info");
        priceInfoButton.setBounds(150, 360, 100, 25);
        priceInfoButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        priceInfoButton.setBackground(new Color(240, 240, 240));
        priceInfoButton.setFocusable(false);
        priceInfoButton.addActionListener(e -> showTicketPriceInfo());
        rightPanel.add(priceInfoButton);

        String passengerTypes[] = { "Standard", "Discounted", "Vip" };
        JComboBox<String> passengerComboBox = new JComboBox<>(passengerTypes);
        passengerComboBox.setBounds(55, 385, 350, 30);
        passengerComboBox.setFont(new Font("SansSerif", Font.BOLD, 16));
        rightPanel.add(passengerComboBox);

        // Destination
        JLabel destinationLabel = new JLabel("Destination: *");
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
        JLabel topayLabel = new JLabel("Payment [Cash]: *");
        topayLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topayLabel.setBounds(55, 480, 350, 20);
        rightPanel.add(topayLabel);

        JTextField topayField = new JTextField();
        topayField.setBounds(55, 505, 350, 30);
        topayField.setForeground(Color.BLACK);
        topayField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        topayField.setOpaque(true);
        // Input verification for payment field
        topayField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String text = topayField.getText();

                // Allow digits, decimal point, and control characters
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Ensure only one decimal point
                if (c == '.' && text.contains(".")) {
                    evt.consume();
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Limit to 2 decimal places
                if (text.contains(".")) {
                    int decimalIndex = text.indexOf('.');
                    if (text.length() - decimalIndex > 2 &&
                            !(c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        evt.consume();
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        });
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
                String passengerName = nameTextField.getText().trim();
                String passengerAge = ageField.getText().trim();
                String passengerContact = contaField.getText().trim();
                String passengerDestination = destinationField.getText().trim();
                String passengerCash = topayField.getText().trim();
                Object selectedItemObject = passengerComboBox.getSelectedItem();
                String selectedPassengerType = (String) selectedItemObject;

                // Comprehensive validation
                StringBuilder errorMessage = new StringBuilder();

                // 1. Check for empty fields
                if (passengerName.isEmpty()) {
                    errorMessage.append("• Name field cannot be empty.\n");
                }
                if (passengerAge.isEmpty()) {
                    errorMessage.append("• Age field cannot be empty.\n");
                }
                if (passengerContact.isEmpty()) {
                    errorMessage.append("• Contact number field cannot be empty.\n");
                }
                if (passengerDestination.isEmpty()) {
                    errorMessage.append("• Destination field cannot be empty.\n");
                }
                if (passengerCash.isEmpty()) {
                    errorMessage.append("• Payment field cannot be empty.\n");
                }

                // 2. Validate name (letters, spaces, apostrophes, hyphens only)
                if (!passengerName.isEmpty() && !NAME_PATTERN.matcher(passengerName).matches()) {
                    errorMessage.append("• Name can only contain letters, spaces, apostrophes, and hyphens.\n");
                }

                // 3. Validate name length
                if (passengerName.length() < 2) {
                    errorMessage.append("• Name must be at least 2 characters long.\n");
                }
                if (passengerName.length() > 50) {
                    errorMessage.append("• Name cannot exceed 50 characters.\n");
                }

                // 4. Validate age
                if (!passengerAge.isEmpty()) {
                    if (!AGE_PATTERN.matcher(passengerAge).matches()) {
                        errorMessage.append("• Age must be a valid number (1-3 digits).\n");
                    } else {
                        int age = Integer.parseInt(passengerAge);
                        if (age < 1) {
                            errorMessage.append("• Age must be at least 1 year.\n");
                        }
                        if (age > 120) {
                            errorMessage.append("• Age cannot exceed 120 years.\n");
                        }

                        // Age validation for passenger types
                        if (age < 12 && !selectedPassengerType.equals("Discounted")) {
                            errorMessage.append("• Children under 12 must select 'Discounted' ticket type.\n");
                        }
                        if (age >= 60 && !selectedPassengerType.equals("Discounted")) {
                            errorMessage.append("• Seniors (60+) should select 'Discounted' ticket type.\n");
                        }
                    }
                }

                // 5. Validate contact number
                if (!passengerContact.isEmpty() && !CONTACT_PATTERN.matcher(passengerContact).matches()) {
                    errorMessage.append("• Contact number must be 10-15 digits (numbers only).\n");
                }

                // 6. Validate destination
                if (!passengerDestination.isEmpty() && !DESTINATION_PATTERN.matcher(passengerDestination).matches()) {
                    errorMessage.append(
                            "• Destination can only contain letters, spaces, commas, periods, apostrophes, and hyphens.\n");
                }
                if (passengerDestination.length() < 2) {
                    errorMessage.append("• Destination must be at least 2 characters long.\n");
                }
                if (passengerDestination.length() > 100) {
                    errorMessage.append("• Destination cannot exceed 100 characters.\n");
                }

                // 7. Validate payment amount
                if (!passengerCash.isEmpty()) {
                    if (!CASH_PATTERN.matcher(passengerCash).matches()) {
                        errorMessage.append("• Payment must be a valid number (e.g., 100 or 100.50).\n");
                    } else {
                        double cash = Double.parseDouble(passengerCash);
                        if (cash <= 0) {
                            errorMessage.append("• Payment must be greater than 0.\n");
                        }
                        if (cash > 10000) {
                            errorMessage.append("• Payment cannot exceed ₱10,000.\n");
                        }
                        if (cash < 10 && selectedPassengerType.equals("Standard")) {
                            errorMessage.append("• Standard ticket minimum payment is ₱10.\n");
                        }
                        if (cash < 5 && selectedPassengerType.equals("Discounted")) {
                            errorMessage.append("• Discounted ticket minimum payment is ₱5.\n");
                        }
                        if (cash < 50 && selectedPassengerType.equals("Vip")) {
                            errorMessage.append("• VIP ticket minimum payment is ₱50.\n");
                        }
                    }
                }

                // 8. Check for suspicious input (SQL injection prevention)
                if (containsSQLInjection(passengerName) || containsSQLInjection(passengerDestination)) {
                    errorMessage.append("• Input contains invalid characters.\n");
                }

                // 9. Check for duplicate entries (optional - depends on your requirements)
                if (!passengerContact.isEmpty() && isDuplicateContact(passengerContact)) {
                    errorMessage.append("• This contact number is already registered.\n");
                }

                // If there are validation errors, show them
                if (errorMessage.length() > 0) {
                    JOptionPane.showMessageDialog(backgroundPanel,
                            "Please correct the following errors:\n\n" + errorMessage.toString(),
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // All validations passed - proceed with registration
                try {
                    double paymentAmount = Double.parseDouble(passengerCash);

                    // Create passenger
                    Passenger p = new Passenger(passengerName, passengerDestination,
                            selectedPassengerType, "Cash", passengerCash);

                    if (mainGUI != null) {
                        // Add passenger to main GUI's ticket area
                        String logMessage = mainGUI.getManager().addPassengerToTicketArea(p);
                        mainGUI.logOperation(logMessage);
                        mainGUI.updateVisuals();

                        // Show success message with details
                        String successMessage = String.format(
                                "Registration Successful!\n\n" +
                                        "Passenger: %s\n" +
                                        "Age: %s\n" +
                                        "Ticket Type: %s\n" +
                                        "Destination: %s\n" +
                                        "Payment: ₱%.2f\n\n" +
                                        "You have been added to the ticket area.",
                                passengerName, passengerAge, selectedPassengerType,
                                passengerDestination, paymentAmount);

                        JOptionPane.showMessageDialog(backgroundPanel,
                                successMessage,
                                "Registration Successful!",
                                JOptionPane.INFORMATION_MESSAGE);
                        if (mainGUI != null) {
                            PassengerStatusFrame statusFrame = new PassengerStatusFrame(p, mainGUI.getManager());
                            statusFrame.setVisible(true);
                        }
                        // Clear form after successful submission
                        nameTextField.setText("");
                        ageField.setText("");
                        contaField.setText("");
                        destinationField.setText("");
                        topayField.setText("");
                        passengerComboBox.setSelectedIndex(0);

                        // Optional: Play success sound or visual feedback
                        nameTextField.requestFocus();

                    } else {
                        // Fallback: use local manager
                        manager.addPassengerToTicketArea(p);
                        String successMessage = String.format(
                                "Welcome Passenger %s!\n" +
                                        "Age: %s\n" +
                                        "Ticket Type: %s\n" +
                                        "Destination: %s\n" +
                                        "Payment: ₱%.2f",
                                passengerName, passengerAge, selectedPassengerType,
                                passengerDestination, paymentAmount);

                        JOptionPane.showMessageDialog(backgroundPanel,
                                successMessage,
                                "Registration Successful!",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(backgroundPanel,
                            "Number format error. Please check your inputs.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(backgroundPanel,
                            "An unexpected error occurred: " + ex.getMessage(),
                            "System Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(backgroundPanel, "Registration cancelled.");
            }
        });

        // Add input listeners for real-time validation feedback
        addRealTimeValidation(nameTextField, ageField, contaField, destinationField, topayField);

        setVisible(true);
    }

    private void addRealTimeValidation(JTextField nameField, JTextField ageField,
            JTextField contactField, JTextField destinationField,
            JTextField paymentField) {

        // Create tooltips for guidance
        nameField.setToolTipText("Enter your full name (letters and spaces only)");
        ageField.setToolTipText("Enter your age (numbers only, 1-120)");
        contactField.setToolTipText("Enter 10-15 digit phone number");
        destinationField.setToolTipText("Enter your destination");
        paymentField.setToolTipText("Enter payment amount (e.g., 100 or 100.50)");

        // Change border color based on validation
        Color validColor = new Color(0, 150, 0);
        Color invalidColor = Color.RED;
        Color defaultColor = Color.GRAY;

        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String text = nameField.getText().trim();
                if (text.isEmpty()) {
                    nameField.setBorder(BorderFactory.createLineBorder(defaultColor));
                } else if (NAME_PATTERN.matcher(text).matches() && text.length() >= 2 && text.length() <= 50) {
                    nameField.setBorder(BorderFactory.createLineBorder(validColor, 2));
                } else {
                    nameField.setBorder(BorderFactory.createLineBorder(invalidColor, 2));
                }
            }
        });

        ageField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String text = ageField.getText().trim();
                if (text.isEmpty()) {
                    ageField.setBorder(BorderFactory.createLineBorder(defaultColor));
                } else if (AGE_PATTERN.matcher(text).matches()) {
                    int age = Integer.parseInt(text);
                    if (age >= 1 && age <= 120) {
                        ageField.setBorder(BorderFactory.createLineBorder(validColor, 2));
                    } else {
                        ageField.setBorder(BorderFactory.createLineBorder(invalidColor, 2));
                    }
                } else {
                    ageField.setBorder(BorderFactory.createLineBorder(invalidColor, 2));
                }
            }
        });
    }

    private boolean containsSQLInjection(String input) {
        // Basic SQL injection prevention
        if (input == null)
            return false;

        String[] sqlKeywords = { "SELECT", "INSERT", "UPDATE", "DELETE", "DROP",
                "UNION", "OR", "AND", "WHERE", "JOIN", "--", "/*", "*/" };

        String upperInput = input.toUpperCase();
        for (String keyword : sqlKeywords) {
            if (upperInput.contains(keyword)) {
                // Check if it's part of a legitimate word
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b",
                        Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(input).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDuplicateContact(String contact) {
        // Implement duplicate checking logic based on your requirements
        // This is a placeholder - modify according to your actual data structure
        return false; // Change this based on your duplicate checking logic
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

    private void showTicketPriceInfo() {
        String priceInfo = """
                <html>
                <h3>Ticket Price Information</h3>
                <table border='1' cellpadding='5'>
                    <tr><th>Ticket Type</th><th>Standard Price</th><th>Requirements</th></tr>
                    <tr><td>VIP</td><td>₱100.00</td><td>Priority boarding, premium seat</td></tr>
                    <tr><td>Standard</td><td>₱50.00</td><td>Regular fare</td></tr>
                    <tr><td>Discounted</td><td>₱35.00</td><td>Students, Seniors, PWD</td></tr>
                </table>
                <br><b>Note:</b> Minimum payment required for verification.
                </html>""";

        JOptionPane.showMessageDialog(this, priceInfo, "Ticket Price Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}