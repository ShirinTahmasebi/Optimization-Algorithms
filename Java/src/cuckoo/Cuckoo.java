package cuckoo;

import main.model.Vertex;

import java.util.List;

public class Cuckoo {

    private List<Vertex> candidateSinks;            // AS
    private List<Vertex> candidateControllers;      //AC
    private boolean isMature = false;
    private double cost;

    private MatureCuckoo matureCuckooInfo = new MatureCuckoo();
    private EggCuckoo eggCuckooInfo = new EggCuckoo();

    public Cuckoo() {
        this(false, null, null);
    }

    public Cuckoo(boolean isMature, List<Vertex> candidateControllers, List<Vertex> candidateSinks) {
        this.isMature = isMature;
        this.candidateControllers = candidateControllers;
        this.candidateSinks = candidateSinks;
    }

    public List<Vertex> getCandidateSinks() {
        return candidateSinks;
    }

    public void setCandidateSinks(List<Vertex> candidateSinks) {
        this.candidateSinks = candidateSinks;
    }

    public List<Vertex> getCandidateControllers() {
        return candidateControllers;
    }

    public void setCandidateControllers(List<Vertex> candidateControllers) {
        this.candidateControllers = candidateControllers;
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
}
