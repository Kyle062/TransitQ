package models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class TransitQManager {
    private Queue<Passenger> ticketAreaQueue;
    private Queue<Passenger> assignAreaQueue;
    private List<Passenger> servedLog;

    private final int TICKET_AREA_CAPACITY = 15;
    private final int ASSIGN_AREA_DISPLAY_CAPACITY = 15;
    private Map<String, Bus> buses;
    private String currentlyAssignedBusName;
    private List<String> busOrder;
    private List<String> availableBusNames;
    private String newlyGeneratedBus;

    // Predefined passengers
    private List<Passenger> predefinedPassengers;

    // Enhanced fields for payment verification and reporting
    private Map<Integer, String> paymentVerificationLog;
    private double totalCashCollected;
    private int vipTicketsSold;
    private int standardTicketsSold;
    private int discountedTicketsSold;
    private List<String> departureLog;
    private LocalDateTime systemStartTime;

    // Constructor - initializes all components
    public TransitQManager() {
        this.ticketAreaQueue = new LinkedList<>();
        this.assignAreaQueue = new LinkedList<>();
        this.servedLog = new ArrayList<>();

        // Initialize buses in order
        this.buses = new LinkedHashMap<>();
        buses.put("BUS A", new Bus("BUS A", 10));
        buses.put("BUS B", new Bus("BUS B", 10));
        buses.put("BUS C", new Bus("BUS C", 10));
        buses.put("BUS D", new Bus("BUS D", 10));

        // Initialize bus order
        this.busOrder = new ArrayList<>(buses.keySet());
        this.currentlyAssignedBusName = busOrder.get(0);

        // Initialize available bus names for rotation
        this.availableBusNames = new ArrayList<>();
        availableBusNames.add("BUS E");
        availableBusNames.add("BUS F");
        availableBusNames.add("BUS G");
        availableBusNames.add("BUS H");
        availableBusNames.add("BUS I");
        availableBusNames.add("BUS J");

        this.newlyGeneratedBus = null;

        // Initialize predefined passengers
        this.predefinedPassengers = createPredefinedPassengers();

        // Initialize enhanced fields
        this.paymentVerificationLog = new HashMap<>();
        this.totalCashCollected = 0.0;
        this.vipTicketsSold = 0;
        this.standardTicketsSold = 0;
        this.discountedTicketsSold = 0;
        this.departureLog = new ArrayList<>();
        this.systemStartTime = LocalDateTime.now();

        // Add some predefined passengers to ticket area initially
        initializeWithPredefinedPassengers();
    }

    // Creates list of predefined passengers with sample data
    private List<Passenger> createPredefinedPassengers() {
        List<Passenger> passengers = new ArrayList<>();

        passengers.add(new Passenger("John Smith", "Downtown", "Standard", "Cash", "50.00"));
        passengers.add(new Passenger("Maria Garcia", "Airport", "VIP", "Cash", "120.00"));
        passengers.add(new Passenger("David Johnson", "University", "Discounted", "Cash", "35.00"));
        passengers.add(new Passenger("Sarah Willi", "Shopping Mall", "Standard", "Cash", "45.00"));
        passengers.add(new Passenger("Mich Brown", "Hospital", "VIP", "Cash", "150.00"));
        passengers.add(new Passenger("Emily Davis", "Beach", "Standard", "Cash", "60.00"));
        passengers.add(new Passenger("Robert Miller", "Stadium", "Discounted", "Cash", "40.00"));
        passengers.add(new Passenger("Lisa Wilson", "Convention Center", "Standard", "Cash", "55.00"));
        passengers.add(new Passenger("James Taylor", "Train Station", "VIP", "Cash", "110.00"));
        passengers.add(new Passenger("Jenni Anderson", "City Center", "Standard", "Cash", "48.00"));
        passengers.add(new Passenger("Thomas Martinez", "Amusement Park", "Discounted", "Cash", "42.00"));
        passengers.add(new Passenger("Susan Thompson", "Business District", "Standard", "Cash", "65.00"));
        passengers.add(new Passenger("Elger Panganti", "Davao", "VIP", "Cash", "200.00"));

        return passengers;
    }

    // Adds initial predefined passengers to ticket area
    private void initializeWithPredefinedPassengers() {
        for (int i = 0; i < 13 && i < predefinedPassengers.size(); i++) {
            Passenger passenger = predefinedPassengers.get(i);
            if (ticketAreaQueue.size() < TICKET_AREA_CAPACITY) {
                ticketAreaQueue.offer(passenger);
            }
        }
    }

    // --- Enhanced Bus Assignment Methods ---

    // Assigns a specific bus to the active queue
    public String assignBusToQueue(String busName) {
        if (!buses.containsKey(busName)) {
            return "ERROR: Bus " + busName + " does not exist.";
        }

        Bus bus = buses.get(busName);

        if (bus.getCurrentLoad() > 0 && !busName.equals(currentlyAssignedBusName)) {
            return "ALERT: " + busName + " has " + bus.getCurrentLoad() +
                    " passengers. Cannot reassign while occupied.";
        }

        if (bus.isFull() && !busName.equals(currentlyAssignedBusName)) {
            return "ALERT: " + busName + " is full. Please depart it first before reassigning.";
        }

        this.currentlyAssignedBusName = busName;

        if (busOrder.contains(busName)) {
            busOrder.remove(busName);
            busOrder.add(0, busName);
        }

        return "ASSIGNED: " + busName + " is now assigned to the queue. Load: " +
                bus.getCurrentLoad() + "/" + bus.getCapacity();
    }

    // Returns list of available (empty) buses
    public List<String> getAvailableBuses() {
        List<String> available = new ArrayList<>();
        for (Map.Entry<String, Bus> entry : buses.entrySet()) {
            String busName = entry.getKey();
            Bus bus = entry.getValue();

            if (!bus.isFull() && bus.getCurrentLoad() == 0 &&
                    !busName.equals(currentlyAssignedBusName)) {
                available.add(busName);
            }
        }
        return available;
    }

    // Generates status report for all buses
    public String getBusStatusReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BUS STATUS REPORT ===\n");
        report.append("Current Assigned Bus: ").append(currentlyAssignedBusName).append("\n");
        report.append("Bus Rotation Order: ").append(busOrder).append("\n");
        report.append("-------------------------\n");

        for (String busName : busOrder) {
            Bus bus = buses.get(busName);
            if (bus != null) {
                String status = busName.equals(currentlyAssignedBusName) ? "[ACTIVE]" : "[WAITING]";
                if (bus.isFull())
                    status = "[FULL]";

                report.append(String.format("%-10s %-10s Load: %2d/%2d | Available: %2d seats\n",
                        busName, status, bus.getCurrentLoad(), bus.getCapacity(),
                        bus.getCapacity() - bus.getCurrentLoad()));
            }
        }

        report.append("Available New Buses: ").append(availableBusNames).append("\n");
        return report.toString();
    }

    // --- Enhanced Payment Verification Methods ---

    // Verifies if passenger paid enough for their ticket type
    private boolean verifyPayment(Passenger passenger) {
        try {
            double amountPaid = Double.parseDouble(passenger.getMoneyPaid());
            String ticketType = passenger.getTicketType();

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

            boolean verified = amountPaid >= requiredAmount;
            String status = verified ? "VERIFIED" : "INSUFFICIENT";
            paymentVerificationLog.put(passenger.getPassengerId(),
                    status + " (Paid: ₱" + amountPaid + ", Required: ₱" + requiredAmount + ")");

            return verified;

        } catch (NumberFormatException e) {
            paymentVerificationLog.put(passenger.getPassengerId(), "INVALID_AMOUNT_FORMAT");
            return false;
        }
    }

    // Records payment in financial tracking
    private void recordPayment(Passenger passenger) {
        try {
            double amount = Double.parseDouble(passenger.getMoneyPaid());
            totalCashCollected += amount;

            switch (passenger.getTicketType().toLowerCase()) {
                case "vip":
                    vipTicketsSold++;
                    break;
                case "discounted":
                    discountedTicketsSold++;
                    break;
                case "standard":
                default:
                    standardTicketsSold++;
                    break;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error recording payment for passenger: " + passenger.getPassengerId());
        }
    }

    // --- Enhanced Core Operations with Payment Verification ---

    // Adds passenger to ticket area queue
    public String addPassengerToTicketArea(Passenger p) {
        if (ticketAreaQueue.size() >= TICKET_AREA_CAPACITY) {
            return "ALERT: TICKET AREA FULL! Cannot add new passenger.";
        }
        ticketAreaQueue.offer(p);
        return "ENQUEUE: Added to Ticket Area. ID: " + p.getPassengerId() +
                ". Queue: " + ticketAreaQueue.size() + "/" + TICKET_AREA_CAPACITY;
    }

    // Moves passenger from ticket area to assign area with payment verification
    public String passPassengerToAssignArea() {
        if (ticketAreaQueue.isEmpty()) {
            return "ERROR: TICKET AREA is empty. No passenger to process.";
        }
        if (assignAreaQueue.size() >= ASSIGN_AREA_DISPLAY_CAPACITY) {
            return "ALERT: ASSIGN PASSENGER AREA FULL! Board a passenger first.";
        }

        Passenger p = ticketAreaQueue.poll();
        if (p != null) {
            boolean paymentVerified = verifyPayment(p);
            p.setPaid(paymentVerified);

            if (!paymentVerified) {
                removePassenger(p.getPassengerId());
                return "DENIED: Passenger ID " + p.getPassengerId() +
                        " payment verification failed. Removed from system.";
            }

            recordPayment(p);

            assignAreaQueue.offer(p);
            return "PASS: Passenger ID " + p.getPassengerId() +
                    " moved to ASSIGN AREA. Payment Verified: ₱" + p.getMoneyPaid() +
                    ". Assigned Bus: " + currentlyAssignedBusName;
        }
        return "ERROR: Failed to pass passenger (unexpected error).";
    }

    // Boards passenger from assign area to bus
    public String addPassengerToBus() {
        if (assignAreaQueue.isEmpty()) {
            return "ERROR: ASSIGN PASSENGER AREA is empty. No passenger to board.";
        }

        Bus assignedBus = buses.get(currentlyAssignedBusName);
        if (assignedBus == null) {
            return "ERROR: No bus is currently assigned.";
        }

        if (assignedBus.getCurrentLoad() >= assignedBus.getCapacity()) {
            return "ALERT: " + assignedBus.getName() + " is full! Please depart the bus.";
        }

        Passenger boarded = assignAreaQueue.poll();
        if (boarded != null) {
            boolean boardedOk = assignedBus.boardPassenger();
            if (boardedOk) {
                servedLog.add(boarded);
                return "BOARDED: Passenger ID " + boarded.getPassengerId() + " has boarded " + currentlyAssignedBusName
                        +
                        ". Load: " + assignedBus.getCurrentLoad() + "/" + assignedBus.getCapacity();
            } else {
                assignAreaQueue.offer(boarded);
                return "ALERT: " + assignedBus.getName()
                        + " became full before boarding. Passenger returned to assign area.";
            }
        }
        return "ERROR: Failed to board passenger (unexpected error).";
    }

    // --- Enhanced Bus Departure Logic ---

    // Departs current bus and rotates bus queue
    public String departBus() {
        Bus currentBus = buses.get(currentlyAssignedBusName);

        if (currentBus == null) {
            return "ERROR: No bus to depart.";
        }

        if (currentBus.getCurrentLoad() == 0) {
            return "ALERT: " + currentBus.getName() + " is empty. No need to depart.";
        }

        String departureMessage = "DEPARTED: " + currentBus.getName() + " has departed with " +
                currentBus.getCurrentLoad() + " passengers.";

        departureLog.add(LocalDateTime.now() + " - " + currentBus.getName() +
                " departed with " + currentBus.getCurrentLoad() + " passengers");

        currentBus.resetBus();

        String departedBusName = busOrder.remove(0);

        String newBusName;
        if (!availableBusNames.isEmpty()) {
            newBusName = availableBusNames.remove(0);
            buses.put(newBusName, new Bus(newBusName, 10));
            busOrder.add(newBusName);
            newlyGeneratedBus = newBusName;
        } else {
            busOrder.add(departedBusName);
            newlyGeneratedBus = null;
        }

        currentlyAssignedBusName = busOrder.get(0);

        return departureMessage + " New active bus: " + currentlyAssignedBusName;
    }

    // --- Enhanced Reporting Methods ---

    // Generates payment verification report
    public String getPaymentReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== PAYMENT REPORT ===\n");
        report.append(String.format("Total Cash Collected: ₱%.2f\n", totalCashCollected));
        report.append("Tickets Sold:\n");
        report.append(String.format("  VIP: %d tickets\n", vipTicketsSold));
        report.append(String.format("  Standard: %d tickets\n", standardTicketsSold));
        report.append(String.format("  Discounted: %d tickets\n", discountedTicketsSold));
        report.append(
                String.format("Total Tickets: %d\n", (vipTicketsSold + standardTicketsSold + discountedTicketsSold)));
        report.append("----------------------\n");

        report.append("Recent Payment Verifications (Last 10):\n");
        List<Integer> recentIds = paymentVerificationLog.keySet().stream()
                .sorted((a, b) -> b - a)
                .limit(10)
                .collect(Collectors.toList());

        for (Integer id : recentIds) {
            report.append(String.format("  Passenger ID %d: %s\n", id, paymentVerificationLog.get(id)));
        }

        return report.toString();
    }

    // Generates financial summary report
    public String getFinancialReport() {
        double vipRevenue = vipTicketsSold * 100.00;
        double standardRevenue = standardTicketsSold * 50.00;
        double discountedRevenue = discountedTicketsSold * 35.00;
        double calculatedTotal = vipRevenue + standardRevenue + discountedRevenue;

        StringBuilder report = new StringBuilder();
        report.append("=== FINANCIAL REPORT ===\n");
        report.append(String.format("Revenue by Ticket Type:\n"));
        report.append(String.format("  VIP (₱100.00 x %d): ₱%.2f\n", vipTicketsSold, vipRevenue));
        report.append(String.format("  Standard (₱50.00 x %d): ₱%.2f\n", standardTicketsSold, standardRevenue));
        report.append(String.format("  Discounted (₱35.00 x %d): ₱%.2f\n", discountedTicketsSold, discountedRevenue));
        report.append(String.format("Total Calculated Revenue: ₱%.2f\n", calculatedTotal));
        report.append(String.format("Total Cash Collected: ₱%.2f\n", totalCashCollected));
        report.append(String.format("Discrepancy: ₱%.2f\n", (totalCashCollected - calculatedTotal)));
        report.append("========================\n");

        return report.toString();
    }

    // Generates comprehensive system report
    public String getComprehensiveReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== TRANSITQ COMPREHENSIVE REPORT ===\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n");
        report.append("System Uptime: ").append(getUptimeString()).append("\n");
        report.append("=====================================\n\n");

        report.append("1. PASSENGER STATISTICS\n");
        report.append("   Total Passengers Served: ").append(servedLog.size()).append("\n");
        report.append("   Current in Ticket Area: ").append(ticketAreaQueue.size()).append("\n");
        report.append("   Current in Assign Area: ").append(assignAreaQueue.size()).append("\n");
        report.append("   Total Processed Today: ").append(servedLog.size() +
                ticketAreaQueue.size() + assignAreaQueue.size()).append("\n\n");

        report.append("2. BUS OPERATIONS\n");
        report.append(getBusStatusReport()).append("\n");

        report.append("3. FINANCIAL SUMMARY\n");
        report.append(getPaymentReport()).append("\n");

        report.append("4. RECENT ACTIVITY\n");
        report.append("   Last 5 Departures:\n");
        int start = Math.max(0, departureLog.size() - 5);
        for (int i = start; i < departureLog.size(); i++) {
            report.append("     ").append(departureLog.get(i)).append("\n");
        }

        report.append("   Last 10 Boardings:\n");
        List<Passenger> recentServed = servedLog.subList(
                Math.max(0, servedLog.size() - 10), servedLog.size());
        for (Passenger p : recentServed) {
            report.append(String.format("     ID %d: %s to %s (₱%s)\n",
                    p.getPassengerId(), p.getName(), p.getDestination(), p.getMoneyPaid()));
        }

        return report.toString();
    }

    // Calculates system uptime as string
    private String getUptimeString() {
        java.time.Duration duration = java.time.Duration.between(systemStartTime, LocalDateTime.now());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Exports comprehensive report to file
    public boolean exportReportToFile(String filename) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
            writer.println(getComprehensiveReport());
            return true;
        } catch (java.io.IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }

    // --- Existing Utility Methods ---

    // Searches for passenger by ID or name
    public Passenger searchPassenger(String searchInput) {
        try {
            int id = Integer.parseInt(searchInput);
            return searchPassengerById(id);
        } catch (NumberFormatException e) {
            return searchPassengerByName(searchInput);
        }
    }

    // Searches passenger by ID
    private Passenger searchPassengerById(int id) {
        for (Passenger p : ticketAreaQueue) {
            if (p.getPassengerId() == id) {
                return p;
            }
        }
        for (Passenger p : assignAreaQueue) {
            if (p.getPassengerId() == id) {
                return p;
            }
        }
        return null;
    }

    // Searches passenger by name
    private Passenger searchPassengerByName(String name) {
        for (Passenger p : ticketAreaQueue) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        for (Passenger p : assignAreaQueue) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    // Updates passenger information
    public String updatePassenger(int id, String newName, String newDest, String newTicketType) {
        Passenger p = searchPassengerById(id);
        if (p != null) {
            p.setName(newName);
            p.setDestination(newDest);
            p.setTicketType(newTicketType);
            return "UPDATE: Passenger ID " + id + " updated successfully. New Name: " + newName;
        }
        return "ERROR: Passenger ID " + id + " not found for update.";
    }

    // Removes passenger from system by ID
    public String removePassenger(int id) {
        for (Passenger p : ticketAreaQueue) {
            if (p.getPassengerId() == id) {
                ticketAreaQueue.remove(p);
                return "REMOVE: Passenger ID " + id + " removed from TICKET AREA.";
            }
        }

        for (Passenger p : assignAreaQueue) {
            if (p.getPassengerId() == id) {
                assignAreaQueue.remove(p);
                return "REMOVE: Passenger ID " + id + " removed from ASSIGN AREA.";
            }
        }

        return "ERROR: Passenger ID " + id + " not found in either queue for removal.";
    }

    // --- Getters and Setters ---

    // Returns copy of ticket area queue
    public Queue<Passenger> getTicketAreaQueue() {
        return new LinkedList<>(ticketAreaQueue);
    }

    // Returns copy of assign area queue
    public Queue<Passenger> getAssignAreaQueue() {
        return new LinkedList<>(assignAreaQueue);
    }

    // Returns copy of served passengers log
    public List<Passenger> getServedLog() {
        return new ArrayList<>(servedLog);
    }

    // Returns ticket area capacity
    public int getTicketAreaCapacity() {
        return TICKET_AREA_CAPACITY;
    }

    // Returns assign area display capacity
    public int getAssignAreaDisplayCapacity() {
        return ASSIGN_AREA_DISPLAY_CAPACITY;
    }

    // Returns currently assigned bus name
    public String getCurrentlyAssignedBusName() {
        return currentlyAssignedBusName;
    }

    // Returns copy of buses map
    public Map<String, Bus> getBuses() {
        return new LinkedHashMap<>(buses);
    }

    // Returns copy of bus order list
    public List<String> getBusOrder() {
        return new ArrayList<>(busOrder);
    }

    // Gets newly generated bus name and resets it
    public String getNewlyGeneratedBus() {
        String newBus = newlyGeneratedBus;
        newlyGeneratedBus = null;
        return newBus;
    }

    // Checks if current bus can depart (must be full)
    public boolean canDepartBus() {
        Bus currentBus = buses.get(currentlyAssignedBusName);
        return currentBus != null && currentBus.isFull();
    }

    // Checks if current bus is full
    public boolean isCurrentBusFull() {
        Bus currentBus = buses.get(currentlyAssignedBusName);
        return currentBus != null && currentBus.getCurrentLoad() >= currentBus.getCapacity();
    }

    // Assigns a bus to active status
    public void assignBus(String busName) {
        if (buses.containsKey(busName)) {
            this.currentlyAssignedBusName = busName;
            if (busOrder.contains(busName)) {
                busOrder.remove(busName);
                busOrder.add(0, busName);
            }
        }
    }

    // --- Predefined Passenger Methods ---

    // Adds specified number of predefined passengers to ticket area
    public String addPredefinedPassengers(int count) {
        if (predefinedPassengers.isEmpty()) {
            return "ALERT: No more predefined passengers available.";
        }

        int added = 0;
        int failed = 0;

        for (Passenger passenger : predefinedPassengers) {
            if (added >= count)
                break;

            boolean alreadyInSystem = isPassengerInSystem(passenger);

            if (!alreadyInSystem && ticketAreaQueue.size() < TICKET_AREA_CAPACITY) {
                Passenger newPassenger = new Passenger(
                        passenger.getName(),
                        passenger.getDestination(),
                        passenger.getTicketType(),
                        "Cash",
                        passenger.getMoneyPaid());
                ticketAreaQueue.offer(newPassenger);
                added++;
            } else if (alreadyInSystem) {
                failed++;
            }
        }

        String message = "ADDED: " + added + " predefined passenger(s) to Ticket Area.";
        if (failed > 0) {
            message += " " + failed + " passenger(s) were already in the system.";
        }
        if (ticketAreaQueue.size() >= TICKET_AREA_CAPACITY) {
            message += " Ticket Area is now full.";
        }

        return message;
    }

    // Checks if passenger is already in the system
    private boolean isPassengerInSystem(Passenger passenger) {
        for (Passenger p : ticketAreaQueue) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        for (Passenger p : assignAreaQueue) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        for (Passenger p : servedLog) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        return false;
    }

    // Returns count of remaining available predefined passengers
    public int getRemainingPredefinedPassengers() {
        int available = 0;
        for (Passenger passenger : predefinedPassengers) {
            if (!isPassengerInSystem(passenger)) {
                available++;
            }
        }
        return available;
    }

    // Generates info about predefined passengers
    public String getPredefinedPassengersInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== Predefined Passengers (Total: ").append(predefinedPassengers.size()).append(") ===\n");

        for (int i = 0; i < predefinedPassengers.size(); i++) {
            Passenger p = predefinedPassengers.get(i);
            boolean inSystem = isPassengerInSystem(p);
            String status = inSystem ? "[IN SYSTEM]" : "[AVAILABLE]";

            info.append(i + 1).append(". ").append(status).append(" ")
                    .append(p.getName()).append(" -> ").append(p.getDestination())
                    .append(" (").append(p.getTicketType()).append(") - ₱").append(p.getMoneyPaid())
                    .append("\n");
        }

        return info.toString();
    }

    // Returns copy of predefined passengers list
    public List<Passenger> getPredefinedPassengers() {
        return new ArrayList<>(predefinedPassengers);
    }

    // Checks if any predefined passengers are available
    public boolean hasAvailablePredefinedPassengers() {
        return getRemainingPredefinedPassengers() > 0;
    }

    // Debug method - prints bus status to console
    public void printBusStatus() {
        System.out.println("=== Bus Status ===");
        System.out.println("Current assigned bus: " + currentlyAssignedBusName);
        System.out.println("Bus order: " + busOrder);
        System.out.println("Available bus names: " + availableBusNames);
        System.out.println("All buses: " + buses.keySet());
        for (String busName : busOrder) {
            Bus bus = buses.get(busName);
            if (bus != null) {
                System.out.println(busName + ": " + bus.getCurrentLoad() + "/" + bus.getCapacity() +
                        " (Full: " + bus.isFull() + ")");
            }
        }
        System.out.println("==================");
    }
}