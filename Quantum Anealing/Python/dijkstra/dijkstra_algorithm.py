import sys

from model.graph import Graph
from model.vertex import Vertex


class DijkstraAlgorithm:
    def __init__(self, graph):
        if isinstance(graph, Graph):
            self.__nodes = graph.get_vertexes()
            self.__edges = graph.get_edges()
            # Set
            self.__settled_nodes = set()
            self.__unSettled_nodes = set()
            # Dict
            self.__predecessors = {}
            self.__distance = {}

    def execute(self, source_vertex):
        if not isinstance(source_vertex, Vertex):
            return NotImplemented
        # Set
        self.__settled_nodes = set()
        self.__unSettled_nodes = set()
        # Dict
        self.__predecessors = {}
        self.__distance = {source_vertex: 0}

        self.__unSettled_nodes.add(source_vertex)

        while len(self.__unSettled_nodes) > 0:
            node_vertex = self.get_minimum(self.__unSettled_nodes)
            self.__settled_nodes.add(node_vertex)
            self.__unSettled_nodes.remove(node_vertex)
            self.find_minimal_distances(node_vertex)

    def find_minimal_distances(self, node_vertex):
        adjacent_nodes = self.get_neighbors(node_vertex)

        for target in adjacent_nodes:
            if self.get_shortest_distance(target) > self.get_shortest_distance(node_vertex) + self.get_distance(
                    node_vertex, target):
                self.__distance[target] = self.get_shortest_distance(node_vertex) + self.get_distance(node_vertex,
                                                                                                      target)
                self.__predecessors[target] = node_vertex
                self.__unSettled_nodes.add(target)

    def get_distance(self, node_vertex, target_vertex):
        if not isinstance(node_vertex, Vertex) and not isinstance(target_vertex, Vertex):
            return NotImplemented

        if not node_vertex and not target_vertex:
            return

        for edge in self.__edges:
            if node_vertex.__eq__(edge) and target_vertex.__eq__(edge.get_destination()):
                return edge.get_weight()

    def get_neighbors(self, node_vertex):
        neighbors = []

        for edge in self.__edges:
            if node_vertex.__eq__(edge.get_source()) and not self.is_settled(edge.get_destination()):
                neighbors.append(edge.get_destination())

        return neighbors

    def is_settled(self, vertex):
        if not isinstance(vertex, Vertex):
            return False
        return self.__settled_nodes.__contains__(vertex)

    '''
        This method returns the path from the source to the selected target 
        and NULL if no path exists
    '''

    def get_path(self, target_vertex):
        if not isinstance(target_vertex, Vertex):
            return NotImplemented
        path = []
        step_vertex = target_vertex

        if not self.__predecessors.get(step_vertex):
            return None

        path.append(step_vertex)

        while self.__predecessors.get(step_vertex) is not None:
            step_vertex = self.__predecessors.get(step_vertex)
            path.append(step_vertex)

        path.reverse()
        return path

    def get_minimum(self, vertexes_set):
        minimum_vertex = None
        for vertex in vertexes_set:
            if not minimum_vertex:
                minimum_vertex = vertex
            else:
                if self.get_shortest_distance(vertex) < self.get_shortest_distance(minimum_vertex):
                    minimum_vertex = vertex

        return minimum_vertex

    def get_shortest_distance(self, destination_vertex):
        if not isinstance(destination_vertex, Vertex):
            return NotImplemented

        d = self.__distance.get(destination_vertex)

        if d is None:
            return sys.maxsize
        else:
            return d
