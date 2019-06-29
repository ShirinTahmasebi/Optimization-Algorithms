package algorithms_modeling.Cuckoo;

import algorithms_modeling.Cuckoo.model.Cuckoo;
import algorithms_modeling.Cuckoo.model.CuckooDataAndBehaviour;

import java.util.List;

public interface CuckooModelingInterface {
    double calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours);

    List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception;

    Cuckoo generateEggByElr(Cuckoo matureCuckoo) throws Exception;

    Cuckoo generateInitialRandomCuckoos();

    void printGeneratedSolution();

    CuckooPlainOldData getData();
}
