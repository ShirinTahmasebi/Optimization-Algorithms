package problem_modelings.base.modeling;

import main.model.Graph;
import main.model.Vertex;
import problem_modelings.base.plain_old_data.BaseMultiControllerProblemModelingPlainOldData;

import java.util.List;

public abstract class BaseMultiControllerProblemModeling<T extends BaseMultiControllerProblemModelingPlainOldData> extends BaseProblemModeling {
    public T modelPlainOldData;

    public void printGeneratedSolution(boolean[] tempControllerXSpinVariables) {
        // --- Print temp lists
        System.out.println();
        System.out.println("Temp Controller X: ");
        for (boolean tempControllerXSpinVariable : tempControllerXSpinVariables) {
            System.out.print(tempControllerXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println();
        // ---
    }

    public void printProblemSpecifications(
            Graph graph,
            List<Vertex> candidateControllers, int[][] controllerY) {
        // Print graph
        graph.getVertexes().stream().forEach((vertex) -> System.out.println("Vertex: " + vertex.toString()));

        System.out.println();

        graph.getEdges().stream().forEach((edge) -> System.out.println("Edge: " + edge.toString()));

        System.out.println();

        // Print candidate controllers
        System.out.print("Candidate controller vertexes are: ");
        candidateControllers.stream().forEach((candidateControllerVertex) -> System.out.print(candidateControllerVertex.toString() + ", "));

        System.out.println();
        System.out.println();

        System.out.println("Controller Y: ");

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                System.out.print(controllerY[i][j] + ", ");
            }
            System.out.println();
        }
    }
}
