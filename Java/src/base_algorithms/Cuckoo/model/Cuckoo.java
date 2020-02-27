package base_algorithms.Cuckoo.model;

import base_algorithms.Cost;

import java.util.Random;

public class Cuckoo {

    private CuckooDataAndBehaviour cuckooDataAndBehaviour;
    private boolean isMature = false;
    private Cost cost;

    private MatureCuckoo matureCuckoo = new MatureCuckoo();

    public Cuckoo() {
        this(false, null);
    }

    public Cuckoo(boolean isMature, CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        this.isMature = isMature;
        this.cuckooDataAndBehaviour = cuckooDataAndBehaviour;

        if (isMature) {
            Random rand = new Random();
            int eggsNumber = Math.min(
                    rand.nextInt(main.Parameters.Cuckoo.MAX_EGG_NUMBER - main.Parameters.Cuckoo.MIN_EGG_NUMBER) + main.Parameters.Cuckoo.MIN_EGG_NUMBER,
                    cuckooDataAndBehaviour.getEggsNumberLowerBound()
            );
            matureCuckoo.setNumberOfEggs(eggsNumber);
        }
    }

    public boolean isMature() {
        return isMature;
    }

    public MatureCuckoo getMatureCuckoo() {
        return matureCuckoo;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public CuckooDataAndBehaviour getCuckooDataAndBehaviour() {
        return cuckooDataAndBehaviour;
    }
}
