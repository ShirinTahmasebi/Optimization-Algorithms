package problem_modelings.modeling_types.first_modeling;

import main.Parameters;
import main.Utils;
import problem_modelings.BaseProblemModeling;

public abstract class FirstModelAbstract extends BaseProblemModeling {

    public FirstModelPlainOldData modelPlainOldData;

    public FirstModelAbstract(FirstModelPlainOldData modelPlainOldData) {
        this.modelPlainOldData = modelPlainOldData;

        if (Parameters.Common.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(
                    modelPlainOldData.graph,
                    modelPlainOldData.candidateSinks,
                    modelPlainOldData.sinkYSpinVariables,
                    modelPlainOldData.candidateControllers,
                    modelPlainOldData.controllerYSpinVariables
            );
        }
    }

    public void printGeneratedSolution(boolean[] tempSinkXSpinVariables, boolean[] tempControllerXSpinVariables) {
        // --- Print temp lists
        System.out.println();
        System.out.println("Temp Sink X: ");
        for (boolean tempSinkXSpinVariable : tempSinkXSpinVariables) {
            System.out.print(tempSinkXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println("Temp Controller X: ");
        for (boolean tempControllerXSpinVariable : tempControllerXSpinVariables) {
            System.out.print(tempControllerXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println();
        // ---
    }
}
