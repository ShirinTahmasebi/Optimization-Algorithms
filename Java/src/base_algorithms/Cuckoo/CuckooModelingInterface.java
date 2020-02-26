package base_algorithms.Cuckoo;

import base_algorithms.Cost;
import base_algorithms.Cuckoo.model.Cuckoo;
import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;

import java.util.List;

public interface CuckooModelingInterface {
    Cost calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours);

    List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception;

    Cuckoo generateEggByElr(Cuckoo matureCuckoo) throws Exception;

    Cuckoo generateInitialRandomCuckoos() throws Exception;

    void printGeneratedSolution(CuckooDataAndBehaviour cuckooDataAndBehaviour);

    CuckooPlainOldData getData();
}
