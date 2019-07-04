public void train() {
String filename = "nn/encog_Neural_";
ResilientPropagation learner = dataSet();

while (true) {
    learner.iteration();
    System.out.println("Error: " + learner.getError());
    System.out.println("Epoch: " + learner.getIteration());

    if (learner.getIteration() % 1000 == 0) {
        TrainingContinuation saved_network = learner.pause();
        saveObject(newFile("Encog_Neural_"+learner.getIteration()),loadFromFileNetwork);
        System.out.println("saved network");
        learner.resume(saved_network);
    }
    if (learner.getError() < tolerance) {
        learner.pause();
        saveObject(new File("nn/backup"+learner.getIteration()),loadFromFileNetwork);
        System.out.println("Done");
        learner.finishTraining();
        saveObject(new File("nn/done" + learner.getIteration()),loadFromFileNetwork);
        break;
        }
    }
}