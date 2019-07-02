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
    double tijdverleden=0.0;
    double tijd;
    public DefaultDriver() {
        initialize();
        neuralNetwork = new NeuralNetwork(false);
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
        //action.steering=DriversUtils.alignToTrackAxis(sensors,0.5);
        action.accelerate=(getAcceleration(sensors));

            action.steering=getSteering(sensors);

            action.brake=neuralNetwork.getOutput(sensors,"brake");


            if(sensors.getSpeed()>=120.0){
                action.accelerate=0.1;
            }


            System.out.println("Time: "+sensors.getTime());
            System.out.println("--------------" + getDriverName() + "--------------");
            System.out.println("Steering: " + action.steering);
            System.out.println("Acceleration: " + action.accelerate);
            System.out.println("trackPos: "+sensors.getTrackPosition());
            System.out.println("Brake: " + action.brake);
            System.out.println("Angle: "+ sensors.getAngleToTrackAxis());
            System.out.println("-----------------------------------------------");

        return action;
    }
}