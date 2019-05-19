package main.model;

import java.util.List;

public class Graph {

    private final List<Vertex> vertexes;
    private final List<Edge> edges;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public Vertex getVertexById(String id) {
        for (Vertex vertex : vertexes) {
            if (vertex.getId().equals(id)) {
                return vertex;
            }
        }
        return null;
    }

    public int getVertexIndexById(String id) {
        for (int i = 0; i < vertexes.size(); i++) {
            if (vertexes.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
