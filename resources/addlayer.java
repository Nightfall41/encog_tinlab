    private Basicnetwork network = new Basicnetwork();
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