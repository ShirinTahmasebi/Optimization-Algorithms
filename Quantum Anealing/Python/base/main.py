import random

from base.utils import initialize_spin_variables
from model.edge import Edge
from model.graph import Graph
from model.vertex import Vertex
from quantum_annealing.test_quantum_annealing_algorithm import TestQuantumAnnealingAlgorithm

SIMULATION_COUNT = 20
SINK_LOAD = 10
CONTROLLER_LOAD = 10
SENSOR_SINK_MAX_DISTANCE = 3
SENSOR_CONTROLLER_MAX_DISTANCE = 2
MAX_SINK_COVERAGE = 6
MAX_CONTROLLER_COVERAGE = 6
MAX_SINK_LOAD = 30
MAX_CONTROLLER_LOAD = 30
COST_SINK = 1
COST_CONTROLLER = 3
#
nodes_list = []
edges_list = []
candidate_sinks_list = []
candidate_controllers_list = []
sink_Y_spin_variables_2d_arr = [[]]
controller_Y_spin_variables_2d_arr = [[]]


def initialize():
    graph = initialize_graph(1)

    initialize_spin_variables(
        graph,
        candidate_sinks_list,
        candidate_controllers_list,
        SENSOR_SINK_MAX_DISTANCE,
        SENSOR_CONTROLLER_MAX_DISTANCE,
        sink_Y_spin_variables_2d_arr,
        controller_Y_spin_variables_2d_arr
    )

    return graph


def initialize_graph(graph_size):
    vertex_count = 0
    candidate_sinks_number = 0
    candidate_controllers_number = 0
    # ArrayList<Pair<String, Pair<Integer, Integer>>> edges_pair_list = new ArrayList<>();
    edges_pair_list = []
    if graph_size == 1:
        vertex_count = 20
        candidate_sinks_number = 6
        candidate_controllers_number = 9
    elif graph_size == 2:
        vertex_count = 40
        candidate_sinks_number = vertex_count / 5
        candidate_controllers_number = vertex_count / 10
    elif graph_size == 3:
        vertex_count = 80
        candidate_sinks_number = vertex_count / 5
        candidate_controllers_number = vertex_count / 10
    elif graph_size == 4:
        vertex_count = 150
        candidate_sinks_number = 50
        candidate_controllers_number = 40

    for i in range(vertex_count):
        ith_node_neighbors_count = vertex_count / 2
        neighbors_number_set = set()

        while len(neighbors_number_set) < ith_node_neighbors_count:
            next_int = random.randint(0, vertex_count - 1)
            if next_int != i:
                neighbors_number_set.add(next_int)

        for neighborNumber in neighbors_number_set:
            edges_pair_list.append(("Edge_" + str(i) + "_To_" + str(neighborNumber), (i, neighborNumber)))

    for i in range(vertex_count):
        location = Vertex("Node_" + str(i), "Node_" + str(i), SINK_LOAD, CONTROLLER_LOAD)
        nodes_list.append(location)

    for edge in edges_pair_list:
        add_lane(edge[0], (edge[1])[0], (edge[1])[0])

    candidate_sinks_number_set = set()
    candidate_controller_number_set = set()

    while len(candidate_sinks_number_set) < candidate_sinks_number:
        next_int = random.randint(0, vertex_count - 1)
        candidate_sinks_number_set.add(next_int)

    while len(candidate_controller_number_set) < candidate_controllers_number:
        next_int = random.randint(0, vertex_count - 1)
        candidate_controller_number_set.add(next_int)

    for candidateControllerNumber in candidate_controller_number_set:
        candidate_controllers_list.append(nodes_list[candidateControllerNumber])

    for candidate_sinks_number in candidate_sinks_number_set:
        candidate_sinks_list.append(nodes_list[candidate_sinks_number])

    graph = Graph(nodes_list, edges_list)
    return graph


def add_lane(lane_id, source_loc_no, dest_loc_no):
    lane1 = Edge(lane_id, nodes_list[source_loc_no], nodes_list[dest_loc_no], 1)
    edges_list.append(lane1)
    lane2 = Edge(lane_id, nodes_list[dest_loc_no], nodes_list[source_loc_no], 1)
    edges_list.append(lane2)


if __name__ == '__main__':
    graph = initialize()
    qaEnergySum = .0

    qaTest = TestQuantumAnnealingAlgorithm(
        graph,
        candidate_sinks_list,
        candidate_controllers_list,
        sink_Y_spin_variables_2d_arr,
        controller_Y_spin_variables_2d_arr,
        SENSOR_SINK_MAX_DISTANCE,
        SENSOR_CONTROLLER_MAX_DISTANCE,
        MAX_SINK_COVERAGE,
        MAX_CONTROLLER_COVERAGE,
        MAX_SINK_LOAD,
        MAX_CONTROLLER_LOAD,
        COST_SINK,
        COST_CONTROLLER
    )

    for i in range(SIMULATION_COUNT):
        qaPotentialEnergy = qaTest.execute()
        qaEnergySum += qaPotentialEnergy
        print("QA Energy: " + str(qaPotentialEnergy) + "\n")

    print("QA average potential energy is: " + str(float(float(qaEnergySum) / SIMULATION_COUNT)))
