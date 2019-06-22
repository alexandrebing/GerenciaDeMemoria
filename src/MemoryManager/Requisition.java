package MemoryManager;

public class Requisition {

    int requisitionID;
    String type;
    int memoryRequired;
    int memoryBlock;

    public Requisition(String type, int data) {
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
}
