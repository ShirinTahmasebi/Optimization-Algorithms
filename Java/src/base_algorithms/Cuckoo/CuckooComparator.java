package base_algorithms.Cuckoo;

import base_algorithms.Cuckoo.model.Cuckoo;

import java.util.Comparator;

public class CuckooComparator implements Comparator<Cuckoo> {
    @Override
    public int compare(Cuckoo o1, Cuckoo o2) {
        try {
            double firstCost = o1.getCost().getPotentialEnergy();
            double secondCost = o2.getCost().getPotentialEnergy();
            return Double.valueOf(firstCost).compareTo(secondCost);
        } catch (Exception e) {
            return -1;
        }
    }
}
