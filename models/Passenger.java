package models;

public class Passenger {
    private static int nextId = 1001;
    private int passengerId;
    private String name;
    private String destination;
    private String ticketType;
    private String paymentMethod;
    private String moneyPaid;
    private boolean isPaid;

    public Passenger(String name, String destination, String ticketType, String paymentMethod, String moneyPaid) {
        this.passengerId = nextId++;
        this.name = name;
        this.destination = destination;
        this.ticketType = ticketType;
        this.paymentMethod = paymentMethod;
        this.moneyPaid = moneyPaid;
        this.isPaid = false;
    }

    // Getters and Setters
    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getDestination() {
        return destination;
    }

    public String getTicketType() {
        return ticketType;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    public String getMoneyPaid() {
        return moneyPaid;
    }

    @Override
    public String toString() {
        // Return ID for icon display
        return String.valueOf(passengerId);
    }
}