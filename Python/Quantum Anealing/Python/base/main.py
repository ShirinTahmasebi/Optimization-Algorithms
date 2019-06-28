import random

from base.utils import initialize_spin_variables
from model.edge import Edge
from model.graph import Graph
from model.vertex import Vertex
from quantum_annealing.test_quantum_annealing_algorithm import TestQuantumAnnealingAlgorithm


class Main:

    def __init__(self):
        self.SIMULATION_COUNT = 20
        self.SINK_LOAD = 10
        self.CONTROLLER_LOAD = 10
        self.SENSOR_SINK_MAX_DISTANCE = 3
        self.SENSOR_CONTROLLER_MAX_DISTANCE = 2
        self.MAX_SINK_COVERAGE = 6
        self.MAX_CONTROLLER_COVERAGE = 6
        self.MAX_SINK_LOAD = 30
        self.MAX_CONTROLLER_LOAD = 30
        self.COST_SINK = 1
        self.COST_CONTROLLER = 3
        self.nodes_list = []
        self.edges_list = []
        self.candidate_sinks_list = []
        self.candidate_controllers_list = []
        self.sink_Y_spin_variables_2d_arr = [[]]
        self.controller_Y_spin_variables_2d_arr = [[]]

    def initialize(self):
        graph_var = self.initialize_graph(1)

        self.sink_Y_spin_variables_2d_arr = [[] for _ in range(len(graph_var.get_vertexes()))]
        self.controller_Y_spin_variables_2d_arr = [[] for _ in range(len(graph_var.get_vertexes()))]
        initialize_spin_variables(
            graph_var,
            self.candidate_sinks_list,
            self.candidate_controllers_list,
            self.SENSOR_SINK_MAX_DISTANCE,
            self.SENSOR_CONTROLLER_MAX_DISTANCE,
            self.sink_Y_spin_variables_2d_arr,
            self.controller_Y_spin_variables_2d_arr
        )

        return graph_var

    def initialize_graph(self, graph_size):
        vertex_count = 0
        candidate_sinks_number = 0
        candidate_controllers_number = 0
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

        for j in range(vertex_count):
            location = Vertex("Node_" + str(j), "Node_" + str(j), self.SINK_LOAD, self.CONTROLLER_LOAD)
            self.nodes_list.append(location)

        for edge in edges_pair_list:
            self.add_lane(edge[0], (edge[1])[0], (edge[1])[0])

        candidate_sinks_number_set = set()
        candidate_controller_number_set = set()

        while len(candidate_sinks_number_set) < candidate_sinks_number:
            next_int = random.randint(0, vertex_count - 1)
            candidate_sinks_number_set.add(next_int)

        while len(candidate_controller_number_set) < candidate_controllers_number:
            next_int = random.randint(0, vertex_count - 1)
            candidate_controller_number_set.add(next_int)

        for candidateControllerNumber in candidate_controller_number_set:
            self.candidate_controllers_list.append(self.nodes_list[candidateControllerNumber])

        for candidate_sinks_number in candidate_sinks_number_set:
            self.candidate_sinks_list.append(self.nodes_list[candidate_sinks_number])

        graph = Graph(self.nodes_list, self.edges_list)
        return graph

    def add_lane(self, lane_id, source_loc_no, dest_loc_no):
        lane1 = Edge(lane_id, self.nodes_list[source_loc_no], self.nodes_list[dest_loc_no], 1)
        self.edges_list.append(lane1)
        lane2 = Edge(lane_id, self.nodes_list[dest_loc_no], self.nodes_list[source_loc_no], 1)
        self.edges_list.append(lane2)


if __name__ == '__main__':
    main = Main()
    graph = main.initialize()
    qaEnergySum = .0

    qaTest = TestQuantumAnnealingAlgorithm(
        graph,
        main.candidate_sinks_list,
        main.candidate_controllers_list,
        main.sink_Y_spin_variables_2d_arr,
        main.controller_Y_spin_variables_2d_arr,
        main.SENSOR_SINK_MAX_DISTANCE,
        main.SENSOR_CONTROLLER_MAX_DISTANCE,
        main.MAX_SINK_COVERAGE,
        main.MAX_CONTROLLER_COVERAGE,
        main.MAX_SINK_LOAD,
        main.MAX_CONTROLLER_LOAD,
        main.COST_SINK,
        main.COST_CONTROLLER
    )

    for i in range(main.SIMULATION_COUNT):
        qaPotentialEnergy = qaTest.execute()
        qaEnergySum += qaPotentialEnergy
        print("QA Energy: " + str(qaPotentialEnergy) + "\n")

    print("QA average potential energy is: " + str(float(float(qaEnergySum) / main.SIMULATION_COUNT)))
