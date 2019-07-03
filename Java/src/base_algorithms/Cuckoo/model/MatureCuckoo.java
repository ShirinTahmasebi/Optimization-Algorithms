package base_algorithms.Cuckoo.model;

// Mature Cuckoo == Habitat
public class MatureCuckoo {
    private int numberOfEggs;
    private int ELR;

    public MatureCuckoo() {
        this(0, 0);
    }

    public MatureCuckoo(int numberOfEggs, int ELR) {
        this.numberOfEggs = numberOfEggs;
        this.ELR = ELR;
    }

    public int getNumberOfEggs() {
        return numberOfEggs;
    }

    public void setNumberOfEggs(int numberOfEggs) {
        this.numberOfEggs = numberOfEggs;
    }

    public int getELR() {
        return ELR;
    }

    public void setELR(int ELR) {
        this.ELR = ELR;
    }
}
