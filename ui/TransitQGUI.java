package ui;


import javax.swing.*;

import components.Bus;
import components.Passenger;
import components.TransitQManager;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors; // Added for detailed logging in update

public class TransitQGUI extends JFrame {
    // NOTE: This assumes TransitQManager and Passenger/Bus classes exist and are
    // correct.
    private TransitQManager manager = new TransitQManager();
    private JTextArea logArea;

    private JPanel assignAreaVisPanel;
    private JPanel ticketAreaVisPanel;
    private JPanel ticketAreaContainer;

    private Map<String, JPanel> busPanels;
    private String pulsingBusName = null; // Tracks which bus should have the pulse effect

    // The moveable button reference
    private JButton addToBusButton;
    // Offsets (relative nudges applied to default computed position)
    private int addBtnOffsetX = 0;
    private int addBtnOffsetY = 0;
    // If user drags the button, we use absolute coordinates to persist position
    private boolean addBtnUseAbsolute = false;
    private int addBtnAbsoluteX = 0;
    private int addBtnAbsoluteY = 0;

    // --- Color Palette ---
    private final Color DARK_BLUE_FRAME = new Color(30, 48, 77);
    private final Color INNER_DARK_BLUE_BG = new Color(30, 48, 77);
    private final Color White = Color.WHITE;
    private final Color LIGHT_BLUE_BUTTON = new Color(173, 216, 230);
    private final Color HOVER_BLUE_BUTTON = new Color(190, 230, 245);
    private final Color YELLOW_BUS = new Color(255, 230, 99);
    private final Color RED_SEPARATOR = new Color(255, 0, 0);
    private final Color TICKET_AREA_BG = Color.WHITE;
    private final Color TICKET_AREA_TEXT_ORANGE = new Color(255, 192, 0);
    private final Color ASSIGN_AREA_ID_TEXT = Color.LIGHT_GRAY;
    private final Color LOG_BG_DARK_GRAY = new Color(34, 34, 34);
    private final Color PULSE_COLOR = new Color(0, 255, 255); // Cyan for pulse

    // --- Dynamic Dimensions (Calculated based on actual panel sizes) ---
    private int CURRENT_CONTENT_WIDTH;
    private int CURRENT_CONTENT_HEIGHT;
    private final int LOG_PANEL_PREFERRED_HEIGHT;

    public TransitQGUI() {
        // Apply Nimbus Look and Feel first for a modern look
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Use maximum window bounds to set the fixed height proportion for the log
        // panel
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        LOG_PANEL_PREFERRED_HEIGHT = (int) (ge.getMaximumWindowBounds().height * 0.18);

        setTitle("TransitQ Bus and Passenger Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the frame

        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BLUE_FRAME);

        // 1. Log Panel (South)
        JScrollPane logScrollPane = createLogPanel();
        logScrollPane.setPreferredSize(new Dimension(0, LOG_PANEL_PREFERRED_HEIGHT));
        add(logScrollPane, BorderLayout.SOUTH);

        // 2. Content Panel (Center)
        JPanel contentPanel = createMainContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // 3. Listener to get the final dimensions of the contentPanel after
        // maximization
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Only trigger if dimensions have meaningfully changed or are being initialized
                if (contentPanel.getWidth() > 0 && contentPanel.getHeight() > 0 &&
                        (contentPanel.getWidth() != CURRENT_CONTENT_WIDTH
                                || contentPanel.getHeight() != CURRENT_CONTENT_HEIGHT)) {

                    CURRENT_CONTENT_WIDTH = contentPanel.getWidth();
                    CURRENT_CONTENT_HEIGHT = contentPanel.getHeight();
                    updateLayoutForContentPanelSize(contentPanel);
                }
            }
        });

        setVisible(true);

        updateVisuals();
    }

    // Public API: reset to computed layout (no absolute or offsets)
    public void resetAddToBusButtonPlacement() {
        addBtnOffsetX = 0;
        addBtnOffsetY = 0;
        addBtnUseAbsolute = false;
        Container content = getContentPane();
        if (content != null && content.getComponentCount() > 0) {
            updateLayoutForContentPanelSize((JPanel) content.getComponent(0));
        }
    }

    // --- Dynamic Layout Method (Ensures proportional sizing and prevents cutoff)
    // ---
    // ---------- updateLayoutForContentPanelSize ----------
    private void updateLayoutForContentPanelSize(JPanel contentPanel) {
        if (CURRENT_CONTENT_WIDTH <= 0 || CURRENT_CONTENT_HEIGHT <= 0) {
            return;
        }

        // Find the inner rounded panel by name (robust)
        JPanel innerRightPanel = null;
        for (Component c : contentPanel.getComponents()) {
            if (c instanceof JPanel && "INNER_RIGHT_PANEL".equals(c.getName())) {
                innerRightPanel = (JPanel) c;
                break;
            }
        }

        // ---------- Sidebar buttons (on contentPanel only) ----------
        int sidebarX = 18;
        int sidebarYStart = 60;
        int sidebarW = 170;
        int sidebarH = 64;
        int sidebarGap = 18;
        int idx = 0;

        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String t = btn.getText();
                if ("ADD PASSENGER TO THE BUS".equals(t)) {
                    // large central button - position relative to innerRightPanel later
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                    // fallback if inner not found
                    btn.setBounds(780, 640, 320, 54);
                } else if ("SEARCH".equals(t) || "REMOVE".equals(t) || "UPDATE".equals(t)
                        || "ASSIGN BUS".equals(t) || "REPORT".equals(t) || "CLEAR LOGS".equals(t)) {
                    btn.setFont(new Font("Arial", Font.BOLD, 18));
                    btn.setBounds(sidebarX, sidebarYStart + idx * (sidebarH + sidebarGap), sidebarW, sidebarH);
                    idx++;
                }
            }
        }

        // If inner panel not found, leave sidebar placed and return safely
        if (innerRightPanel == null) {
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        int innerX = sidebarX + sidebarW + 30;
        int innerY = 20;
        // size proportional to available content area to keep large visual
        int innerW = Math.max((int) (CURRENT_CONTENT_WIDTH * 0.75), 1100);
        int innerH = Math.max((int) (CURRENT_CONTENT_HEIGHT * 0.78), 720);
        innerRightPanel.setBounds(innerX, innerY, innerW, innerH);

        // ---------- Bus Panels (2x2 grid) inside innerRightPanel ----------
        int busW = 160;
        int busH = 160;
        int leftColX = 34;
        int rightColX = leftColX + busW + 110;
        int topRowY = 42;
        int bottomRowY = topRowY + busH + 36;

        if (busPanels != null) {
            if (busPanels.get("BUS C") != null)
                busPanels.get("BUS C").setBounds(leftColX + 170, topRowY + 320, busW, busH);
            if (busPanels.get("BUS A") != null)
                busPanels.get("BUS A").setBounds(rightColX - 100, topRowY - 30, busW, busH);
            if (busPanels.get("BUS D") != null)
                busPanels.get("BUS D").setBounds(leftColX + 170, bottomRowY + 300, busW, busH);
            if (busPanels.get("BUS B") != null)
                busPanels.get("BUS B").setBounds(rightColX - 100, bottomRowY - 50, busW, busH);
        }

        // ---------- ASSIGN PASSENGER AREA (center-top) ----------
        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JPanel && "ASSIGN_AREA_TOP_LEVEL".equals(comp.getName())) {
                int assignW = 400;
                int assignH = 420;
                // slightly left-of-center, matching screenshot composition
                int assignX = (innerW / 2) - (assignW / 2) + 60;
                int assignY = 20;
                comp.setBounds(assignX, assignY, assignW, assignH);
                break;
            }
        }

        // ---------- Red Separator (vertical) ----------
        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JPanel && "RED_SEPARATOR".equals(comp.getName())) {
                int sepW = 14;
                int sepX = innerW - 380;
                int sepY = 30;
                comp.setBounds(sepX, sepY, sepW, innerH - 110);
                break;
            }
        }

        // ---------- Ticket Area (rounded box at right) ----------
        if (ticketAreaContainer != null) {
            int ticketW = 450;
            int ticketH = 400;
            int ticketX = innerW - 330; // position to the right of separator
            int ticketY = 80;
            ticketAreaContainer.setBounds(ticketX, ticketY, ticketW, ticketH);
        }

        // ---------- Ticket area buttons (stacked under ticket area) ----------
        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if ("ADD PASSENGER".equals(btn.getText())) {
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                    if (ticketAreaContainer != null) {
                        btn.setBounds(ticketAreaContainer.getX() + 85,
                                ticketAreaContainer.getY() + ticketAreaContainer.getHeight() + 18,
                                280, 48);
                    } else {
                        btn.setBounds(innerW - 340, innerH - 170, 280, 48);
                    }
                } else if ("PASS PASSENGER".equals(btn.getText())) {
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                    if (ticketAreaContainer != null) {
                        btn.setBounds(ticketAreaContainer.getX() + 85,
                                ticketAreaContainer.getY() + ticketAreaContainer.getHeight() + 86,
                                280, 48);
                    } else {
                        btn.setBounds(innerW - 340, innerH - 100, 280, 48);
                    }
                }
            }
        }

        // ---------- Big "ADD PASSENGER TO THE BUS" button (center-bottom of inner
        // area) ----------
        if (addToBusButton != null) {
            addToBusButton.setFont(new Font("Arial", Font.BOLD, 16));
            int bigW = 360;
            int bigH = 54;

            if (addBtnUseAbsolute) {
                // Use absolute coordinates preserved from drag
                addToBusButton.setBounds(addBtnAbsoluteX, addBtnAbsoluteY, bigW, bigH);
            } else {
                // Default computed position relative to inner area + offsets
                int baseBigX = innerX + (innerW / 2) - (bigW / 2) - 80; // same base as before
                int baseBigY = innerY + innerH - 200;
                int bigX = baseBigX + addBtnOffsetX;
                int bigY = baseBigY + addBtnOffsetY;
                addToBusButton.setBounds(bigX, bigY, bigW, bigH);
                // update absolute too (useful if user later drags)
                addBtnAbsoluteX = bigX;
                addBtnAbsoluteY = bigY;
            }
        }

        innerRightPanel.setBounds(220, 12, 1580, 760);

        // Revalidate & repaint
        contentPanel.revalidate();
        contentPanel.repaint();
        innerRightPanel.revalidate();
        innerRightPanel.repaint();
    }

    // --- 1. Main Content Panel Setup ---
    // ---------- createMainContentPanel ----------
    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(null);
        contentPanel.setBackground(White);

        // Inner rounded right panel (big visual area) - give it a name for reliable
        // lookup
        JPanel rightJPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 28; // rounded corners
                int w = getWidth();
                int h = getHeight();
                g2.setColor(INNER_DARK_BLUE_BG);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                g2.dispose();
                super.paintComponent(g);

            }
        };
        rightJPanel.setName("INNER_RIGHT_PANEL");
        rightJPanel.setOpaque(false);
        rightJPanel.setLayout(null);
        // initial bounds — updateLayout will set final sizes
        rightJPanel.setBounds(420, 12, 1280, 760);

        contentPanel.add(rightJPanel);

        // --- Sidebar Buttons on contentPanel (stacked vertically) ---
        contentPanel.add(createStyledButton("SEARCH", 0, 0, 1, 1, e -> searchPassengerAction()));
        contentPanel.add(createStyledButton("REMOVE", 0, 0, 1, 1, e -> removePassengerAction()));
        contentPanel.add(createStyledButton("UPDATE", 0, 0, 1, 1, e -> updatePassengerAction()));
        contentPanel.add(createStyledButton("ASSIGN BUS", 0, 0, 1, 1, e -> showBusAssignmentForm()));
        contentPanel.add(createStyledButton("REPORT", 0, 0, 1, 1, e -> reportAction()));
        contentPanel.add(createStyledButton("CLEAR LOGS", 0, 0, 1, 1, e -> clearLogsAction()));

        // --- Bus Panels (2x2) inside the innerRightPanel ---
        busPanels = new HashMap<>();
        busPanels.put("BUS C", createBusPanel("BUS C"));
        rightJPanel.add(busPanels.get("BUS C"));
        busPanels.put("BUS A", createBusPanel("BUS A"));
        rightJPanel.add(busPanels.get("BUS A"));
        busPanels.put("BUS D", createBusPanel("BUS D"));
        rightJPanel.add(busPanels.get("BUS D"));
        busPanels.put("BUS B", createBusPanel("BUS B"));
        rightJPanel.add(busPanels.get("BUS B"));

        // --- Assign Passenger Area (top-center of innerRightPanel) ---
        JPanel assignAreaTopLevel = createAssignAreaVisPanel(1, 1);
        assignAreaTopLevel.setName("ASSIGN_AREA_TOP_LEVEL");
        rightJPanel.add(assignAreaTopLevel);

        // --- Big "ADD PASSENGER TO THE BUS" button (placed on contentPanel so it
        // visually overlaps inner area) ---
        addToBusButton = createStyledButton("ADD PASSENGER TO THE BUS", 0, 0, 1, 1, e -> addPassengerToBusAction());
        rightJPanel.add(addToBusButton);

        // Permanently place the button at X=1200 Y=600 (change these values)
        addBtnUseAbsolute = true;
        addBtnAbsoluteX = 610; // desired X
        addBtnAbsoluteY = 500; // desired Y
        // set actual bounds now (width and height must match those used in layout)
        addToBusButton.setBounds(addBtnAbsoluteX, addBtnAbsoluteY, 360, 54);

        // --- Red Separator inside innerRightPanel ---
        JPanel redLine = new JPanel();
        redLine.setBackground(RED_SEPARATOR);
        redLine.setName("RED_SEPARATOR");
        rightJPanel.add(redLine);

        // --- Ticket area inside innerRightPanel ---
        ticketAreaContainer = createTicketAreaContainer(1, 1);
        rightJPanel.add(ticketAreaContainer);

        // --- Ticket area buttons inside innerRightPanel ---
        rightJPanel.add(createStyledButton("ADD PASSENGER", 0, 0, 1, 1, e -> showAddPassengerForm()));
        rightJPanel.add(createStyledButton("PASS PASSENGER", 0, 0, 1, 1, e -> passPassengerAction()));

        return contentPanel;
    }

    // --- 2. Log Panel Setup ---
    private JScrollPane createLogPanel() {
        logArea = new JTextArea("[10:30:43] SYSTEM START: TransitQ Initialized.", 5, 80);
        logArea.setEditable(false);
        logArea.setBackground(LOG_BG_DARK_GRAY);
        logArea.setForeground(Color.LIGHT_GRAY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel logTitlePanel = new JPanel(new BorderLayout());
        logTitlePanel.setBackground(Color.BLACK);
        logTitlePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel titleLabel = new JLabel("OPERATION LOGS", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        logTitlePanel.add(titleLabel, BorderLayout.CENTER);

        JLabel pageLabel = new JLabel("1", SwingConstants.RIGHT);
        pageLabel.setForeground(Color.WHITE);
        pageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        logTitlePanel.add(pageLabel, BorderLayout.EAST);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(logTitlePanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        logPanel.add(scrollPane, BorderLayout.CENTER);

        JScrollPane finalScrollPane = new JScrollPane(logPanel);
        finalScrollPane.setPreferredSize(new Dimension(0, LOG_PANEL_PREFERRED_HEIGHT));
        finalScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        finalScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        finalScrollPane.setBorder(BorderFactory.createEmptyBorder());

        return finalScrollPane;
    }

    // --- Component Creation Helpers ---

    private JButton createStyledButton(String text, int x, int y, int w, int h, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(LIGHT_BLUE_BUTTON);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setBounds(x, y, w, h);

        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_BLUE_BUTTON);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(LIGHT_BLUE_BUTTON);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }

    // Custom Paint for Bus Panel to add a pulsing border effect
    private JPanel createBusPanel(String name) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 40;

                // Bus Body
                g2.setColor(YELLOW_BUS);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

                // Default Border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                // --- PULSE EFFECT ---
                if (name.equals(pulsingBusName)) {
                    g2.setColor(PULSE_COLOR);
                    // Use a slightly thicker stroke for the pulse effect
                    g2.setStroke(new BasicStroke(4));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                }

                // Headlights/Taillights - proportional drawing
                g2.setColor(Color.WHITE);
                g2.fillOval(w - (int) (w * 0.1), (int) (h * 0.05), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval(w - (int) (w * 0.1), h - (int) (h * 0.13), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval((int) (w * 0.03), (int) (h * 0.05), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval((int) (w * 0.03), h - (int) (h * 0.13), (int) (w * 0.08), (int) (h * 0.08));
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        // Fix: Use the name as is (e.g., "BUS A")
        String displayName = name.toUpperCase();

        JLabel label = new JLabel("<html><center>" + displayName + "<br>(0/10)</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAssignAreaVisPanel(int width, int height) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JLabel titleLabel = new JLabel("ASSIGN PASSENGER AREA", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 8, 0));
        container.add(titleLabel, BorderLayout.NORTH);

        assignAreaVisPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        // Using a slightly different shade for visual separation
        assignAreaVisPanel.setBackground(new Color(40, 60, 95));

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setOpaque(false);
        borderPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        borderPanel.add(assignAreaVisPanel, BorderLayout.CENTER);

        container.add(borderPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createTicketAreaContainer(int width, int height) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 40;

                g2.setColor(TICKET_AREA_BG);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

                g2.setColor(TICKET_AREA_TEXT_ORANGE);
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("TICKET AREA (0/" + manager.getTicketAreaCapacity() + ")",
                SwingConstants.CENTER);
        titleLabel.setForeground(TICKET_AREA_TEXT_ORANGE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        ticketAreaVisPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        ticketAreaVisPanel.setOpaque(false);

        panel.add(ticketAreaVisPanel, BorderLayout.CENTER);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(titleLabel, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);

        return container;
    }

    /**
     * Creates a custom passenger icon with ID/Name label and a silhouette.
     *
     * @param nameText    The passenger's name.
     * @param textColor   The color for the ID/Name text.
     * @param passengerId The passenger's unique ID.
     * @return A JPanel representing the passenger.
     */
    private JPanel createPassengerIcon(String nameText, Color textColor, int passengerId) {
        Color silhouetteColor = generateColorFromId(passengerId);

        // Use current content width to calculate icon size dynamically
        int size = (CURRENT_CONTENT_WIDTH > 0) ? (int) (CURRENT_CONTENT_WIDTH * 0.035) : 50;
        int iconWidth = size;
        int iconHeight = (int) (size * 1.5);
        int headSize = iconWidth / 3;
        int bodyWidth = (int) (iconWidth * 0.8);
        int bodyHeight = (int) (iconHeight * 0.7) - headSize;

        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(iconWidth, iconHeight));

        // Display ID/Name combined
        JLabel idNameLabel = new JLabel(
                "<html><center><font size='-2'>ID: " + passengerId + "</font><br>" + nameText + "</center></html>",
                SwingConstants.CENTER);
        idNameLabel.setForeground(textColor);
        idNameLabel.setFont(new Font("Arial", Font.BOLD, (int) (size * 0.2)));
        idNameLabel.setOpaque(false);
        idNameLabel.setToolTipText("ID: " + passengerId + " | Name: " + nameText);

        // Add a small border just for spacing at the top
        idNameLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        iconPanel.add(idNameLabel, BorderLayout.NORTH);

        // Custom drawn silhouette for a nicer look
        JPanel silhouettePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth() + 3;
                int h = getHeight();

                // Draw a simple head (circle)
                g2.setColor(silhouetteColor.brighter());
                g2.fillOval((w - headSize) / 2, 0, headSize, headSize);

                // Draw a simple body (rounded rectangle)
                g2.setColor(silhouetteColor.darker());
                int bodyY = headSize - (int) (bodyHeight * 0.1); // Overlap slightly
                g2.fillRoundRect((w - bodyWidth) / 2, bodyY, bodyWidth, bodyHeight, 10, 10);
            }
        };
        silhouettePanel.setOpaque(false);
        // Adjust preferred height to make space for the two-line label
        silhouettePanel.setPreferredSize(new Dimension(iconWidth, iconHeight - (int) (size * 0.4)));
        iconPanel.add(silhouettePanel, BorderLayout.CENTER);

        return iconPanel;
    }

    // Generates a consistent, visually distinct color based on a passenger's ID
    private Color generateColorFromId(int id) {
        // Use a simple, predictable hash to get a base color
        int hash = id * 133;
        float h = (hash % 256) / 256.0f;
        // Keep Saturation and Brightness high for vibrant colors
        float s = 0.9f;
        float b = 0.7f;
        return Color.getHSBColor(h, s, b);
    }

    // --- Action Methods ---

    private void showAddPassengerForm() {
        // Enhanced Form Panel Styling
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.LIGHT_GRAY);

        JTextField nameField = new JTextField(15);
        JTextField destField = new JTextField(15);
        String[] ticketTypes = { "Standard", "Discounted", "VIP" };
        JComboBox<String> ticketTypeCombo = new JComboBox<>(ticketTypes);

        formPanel.add(new JLabel("Passenger Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destField);
        formPanel.add(new JLabel("Ticket Type:"));
        formPanel.add(ticketTypeCombo);

        // Customizing JOptionPane colors to match the theme (if L&F allows)
        UIManager.put("OptionPane.background", Color.LIGHT_GRAY);
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add New Passenger", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);

        if (result == JOptionPane.OK_OPTION) {
            // Check for empty fields
            if (nameField.getText().trim().isEmpty() || destField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Destination cannot be empty.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Assume Passenger constructor is correct
            // NOTE: The ID assignment should happen inside the manager class.
            Passenger p = new Passenger(nameField.getText(), destField.getText(),
                    (String) ticketTypeCombo.getSelectedItem(), "Cash");
            String logMessage = manager.addPassengerToTicketArea(p);
            logOperation(logMessage);
            updateVisuals();
        }
    }

    private void passPassengerAction() {
        String logMessage = manager.passPassengerToAssignArea();
        logOperation(logMessage);
        updateVisuals();
    }

    private void addPassengerToBusAction() {
        String logMessage = manager.addPassengerToBus();
        logOperation(logMessage);
        updateVisuals();
    }

    private void showBusAssignmentForm() {
        Collection<Bus> buses = manager.getBuses().values();
        String[] options = buses.stream().map(Bus::getName).toArray(String[]::new);
        String bus = (String) JOptionPane.showInputDialog(this, "Select Bus to Assign to Queue:", "Assign Bus",
                JOptionPane.QUESTION_MESSAGE, null, options, manager.getCurrentlyAssignedBusName());
        if (bus != null && !bus.isEmpty()) {
            manager.assignBus(bus);
            logOperation("ASSIGN BUS: Queue is now assigned to " + bus);
            updateVisuals();
        }
    }

    private void searchPassengerAction() {
        String searchInput = JOptionPane.showInputDialog(this, "Enter Passenger ID or Full Name to Search:",
                "Search Passenger");

        if (searchInput == null || searchInput.trim().isEmpty()) {
            logOperation("SEARCH: Search aborted by user or no input provided.");
            return; // Stop if input is null or empty
        }

        Passenger p = manager.searchPassenger(searchInput.trim());
        if (p != null) {
            logOperation("SEARCH: Found Passenger ID " + p.getPassengerId() + " (" + p.getName() + ")");
            JOptionPane.showMessageDialog(this,
                    "Found Passenger:\nID: " + p.getPassengerId() + "\nName: " + p.getName() +
                            "\nDestination: " + p.getDestination() + "\nTicket Type: " + p.getTicketType(),
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            logOperation("SEARCH: Passenger '" + searchInput.trim() + "' not found in active queues.");
            JOptionPane.showMessageDialog(this, "Passenger not found.", "Search Result",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Allows removal by either Passenger ID (int) or Passenger Name (String).
     */
    private void removePassengerAction() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Passenger ID or Full Name to REMOVE:",
                "Remove Passenger");

        if (input == null || input.trim().isEmpty()) {
            logOperation("REMOVE: Removal aborted by user or no input provided.");
            return;
        }

        String searchInput = input.trim();
        Passenger p = manager.searchPassenger(searchInput); // Manager should handle ID or Name

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Passenger '" + searchInput + "' not found in active queues.",
                    "Removal Error", JOptionPane.ERROR_MESSAGE);
            logOperation("REMOVE: ERROR - Passenger '" + searchInput + "' not found.");
            return;
        }

        // If found, remove by the confirmed ID
        String logMessage = manager.removePassenger(p.getPassengerId());
        logOperation(logMessage);
        updateVisuals();
    }

    /**
     * Allows update by either Passenger ID (int) or Passenger Name (String),
     * and logs the specific fields that were updated.
     */
    private void updatePassengerAction() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Passenger ID or Full Name to UPDATE:",
                "Update Passenger");

        if (input == null || input.trim().isEmpty()) {
            logOperation("UPDATE: Update aborted by user or no input provided.");
            return;
        }

        String searchInput = input.trim();
        Passenger p = manager.searchPassenger(searchInput); // Manager should handle ID or Name

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Passenger '" + searchInput + "' not found in active queues.",
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            logOperation("UPDATE: ERROR - Passenger '" + searchInput + "' not found.");
            return;
        }

        // Store original values for comparison
        String originalName = p.getName();
        String originalDest = p.getDestination();
        String originalTicketType = p.getTicketType();

        // Create and style the update form using the found passenger's data
        JTextField nameField = new JTextField(originalName, 15);
        JTextField destField = new JTextField(originalDest, 15);
        String[] ticketTypes = { "Standard", "Discounted", "VIP" };
        JComboBox<String> ticketTypeCombo = new JComboBox<>(ticketTypes);
        ticketTypeCombo.setSelectedItem(originalTicketType);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.LIGHT_GRAY);

        formPanel.add(new JLabel("Passenger Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destField);
        formPanel.add(new JLabel("Ticket Type:"));
        formPanel.add(ticketTypeCombo);

        UIManager.put("OptionPane.background", Color.LIGHT_GRAY);
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        int result = JOptionPane.showConfirmDialog(this, formPanel,
                "Update Details for ID " + p.getPassengerId() + " (" + originalName + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);

        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            String newDest = destField.getText();
            String newTicketType = (String) ticketTypeCombo.getSelectedItem();

            // Collect the list of updated fields
            Map<String, String> updates = new HashMap<>();
            if (!newName.equals(originalName)) {
                updates.put("Name", originalName + " -> " + newName);
            }
            if (!newDest.equals(originalDest)) {
                updates.put("Destination", originalDest + " -> " + newDest);
            }
            if (!newTicketType.equals(originalTicketType)) {
                updates.put("Ticket Type", originalTicketType + " -> " + newTicketType);
            }

            if (!updates.isEmpty()) {
                // Call manager to perform the update
                String managerLog = manager.updatePassenger(p.getPassengerId(), newName, newDest, newTicketType);

                // Detailed log generation based on collected changes
                String detailLog = updates.entrySet().stream()
                        .map(entry -> entry.getKey() + " [" + entry.getValue() + "]")
                        .collect(Collectors.joining(", "));

                logOperation("UPDATE SUCCESS: ID " + p.getPassengerId() + " updated. Fields changed: " + detailLog);

                updateVisuals();
            } else {
                logOperation("UPDATE: Passenger ID " + p.getPassengerId() + " update cancelled (no changes made).");
            }
        }
    }

    private void reportAction() {
        int served = manager.getServedLog().size();
        String report = "--- Passenger Report ---\n" +
                "Total Passengers Served: " + served + "\n" +
                "Passengers Waiting (Ticket Area): " + manager.getTicketAreaQueue().size() + "\n" +
                "Passengers Assigned (Boarding Area): " + manager.getAssignAreaQueue().size() + "\n" +
                "Currently Assigned Bus: " + manager.getCurrentlyAssignedBusName();

        logOperation("REPORT: Generated service report. Total Served: " + served);
        JOptionPane.showMessageDialog(this, report, "System Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearLogsAction() {
        logArea.setText("");
        logOperation("LOGS: Operation logs cleared by user.");
    }

    // --- Visual Update Method ---
    private void updateVisuals() {
        // Update the name of the pulsing bus based on manager state
        pulsingBusName = manager.getCurrentlyAssignedBusName();

        // 1. Update ASSIGN PASSENGER AREA
        assignAreaVisPanel.removeAll();
        Queue<Passenger> assignQueue = manager.getAssignAreaQueue();

        JPanel assignContainer = (JPanel) assignAreaVisPanel.getParent().getParent();
        JLabel assignTitleLabel = (JLabel) assignContainer.getComponent(0);
        assignTitleLabel.setText(
                "ASSIGN PASSENGER AREA (" + assignQueue.size() + "/" + manager.getAssignAreaDisplayCapacity() + ")");

        for (Passenger p : assignQueue) {
            // Pass the passenger name to createPassengerIcon for display
            assignAreaVisPanel.add(createPassengerIcon(p.getName(), ASSIGN_AREA_ID_TEXT, p.getPassengerId()));
        }

        // 2. Update TICKET AREA
        ticketAreaVisPanel.removeAll();
        Queue<Passenger> ticketQueue = manager.getTicketAreaQueue();

        JPanel ticketContainer = (JPanel) ticketAreaVisPanel.getParent().getParent();
        JLabel ticketTitleLabel = (JLabel) ticketContainer.getComponent(0);
        ticketTitleLabel.setText("TICKET AREA (" + ticketQueue.size() + "/" + manager.getTicketAreaCapacity() + ")");

        for (Passenger p : ticketQueue) {
            // Pass the passenger name to createPassengerIcon for display
            ticketAreaVisPanel
                    .add(createPassengerIcon(p.getName(), TICKET_AREA_TEXT_ORANGE.darker(), p.getPassengerId()));
        }

        // 3. Update Bus Capacities and repaint to show pulse effect
        for (Map.Entry<String, JPanel> entry : busPanels.entrySet()) {
            Bus bus = manager.getBuses().get(entry.getKey());
            JLabel label = (JLabel) entry.getValue().getComponent(0);

            label.setText("<html><center>" + bus.getName() + "<br>(" + bus.getCurrentLoad() + "/" + bus.getCapacity()
                    + ")</center></html>");
            // Important: Repaint the bus panel to redraw the custom paintComponent,
            // including the pulsing border if it's the assigned bus.
            entry.getValue().repaint();
        }

        revalidate();
        repaint();
    }

    private void logOperation(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("\n[" + timestamp + "] " + message);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransitQGUI::new);
    }
}
