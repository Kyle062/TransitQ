package ui;

import javax.swing.*;
import java.awt.*;
import models.Passenger;
import models.TransitQManager;

public class PassengerStatusFrame extends JFrame {

    private TransitQManager manager;
    private Passenger passenger;
    private JLabel statusLabel;
    private JLabel positionLabel;
    private JLabel queueLabel;
    private Timer refreshTimer;

    public PassengerStatusFrame(Passenger passenger, TransitQManager manager) {
        this.passenger = passenger;
        this.manager = manager;

        initializeUI();
        startStatusUpdates();
    }

    private void initializeUI() {
        setTitle("Passenger Status - " + passenger.getName());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 79, 114));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Current Status");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(7, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Passenger Details
        addDetailRow(contentPanel, "Passenger ID:", String.valueOf(passenger.getPassengerId()));
        addDetailRow(contentPanel, "Name:", passenger.getName());
        addDetailRow(contentPanel, "Destination:", passenger.getDestination());
        addDetailRow(contentPanel, "Ticket Type:", passenger.getTicketType());
        addDetailRow(contentPanel, "Amount Paid:", "₱" + passenger.getMoneyPaid());

        // Status (will be updated by timer)
        statusLabel = createStatusLabel("Checking status...");
        contentPanel.add(statusLabel);

        // Position in queue
        positionLabel = createStatusLabel("");
        contentPanel.add(positionLabel);

        add(contentPanel, BorderLayout.CENTER);

        // Footer Panel with instructions
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("<html><center>This window will automatically update your status.<br>" +
                "Please keep it open to track your position.</center></html>");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.add(infoLabel);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        rowPanel.add(nameLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.EAST);
        panel.add(rowPanel);
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(new Color(55, 79, 114));
        return label;
    }

    private void startStatusUpdates() {
        refreshTimer = new Timer(2000, e -> updatePassengerStatus()); // Update every 2 seconds
        refreshTimer.start();
    }

    private void updatePassengerStatus() {
        // Check where the passenger is
        boolean inTicketArea = false;
        boolean inAssignArea = false;
        int ticketPosition = -1;
        int assignPosition = -1;

        // Check ticket area
        int index = 0;
        for (Passenger p : manager.getTicketAreaQueue()) {
            if (p.getPassengerId() == passenger.getPassengerId()) {
                inTicketArea = true;
                ticketPosition = index + 1;
                break;
            }
            index++;
        }

        // Check assign area
        index = 0;
        for (Passenger p : manager.getAssignAreaQueue()) {
            if (p.getPassengerId() == passenger.getPassengerId()) {
                inAssignArea = true;
                assignPosition = index + 1;
                break;
            }
            index++;
        }

        // Update status label
        if (inTicketArea) {
            statusLabel.setText("📍 Current Location: TICKET AREA");
            positionLabel
                    .setText("Position in queue: #" + ticketPosition + " of " + manager.getTicketAreaQueue().size());
            statusLabel.setForeground(new Color(255, 153, 0)); // Orange for ticket area
        } else if (inAssignArea) {
            statusLabel.setText("🚌 Current Location: ASSIGN BUS AREA");
            positionLabel.setText(
                    "Position in boarding queue: #" + assignPosition + " of " + manager.getAssignAreaQueue().size());
            statusLabel.setForeground(new Color(0, 153, 0)); // Green for assign area
        } else {
            // Check if already boarded/bus departed
            boolean isServed = false;
            for (Passenger p : manager.getServedLog()) {
                if (p.getPassengerId() == passenger.getPassengerId()) {
                    isServed = true;
                    break;
                }
            }

            if (isServed) {
                statusLabel.setText("✅ Status: BOARDED & DEPARTED");
                positionLabel.setText("Your bus has departed. Thank you for traveling with us!");
                statusLabel.setForeground(new Color(0, 100, 0)); // Dark green for boarded

                // Stop timer once boarded
                refreshTimer.stop();
            } else {
                statusLabel.setText("❓ Status: NOT FOUND IN SYSTEM");
                positionLabel.setText("Please check with station attendant");
                statusLabel.setForeground(Color.RED);
            }
        }
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }
}