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

            switch (requisition.getType()){
                case "S":
                    requireMemory(requisition);
                    break;
                case "L":
                    liberateMemory(requisition);
                    break;
            }
        }

    }

    private void requireMemory( Requisition requisition){

        int availableMemory;

        //check if there is any empty block to allocate
        if(!memoryBlocks.isEmpty()){
            int finalBlockAdress = initialAdress;
            for (MemoryBlock block: memoryBlocks
            ) {
                if (!block.isAllocated()){
                    if (block.getBlockSize() >= requisition.getMemoryRequired()){
                        block.setAllocated(true);
                        block.setUsedMemory(requisition.getMemoryBlock());
                        return;
                    }

                }

                finalBlockAdress = block.getFinalAdress();

            }
            createMemoryBlock(finalBlockAdress, requisition.getMemoryRequired());
        } else{
            createMemoryBlock(this.initialAdress, requisition.getMemoryRequired());

        }

        //check for available size to create new block




    }

    public void createMemoryBlock(int lastAdress, int memoryRequired){
        int availableMemory = this.finalAdress - lastAdress;
        if (availableMemory >= memoryRequired){
            MemoryBlock mb = new MemoryBlock(memoryBlocks.size()+1, lastAdress, memoryRequired);
            mb.setAllocated(true);
            memoryBlocks.add(mb);
        } else {
            System.out.println("External fragmentation!");
            printMemoryblocks();

        }


    }

    private void liberateMemory(Requisition requisition){

        MemoryBlock mb = getMemoryBlock(requisition.getMemoryBlock());
        mb.setUsedMemory(0);
        mb.setAllocated(false);

    }

    private MemoryBlock getMemoryBlock(int blockId){
        for (MemoryBlock mb: memoryBlocks
             ) {

            if (mb.getId() == blockId){
                return mb;
            }

        }
        return null;
    }

    private void printMemoryblocks(){
        for (MemoryBlock mb: memoryBlocks
             ) {
            System.out.println(mb.toString());

        }
    }

}
