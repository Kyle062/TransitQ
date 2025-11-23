package components;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransitQManager {
    private Queue<Passenger> ticketAreaQueue;
    private Queue<Passenger> assignAreaQueue;
    private List<Passenger> servedLog;

    private final int TICKET_AREA_CAPACITY = 6;
    private final int ASSIGN_AREA_DISPLAY_CAPACITY = 6;
    private Map<String, Bus> buses;
    private String currentlyAssignedBusName;

    public TransitQManager() {
        this.ticketAreaQueue = new LinkedList<>();
        this.assignAreaQueue = new LinkedList<>();
        this.servedLog = new ArrayList<>();

        this.buses = new HashMap<>();
        buses.put("BUS A", new Bus("BUS A", 10));
        buses.put("BUS B", new Bus("BUS B", 10));
        buses.put("BUS C", new Bus("BUS C", 10));
        buses.put("BUS D", new Bus("BUS D", 10));

        this.currentlyAssignedBusName = "BUS A";
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
            return "ALERT: " + assignedBus.getName() + " is full! Change assigned bus or wait.";
        }

        Passenger boarded = assignAreaQueue.poll();
        if (boarded != null) {
            assignedBus.boardPassenger();
            servedLog.add(boarded);
            return "BOARDED: Passenger ID " + boarded.getPassengerId() + " has boarded " + currentlyAssignedBusName +
                    ". Load: " + assignedBus.getCurrentLoad() + "/" + assignedBus.getCapacity();
        }
        return "ERROR: Failed to board passenger (unexpected error).";
    }

    // --- Utility Methods ---
    public Passenger searchPassenger(String searchInput) {
        try {
            int id = Integer.parseInt(searchInput);
            return ticketAreaQueue.stream().filter(p -> p.getPassengerId() == id).findFirst().orElse(
                    assignAreaQueue.stream().filter(p -> p.getPassengerId() == id).findFirst().orElse(null));
        } catch (NumberFormatException e) {
            return ticketAreaQueue.stream().filter(p -> p.getName().equalsIgnoreCase(searchInput)).findFirst().orElse(
                    assignAreaQueue.stream().filter(p -> p.getName().equalsIgnoreCase(searchInput)).findFirst()
                            .orElse(null));
        }
    }

    public String updatePassenger(int id, String newName, String newDest, String newTicketType) {
        Passenger p = searchPassenger(String.valueOf(id));
        if (p != null) {
            p.setName(newName);
            p.setDestination(newDest);
            p.setTicketType(newTicketType);
            return "UPDATE: Passenger ID " + id + " updated successfully. New Name: " + newName;
        }
        return "ERROR: Passenger ID " + id + " not found for update.";
    }

    public String removePassenger(int id) {
        boolean removed = ticketAreaQueue.removeIf(p -> p.getPassengerId() == id);
        if (removed)
            return "REMOVE: Passenger ID " + id + " removed from TICKET AREA.";

        removed = assignAreaQueue.removeIf(p -> p.getPassengerId() == id);
        if (removed)
            return "REMOVE: Passenger ID " + id + " removed from ASSIGN AREA.";

        return "ERROR: Passenger ID " + id + " not found in either queue for removal.";
    }

    // --- Getters and Setters ---
    public Queue<Passenger> getTicketAreaQueue() {
        return ticketAreaQueue;
    }

    public Queue<Passenger> getAssignAreaQueue() {
        return assignAreaQueue;
    }

    public List<Passenger> getServedLog() {
        return servedLog;
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
        return buses;
    }

    public void assignBus(String busName) {
        this.currentlyAssignedBusName = busName;
    }
}