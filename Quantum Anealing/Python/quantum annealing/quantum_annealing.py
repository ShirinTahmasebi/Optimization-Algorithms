from copy import deepcopy
import random
import sys

import math

from main.main import DO_PRINT_STEPS, DO_PRINT_INSTANCES
from main.utils import print_problem_specifications, print_generated_solution, get_reliability_energy, \
    get_load_balancing_energy, get_cost_energy, is_distance_favorable
from model.graph import Graph


class QuantumAnnealing:
    def __init__(
            self,
            graph,
            candidate_sinks_list,
            candidate_controllers_list,
            sensor_sink_max_distance,
            sensor_controller_max_distance,
            max_sink_coverage,
            max_controller_coverage,
            max_sink_load,
            max_controller_load,
            cost_sink,
            cost_controller,
            cost_reduction_factor,
            trotter_replicas,
            temperature,
            monte_carlo_steps,
            tunneling_field_initial,
            tunneling_field_final,
            tunneling_field_evaporation
    ):
        if isinstance(graph, Graph):
            self.__graph = graph
            self.__candidate_sinks_list = candidate_sinks_list
            self.__candidate_controllers_list = candidate_controllers_list
            self.__sensor_sink_max_distance = sensor_sink_max_distance
            self.__sensor_controller_max_distance = sensor_controller_max_distance

            self.__sink_Y_spin_variables_2d_arr = [[]]
            self.__controller_Y_spin_variables_2d_arr = [[]]

            self.__sink_X_spin_variables_list = []
            self.__controller_X_spin_variables_list = []

            self.__replicas_of_sink_X_spin_variables_2d_arr = [[]]
            self.__replicas_of_controller_X_spin_variables_2d_arr = [[]]

            self.__temp_sink_X_spin_variables_list = []
            self.__temp_controller_X_spin_variables_list = []

            self.__max_sink_coverage = max_sink_coverage
            self.__max_controller_coverage = max_controller_coverage
            self.__max_sink_load = max_sink_load
            self.__max_controller_load = max_controller_load
            self.__cost_sink = cost_sink
            self.__cost_controller = cost_controller
            self.__cost_reduction_factor = cost_reduction_factor
            self.__trotter_replicas = trotter_replicas
            self.__temperature_quantum = temperature
            self.__temperature = temperature
            self.__monte_carlo_steps = monte_carlo_steps
            self.__tunneling_field = tunneling_field_initial
            self.__tunnling_field_initial = tunneling_field_initial
            self.__tunnling_field_final = tunneling_field_final
            self.__tunnling_field_evaporation = tunneling_field_evaporation
            self.__cooling_rate = .7
            self.__prev_energy_tuple = ()
            # lineChartEx = new LineChartEx();
            self.initialize_spin_variables()
            if DO_PRINT_STEPS:
                print_problem_specifications(graph, candidate_sinks_list, self.__sink_Y_spin_variables_2d_arr,
                                             candidate_controllers_list, self.__controller_Y_spin_variables_2d_arr)

    def execute(self):
        # Reset Temperature and Tunneling Field
        self.__tunneling_field = self.__tunnling_field_initial
        self.__temperature = self.__temperature_quantum

        # Generate replicas (Fill replicasOfSinkXSpinVariables, replicasOfControllerXSpinVariables )
        self.generate_replicas_of_solution()
        self.generate_initial_spin_variables_and_energy()

        counter = 0

        min_energy_tuple = (sys.maxsize, sys.maxsize)

        # Do while tunnlig field is favorable
        while True:
            # For each replica
            for ro in range(self.__trotter_replicas):
                self.__temp_sink_X_spin_variables_list = deepcopy(self.__replicas_of_sink_X_spin_variables_2d_arr[ro])
                self.__temp_controller_X_spin_variables_list = deepcopy(
                    self.__replicas_of_controller_X_spin_variables_2d_arr[ro])
                # For each montecarlo step
                for step in range(self.__monte_carlo_steps):
                    counter += 1
                    # Generate Neighbors
                    self.generateNeighbor()
                    # Calculate energy of temp solution
                    energy_tuple = self.calculateEnergy(ro)
                    energy = self.calculate_energy_from_pair(energy_tuple)
                    prev_energy = self.calculate_energy_from_pair(self.__prev_energy_tuple)
                    min_energy = self.calculate_energy_from_pair(min_energy_tuple)

                    if energy < min_energy:
                        min_energy_tuple = energy_tuple

                    if energy_tuple[0] < self.__prev_energy_tuple[0] or energy < prev_energy:
                        # If energy has decreased: accept solution
                        self.__prev_energy_tuple = deepcopy(energy_tuple)
                        self.__sink_X_spin_variables_list = deepcopy(self.__temp_sink_X_spin_variables_list)
                        self.__controller_X_spin_variables_list = deepcopy(self.__temp_controller_X_spin_variables_list)
                    else:
                        # Else with given probability decide to accept or not
                        baseProp = math.exp(-float(prev_energy - energy) / self.__temperature)

                        if DO_PRINT_STEPS:
                            print("BaseProp " + str(baseProp))

                        rand = random.random()

                        if rand < baseProp:
                            self.__prev_energy_tuple = energy_tuple
                            self.__sink_X_spin_variables_list = deepcopy(self.__temp_sink_X_spin_variables_list)
                            self.__controller_X_spin_variables_list = deepcopy(
                                self.__temp_controller_X_spin_variables_list)

            # Update tunnling field
            self.__tunneling_field *= self.__tunnling_field_evaporation
            self.__temperature *= self.__cooling_rate

            if self.__tunneling_field > self.__tunnling_field_final:
                break

        if DO_PRINT_INSTANCES:
            # Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            print("Counter: " + counter)
            print("Accepted Energy: " + self.calculate_energy_from_pair(self.__prev_energy_tuple))
            print("Accepted Potential Energy: " + self.__prev_energy_tuple[0])
            print("Min Energy: " + self.calculate_energy_from_pair(min_energy_tuple))
            print("Final Temperature: " + self.__temperature)

        return self.__prev_energy_tuple[0]

    def generate_initial_spin_variables_and_energy(self):
        # Initialize temp lists to false
        for i in range(len(self.__candidate_controllers_list)):
            self.__controller_X_spin_variables_list[i] = False
        for i in range(len(self.__candidate_sinks_list)):
            self.__sink_X_spin_variables_list[i] = False

        self.__temp_controller_X_spin_variables_list = deepcopy(self.__controller_X_spin_variables_list)
        self.__temp_sink_X_spin_variables_list = deepcopy(self.__sink_X_spin_variables_list)

        energy_pair = self.calculateEnergy(-1)
        self.__prev_energy_tuple = energy_pair

    def generate_neighbour(self):
        rand_int = random.randint(0, len(self.__temp_sink_X_spin_variables_list) + len(
            self.__temp_controller_X_spin_variables_list) - 1)

        if rand_int < len(self.__temp_sink_X_spin_variables_list):
            # Change randInt-th item in sink array
            prev_value = self.__temp_sink_X_spin_variables_list[rand_int]
            self.__temp_sink_X_spin_variables_list[rand_int] = not prev_value
        else:
            # Change index-th item in controller array
            index = rand_int - (len(self.__temp_sink_X_spin_variables_list) - 1) - 1
            prev_value = self.__temp_controller_X_spin_variables_list[index]
            self.__temp_controller_X_spin_variables_list[index] = not prev_value

        if DO_PRINT_STEPS:
            print_generated_solution(self.__temp_sink_X_spin_variables_list,
                                     self.__temp_controller_X_spin_variables_list)

    def calculateEnergy(self, currentReplicaNum):
        reliability_energy = get_reliability_energy(self.__graph,
                                                    self.__sink_Y_spin_variables_2d_arr,
                                                    self.__controller_Y_spin_variables_2d_arr,
                                                    self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
                                                    self.__candidate_controllers_list,
                                                    self.__temp_controller_X_spin_variables_list,
                                                    self.__max_sink_coverage, self.__max_controller_coverage)

        load_balancing_energy = get_load_balancing_energy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_load, self.__max_sink_coverage,
            self.__max_controller_load, self.__max_controller_coverage
        )

        cost_energy = get_cost_energy(
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__cost_sink, self.__cost_controller, self.__cost_reduction_factor
        )

        potential_energy = reliability_energy + load_balancing_energy + cost_energy
        kinetic_energy = self.get_kinetic_energy(currentReplicaNum)
        energy = kinetic_energy + potential_energy

        return potential_energy, kinetic_energy

    def calculatePotentialEnergy(self, currentReplicaNum):
        reliability_energy = get_reliability_energy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_coverage, self.__max_controller_coverage)

        load_balancing_energy = get_load_balancing_energy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_load, self.__max_sink_coverage,
            self.__max_controller_load, self.__max_controller_coverage)

        cost_energy = get_cost_energy(
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__cost_sink, self.__cost_controller, self.__cost_reduction_factor
        )

        return reliability_energy + load_balancing_energy + cost_energy

    def get_kinetic_energy(self, current_replica_num):

        if current_replica_num + 1 >= self.__trotter_replicas or current_replica_num < 0:
            return 0

        # Calculate coupling among replicas
        half_temperature_quantum = self.__temperature_quantum / 2
        angle = self.__tunneling_field / (self.__trotter_replicas * self.__temperature_quantum)
        coupling = - half_temperature_quantum * math.log((math.tanh(angle)))

        sink_replica_coupling = 0
        controller_replica_coupling = 0

        for i in range(len(self.__candidate_sinks_list)):
            are_spin_variables_the_same = self.__replicas_of_sink_X_spin_variables_2d_arr[current_replica_num][i] and \
                                          self.__replicas_of_sink_X_spin_variables_2d_arr[current_replica_num + 1][i]
            if are_spin_variables_the_same:
                sink_replica_coupling = 1
            else:
                sink_replica_coupling = -1

        for i in range(len(self.__candidate_controllers_list)):
            are_spin_variables_the_same = self.__replicas_of_controller_X_spin_variables_2d_arr[current_replica_num][
                                              i] and \
                                          self.__replicas_of_controller_X_spin_variables_2d_arr[
                                              current_replica_num + 1][
                                              i]

            if are_spin_variables_the_same:
                controller_replica_coupling = 1
            else:
                controller_replica_coupling = -1

        return coupling * (sink_replica_coupling + controller_replica_coupling)

    def initialize_spin_variables(self):
        for i in range(len(self.__graph.get_vertexes())):
            for j in range(len(self.__candidate_sinks_list)):
                self.__sink_Y_spin_variables_2d_arr[i][j] = False

        for i in range(len(self.__graph.get_vertexes())):
            for j in range(len(self.__candidate_controllers_list)):
                self.__controller_Y_spin_variables_2d_arr[i][j] = False

        j_upper_bound = max(len(self.__candidate_sinks_list), len(self.__candidate_controllers_list))

        for i in range(len(self.__graph.get_vertexes())):
            for j in range(j_upper_bound):
                if j < len(self.__candidate_sinks_list):
                    # The following line can be replaced with vertexIndex = i - but I preferred to write this in the
                    # following way for more readability
                    spin_vertex_index1 = self.__graph.get_vertex_index_by_id((self.__graph.get_vertexes()[i]).get_id())
                    spin_vertex_index2 = self.__graph.get_vertex_index_by_id(self.__candidate_sinks_list[j].get_id())
                    self.__sink_Y_spin_variables_2d_arr[i][j] = is_distance_favorable(
                        self.__graph, spin_vertex_index1, spin_vertex_index2, self.__sensor_sink_max_distance
                    )

                if j < len(self.__candidate_controllers_list):
                    # The following line can be replaced with vertexIndex = i - but I preferred to write this in the
                    # following way for more readability
                    vertex_index1 = self.__graph.get_vertex_index_by_id(self.__graph.get_vertexes()[i].get_id())
                    vertex_index2 = self.__graph.get_vertex_index_by_id(self.__candidate_controllers_list()[j].get_id())
                    self.__controller_Y_spin_variables_2d_arr[i][j] = is_distance_favorable(
                        self.__graph, vertex_index1, vertex_index2, self.__sensor_controller_max_distance
                    )

    def generate_replicas_of_solution(self):
        for i in range(self.__trotter_replicas):
            # Select random configuration for replicas
            for j in range(len(self.__candidate_sinks_list)):
                probability_of_one = random.random()
                self.__replicas_of_sink_X_spin_variables_2d_arr[i][j] = (probability_of_one < .5)

            for j in range(len(self.__candidate_controllers_list)):
                probability_of_one = random.random()
                self.__replicas_of_controller_X_spin_variables_2d_arr[i][j] = (probability_of_one < .5)

    @staticmethod
    def calculate_energy_from_pair(energy_pair):
        return energy_pair[0] + energy_pair[1]
