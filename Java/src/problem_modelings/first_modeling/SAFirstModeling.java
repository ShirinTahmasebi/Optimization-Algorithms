package problem_modelings.first_modeling;

import algorithms_modeling.SA.SAModelingInterface;
import problem_modelings.modeling_types.first_modeling.FirstModelAbstract;
import problem_modelings.modeling_types.first_modeling.FirstModelPlainOldData;

public class SAFirstModeling extends FirstModelAbstract implements SAModelingInterface {

    public SAFirstModeling(FirstModelPlainOldData firstModelPlainOldData) {
        super(firstModelPlainOldData);
    }
}
