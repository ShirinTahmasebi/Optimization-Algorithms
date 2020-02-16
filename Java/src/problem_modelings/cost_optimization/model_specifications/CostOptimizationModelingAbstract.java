package problem_modelings.cost_optimization.model_specifications;

import main.Parameters;
import problem_modelings.base.modeling.BaseMultiControllerMultiSinkProblemModeling;

public abstract class CostOptimizationModelingAbstract extends BaseMultiControllerMultiSinkProblemModeling<CostOptimizationModelingPlainOldData> {

    public CostOptimizationModelingAbstract(CostOptimizationModelingPlainOldData modelPlainOldData) {
        this.modelPlainOldData = modelPlainOldData;

        if (Parameters.Common.DO_PRINT_STEPS) {
            printProblemSpecifications(
                    modelPlainOldData.graph,
                    modelPlainOldData.candidateSinks,
                    modelPlainOldData.sinkYSpinVariables,
                    modelPlainOldData.candidateControllers,
                    modelPlainOldData.controllerYSpinVariables
            );
        }
    }
}