package cuckoo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cuckoo {

    protected boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    protected boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)
    private boolean isMature = false;
    private double cost;

    private MatureCuckoo matureCuckooInfo = new MatureCuckoo();
    private EggCuckoo eggCuckooInfo = new EggCuckoo();

    public Cuckoo() {
        this(false, null, null);
    }

    public Cuckoo(boolean isMature, boolean[] sinkXSpinVariables, boolean[] controllerXSpinVariables) {
        this.isMature = isMature;
        this.controllerXSpinVariables = controllerXSpinVariables;
        this.sinkXSpinVariables = sinkXSpinVariables;

        if (isMature) {
            Random rand = new Random();
            int eggsNumber = rand.nextInt(CuckooAlgorithm.maxEggNumber - CuckooAlgorithm.minEggNumber) + CuckooAlgorithm.minEggNumber;
            matureCuckooInfo.setNumberOfEggs(eggsNumber);
        }
    }

    public boolean[] getSinkXSpinVariables() {
        return sinkXSpinVariables;
    }

    public void setSinkXSpinVariables(boolean[] sinkXSpinVariables) {
        this.sinkXSpinVariables = sinkXSpinVariables;
    }

    public boolean[] getControllerXSpinVariables() {
        return controllerXSpinVariables;
    }

    public void setControllerXSpinVariables(boolean[] controllerXSpinVariables) {
        this.controllerXSpinVariables = controllerXSpinVariables;
    }

    public boolean isMature() {
        return isMature;
    }

    public void setIsMature(boolean isMature) {
        this.isMature = isMature;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public MatureCuckoo getMatureCuckooInfo() {
        return matureCuckooInfo;
    }

    public void setMatureCuckooInfo(MatureCuckoo matureCuckooInfo) {
        this.matureCuckooInfo = matureCuckooInfo;
    }

    public EggCuckoo getEggCuckooInfo() {
        return eggCuckooInfo;
    }

    public void setEggCuckooInfo(EggCuckoo eggCuckooInfo) {
        this.eggCuckooInfo = eggCuckooInfo;
    }

    public List<Cuckoo> generateEggs() {
        List<Cuckoo> eggs = new ArrayList<>();
        for (int i = 0; i < matureCuckooInfo.getNumberOfEggs(); i++) {
            // TODO: Fill candidate sinks and controllers
            eggs.add(new Cuckoo());
        }
        return eggs;
    }
}
