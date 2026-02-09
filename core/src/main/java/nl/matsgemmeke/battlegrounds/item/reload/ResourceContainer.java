package nl.matsgemmeke.battlegrounds.item.reload;

/**
 * Describes how many resources a reloadable item can store.
 */
public class ResourceContainer {

    private int capacity;
    private int loadedAmount;
    private int reserveAmount;
    private int maxReserveAmount;

    public ResourceContainer(int capacity, int loadedAmount, int reserveAmount, int maxReserveAmount) {
        this.capacity = capacity;
        this.loadedAmount = loadedAmount;
        this.reserveAmount = reserveAmount;
        this.maxReserveAmount = maxReserveAmount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLoadedAmount() {
        return loadedAmount;
    }

    public void setLoadedAmount(int loadedAmount) {
        this.loadedAmount = loadedAmount;
    }

    public int getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(int reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public int getMaxReserveAmount() {
        return maxReserveAmount;
    }

    public void setMaxReserveAmount(int maxReserveAmount) {
        this.maxReserveAmount = maxReserveAmount;
    }
}
