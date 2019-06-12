package cuckoo;

import java.util.Comparator;

public class CuckooComparator implements Comparator<Cuckoo> {
    @Override
    public int compare(Cuckoo o1, Cuckoo o2) {
        return Double.valueOf(o1.getCost()).compareTo(o2.getCost());
    }
}
