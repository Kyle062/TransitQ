package models;

public class Bus {
    private String name;
    private int capacity;
    private int currentLoad;

    public Bus(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.currentLoad = 0;
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

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    // Add toString for better debugging
    @Override
    public String toString() {
        return String.format("Bus{name='%s', load=%d/%d, full=%s}", 
            name, currentLoad, capacity, isFull());
    }
}