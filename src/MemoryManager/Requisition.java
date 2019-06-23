package MemoryManager;

public class Requisition {

    private int requisitionID;
    private String type;
    private int memoryRequired;
    private int memoryBlock;

    public Requisition(int id, String type, int data) {
        this.requisitionID = id;
        this.type = type;

        switch (this.type){
            case "S":
                this.memoryRequired = data;
                this.memoryBlock = -1;
                return;
            case "L":
                this.memoryBlock = data;
                this.memoryRequired = -1;
                return;
        }
    }

    @Override
    public String toString() {
        return "Requisition{" +
                "requisitionID=" + requisitionID +
                ", type='" + type + '\'' +
                ", memoryRequired=" + memoryRequired +
                ", memoryBlock=" + memoryBlock +
                '}';
    }

    public int getMemoryBlock() {
        return memoryBlock;
    }

    public int getMemoryRequired() {
        return memoryRequired;
    }

    public String getType() {
        return type;
    }
}
