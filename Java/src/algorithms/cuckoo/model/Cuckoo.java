package algorithms.cuckoo.model;

import java.util.*;

public class Cuckoo {

    public boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)
    private boolean isMature = false;
    private double cost;

    private MatureCuckoo matureCuckoo = new MatureCuckoo();

    public Cuckoo() {
        this(false, null, null);
    }

    public Cuckoo(boolean isMature, boolean[] sinkXSpinVariables, boolean[] controllerXSpinVariables) {
        this.isMature = isMature;
        this.controllerXSpinVariables = controllerXSpinVariables;
        this.sinkXSpinVariables = sinkXSpinVariables;

        if (isMature) {
            Random rand = new Random();
            int eggsNumber = Math.min(
                    rand.nextInt(main.Parameters.Cuckoo.MAX_EGG_NUMBER - main.Parameters.Cuckoo.MIN_EGG_NUMBER) + main.Parameters.Cuckoo.MIN_EGG_NUMBER,
                    (controllerXSpinVariables.length + sinkXSpinVariables.length) / 3
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

    public boolean[] getSinkXSpinVariables() {
        return sinkXSpinVariables;
    }

    public boolean[] getControllerXSpinVariables() {
        return controllerXSpinVariables;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

}
