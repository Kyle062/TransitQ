package ui;

import javax.swing.*;
import models.Bus;
import models.Passenger;
import models.TransitQManager;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class TransitQGUI extends JFrame {
    // ====================================================================
    // CONSTRUCTOR AND INITIALIZATION METHODS
    // ====================================================================
    // TransitQGUI() - Main constructor that initializes the GUI with look and feel,
    // sets up panels, and starts the application.
    // startBlinkTimer() - Starts a timer for blinking queue indicators.

    // ====================================================================
    // MANAGER AND LOGGING METHODS
    // ====================================================================
    // getManager() - Returns the TransitQManager instance.
    // logOperation(String message) - Adds timestamped messages to the operation log area.

    // ====================================================================
    // GUI SETUP AND LAYOUT METHODS
    // ====================================================================
    // updateQueueIndicators() - Updates blinking indicators in ticket and assign areas.
    // updateLayoutForContentPanelSize(JPanel contentPanel) - Dynamically adjusts layout
    // based on window size changes.
    // updateBusPositions(JPanel innerRightPanel) - Updates positions of bus panels in the layout.

    // ====================================================================
    // PANEL CREATION METHODS
    // ====================================================================
    // createMainContentPanel() - Creates and configures the main content panel with all UI components.
    // initializeBusPanels(JPanel rightJPanel) - Initializes bus panels from manager's bus order.
    // addNewBusPanel(String busName, JPanel rightJPanel) - Dynamically adds a new bus panel to the display.
    // createBusPanel(String name) - Creates a visual panel representation of a bus.
    // createLogPanel() - Creates the operation log panel at the bottom of the window.
    // createStyledButton(String text, int x, int y, int w, int h, ActionListenerlistener) - Creates a styled button with hover effects.
    // createAssignAreaVisPanel(int width, int height) - Creates the assign area
    // visualization panel.
    // createTicketAreaContainer(int width, int height) - Creates the ticket are container panel.

    // ====================================================================
    // VISUALIZATION METHODS
    // ====================================================================
    // updateVisuals() - Updates all visual components including buses, queues, and buttons.
    // createPassengerIcon(...) - Creates a visual icon representation of a passenger.
    // generateColorFromId(int id) - Generates a unique color based on passenger ID.

    // ====================================================================
    // ACTION METHODS (USER INTERACTIONS)
    // ====================================================================
    // showAddPassengerForm() - Shows a dialog form to add a new passenger to ticket area.
    // passPassengerAction() - Passes passenger from ticket area to assign area.
    // addPassengerToBusAction() - Adds passenger from assign area to bus.
    // departBusAction() - Handles bus departure (only when bus is full).
    // showEnhancedBusAssignment() - Shows dialog for assigning different buses to active queue.
    // searchPassengerAction() - Searches for passenger by ID or name.
    // removePassengerAction() - Removes passenger from system.
    // updatePassengerAction() - Updates passenger information with validation.
    // verifyPaymentForUpdate(...) - Helper method to verify payment for updated passengers.

    // ====================================================================
    // REPORT METHODS
    // ====================================================================
    // showReportOptions() - Shows dialog with different report options.
    // showPaymentReport() - Displays payment verification report.
    // showFinancialReport() - Displays financial summary report.
    // showBusStatusReport() - Displays current bus status report.
    // showComprehensiveReport() - Shows comprehensive report with multiple tabs.
    // getTotalCashCollected() - Placeholder for calculating total cash collected.

    // ====================================================================
    // UTILITY METHODS
    // ====================================================================
    // findInnerRightPanel() - Finds the inner right panel in component hierarchy.
    // updateAllBusPanels() - Updates all bus panels after changes.
    // clearLogsAction() - Clears operation logs.
    // showTicketPrices() - Displays ticket price information.

    // ====================================================================
    // LIFECYCLE METHODS
    // ====================================================================
    // dispose() - Cleans up resources (stops timers) when closing the window.

    private TransitQManager manager = new TransitQManager();
    private JTextArea logArea;
    private JPanel assignAreaVisPanel;
    private JPanel ticketAreaVisPanel;
    private JPanel ticketAreaContainer;
    private JButton departBusButton;
    private Map<String, JPanel> busPanels;

    private javax.swing.Timer blinkTimer;
    private boolean blinkState = true;

    private JButton addToBusButton;
    private int addBtnOffsetX = 0;
    private int addBtnOffsetY = 0;
    private boolean addBtnUseAbsolute = false;
    private int addBtnAbsoluteX = 0;
    private int addBtnAbsoluteY = 0;

    // Color Palette
    private final Color DARK_BLUE_FRAME = new Color(30, 48, 77);
    private final Color INNER_DARK_BLUE_BG = new Color(30, 48, 77);
    private final Color White = Color.WHITE;
    private final Color LIGHT_BLUE_BUTTON = new Color(173, 216, 230);
    private final Color HOVER_BLUE_BUTTON = new Color(190, 230, 245);
    private final Color YELLOW_BUS = new Color(255, 230, 99);
    private final Color BUS_FULL_COLOR = new Color(255, 100, 100);
    private final Color RED_SEPARATOR = new Color(255, 0, 0);
    private final Color TICKET_AREA_BG = Color.WHITE;
    private final Color TICKET_AREA_TEXT_ORANGE = new Color(255, 192, 0);
    private final Color ASSIGN_AREA_ID_TEXT = Color.LIGHT_GRAY;
    private final Color PULSE_COLOR = new Color(0, 255, 255);

    private int CURRENT_CONTENT_WIDTH;
    private int CURRENT_CONTENT_HEIGHT;
    private int LOG_PANEL_PREFERRED_HEIGHT;

    // Returns the manager instance
    public TransitQManager getManager() {
        return manager;
    }

    // Logs operation messages with timestamp
    public void logOperation(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("\n[" + timestamp + "] " + message);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ====================================================================
    // CONSTRUCTOR AND INITIALIZATION
    // ====================================================================
    public TransitQGUI() {
        initializeLookAndFeel();
        calculateDimensions();
        setupMainWindow();
        createMainLayout();
        setupDynamicLayoutListener();
        startBlinkTimer();
        setVisible(true);
        updateVisuals();
    }

    private void initializeLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void calculateDimensions() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        LOG_PANEL_PREFERRED_HEIGHT = (int) (ge.getMaximumWindowBounds().height * 0.18);
    }

    private void setupMainWindow() {
        setTitle("TransitQ Bus and Passenger Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BLUE_FRAME);
    }

    private void createMainLayout() {
        // 1. Log Panel (South)
        add(createLogPanel(), BorderLayout.SOUTH);
        // 2. Content Panel (Center)
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private void setupDynamicLayoutListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component centerComponent = getContentPane().getComponent(1);
                if (centerComponent instanceof JPanel) {
                    JPanel contentPanel = (JPanel) centerComponent;
                    if (contentPanel.getWidth() > 0 && contentPanel.getHeight() > 0 &&
                            (contentPanel.getWidth() != CURRENT_CONTENT_WIDTH
                                    || contentPanel.getHeight() != CURRENT_CONTENT_HEIGHT)) {
                        
                        CURRENT_CONTENT_WIDTH = contentPanel.getWidth();
                        CURRENT_CONTENT_HEIGHT = contentPanel.getHeight();
                        updateLayoutForContentPanelSize(contentPanel);
                    }
                }
            }
        });
    }

    // ====================================================================
    // SIDEBAR CREATION
    // ====================================================================
    private void createSidebarButtons(JPanel contentPanel) {
        contentPanel.add(createStyledButton("SEARCH", 0, 0, 1, 1, e -> searchPassengerAction()));
        contentPanel.add(createStyledButton("REMOVE", 0, 0, 1, 1, e -> removePassengerAction()));
        contentPanel.add(createStyledButton("UPDATE", 0, 0, 1, 1, e -> updatePassengerAction()));
        contentPanel.add(createStyledButton("ASSIGN BUS", 0, 0, 1, 1, e -> showEnhancedBusAssignment()));
        contentPanel.add(createStyledButton("REPORTS", 0, 0, 1, 1, e -> showReportOptions()));
        contentPanel.add(createStyledButton("CLEAR LOGS", 0, 0, 1, 1, e -> clearLogsAction()));
        contentPanel.add(createStyledButton("TICKET PRICES", 0, 0, 1, 1, e -> showTicketPrices()));
        departBusButton = createStyledButton("DEPART BUS", 0, 0, 1, 1, e -> departBusAction());
        contentPanel.add(departBusButton);
    }

    // ====================================================================
    // MAIN CONTENT AREA CREATION
    // ====================================================================
    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(null);
        contentPanel.setBackground(White);
        
        createSidebarButtons(contentPanel);
        createRightPanel(contentPanel);
        
        return contentPanel;
    }

    private void createRightPanel(JPanel contentPanel) {
        JPanel rightJPanel = createInnerRightPanel();
        contentPanel.add(rightJPanel);
        
        createBusVisualization(rightJPanel);
        createPassengerQueueAreas(rightJPanel);
        createActionButtons(rightJPanel);
        createVisualElements(rightJPanel);
    }

    // ====================================================================
    // RIGHT PANEL COMPONENTS
    // ====================================================================
    private JPanel createInnerRightPanel() {
        JPanel rightJPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 28;
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
        rightJPanel.setBounds(420, 12, 2000, 960);
        return rightJPanel;
    }

    private void createBusVisualization(JPanel rightJPanel) {
        busPanels = new HashMap<>();
        initializeBusPanels(rightJPanel);
    }

    private void createPassengerQueueAreas(JPanel rightJPanel) {
        JPanel assignAreaTopLevel = createAssignAreaVisPanel(1, 1);
        assignAreaTopLevel.setName("ASSIGN_AREA_TOP_LEVEL");
        rightJPanel.add(assignAreaTopLevel);

        ticketAreaContainer = createTicketAreaContainer(1, 1);
        rightJPanel.add(ticketAreaContainer);
    }

    private void createActionButtons(JPanel rightJPanel) {
        rightJPanel.add(createStyledButton("ADD PASSENGER", 0, 0, 1, 1, e -> showAddPassengerForm()));
        rightJPanel.add(createStyledButton("PASS PASSENGER", 0, 0, 1, 1, e -> passPassengerAction()));
        
        addToBusButton = createStyledButton("ADD PASSENGER TO THE BUS", 0, 0, 1, 1, e -> addPassengerToBusAction());
        rightJPanel.add(addToBusButton);
        addBtnUseAbsolute = true;
        addBtnAbsoluteX = 610;
        addBtnAbsoluteY = 560;
        addToBusButton.setBounds(addBtnAbsoluteX, addBtnAbsoluteY, 360, 54);
    }

    private void createVisualElements(JPanel rightJPanel) {
        JPanel redLine = new JPanel();
        redLine.setBackground(RED_SEPARATOR);
        redLine.setName("RED_SEPARATOR");
        rightJPanel.add(redLine);
    }

    // ====================================================================
    // TIMER AND INDICATOR METHODS
    // ====================================================================
    private void startBlinkTimer() {
        blinkTimer = new javax.swing.Timer(500, e -> {
            blinkState = !blinkState;
            updateQueueIndicators();
        });
        blinkTimer.start();
    }

    private void updateQueueIndicators() {
        if (ticketAreaVisPanel != null) {
            ticketAreaVisPanel.repaint();
        }
        if (assignAreaVisPanel != null) {
            assignAreaVisPanel.repaint();
        }
    }

    // ====================================================================
    // LAYOUT MANAGEMENT METHODS
    // ====================================================================
    private void updateLayoutForContentPanelSize(JPanel contentPanel) {
        if (CURRENT_CONTENT_WIDTH <= 0 || CURRENT_CONTENT_HEIGHT <= 0) {
            return;
        }

        positionSidebarButtons(contentPanel);
        positionRightPanelComponents(contentPanel);
        positionTicketAreaButtons(contentPanel);
        positionAddToBusButton(contentPanel);
        
        contentPanel.revalidate();
        contentPanel.repaint();
        updateInnerRightPanelRepaint(contentPanel);
    }

    private void positionSidebarButtons(JPanel contentPanel) {
        int sidebarX = 18;
        int sidebarYStart = 60;
        int sidebarW = 170;
        int sidebarH = 64;
        int sidebarGap = 18;
        
        String[] buttonOrder = { "SEARCH", "REMOVE", "UPDATE", "ASSIGN BUS",
                "TICKET PRICES", "REPORTS", "CLEAR LOGS", "DEPART BUS" };

        for (int i = 0; i < buttonOrder.length; i++) {
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (btn.getText().equals(buttonOrder[i])) {
                        btn.setBounds(sidebarX, sidebarYStart + i * (sidebarH + sidebarGap), sidebarW, sidebarH);
                        break;
                    }
                }
            }
        }
    }

    private void positionRightPanelComponents(JPanel contentPanel) {
        JPanel innerRightPanel = findInnerRightPanel(contentPanel);
        if (innerRightPanel == null) {
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        int sidebarX = 18;
        int sidebarW = 170;
        int innerX = sidebarX + sidebarW + 30;
        int innerY = 20;
        int innerW = Math.max((int) (CURRENT_CONTENT_WIDTH * 0.75), 1700);
        int innerH = Math.max((int) (CURRENT_CONTENT_HEIGHT * 0.78), 720);
        innerRightPanel.setBounds(innerX, innerY, innerW, innerH);

        updateBusPositions(innerRightPanel);
        positionAssignArea(innerRightPanel, innerW, innerH);
        positionRedSeparator(innerRightPanel, innerW, innerH);
        positionTicketAreaContainer(innerRightPanel, innerW, innerH);
    }

    private JPanel findInnerRightPanel(JPanel contentPanel) {
        for (Component c : contentPanel.getComponents()) {
            if (c instanceof JPanel && "INNER_RIGHT_PANEL".equals(c.getName())) {
                return (JPanel) c;
            }
        }
        return null;
    }

    private void positionAssignArea(JPanel innerRightPanel, int innerW, int innerH) {
        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JPanel && "ASSIGN_AREA_TOP_LEVEL".equals(comp.getName())) {
                int assignW = 500;
                int assignH = 420;
                int assignX = (innerW / 2) - (assignW / 2);
                int assignY = 80;
                comp.setBounds(assignX - 50, assignY, assignW, assignH);
                break;
            }
        }
    }

    private void positionRedSeparator(JPanel innerRightPanel, int innerW, int innerH) {
        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JPanel && "RED_SEPARATOR".equals(comp.getName())) {
                int sepW = 14;
                int sepX = innerW - 530;
                int sepY = 30;
                comp.setBounds(sepX, sepY, sepW, innerH - 110);
                break;
            }
        }
    }

    private void positionTicketAreaContainer(JPanel innerRightPanel, int innerW, int innerH) {
        if (ticketAreaContainer != null) {
            int ticketW = 450;
            int ticketH = 450;
            int ticketX = innerW - 500;
            int ticketY = 80;
            ticketAreaContainer.setBounds(ticketX, ticketY, ticketW, ticketH);
        }
    }

    private void positionTicketAreaButtons(JPanel contentPanel) {
        JPanel innerRightPanel = findInnerRightPanel(contentPanel);
        if (innerRightPanel == null) return;

        for (Component comp : innerRightPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if ("ADD PASSENGER".equals(btn.getText())) {
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                    if (ticketAreaContainer != null) {
                        btn.setBounds(ticketAreaContainer.getX() + 85,
                                ticketAreaContainer.getY() + ticketAreaContainer.getHeight() + 18,
                                280, 48);
                    }
                } else if ("PASS PASSENGER".equals(btn.getText())) {
                    btn.setFont(new Font("Arial", Font.BOLD, 16));
                    if (ticketAreaContainer != null) {
                        btn.setBounds(ticketAreaContainer.getX() + 85,
                                ticketAreaContainer.getY() + ticketAreaContainer.getHeight() + 86,
                                280, 48);
                    }
                }
            }
        }
    }

    private void positionAddToBusButton(JPanel contentPanel) {
        if (addToBusButton != null) {
            addToBusButton.setFont(new Font("Arial", Font.BOLD, 16));
            int bigW = 360;
            int bigH = 54;

            if (addBtnUseAbsolute) {
                addToBusButton.setBounds(addBtnAbsoluteX, addBtnAbsoluteY, bigW, bigH);
            } else {
                JPanel innerRightPanel = findInnerRightPanel(contentPanel);
                if (innerRightPanel != null) {
                    int sidebarX = 18;
                    int sidebarW = 170;
                    int innerX = sidebarX + sidebarW + 30;
                    int innerY = 20;
                    int innerW = Math.max((int) (CURRENT_CONTENT_WIDTH * 0.75), 1700);
                    int innerH = Math.max((int) (CURRENT_CONTENT_HEIGHT * 0.78), 720);
                    
                    int baseBigX = innerX + (innerW / 2) - (bigW / 2) - 80;
                    int baseBigY = innerY + innerH - 200;
                    int bigX = baseBigX + addBtnOffsetX;
                    int bigY = baseBigY + addBtnOffsetY;
                    addToBusButton.setBounds(bigX, bigY, bigW, bigH);
                    addBtnAbsoluteX = bigX;
                    addBtnAbsoluteY = bigY;
                }
            }
        }
    }

    private void updateInnerRightPanelRepaint(JPanel contentPanel) {
        JPanel innerRightPanel = findInnerRightPanel(contentPanel);
        if (innerRightPanel != null) {
            innerRightPanel.revalidate();
            innerRightPanel.repaint();
        }
    }

    // ====================================================================
    // BUS MANAGEMENT METHODS
    // ====================================================================
    private void updateBusPositions(JPanel innerRightPanel) {
        int busW = 150;
        int busH = 150;
        int busStartX = 180;
        int busStartY = 20;
        int busGap = 20;
        java.util.List<String> busOrder = manager.getBusOrder();

        if (busPanels != null) {
            for (int i = 0; i < busOrder.size(); i++) {
                String busName = busOrder.get(i);
                JPanel busPanel = busPanels.get(busName);
                if (busPanel != null) {
                    int busY = busStartY + (i * (busH + busGap));
                    busPanel.setBounds(busStartX, busY, busW, busH);
                    busPanel.setVisible(true);
                }
            }

            for (String busName : busPanels.keySet()) {
                if (!busOrder.contains(busName)) {
                    busPanels.get(busName).setVisible(false);
                }
            }
        }
    }

    private void initializeBusPanels(JPanel rightJPanel) {
        for (String busName : manager.getBusOrder()) {
            if (!busPanels.containsKey(busName)) {
                JPanel busPanel = createBusPanel(busName);
                busPanels.put(busName, busPanel);
                rightJPanel.add(busPanel);
            }
        }
    }

    private void addNewBusPanel(String busName, JPanel rightJPanel) {
        if (!busPanels.containsKey(busName)) {
            JPanel busPanel = createBusPanel(busName);
            busPanels.put(busName, busPanel);
            rightJPanel.add(busPanel);
            rightJPanel.revalidate();
            rightJPanel.repaint();
        }
    }

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

                Color busColor = YELLOW_BUS;
                Bus bus = manager.getBuses().get(name);
                if (bus != null && bus.isFull()) {
                    busColor = BUS_FULL_COLOR;
                }

                g2.setColor(busColor);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                if (name.equals(manager.getCurrentlyAssignedBusName())) {
                    g2.setColor(PULSE_COLOR);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                }

                g2.setColor(Color.WHITE);
                g2.fillOval(w - (int) (w * 0.1), (int) (h * 0.05), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval(w - (int) (w * 0.1), h - (int) (h * 0.13), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval((int) (w * 0.03), (int) (h * 0.05), (int) (w * 0.08), (int) (h * 0.08));
                g2.fillOval((int) (w * 0.03), h - (int) (h * 0.13), (int) (w * 0.08), (int) (h * 0.08));
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        String displayName = name.toUpperCase();
        JLabel label = new JLabel("<html><center>" + displayName + "<br>(0/10)</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.BLACK);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    // ====================================================================
    // LOG PANEL CREATION
    // ====================================================================
    private JScrollPane createLogPanel() {
        logArea = new JTextArea("[10:30:43] SYSTEM START: TransitQ Initialized.", 5, 80);
        logArea.setEditable(false);
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(Color.black);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel logTitlePanel = new JPanel(new BorderLayout());
        logTitlePanel.setBackground(DARK_BLUE_FRAME);
        logTitlePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel titleLabel = new JLabel("OPERATION LOGS", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
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

    // ====================================================================
    // UI COMPONENT CREATION METHODS
    // ====================================================================
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

    private JPanel createAssignAreaVisPanel(int width, int height) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JLabel titleLabel = new JLabel("ASSIGN PASSENGER AREA", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 8, 0));
        container.add(titleLabel, BorderLayout.NORTH);

        assignAreaVisPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
                super.paintComponent(g);
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

    // ====================================================================
    // VISUALIZATION METHODS
    // ====================================================================
    public void updateVisuals() {
        updateBusButtonStates();
        updateBusVisuals();
        updateAssignAreaVisuals();
        updateTicketAreaVisuals();
        revalidate();
        repaint();
    }

    private void updateBusButtonStates() {
        if (departBusButton != null) {
            departBusButton.setEnabled(manager.canDepartBus());
        }
    }

    private void updateBusVisuals() {
        for (Map.Entry<String, JPanel> entry : busPanels.entrySet()) {
            String busName = entry.getKey();
            JPanel busPanel = entry.getValue();
            Bus bus = manager.getBuses().get(busName);

            if (bus != null) {
                JLabel label = (JLabel) busPanel.getComponent(0);
                label.setText("<html><center>" + bus.getName() + "<br>(" + bus.getCurrentLoad() + "/"
                        + bus.getCapacity() + ")</center></html>");
                busPanel.repaint();
            }
        }

        Component centerComponent = getContentPane().getComponent(1);
        if (centerComponent instanceof JPanel) {
            updateBusPositions((JPanel) centerComponent);
        }
    }

    private void updateAssignAreaVisuals() {
        assignAreaVisPanel.removeAll();
        Queue<Passenger> assignQueue = manager.getAssignAreaQueue();
        JPanel assignContainer = (JPanel) assignAreaVisPanel.getParent().getParent();
        JLabel assignTitleLabel = (JLabel) assignContainer.getComponent(0);
        assignTitleLabel.setText(
                "ASSIGN PASSENGER AREA (" + assignQueue.size() + "/" + manager.getAssignAreaDisplayCapacity() + ")");

        int assignIndex = 0;
        for (Passenger p : assignQueue) {
            boolean isFirst = (assignIndex == 0);
            assignAreaVisPanel.add(createPassengerIcon(p.getName(), ASSIGN_AREA_ID_TEXT,
                    p.getPassengerId(), p.getMoneyPaid(), p.getDestination(), p.isPaid(), isFirst, "ASSIGN",
                    p.getTicketType()));
            assignIndex++;
        }
    }

    private void updateTicketAreaVisuals() {
        ticketAreaVisPanel.removeAll();
        Queue<Passenger> ticketQueue = manager.getTicketAreaQueue();
        JPanel ticketContainer = (JPanel) ticketAreaVisPanel.getParent().getParent();
        JLabel ticketTitleLabel = (JLabel) ticketContainer.getComponent(0);
        ticketTitleLabel.setText("TICKET AREA (" + ticketQueue.size() + "/" + manager.getTicketAreaCapacity() + ")");

        int ticketIndex = 0;
        for (Passenger p : ticketQueue) {
            boolean isFirst = (ticketIndex == 0);
            ticketAreaVisPanel.add(createPassengerIcon(p.getName(), TICKET_AREA_TEXT_ORANGE.darker(),
                    p.getPassengerId(), p.getMoneyPaid(), p.getDestination(), p.isPaid(), isFirst, "TICKET",
                    p.getTicketType()));
            ticketIndex++;
        }
    }

    private JPanel createPassengerIcon(String nameText, Color textColor, int passengerId,
            String moneyPaid, String destination, boolean isPaid,
            boolean isFirstInQueue, String areaType, String ticketType) {
        Color silhouetteColor = generateColorFromId(passengerId);
        int size = (CURRENT_CONTENT_WIDTH > 0) ? (int) (CURRENT_CONTENT_WIDTH * 0.035) : 50;
        int iconWidth = size;
        int iconHeight = (int) (size * 1.5);
        int headSize = iconWidth / 3;
        int bodyWidth = (int) (iconWidth * 0.8);
        int bodyHeight = (int) (iconHeight * 0.7) - headSize;

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (isFirstInQueue && blinkState) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(0, 255, 0, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                    g2.setColor(Color.GREEN);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String firstText = "";
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(firstText);
                    g2.drawString(firstText, (getWidth() - textWidth) / 2, 12);

                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                }
            }
        };
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(iconWidth, iconHeight));

        String paymentStatus = isPaid ? "<font color='green'>✓</font>" : "<font color='red'>✗</font>";
        JLabel idNameLabel = new JLabel(
                "<html><center><font size='-2'>" + destination + "</font><br>" +
                        nameText + "<br>" +
                        "<font size='-2'>" + ticketType + " • ₱" + moneyPaid + " " + paymentStatus
                        + "</font></center></html>",
                SwingConstants.CENTER);
        idNameLabel.setForeground(textColor);
        idNameLabel.setFont(new Font("Arial", Font.BOLD, (int) (size * 0.18)));
        idNameLabel.setOpaque(false);
        idNameLabel.setToolTipText(
                "Ticket Type: " + ticketType + " | Destination: " + destination + " | Name: " + nameText
                        + " | Money Paid: ₱" + moneyPaid + " | Paid: "
                        + isPaid);
        idNameLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        iconPanel.add(idNameLabel, BorderLayout.NORTH);

        JPanel silhouettePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth() + 3;
                int h = getHeight();
                g2.setColor(silhouetteColor.brighter());
                g2.fillOval((w - headSize) / 2, 0, headSize, headSize);
                g2.setColor(silhouetteColor.darker());
                int bodyY = headSize - (int) (bodyHeight * 0.1);
                g2.fillRoundRect((w - bodyWidth) / 2, bodyY, bodyWidth, bodyHeight, 10, 10);
            }
        };
        silhouettePanel.setOpaque(false);
        silhouettePanel.setPreferredSize(new Dimension(iconWidth, iconHeight - (int) (size * 0.5)));
        iconPanel.add(silhouettePanel, BorderLayout.CENTER);

        return iconPanel;
    }

    private Color generateColorFromId(int id) {
        int hash = id * 133;
        float h = (hash % 256) / 256.0f;
        float s = 0.9f;
        float b = 0.7f;
        return Color.getHSBColor(h, s, b);
    }

    // ====================================================================
    // BUS ACTION METHODS
    // ====================================================================
    private void departBusAction() {
        if (!manager.canDepartBus()) {
            JOptionPane.showMessageDialog(this,
                    "Current bus is not full yet. Please wait until the bus is full before departing.",
                    "Bus Not Full",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentBusName = manager.getCurrentlyAssignedBusName();

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to depart " + currentBusName + "?\n" +
                        "This will send the bus on its route and rotate the bus queue.",
                "Confirm Bus Departure",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String departureMessage = manager.departBus();
            logOperation(departureMessage);

            String newBusName = manager.getNewlyGeneratedBus();
            if (newBusName != null && !newBusName.equals(currentBusName)) {
                JPanel innerRightPanel = findInnerRightPanel();
                if (innerRightPanel != null) {
                    addNewBusPanel(newBusName, innerRightPanel);
                }
            }

            updateAllBusPanels();
            updateVisuals();

            JOptionPane.showMessageDialog(this,
                    departureMessage + "\n" +
                            "Next bus assigned: " + manager.getCurrentlyAssignedBusName(),
                    "Bus Departed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ====================================================================
    // PASSENGER ACTION METHODS
    // ====================================================================
    private void showAddPassengerForm() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.LIGHT_GRAY);

        JTextField nameField = new JTextField(15);
        JTextField destField = new JTextField(15);
        JTextField moneyField = new JTextField(15);
        String[] ticketTypes = { "Standard", "Discounted", "VIP" };
        JComboBox<String> ticketTypeCombo = new JComboBox<>(ticketTypes);

        formPanel.add(new JLabel("Passenger Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destField);
        formPanel.add(new JLabel("Ticket Type:"));
        formPanel.add(ticketTypeCombo);
        formPanel.add(new JLabel("Money Paid:"));
        formPanel.add(moneyField);

        UIManager.put("OptionPane.background", Color.LIGHT_GRAY);
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add New Passenger", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);

        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().trim().isEmpty() || destField.getText().trim().isEmpty()
                    || moneyField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Destination, and Money Paid cannot be empty.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double money = Double.parseDouble(moneyField.getText().trim());
                if (money < 0) {
                    JOptionPane.showMessageDialog(this, "Money paid cannot be negative.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount for money paid.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Passenger p = new Passenger(nameField.getText(), destField.getText(),
                    (String) ticketTypeCombo.getSelectedItem(), "Cash", moneyField.getText().trim());
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

    // ====================================================================
    // BUS ASSIGNMENT METHODS
    // ====================================================================
    private void showEnhancedBusAssignment() {
        java.util.List<String> availableBuses = manager.getAvailableBuses();
        String currentBus = manager.getCurrentlyAssignedBusName();

        if (availableBuses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available buses to assign. All buses are either full or currently assigned.",
                    "No Available Buses",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea statusArea = new JTextArea(manager.getBusStatusReport());
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setPreferredSize(new Dimension(400, 200));

        JPanel selectionPanel = new JPanel(new FlowLayout());
        selectionPanel.add(new JLabel("Select Bus to Assign:"));
        JComboBox<String> busCombo = new JComboBox<>(availableBuses.toArray(new String[0]));
        selectionPanel.add(busCombo);

        panel.add(new JLabel("Current Bus Status:"), BorderLayout.NORTH);
        panel.add(statusScroll, BorderLayout.CENTER);
        panel.add(selectionPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Assign Bus to Queue", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selectedBus = (String) busCombo.getSelectedItem();
            if (selectedBus != null) {
                String logMessage = manager.assignBusToQueue(selectedBus);
                logOperation("ASSIGN BUS: " + logMessage);
                updateVisuals();

                JOptionPane.showMessageDialog(this,
                        "Successfully assigned " + selectedBus + " to the queue.\n" +
                                "Current assigned bus: " + manager.getCurrentlyAssignedBusName(),
                        "Bus Assigned",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // ====================================================================
    // REPORT METHODS
    // ====================================================================
    private void showReportOptions() {
        String[] options = { "Quick Summary", "Payment Report", "Financial Report",
                "Bus Status", "Comprehensive Report", "Export Report" };
        int choice = JOptionPane.showOptionDialog(this,
                "Select Report Type:",
                "Generate Report",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Quick Summary
                showQuickSummary();
                break;
            case 1: // Payment Report
                showPaymentReport();
                break;
            case 2: // Financial Report
                showFinancialReport();
                break;
            case 3: // Bus Status
                showBusStatusReport();
                break;
            case 4: // Comprehensive Report
                showComprehensiveReport();
                break;
        }
    }

    private void showQuickSummary() {
        int served = manager.getServedLog().size();
        String report = "--- Quick Summary ---\n" +
                "Total Passengers Served: " + served + "\n" +
                "Passengers Waiting (Ticket Area): " + manager.getTicketAreaQueue().size() + "\n" +
                "Passengers Assigned (Boarding Area): " + manager.getAssignAreaQueue().size() + "\n" +
                "Currently Assigned Bus: " + manager.getCurrentlyAssignedBusName() + "\n" +
                "Total Cash Collected: ₱" + String.format("%.2f", getTotalCashCollected());

        logOperation("REPORT: Generated quick summary. Total Served: " + served);
        JOptionPane.showMessageDialog(this, report, "Quick Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPaymentReport() {
        String report = manager.getPaymentReport();

        JTextArea textArea = new JTextArea(report);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Payment Verification Report",
                JOptionPane.INFORMATION_MESSAGE);

        logOperation("REPORT: Generated payment verification report.");
    }

    private void showFinancialReport() {
        String report = manager.getFinancialReport();

        JTextArea textArea = new JTextArea(report);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Financial Report",
                JOptionPane.INFORMATION_MESSAGE);

        logOperation("REPORT: Generated financial report.");
    }

    private void showBusStatusReport() {
        String report = manager.getBusStatusReport();
        JTextArea busArea = new JTextArea(report);
        busArea.setEditable(false);
        busArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane busScroll = new JScrollPane(busArea);
        busScroll.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, busScroll,
                "Bus Status Report",
                JOptionPane.INFORMATION_MESSAGE);
        logOperation("REPORT: Generated bus status report.");
    }

    private void showComprehensiveReport() {
        String report = manager.getComprehensiveReport();

        JTabbedPane tabbedPane = new JTabbedPane();

        JTextArea fullReportArea = new JTextArea(report);
        fullReportArea.setEditable(false);
        fullReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane fullScroll = new JScrollPane(fullReportArea);
        tabbedPane.addTab("Full Report", fullScroll);

        JTextArea financialArea = new JTextArea(manager.getFinancialReport());
        financialArea.setEditable(false);
        financialArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane financialScroll = new JScrollPane(financialArea);
        tabbedPane.addTab("Financial Details", financialScroll);

        JTextArea busArea = new JTextArea(manager.getBusStatusReport());
        busArea.setEditable(false);
        busArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane busScroll = new JScrollPane(busArea);
        tabbedPane.addTab("Bus Status", busScroll);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, mainPanel,
                "Comprehensive System Report",
                JOptionPane.INFORMATION_MESSAGE);

        logOperation("REPORT: Generated comprehensive system report.");
    }

    // ====================================================================
    // PASSENGER MANAGEMENT METHODS
    // ====================================================================
    private void searchPassengerAction() {
        String searchInput = JOptionPane.showInputDialog(this, "Enter Passenger ID or Full Name to Search:",
                "Search Passenger");

        if (searchInput == null || searchInput.trim().isEmpty()) {
            logOperation("SEARCH: Search aborted by user or no input provided.");
            return;
        }

        Passenger p = manager.searchPassenger(searchInput.trim());
        if (p != null) {
            logOperation("SEARCH: Found Passenger ID " + p.getPassengerId() + " (" + p.getName() + ")");
            JOptionPane.showMessageDialog(this,
                    "Found Passenger:\nID: " + p.getPassengerId() + "\nName: " + p.getName() +
                            "\nDestination: " + p.getDestination() + "\nTicket Type: " + p.getTicketType() +
                            "\nCash: " + p.getMoneyPaid() + "\nPayment Verified: " + p.isPaid(),
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            logOperation("SEARCH: Passenger '" + searchInput.trim() + "' not found in active queues.");
            JOptionPane.showMessageDialog(this, "Passenger not found.", "Search Result",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removePassengerAction() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Passenger ID or Full Name to REMOVE:",
                "Remove Passenger");

        if (input == null || input.trim().isEmpty()) {
            logOperation("REMOVE: Removal aborted by user or no input provided.");
            return;
        }

        String searchInput = input.trim();
        Passenger p = manager.searchPassenger(searchInput);

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Passenger '" + searchInput + "' not found in active queues.",
                    "Removal Error", JOptionPane.ERROR_MESSAGE);
            logOperation("REMOVE: ERROR - Passenger '" + searchInput + "' not found.");
            return;
        }

        String logMessage = manager.removePassenger(p.getPassengerId());
        logOperation(logMessage);
        updateVisuals();
    }

    private void updatePassengerAction() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Passenger ID or Full Name to UPDATE:",
                "Update Passenger");

        if (input == null || input.trim().isEmpty()) {
            logOperation("UPDATE: Update aborted by user or no input provided.");
            return;
        }

        String searchInput = input.trim();
        Passenger p = manager.searchPassenger(searchInput);

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Passenger '" + searchInput + "' not found in active queues.",
                    "Update Error", JOptionPane.ERROR_MESSAGE);
            logOperation("UPDATE: ERROR - Passenger '" + searchInput + "' not found.");
            return;
        }

        String originalName = p.getName();
        String originalDest = p.getDestination();
        String originalTicketType = p.getTicketType();

        // Store the original payment status
        boolean originalPaidStatus = p.isPaid();
        String originalMoneyPaid = p.getMoneyPaid();

        JTextField nameField = new JTextField(originalName, 15);
        JTextField destField = new JTextField(originalDest, 15);
        JTextField moneyField = new JTextField(originalMoneyPaid, 15); // Add money field
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
        formPanel.add(new JLabel("Money Paid:"));
        formPanel.add(moneyField);

        // Add validation for money field
        moneyField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String text = moneyField.getText();

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

        UIManager.put("OptionPane.background", Color.LIGHT_GRAY);
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        int result = JOptionPane.showConfirmDialog(this, formPanel,
                "Update Details for ID " + p.getPassengerId() + " (" + originalName + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);

        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newDest = destField.getText().trim();
            String newTicketType = (String) ticketTypeCombo.getSelectedItem();
            String newMoneyPaid = moneyField.getText().trim();

            // Validate inputs
            if (newName.isEmpty() || newDest.isEmpty() || newMoneyPaid.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All fields must be filled out.",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                logOperation("UPDATE ERROR: Incomplete information for passenger ID " + p.getPassengerId());
                return;
            }

            try {
                double money = Double.parseDouble(newMoneyPaid);
                if (money < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Money paid cannot be negative.",
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                    logOperation("UPDATE ERROR: Invalid money amount for passenger ID " + p.getPassengerId());
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid amount for money paid.",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                logOperation("UPDATE ERROR: Invalid money format for passenger ID " + p.getPassengerId());
                return;
            }

            // Update the passenger in the manager - preserving payment status
            String updateResult = manager.updatePassenger(p.getPassengerId(), newName, newDest, newTicketType);

            // Also update the money field if it changed
            if (!newMoneyPaid.equals(originalMoneyPaid)) {
                p.setMoneyPaid(newMoneyPaid);

                // Re-verify payment with new amount
                boolean paymentVerified = verifyPaymentForUpdate(p, newMoneyPaid, newTicketType);
                p.setPaid(paymentVerified);
            }

            Map<String, String> updates = new HashMap<>();
            if (!newName.equals(originalName)) {
                updates.put("Name", originalName + " → " + newName);
            }
            if (!newDest.equals(originalDest)) {
                updates.put("Destination", originalDest + " → " + newDest);
            }
            if (!newTicketType.equals(originalTicketType)) {
                updates.put("Ticket Type", originalTicketType + " → " + newTicketType);
            }
            if (!newMoneyPaid.equals(originalMoneyPaid)) {
                updates.put("Money Paid", "₱" + originalMoneyPaid + " → ₱" + newMoneyPaid);
            }

            if (!updates.isEmpty()) {
                String detailLog = String.join(", ", updates.values());
                logOperation("UPDATE SUCCESS: ID " + p.getPassengerId() + " updated. Fields changed: " + detailLog);

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Successfully updated passenger ID " + p.getPassengerId() + "\n\n" +
                                "Updated fields:\n" +
                                (updates.containsKey("Name") ? "• " + updates.get("Name") + "\n" : "") +
                                (updates.containsKey("Destination") ? "• " + updates.get("Destination") + "\n" : "") +
                                (updates.containsKey("Ticket Type") ? "• " + updates.get("Ticket Type") + "\n" : "") +
                                (updates.containsKey("Money Paid") ? "• " + updates.get("Money Paid") + "\n" : "") +
                                "\nPayment Status: " + (p.isPaid() ? "VERIFIED ✓" : "UNVERIFIED ✗"),
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                updateVisuals();
            } else {
                logOperation("UPDATE: Passenger ID " + p.getPassengerId() + " update cancelled (no changes made).");
            }
        }
    }

    private boolean verifyPaymentForUpdate(Passenger passenger, String moneyPaid, String ticketType) {
        try {
            double amountPaid = Double.parseDouble(moneyPaid);
            double requiredAmount = 0.0;

            switch (ticketType.toLowerCase()) {
                case "vip":
                    requiredAmount = 100.00;
                    break;
                case "discounted":
                    requiredAmount = 35.00;
                    break;
                case "standard":
                default:
                    requiredAmount = 50.00;
                    break;
            }

            return amountPaid >= requiredAmount;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ====================================================================
    // UTILITY METHODS
    // ====================================================================
    private JPanel findInnerRightPanel() {
        Component centerComponent = getContentPane().getComponent(1);
        if (centerComponent instanceof JPanel) {
            JPanel contentPanel = (JPanel) centerComponent;
            for (Component c : contentPanel.getComponents()) {
                if (c instanceof JPanel && "INNER_RIGHT_PANEL".equals(c.getName())) {
                    return (JPanel) c;
                }
            }
        }
        return null;
    }

    private void clearLogsAction() {
        logArea.setText("");
        logOperation("LOGS: Operation logs cleared by user.");
    }

    private void updateAllBusPanels() {
        JPanel innerRightPanel = findInnerRightPanel();
        if (innerRightPanel != null) {
            for (JPanel busPanel : busPanels.values()) {
                innerRightPanel.remove(busPanel);
            }
            busPanels.clear();

            for (String busName : manager.getBusOrder()) {
                JPanel busPanel = createBusPanel(busName);
                busPanels.put(busName, busPanel);
                innerRightPanel.add(busPanel);
            }

            innerRightPanel.revalidate();
            innerRightPanel.repaint();
        }
    }

    private void showTicketPrices() {
        String priceInfo = """
                ===============================
                TRANSITQ TICKET PRICE LIST
                ===============================

                🎫 VIP TICKET
                • Price: ₱100.00
                • Priority boarding
                • Guaranteed seating
                • Premium service

                🎫 STANDARD TICKET
                • Price: ₱50.00
                • Regular service
                • Standard seating

                🎫 DISCOUNTED TICKET
                • Price: ₱35.00
                • For: Students, Seniors (60+), PWD
                • Valid ID required
                • Standard seating

                ===============================
                PAYMENT VERIFICATION RULES:
                • Payment must match or exceed ticket price
                • No change will be given
                • Cash payments only
                • Minimum payment required for boarding
                ===============================
                """;

        JTextArea textArea = new JTextArea(priceInfo);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(240, 248, 255));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Ticket Price Information",
                JOptionPane.INFORMATION_MESSAGE);

        logOperation("INFO: Displayed ticket price list.");
    }

    private double getTotalCashCollected() {
        return 0.0; // Placeholder
    }

    // ====================================================================
    // LIFECYCLE METHODS
    // ====================================================================
    @Override
    public void dispose() {
        if (blinkTimer != null) {
            blinkTimer.stop();
        }
        super.dispose();
    }
}