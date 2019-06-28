from dijkstra.dijkstra_algorithm import DijkstraAlgorithm
from model.edge import Edge
from model.graph import Graph
from model.vertex import Vertex

nodes_vertex_list = []
edges_vertex_list = []


def add_lane(lane_id, source_location_number, destination_location_number):
    lane_1_edge = Edge(lane_id, nodes_vertex_list[source_location_number],
                       nodes_vertex_list[destination_location_number], 1)
    edges_vertex_list.append(lane_1_edge)
    lane_2_edge = Edge(lane_id, nodes_vertex_list[destination_location_number],
                       nodes_vertex_list[source_location_number], 1)
    edges_vertex_list.append(lane_2_edge)


if __name__ == '__main__':
    nodes_vertex_list = []
    edges_vertex_list = []

    for i in range(0, 11):
        location_vertex = Vertex("Node_" + str(i), "Node_" + str(i), i, i)
        nodes_vertex_list.append(location_vertex)

    add_lane("Edge_0", 0, 1)
    add_lane("Edge_1", 0, 2)
    add_lane("Edge_2", 0, 4)
    add_lane("Edge_3", 2, 6)
    add_lane("Edge_4", 2, 7)
    add_lane("Edge_5", 3, 7)
    add_lane("Edge_6", 5, 8)
    add_lane("Edge_7", 8, 9)
    add_lane("Edge_8", 7, 9)
    add_lane("Edge_9", 4, 9)
    add_lane("Edge_10", 9, 10)
    add_lane("Edge_11", 1, 10)

    graph = Graph(nodes_vertex_list, edges_vertex_list)
    dijkstra = DijkstraAlgorithm(graph)
    dijkstra.execute(nodes_vertex_list[6])

    path = dijkstra.get_path(nodes_vertex_list[10])
    for vertex in path:
        print(vertex.__str__())
