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

    public void boardPassenger() {
        if (currentLoad < capacity) {
            currentLoad++;
        }
    }
    
    public void resetLoad() {
        currentLoad = 0;
    }
}