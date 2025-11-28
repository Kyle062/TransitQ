// In Bus.java - Add the isFull() method
package components;

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

    // Add reset method
    public void resetBus() {
        this.currentLoad = 0;
    }

    // Add isFull method
    public boolean isFull() {
        return currentLoad >= capacity;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }
}