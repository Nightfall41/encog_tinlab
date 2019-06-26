import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

public class DefaultDriver extends AbstractDriver {

    private NeuralNetwork neuralNetwork;

    public DefaultDriver() {
        initialize();
        neuralNetwork = new NeuralNetwork(true);
    }

    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        this.enableExtras(new AutomatedRecovering());
        this.enableExtras(new ABS());
    }

    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome myGenome = (DefaultDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        //double[] sensorArray = new double[4];
        double output = neuralNetwork.getOutput(sensors,"acceleration");
        return output;
    }

    @Override
    public double getSteering(SensorModel sensors) {
        Double output = neuralNetwork.getOutput(sensors,"steering");
        return output;
    }


    @Override
    public String getDriverName() {
        return "Baudet Racing Team";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        if (action == null) {
            action = new Action();
        }
        action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);

        action.accelerate=getAcceleration(sensors);
        action.steering=getSteering(sensors);
        action.brake=neuralNetwork.getOutput(sensors,"brake");

        double [] trackedge=sensors.getTrackEdgeSensors();

    /*    for (int i=0;i<trackedge.length;i++){
            System.out.println(trackedge[i]+" sensor "+ i);
        }
*/


/*        if (sensors.getSpeed() > 60.0D) {
            action.accelerate = getAcceleration(sensors);
            action.brake = 0.0D;
        }

       if (sensors.getSpeed() > 70.0D) {
            action.accelerate = 0.0D;
            action.brake = -1.0D;
        }

        if (sensors.getSpeed() < 350.0D) {
            action.accelerate = (350.0D - sensors.getSpeed()) / 350.0D;
            action.brake = 0.0D;
        }

        if (sensors.getSpeed() < 30.0D) {
            action.accelerate = 1.0D;
            action.brake = 0.0D;
        }*/
        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}