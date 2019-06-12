package cuckoo;

import main.Utils;

import java.util.*;

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
            int eggsNumber = Math.min(
                    rand.nextInt(CuckooAlgorithm.maxEggNumber - CuckooAlgorithm.minEggNumber) + CuckooAlgorithm.minEggNumber,
                    (controllerXSpinVariables.length + sinkXSpinVariables.length) / 3
            );
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

    public List<Cuckoo> generateEggs() throws Exception {
        if (!isMature) {
            throw new Exception("Using generateEggs is not valid for not mature cuckoos!");
        }
        List<Cuckoo> eggs = new ArrayList<>();
        // TODO: Remove this line later
        this.matureCuckooInfo.setELR(
                Math.min(this.getControllerXSpinVariables().length, this.getSinkXSpinVariables().length) / 2
        );
        for (int i = 0; i < matureCuckooInfo.getNumberOfEggs(); i++) {
            eggs.add(generateEggByElr());
        }
        return eggs;
    }

    private Cuckoo generateEggByElr() throws Exception {
        if (!isMature) {
            throw new Exception("Using generateEggByElr is not valid for not mature cuckoos!");
        }

        Random random = new Random();

        boolean[] tempCandidateSink = this.getSinkXSpinVariables().clone();
        boolean[] tempCandidateController = this.getControllerXSpinVariables().clone();

        int maxElr = this.matureCuckooInfo.getELR();

        int sinkElr = random.nextInt(Math.min(maxElr, tempCandidateSink.length));
        int candidateElr = random.nextInt(Math.min(maxElr, tempCandidateController.length));

        Set<Integer> sinkInversionIndices = new HashSet<>();
        Set<Integer> controllerInversionIndices = new HashSet<>();

        while (sinkInversionIndices.size() < sinkElr) {
            int index = random.nextInt(tempCandidateSink.length);
            sinkInversionIndices.add(index);

            boolean prevValue = tempCandidateSink[index];
            tempCandidateSink[index] = !prevValue;
        }

        while (controllerInversionIndices.size() < candidateElr) {
            int index = random.nextInt(tempCandidateController.length);
            controllerInversionIndices.add(index);

            boolean prevValue = tempCandidateController[index];
            tempCandidateController[index] = !prevValue;
        }

        return new Cuckoo(false, tempCandidateSink, tempCandidateController);
    }


}
