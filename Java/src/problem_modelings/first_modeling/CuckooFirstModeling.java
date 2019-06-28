package problem_modelings.first_modeling;

import algorithms_modeling.Cuckoo.CuckooModelingInterface;
import problem_modelings.modeling_types.first_modeling.FirstModelAbstract;
import problem_modelings.modeling_types.first_modeling.FirstModelPlainOldData;

public class CuckooFirstModeling extends FirstModelAbstract implements CuckooModelingInterface {

    public CuckooFirstModeling(FirstModelPlainOldData firstModelPlainOldData) {
        super(firstModelPlainOldData);
    }
}
