
import org.encog.ml.data.basic.BasicMLData;
import scr.SensorModel;
import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.*;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import java.io.*;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public class NeuralNetwork implements Serializable {


    /*
    * Fields for the neural network
    * */
    private int inputs = 21;
    private int hidden0 = 48;
    private int hidden1 = 66;
    private int hidden2 = 48;
    private int output = 3;
    private double tolerance = 0.001;
    private BasicNetwork loadFromFileNetwork; // pointer to a Basicnetwork object for reloading a network
    private BasicNetwork network; // deprecated... was used to create a neural network object



    /*
    * constructor if the boolean comes in true the network wil train.
    * if the boolean coms in false the network will be loaded from file
    * if anthor network needs to be loaded simoply change the getlastsavednetwork() method to the path of the neural network file path
    * the file needs to be saved with the saveobject function of encog.
     * */
    public NeuralNetwork(boolean train) { //
        if (!train) { //
            loadFromFileNetwork = (BasicNetwork) loadObject(new File(getLastSavedNetwork()));
        } else if (train) {
            loadFromFileNetwork = (BasicNetwork) loadObject(new File(getLastSavedNetwork()));
            train();
        }
    }


    public String getLastSavedNetwork() { //Gives the last modified file to be loaded in as a neural network
        String downloadFolder = "nn/";

        File dir = new File(downloadFolder);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("no files amk");
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        String k = lastModifiedFile.toString();

        System.out.println(k);
        return k;
    }

    // loads the dataset in.
    // makes a resillientpropagation object to learn
    //returns the rprop object
    public ResilientPropagation dataSet() { //

        final MLDataSet training = TrainingSetUtil.loadCSVTOMemory(CSVFormat.DECIMAL_POINT, "resources/trackmulti.csv", true, 22, 3);
        ResilientPropagation learner = new ResilientPropagation(loadFromFileNetwork, training);
        return learner;
    }

    //depreciated... was once used to timestamp neuralnetworks files
    public String giveDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd__hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    //training method
    public void train() {
        String filename = "nn/encog_Neural_";
        ResilientPropagation learner = dataSet();

        while (true) {
            learner.iteration();
            System.out.println("Error: " + learner.getError());
            System.out.println("Epoch: " + learner.getIteration());

            if (learner.getIteration() % 1000 == 0) {
                TrainingContinuation saved_network = learner.pause();
                saveObject(new File("Encog_Neural_"+learner.getIteration()), loadFromFileNetwork);
                System.out.println("saved network");
                learner.resume(saved_network);
            }
            if (learner.getError() < tolerance) {
                learner.pause();
                saveObject(new File("nn/backup" + learner.getIteration()), loadFromFileNetwork);
                System.out.println("Done");
                learner.finishTraining();
                saveObject(new File("nn/done" + learner.getIteration()), loadFromFileNetwork);
                break;
            }
        }
    }

    // method to parse the current state of the game and vectorize it to the network
    public double getOutput(SensorModel sensors, String key) {
        // preparing data
        double trackPosition = sensors.getTrackPosition();
        double trackAngle = sensors.getAngleToTrackAxis();
        double speed = sensors.getSpeed();
        double[] trackEdges = sensors.getTrackEdgeSensors();
        double[] input = new double[22];
        input[0] = speed;
        input[1] = trackPosition;
        input[2] = trackAngle;
        //putting all data into array in the right order
        for (int i = 3; i < input.length; i++) {
            input[i] = trackEdges[i - 3];
        }
        // parsing data into a vector to be fed to the neural network
        BasicMLData nn_input = new BasicMLData(input);
        //feeding the vector to the neural network
        loadFromFileNetwork.compute(nn_input);
        //switch statement to get the desired outcome.
        switch (key) {
            case "acceleration":
                return loadFromFileNetwork.getLayerOutput(4, 0);
            case "brake":
                return loadFromFileNetwork.getLayerOutput(4, 1);
            case "steering":
                return loadFromFileNetwork.getLayerOutput(4, 2);
            default:
                break;
        }
        // def  ault return vallue;
        return 0.5;
    }

    //depreciated... used to build the network
    public void addLayers() {

        network.addLayer(new BasicLayer(null, true, inputs));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, hidden0));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, hidden1));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, hidden2));
        network.addLayer(new BasicLayer(new ActivationTANH(), false, output));
        network.getStructure().finalizeStructure(); // finalizing network
        network.reset();
    }

 }





