package MemoryManager;

import java.util.ArrayList;

public class MemoryManager {

    private int mode;
    private int initialAdress;
    private int finalAdress;

    private ArrayList<MemoryBlock> memoryBlocks = new ArrayList<MemoryBlock>();

    private ArrayList<Requisition> requisitionsToMemory;

    private ArrayList<Requisition> pendingRequisitions = new ArrayList<Requisition>();

    public MemoryManager(int mode, int initialAdress, int finalAdress, ArrayList<Requisition> requisitionsToMemory) {
        this.mode = mode;
        this.initialAdress = initialAdress;
        this.finalAdress = finalAdress;
        this.requisitionsToMemory = requisitionsToMemory;
    }

    public void run(){

        for (Requisition requisition: requisitionsToMemory
             ) {

            executePendingRequisitions();

            switch (requisition.getType()){
                case "S":
                    requireMemory(requisition);
                    break;
                case "L":
                    liberateMemory(requisition);
                    break;
            }
        }
        printMemoryblocks(0);

    }

    private void executePendingRequisitions(){
        if (!pendingRequisitions.isEmpty()){
            for (Requisition req: pendingRequisitions
                 ) {
                requireMemory(req);
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

            //tries to create a new memory block
            boolean memoryBlockCreated = createMemoryBlock(finalBlockAdress, requisition.getMemoryRequired());

            //if fails to create memory block and there is not fragmentation
            if (!memoryBlockCreated){

                if(!requisition.isPending()){
                    requisition.setPending(true);
                    pendingRequisitions.add(requisition);
                }
            } else { //pending requisition is created
                if (requisition.isPending()){
                    requisition.setPending(false);
                    pendingRequisitions.remove(requisition);
                }
            }
        } else{

            //no previous blocks to check. creates new memory block.
            createMemoryBlock(initialAdress,requisition.getMemoryRequired());
        }

    }

    public boolean createMemoryBlock(int lastAdress, int memoryRequired){
        int availableMemory = this.finalAdress - lastAdress;
        if (availableMemory >= memoryRequired){
            MemoryBlock mb = new MemoryBlock(memoryBlocks.size()+1, lastAdress, memoryRequired);
            mb.setAllocated(true);
            memoryBlocks.add(mb);
            return true;
        } else {
            //Check for fragmentation
            if (checkExternalFragmentation() >= memoryRequired) {
                printMemoryblocks(memoryRequired);
                System.exit(0);
            }

        }
        return false;

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

    private void printMemoryblocks(int requisitionSize){
        int lastMemoryAddress = 0;
        for (MemoryBlock mb: memoryBlocks
             ) {
            System.out.println(mb.toString());
            lastMemoryAddress = mb.getFinalAdress();
        }

        int freeMemory = finalAdress - lastMemoryAddress;

        if (freeMemory > 0){
            System.out.println(lastMemoryAddress + " - " + finalAdress + " livre " + "(tamanho " + (finalAdress - lastMemoryAddress) + ")");
        }

        int availableMemoryInBlocks = checkExternalFragmentation();
        if (availableMemoryInBlocks > 0){
            System.out.println();
            System.out.println(availableMemoryInBlocks + " livres, " + requisitionSize + " solicitados - fragmentação externa." );
        }
    }

    private int checkExternalFragmentation(){

        int freeMemory = 0;
        int finalAddress = 0;
        for (MemoryBlock mb: memoryBlocks
             ) {
            if (!mb.isAllocated()){
                freeMemory += mb.getBlockSize();
            }
            finalAddress = mb.getFinalAdress();
        }
        int availableMemory = this.finalAdress - finalAddress;
        freeMemory += availableMemory;
        return freeMemory;
    }

}
