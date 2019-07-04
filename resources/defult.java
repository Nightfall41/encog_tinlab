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