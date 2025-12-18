package models;

public class Passenger {
    private int passengerId;
    private String name;
    private String destination;
    private String ticketType;
    private String paymentMethod;
    private String moneyPaid;
    private boolean isPaid;
    private String status;
    private String lineType;
    private int positionInLine;
    private String assignedBus;

    // Constructor for new passengers
    public Passenger(String name, String destination, String ticketType, 
                    String paymentMethod, String moneyPaid) {
        this.name = name;
        this.destination = destination;
        this.ticketType = ticketType;
        this.paymentMethod = paymentMethod;
        this.moneyPaid = moneyPaid;
        this.isPaid = false;
        this.status = "TicketArea";
        
        // Determine line type
        if ("VIP".equalsIgnoreCase(ticketType)) {
            this.lineType = "VIP";
        } else {
            this.lineType = "REGULAR";
        }
        this.positionInLine = 0;
        this.assignedBus = null;
    }
    
    // Constructor for database retrieval
    public Passenger(int id, String name, String destination, String ticketType, 
                    String paymentMethod, String moneyPaid, boolean isPaid, 
                    String status, String lineType, int positionInLine) {
        this.passengerId = id;
        this.name = name;
        this.destination = destination;
        this.ticketType = ticketType;
        this.paymentMethod = paymentMethod;
        this.moneyPaid = moneyPaid;
        this.isPaid = isPaid;
        this.status = status;
        this.lineType = lineType;
        this.positionInLine = positionInLine;
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

    public String getStatus() {
        return status;
    }
    
    public String getLineType() {
        return lineType;
    }
    
    public int getPositionInLine() {
        return positionInLine;
    }
    
    public String getAssignedBus() {
        return assignedBus;
    }
    
    public boolean isVIP() {
        return "VIP".equalsIgnoreCase(ticketType);
    }

    // Setters
    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
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

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setMoneyPaid(String moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }
    
    public void setPositionInLine(int positionInLine) {
        this.positionInLine = positionInLine;
    }
    
    public void setAssignedBus(String assignedBus) {
        this.assignedBus = assignedBus;
    }

    @Override
    public String toString() {
        return String.valueOf(passengerId);
    }

    public String toDetailedString() {
        return String.format("Passenger{id=%d, name='%s', dest='%s', ticket='%s', " +
                           "paid='%s', amount='%s', line='%s', status='%s'}",
                passengerId, name, destination, ticketType, paymentMethod, 
                moneyPaid, lineType, status);
    }
}