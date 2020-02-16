package problem_modelings.base.modeling;

import main.model.Graph;
import main.model.Vertex;
import problem_modelings.base.plain_old_data.BaseMultiControllerMultiSinkProblemModelingPlainOldData;

import java.util.List;

public abstract class BaseMultiControllerMultiSinkProblemModeling<T extends BaseMultiControllerMultiSinkProblemModelingPlainOldData> extends BaseProblemModeling {
    public T modelPlainOldData;

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

    public void printProblemSpecifications(
            Graph graph,
            List<Vertex> candidateSinks, boolean[][] sinkYSpinVariables,
            List<Vertex> candidateControllers, boolean[][] controllerYSpinVariables) {
        // Print graph
        graph.getVertexes().stream().forEach((vertex) -> System.out.println("Vertex: " + vertex.toString()));

        System.out.println();

        graph.getEdges().stream().forEach((edge) -> System.out.println("Edge: " + edge.toString()));

        System.out.println();

        // Print candidate sinks
        System.out.print("Candidate sink vertexes are: ");
        candidateSinks.stream().forEach((candidateSinkVertex) -> System.out.print(candidateSinkVertex.toString() + ", "));

        System.out.println();
        System.out.println();

        // Print candidate controllers
        System.out.print("Candidate controller vertexes are: ");
        candidateControllers.stream().forEach((candidateControllerVertex) -> System.out.print(candidateControllerVertex.toString() + ", "));

        System.out.println();
        System.out.println();

        System.out.println("Sink Y: ");

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                System.out.print(sinkYSpinVariables[i][j] + ", ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        System.out.println("Controller Y: ");

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                System.out.print(controllerYSpinVariables[i][j] + ", ");
            }
            System.out.println();
        }
    }
}
