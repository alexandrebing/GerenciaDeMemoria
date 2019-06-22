package MemoryManager;

public class MemoryBlock {


    private int id;
    private int initialAdress;
    private int finalAdress;
    private int usedMemory;
    private boolean isAllocated;

    public MemoryBlock(int id, int initialAdress, int size) {
        this.id = id;
        this.initialAdress = initialAdress;
        this.finalAdress = initialAdress + size;
        this.usedMemory = size;
    }

    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    public int getBlockSize(){
        return finalAdress - initialAdress;
    }

    public int getFinalAdress() {
        return finalAdress;
    }

    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public int getUnusedMemory(){
        return (finalAdress - initialAdress) - usedMemory;
    }
}
