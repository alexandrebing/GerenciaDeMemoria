package MemoryManager;

import java.util.ArrayList;

public class MemoryManager {

    private int mode;
    private int initialAdress;
    private int finalAdress;

    private ArrayList<MemoryBlock> memoryBlocks = new ArrayList<MemoryBlock>();

    private ArrayList<Requisition> requisitionsToMemory;

    private ArrayList<Requisition> pendingRequisitions = new ArrayList<Requisition>();

    private boolean hasFragmentation = false;

    private int reqSize = 0;

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
                    reqSize = requisition.getMemoryRequired();
                    if(requireMemory(requisition)){
                        reqSize = 0;
                    }
                    if(hasFragmentation){
                        hasFragmentation = false;
                    }
                    break;
                case "L":
                    liberateMemory(requisition);
                    break;
            }
            if(requisition.getRequisitionID() == 16){
                System.out.println();
            }
            printMemoryblocks(reqSize, requisition.getRequisitionID());
            System.out.println();
            System.out.println();

            System.out.println("********** FIM DA INSTRUÇÃO " + requisition.getRequisitionID() + "**********");

            executePendingRequisitions();

        }

        System.out.println();
        System.out.println("**********Final da execução**********");
        printMemoryblocks(0, requisitionsToMemory.size() + 4);

    }

    private void executePendingRequisitions(){
        if (!pendingRequisitions.isEmpty()){
            for (Requisition req: pendingRequisitions
                 ) {
                reqSize = req.getMemoryRequired();
                if(requireMemory(req)){
                    reqSize = 0;
                    req.setPending(false);
                }
                System.out.println();
                System.out.println();
                printMemoryblocks(reqSize,req.getRequisitionID());
                System.out.println();
                System.out.println("********** FIM DA INSTRUÇÃO PENDENTE " + req.getRequisitionID() + "**********");
            }
            ArrayList<Requisition> aux = new ArrayList<Requisition>();
            for (Requisition req: pendingRequisitions
                 ) {
                if(req.isPending()){
                    aux.add(req);
                }

            }
            pendingRequisitions = aux;

        }
    }

    private boolean requireMemory( Requisition requisition){

        //check if there is any empty block to allocate
        if(!memoryBlocks.isEmpty()){
            int finalBlockAdress = initialAdress;
            for (MemoryBlock block: memoryBlocks
            ) {
                if (!block.isAllocated()){
                    if (block.getBlockSize() >= requisition.getMemoryRequired()){
                        block.setAllocated(true);
                        block.setUsedMemory(requisition.getMemoryBlock());
                        if(requisition.isPending()){
                            requisition.setPending(false);
                        }
                        System.out.println();
                        System.out.println("Linha da instrução:" + requisition.getRequisitionID());
                        System.out.println("Memória alocada: " + requisition.getMemoryRequired() + " no bloco de memória " + block.getId());
                        return true;
                    }

                }

                finalBlockAdress = block.getFinalAdress();

            }

            //tries to create a new memory block
            boolean memoryBlockCreated = createMemoryBlock(finalBlockAdress, requisition.getMemoryRequired(), requisition.getRequisitionID());

            //if fails to create memory block and there is not fragmentation
            if (!memoryBlockCreated){

                //pending requisition is created
                if(!requisition.isPending()){
                    requisition.setPending(true);
                    pendingRequisitions.add(requisition);
                }
                return false;
            }
        } else{

            //no previous blocks to check. creates new memory block.
            createMemoryBlock(initialAdress,requisition.getMemoryRequired(), requisition.getRequisitionID());
        }
        return true;

    }

    public boolean createMemoryBlock(int lastAdress, int memoryRequired, int requisitionID){
        int availableMemory = this.finalAdress - lastAdress;
        if (availableMemory >= memoryRequired){
            MemoryBlock mb = new MemoryBlock(memoryBlocks.size()+1, lastAdress, memoryRequired);
            mb.setAllocated(true);
            memoryBlocks.add(mb);
            System.out.println();
            System.out.println("Linha da instrução: " + requisitionID);
            System.out.println("Bloco criado de tamanho: " + memoryRequired);
            return true;
        } else {
            //Check for fragmentation
            if (checkExternalFragmentation() >= memoryRequired) {
                reqSize = memoryRequired;
                hasFragmentation = true;
                System.out.println();
                System.out.println("Linha da instrução: " + requisitionID);
                System.out.println("Não foi possível armazenar a memória para a requisição pois ocorreu fragmentação externa");
            } else{
                //Memory block wasn't created but hasn't occured external fragmentation.
                System.out.println();
                System.out.println("Linha da instrução: " + requisitionID);
                System.out.println("Não foi possível armazenar a memória por falta de memória interna");
            }

        }
        return false;

    }

    private void liberateMemory(Requisition requisition){

        MemoryBlock mb = getMemoryBlock(requisition.getMemoryBlock());
        mb.setUsedMemory(0);
        mb.setAllocated(false);
        System.out.println();
        System.out.println("Linha da instrução: " + requisition.getRequisitionID());
        System.out.println("Bloco " + mb.getId() + " liberou " + mb.getBlockSize());
        checkAdjacentMemory(requisition);

    }

    private void checkAdjacentMemory(Requisition requisition){

        MemoryBlock mb = getMemoryBlock(requisition.getMemoryBlock());

        //check if next block is alocated
        int lastBlockIndex = memoryBlocks.size();
        if (mb.getId()<lastBlockIndex){
            MemoryBlock nextMemoryBlock = memoryBlocks.get(mb.getId());
            if (!nextMemoryBlock.isAllocated()){
                mb.setFinalAdress(nextMemoryBlock.getFinalAdress());
                mb.setBlockSize(mb.getFinalAdress() - mb.getInitialAdress());
                memoryBlocks.remove(nextMemoryBlock);
            }
        }
        //check if previous block is allocaged
        if(mb.getId()>1){
            MemoryBlock previousMemoryBlock = memoryBlocks.get(mb.getId()-2);
            if (!previousMemoryBlock.isAllocated()){
                previousMemoryBlock.setFinalAdress(mb.getFinalAdress());
                previousMemoryBlock.setBlockSize(previousMemoryBlock.getFinalAdress() - previousMemoryBlock.getInitialAdress());
                memoryBlocks.remove(mb);
            }
        }

        checkBlocks();

    }

    private void checkBlocks(){
        int id = 1;
        ArrayList <Integer> gaps = new ArrayList<Integer>();
        for (MemoryBlock b: memoryBlocks
             ) {
            if(id < memoryBlocks.size()-1){
                MemoryBlock nextBlock = memoryBlocks.get(id);
                if (b.getFinalAdress() != nextBlock.getInitialAdress()){
                    gaps.add(id);
                    id++;
                }
            }
            id++;
        }
        if (!gaps.isEmpty()){
            fillMemoryGaps(gaps);
        }

    }

    private void fillMemoryGaps(ArrayList<Integer> gaps){

        for (Integer i: gaps
             ) {
            MemoryBlock previousMemoryBlock = memoryBlocks.get(i - 1);
            int memoryRequired = this.finalAdress - previousMemoryBlock.getFinalAdress();
            if(i< memoryBlocks.size()){
                MemoryBlock nextBlock = memoryBlocks.get(i + 1);

                memoryRequired = nextBlock.getInitialAdress() - previousMemoryBlock.getFinalAdress();
            }
            MemoryBlock mb = new MemoryBlock(i, previousMemoryBlock.getFinalAdress(), memoryRequired);
            memoryBlocks.add(i-1, mb);

        }
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

    private void printMemoryblocks(int requisitionSize, int requisitionID){
        int lastMemoryAddress = 0;
        System.out.println();
        System.out.println("Estado da memória após instrução " + requisitionID);
        System.out.println();

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
        if ((requisitionSize > 0) && (availableMemoryInBlocks >= requisitionSize)){
            System.out.println();
            System.out.println(availableMemoryInBlocks + " livres, " + requisitionSize + " solicitados - fragmentação externa." );
            System.out.println();
        } else if (requisitionSize > 0){
            System.out.println();
            System.out.println("Sem memória disponível para requisição de tamanho: " + requisitionSize +". Espaço disponível em memória: " + availableMemoryInBlocks);
            System.out.println();
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
