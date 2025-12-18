package models;

public class Bus {
    private String name;
    private int capacity;
    private int currentLoad;
    private boolean isActive;

    public Bus(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.isActive = false;
    }

    public boolean boardPassenger() {
        if (currentLoad < capacity) {
            currentLoad++;
            return true;
        }
        return false;
    }

    public void resetBus() {
        this.currentLoad = 0;
    }

    public boolean isFull() {
        return currentLoad >= capacity;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int load) {
        this.currentLoad = load;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return String.format("Bus{name='%s', load=%d/%d, full=%s}", 
            name, currentLoad, capacity, isFull());
    }
}