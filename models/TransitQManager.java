package models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class TransitQManager {
    private Queue<Passenger> ticketAreaQueue;
    private Queue<Passenger> assignAreaQueue;
    private List<Passenger> servedLog;

    private final int TICKET_AREA_CAPACITY = 15;
    private final int ASSIGN_AREA_DISPLAY_CAPACITY = 15;
    private Map<String, Bus> buses;
    private String currentlyAssignedBusName;
    private List<String> busOrder; // Maintain bus order for rotation
    private List<String> availableBusNames; // New bus names for rotation
    private String newlyGeneratedBus; // Track newly generated bus for GUI updates

    // Predefined passengers
    private List<Passenger> predefinedPassengers;

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
        this.currentlyAssignedBusName = busOrder.get(0); // First bus is assigned

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

        // Add some predefined passengers to ticket area initially
        initializeWithPredefinedPassengers();
    }

    // --- Predefined Passengers Creation ---
    private List<Passenger> createPredefinedPassengers() {
        List<Passenger> passengers = new ArrayList<>();

        passengers.add(new Passenger("John Smith", "Downtown", "Standard", "Cash", "50.00"));
        passengers.add(new Passenger("Maria Garcia", "Airport", "VIP", "Cash", "120.00"));
        passengers.add(new Passenger("David Johnson", "University", "Discounted", "Cash", "35.00"));
        passengers.add(new Passenger("Sarah Williams", "Shopping Mall", "Standard", "Cash", "45.00"));
        passengers.add(new Passenger("Michael Brown", "Hospital", "VIP", "Cash", "150.00"));
        passengers.add(new Passenger("Emily Davis", "Beach", "Standard", "Cash", "60.00"));
        passengers.add(new Passenger("Robert Miller", "Stadium", "Discounted", "Cash", "40.00"));
        passengers.add(new Passenger("Lisa Wilson", "Convention Center", "Standard", "Cash", "55.00"));
        passengers.add(new Passenger("James Taylor", "Train Station", "VIP", "Cash", "110.00"));
        passengers.add(new Passenger("Jennifer Anderson", "City Center", "Standard", "Cash", "48.00"));
        passengers.add(new Passenger("Thomas Martinez", "Amusement Park", "Discounted", "Cash", "42.00"));
        passengers.add(new Passenger("Susan Thompson", "Business District", "Standard", "Cash", "65.00"));

        return passengers;
    }

    // --- Initialize with some predefined passengers ---
    private void initializeWithPredefinedPassengers() {
        // Add first 5 passengers to ticket area
        for (int i = 0; i < 12 && i < predefinedPassengers.size(); i++) {
            Passenger passenger = predefinedPassengers.get(i);
            if (ticketAreaQueue.size() < TICKET_AREA_CAPACITY) {
                ticketAreaQueue.offer(passenger);
            }
        }
    }

    // --- Method to add predefined passengers to ticket area ---
    public String addPredefinedPassengers(int count) {
        if (predefinedPassengers.isEmpty()) {
            return "ALERT: No more predefined passengers available.";
        }

        int added = 0;
        int failed = 0;

        for (Passenger passenger : predefinedPassengers) {
            if (added >= count)
                break;

            // Check if passenger is already in any queue
            boolean alreadyInSystem = isPassengerInSystem(passenger);

            if (!alreadyInSystem && ticketAreaQueue.size() < TICKET_AREA_CAPACITY) {
                // Create a copy to avoid reference issues
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

    // --- Check if passenger is already in the system ---
    private boolean isPassengerInSystem(Passenger passenger) {
        // Check ticket area
        for (Passenger p : ticketAreaQueue) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        // Check assign area
        for (Passenger p : assignAreaQueue) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        // Check served log
        for (Passenger p : servedLog) {
            if (p.getName().equals(passenger.getName()) &&
                    p.getDestination().equals(passenger.getDestination())) {
                return true;
            }
        }

        return false;
    }

    // --- Get remaining predefined passengers count ---
    public int getRemainingPredefinedPassengers() {
        int available = 0;
        for (Passenger passenger : predefinedPassengers) {
            if (!isPassengerInSystem(passenger)) {
                available++;
            }
        }
        return available;
    }

    // --- Get all predefined passengers info ---
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

    // --- Core Operations ---
    public String addPassengerToTicketArea(Passenger p) {
        if (ticketAreaQueue.size() >= TICKET_AREA_CAPACITY) {
            return "ALERT: TICKET AREA FULL! Cannot add new passenger.";
        }
        ticketAreaQueue.offer(p);
        return "ENQUEUE: Added to Ticket Area. ID: " + p.getPassengerId() +
                ". Queue: " + ticketAreaQueue.size() + "/" + TICKET_AREA_CAPACITY;
    }

    public String passPassengerToAssignArea() {
        if (ticketAreaQueue.isEmpty()) {
            return "ERROR: TICKET AREA is empty. No passenger to process.";
        }
        if (assignAreaQueue.size() >= ASSIGN_AREA_DISPLAY_CAPACITY) {
            return "ALERT: ASSIGN PASSENGER AREA FULL! Board a passenger first.";
        }

        Passenger p = ticketAreaQueue.poll();
        if (p != null) {
            p.setPaid(true); // Simulate payment verification
            assignAreaQueue.offer(p);
            return "PASS: Passenger ID " + p.getPassengerId() + " moved to ASSIGN AREA. Assigned Bus: "
                    + currentlyAssignedBusName;
        }
        return "ERROR: Failed to pass passenger (unexpected error).";
    }

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
    public String departBus() {
        Bus currentBus = buses.get(currentlyAssignedBusName);

        if (currentBus == null) {
            return "ERROR: No bus to depart.";
        }

        if (currentBus.getCurrentLoad() == 0) {
            return "ALERT: " + currentBus.getName() + " is empty. No need to depart.";
        }

        // Record departure
        String departureMessage = "DEPARTED: " + currentBus.getName() + " has departed with " +
                currentBus.getCurrentLoad() + " passengers.";

        // Reset the departed bus
        currentBus.resetBus();

        // Remove the departed bus from the front of the queue
        String departedBusName = busOrder.remove(0);

        // Add new bus if available, otherwise add the departed bus back to the end
        String newBusName;
        if (!availableBusNames.isEmpty()) {
            newBusName = availableBusNames.remove(0);
            buses.put(newBusName, new Bus(newBusName, 10));
            busOrder.add(newBusName);
            newlyGeneratedBus = newBusName; // Set the newly generated bus for GUI
        } else {
            // If no new buses available, put the departed bus at the end
            busOrder.add(departedBusName);
            newlyGeneratedBus = null; // No new bus generated
        }

        // Assign the new first bus
        currentlyAssignedBusName = busOrder.get(0);

        return departureMessage + " New active bus: " + currentlyAssignedBusName;
    }

    // --- New method to get newly generated bus for GUI updates ---
    public String getNewlyGeneratedBus() {
        String newBus = newlyGeneratedBus;
        newlyGeneratedBus = null; // Reset after retrieval
        return newBus;
    }

    public boolean canDepartBus() {
        Bus currentBus = buses.get(currentlyAssignedBusName);
        return currentBus != null && currentBus.isFull();
    }

    public boolean isCurrentBusFull() {
        Bus currentBus = buses.get(currentlyAssignedBusName);
        return currentBus != null && currentBus.getCurrentLoad() >= currentBus.getCapacity();
    }

    public List<String> getBusOrder() {
        return new ArrayList<>(busOrder);
    }

    // --- Utility Methods ---
    public Passenger searchPassenger(String searchInput) {
        try {
            int id = Integer.parseInt(searchInput);
            return searchPassengerById(id);
        } catch (NumberFormatException e) {
            return searchPassengerByName(searchInput);
        }
    }

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

    public String removePassenger(int id) {
        // Remove from ticket area
        for (Passenger p : ticketAreaQueue) {
            if (p.getPassengerId() == id) {
                ticketAreaQueue.remove(p);
                return "REMOVE: Passenger ID " + id + " removed from TICKET AREA.";
            }
        }

        // Remove from assign area
        for (Passenger p : assignAreaQueue) {
            if (p.getPassengerId() == id) {
                assignAreaQueue.remove(p);
                return "REMOVE: Passenger ID " + id + " removed from ASSIGN AREA.";
            }
        }

        return "ERROR: Passenger ID " + id + " not found in either queue for removal.";
    }

    // --- Getters and Setters ---
    public Queue<Passenger> getTicketAreaQueue() {
        return new LinkedList<>(ticketAreaQueue);
    }

    public Queue<Passenger> getAssignAreaQueue() {
        return new LinkedList<>(assignAreaQueue);
    }

    public List<Passenger> getServedLog() {
        return new ArrayList<>(servedLog);
    }

    public int getTicketAreaCapacity() {
        return TICKET_AREA_CAPACITY;
    }

    public int getAssignAreaDisplayCapacity() {
        return ASSIGN_AREA_DISPLAY_CAPACITY;
    }

    public String getCurrentlyAssignedBusName() {
        return currentlyAssignedBusName;
    }

    public Map<String, Bus> getBuses() {
        return new LinkedHashMap<>(buses);
    }

    public void assignBus(String busName) {
        if (buses.containsKey(busName)) {
            this.currentlyAssignedBusName = busName;
            // Ensure this bus is at the front of the queue
            if (busOrder.contains(busName)) {
                busOrder.remove(busName);
                busOrder.add(0, busName);
            }
        }
    }

    // --- Debug method to check bus status ---
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

    // --- New methods for predefined passengers ---
    public List<Passenger> getPredefinedPassengers() {
        return new ArrayList<>(predefinedPassengers);
    }

    public boolean hasAvailablePredefinedPassengers() {
        return getRemainingPredefinedPassengers() > 0;
    }
}