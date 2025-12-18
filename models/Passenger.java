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
        this.isPaid = false; // Initialize as unpaid
    }

    // Getters
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getMoneyPaid() {
        return moneyPaid;
    }

    public boolean isPaid() {
        return isPaid;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setMoneyPaid(String moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    @Override
    public String toString() {
        return String.valueOf(passengerId);
    }

    // Detailed toString for reports
    public String toDetailedString() {
        return String.format("Passenger{id=%d, name='%s', dest='%s', ticket='%s', paid='%s', amount='%s', verified=%s}",
                passengerId, name, destination, ticketType, paymentMethod, moneyPaid, isPaid);
    }
}