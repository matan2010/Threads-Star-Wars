package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        //initializng output parameter to get data from json.
        Input input = null;
        while (input == null) {
            try {

                Gson gson = new Gson();
                try (Reader reader = new FileReader(args[0])) {
                    input = gson.fromJson(reader, Input.class);

                } catch (Exception e) {
                }

            } catch (Exception e) {
            }
        }


        //creating MSs & store them in an Array
        LeiaMicroservice leiaMicroservice = new LeiaMicroservice(input.getAttacks());
        HanSoloMicroservice hanSoloMicroservice = new HanSoloMicroservice();
        C3POMicroservice c3POMicroservice = new C3POMicroservice();
        R2D2Microservice r2D2Microservice = new R2D2Microservice(input.getR2D2());
        LandoMicroservice landoMicroservice = new LandoMicroservice(input.getLando());

        final int NUMBER_OF_MICROSERVICES = 5;

        MicroService[] microServicesArray = new MicroService[5];
        microServicesArray[0] = hanSoloMicroservice;
        microServicesArray[1] = c3POMicroservice;
        microServicesArray[2] = r2D2Microservice;
        microServicesArray[3] = landoMicroservice;
        microServicesArray[4] = leiaMicroservice;

        //ewoks Initialization
        int numberOfEwoks = input.getEwoks();
        Ewoks ewoks = Ewoks.getInstance();
        //creating list of ewoks with serial numbers from 1 to numberOfEwoks
        Ewok[] ewokListToSet = new Ewok[numberOfEwoks + 1];
        for (int i = 0; i < numberOfEwoks; i++) {
            ewokListToSet[i + 1] = new Ewok(i + 1);
        }
        //set this list to the main ewoks storage of the program.
        ewoks.setEwokArrayList(Arrays.asList(ewokListToSet));

        Thread[] threadsArray = new Thread[NUMBER_OF_MICROSERVICES];
        //create threads in the threadsArray
        for (int i = 0; i < NUMBER_OF_MICROSERVICES; i++) {
            threadsArray[i] = new Thread(microServicesArray[i]);
        }
        //starting threads
        for (Thread thread : threadsArray) {
            thread.start();
        }

        //PROCESS HAPPENING


        //joining all threads to get Diary data ready.
        for (Thread thread : threadsArray) {
            thread.join();
        }

        //Diary data collected and converting to output.json.
        Diary diary = Diary.getInstance();

        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output.json")){
            gsonBuilder.toJson(Diary.getInstance(),writer);}catch (IOException neverMind){
        }

    }
}
