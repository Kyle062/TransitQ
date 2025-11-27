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

    private final int TICKET_AREA_CAPACITY = 15;
    private final int ASSIGN_AREA_DISPLAY_CAPACITY = 15;
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

        // Seed predefined passengers so UI starts with some data
        seedPredefinedPassengers();
    }

    private void seedPredefinedPassengers() {
        // Only add if there's space (and avoid seeding multiple times if constructor
        // called repeatedly)
        if (ticketAreaQueue.size() >= TICKET_AREA_CAPACITY)
            return;

        // Passenger 1-3 (Your original pattern)
        Passenger p1 = new Passenger("Alice Santos", "Downtown", "Standard", "Cash");
        Passenger p2 = new Passenger("Mark Ruiz", "Airport", "VIP", "Card");
        Passenger p3 = new Passenger("Carla Reyes", "Market", "Discounted", "Cash");

        // Passenger 4-6 (Repeating the pattern)
        Passenger p4 = new Passenger("Alice Santos", "Downtown", "Standard", "Cash");
        Passenger p5 = new Passenger("Mark Ruiz", "Airport", "VIP", "Card");
        Passenger p6 = new Passenger("Carla Reyes", "Market", "Discounted", "Cash");

        // Passenger 7-9
        Passenger p7 = new Passenger("Alice Santos", "Downtown", "Standard", "Cash");
        Passenger p8 = new Passenger("Mark Ruiz", "Airport", "VIP", "Card");
        Passenger p9 = new Passenger("Carla Reyes", "Market", "Discounted", "Cash");

        // Passenger 10-12
        Passenger p10 = new Passenger("Alice Santos", "Downtown", "Standard", "Cash");
        Passenger p11 = new Passenger("Mark Ruiz", "Airport", "VIP", "Card");
        Passenger p12 = new Passenger("Carla Reyes", "Market", "Discounted", "Cash");

      

        // Offer them into queue while capacity permits
        addPassengerToTicketArea(p1);
        addPassengerToTicketArea(p2);
        addPassengerToTicketArea(p3);
        addPassengerToTicketArea(p4);
        addPassengerToTicketArea(p5);
        addPassengerToTicketArea(p6);
        addPassengerToTicketArea(p7);
        addPassengerToTicketArea(p8);
        addPassengerToTicketArea(p9);
        addPassengerToTicketArea(p10);
        addPassengerToTicketArea(p11);
        addPassengerToTicketArea(p12);
       
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
            // Use return value of boardPassenger to be safe
            boolean boardedOk = assignedBus.boardPassenger();
            if (boardedOk) {
                servedLog.add(boarded);
                return "BOARDED: Passenger ID " + boarded.getPassengerId() + " has boarded " + currentlyAssignedBusName
                        +
                        ". Load: " + assignedBus.getCurrentLoad() + "/" + assignedBus.getCapacity();
            } else {
                // In case bus was filled between checks, put passenger back to the assign area
                assignAreaQueue.offer(boarded);
                return "ALERT: " + assignedBus.getName()
                        + " became full before boarding. Passenger returned to assign area.";
            }
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
