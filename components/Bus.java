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

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    // Returns true if boarding succeeds, false if bus is full
    public boolean boardPassenger() {
        if (currentLoad < capacity) {
            currentLoad++;
            return true; // boarding successful
        }
        return false; // bus is full
    }

    public void resetLoad() {
        currentLoad = 0;
    }

    @Override
    public String toString() {
        return name + " (" + currentLoad + "/" + capacity + ")";
    }
}
