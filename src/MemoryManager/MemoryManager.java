package MemoryManager;

import java.util.ArrayList;

public class MemoryManager {

    int mode;
    int initialAdress;
    int finalAdress;

    ArrayList<MemoryBlock> memoryBlocks = new ArrayList<MemoryBlock>();

    ArrayList<Requisition> requisitionsToMemory;

    public MemoryManager(int mode, int initialAdress, int finalAdress, ArrayList<Requisition> requisitionsToMemory) {
        this.mode = mode;
        this.initialAdress = initialAdress;
        this.finalAdress = finalAdress;
        this.requisitionsToMemory = requisitionsToMemory;
    }

    public void run(){

        for (Requisition requisition: requisitionsToMemory
             ) {
            int availableMemory;
            //check if there is any empty block to allocate
            if(!memoryBlocks.isEmpty()){
                int finalBlockAdress;
                for (MemoryBlock block: memoryBlocks
                     ) {
                    if (!block.isAllocated()){
                        if (block.getBlockSize() >= requisition.memoryRequired){
                            block.setAllocated(true);
                        }

                    }

                    finalBlockAdress = block.getFinalAdress();
                }
            } else{
                availableMemory = this.finalAdress - this.initialAdress;

            }

            //check for available size to create new block



        }

    }
}
