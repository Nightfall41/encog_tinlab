        if (sensors.getSpeed() > 60.0D) {
            action.accelerate = 0.0D;
            action.brake = 0.0D;
        }

        if (sensors.getSpeed() > 70.0D) {
            action.accelerate = 0.0D;
            action.brake = -1.0D;
        }

        if (sensors.getSpeed() <= 60.0D) {
            action.accelerate = (80.0D - sensors.getSpeed()) / 80.0D;
            action.brake = 0.0D;
        }

        if (sensors.getSpeed() < 30.0D) {
            action.accelerate = 1.0D;
            action.brake = 0.0D;
        }

        action.steering = moveTowardsTrackPosition(sensors, 1.0D, 0.0D);