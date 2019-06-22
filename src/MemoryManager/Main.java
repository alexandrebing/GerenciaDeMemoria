package MemoryManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world");


        ArrayList<Requisition> memoryAccessRequisitions = new ArrayList<Requisition>();

        //READ DATA FROM FILE

        //READING FROM FILE
        Scanner s = new Scanner(System.in);
        System.out.println("Digite o nome do arquivo de entrada (sem extensão .txt)");
        String enter = s.nextLine();
        System.out.println();
        System.out.println("====================================================================================");
        File file = new File("src/"+ enter + ".txt");
        BufferedReader in = new BufferedReader(new FileReader(file));

        System.out.println("Entrada lida");

        //is always equal 1
        int memoryMode = Integer.parseInt(in.readLine());

        int initialAdress = Integer.parseInt(in.readLine());

        int finalAdress = Integer.parseInt(in.readLine());

        //Create requisitions

        String line;

        while ((line = in.readLine()) != null){

            String data [] = line.split(" ");

            Requisition r = new Requisition(data[0], Integer.parseInt(data[1]));

            memoryAccessRequisitions.add(r);

            //System.out.println(r.toString());

        }

        // Run memory manager according to data

        MemoryManager mr = new MemoryManager(memoryMode, initialAdress, finalAdress, memoryAccessRequisitions);

        mr.run();






    }

}
