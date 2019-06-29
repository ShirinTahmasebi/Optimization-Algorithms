package algorithms_modeling.Cuckoo;

import algorithms_modeling.Cuckoo.model.Cuckoo;

import java.util.ArrayList;
import java.util.List;

public class CuckooPlainOldData {

    public List<Cuckoo> matureCuckoos;
    public List<Cuckoo> eggs;

    public CuckooPlainOldData() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public CuckooPlainOldData(List<Cuckoo> matureCuckoos, List<Cuckoo> eggs) {
        this.eggs = eggs;
        this.matureCuckoos = matureCuckoos;
    }
}
