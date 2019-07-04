import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.algorithm.abstracts.map.TrackMap;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;


public class DefaultDriver extends AbstractDriver {

    private NeuralNetwork neuralNetwork; // make pointer to neuralnetwork object
    private DriversUtils tools = new DriversUtils(); // make pointer to Driverutils object

    public DefaultDriver() { // contstuctor of the class
        initialize();
        neuralNetwork = new NeuralNetwork(false); // making pointer to neuralnetwork
    }
    // activate extras of the car for simplying the process
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

    @Override // give the acceleration value to the car by giving the state of the game to the neural network
    public double getAcceleration(SensorModel sensors) {
        double output = neuralNetwork.getOutput(sensors, "acceleration");
        return output;
    }

    @Override // give the steering value to the car by giving the state of the game to the neural network
    public double getSteering(SensorModel sensors) {
        Double output = neuralNetwork.getOutput(sensors, "steering");
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

    @Override // controls the car in game
    public Action defaultControl(Action action, SensorModel sensors) {
        if (action == null) {
            action = new Action();
        }
        //set actions to take in game by sending the current state to the neural network.
        action.accelerate = (getAcceleration(sensors));
        action.steering = getSteering(sensors);
        action.brake = neuralNetwork.getOutput(sensors, "brake");

        //get trackedge sensors
        double[] track = sensors.getTrackEdgeSensors();

        if (track[9] < 1) { // if the car is outside the track boundries the value of the sensors become -1
            System.out.println("car off track");
            if(sensors.getSpeed()<10 && sensors.getDistanceRaced()> 10){
                tools.calm(action, sensors); // try to calmly get back to the track
                action.steering=(DriversUtils.alignToTrackAxis(sensors,1));
            }
            action.brake=0.5;
        }
        if(sensors.getDistanceRaced()<10){
            action.accelerate=1;
            action.brake=0;
            System.out.println("fullgas");
        }

        if (sensors.getSpeed() >= 120) {
            /* once the car achieves 120km/h the bot needs to
            control the distance between the itself and the trackedge
             *track 9 track edge of the front of the car
             * track 10 trackedge of the upperright position of the car
             * track 8 trackedge of the upperleft position of the car
             */

            if (track[9] < 60) {
                /* if the distance of the trackedge
                in front of the car is less than 60m: full brake
                */
                action.brake = 1;
                if (track[9] < 30) {
                    /* if the distance of the trackedge
                    *in front of the cat is less than 30m: stop accelerating
                     */
                    action.brake=1;
                    action.accelerate=0;
                }
            }
        }


        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");


        return action;
    }
}