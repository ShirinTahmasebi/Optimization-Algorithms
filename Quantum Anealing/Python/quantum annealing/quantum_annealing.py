import random
import sys

import math

from main.main import DO_PRINT_STEPS, DO_PRINT_INSTANCES
from main.utils import print_problem_specifications, print_generated_solution, getReliabilityEnergy, \
    getLoadBalancingEnergy, getCostEnergy
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
            tunnling_field_initial,
            tunnling_field_final,
            tunnling_field_evaporation
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
            self.__tunnling_field = tunnling_field_initial
            self.__tunnling_field_initial = tunnling_field_initial
            self.__tunnling_field_final = tunnling_field_final
            self.__tunnling_field_evaporation = tunnling_field_evaporation
            self.__cooling_rate = .7
            self.__prev_energy_tuple = ()
            # lineChartEx = new LineChartEx();
            self.initializeSpinVariables()
            if DO_PRINT_STEPS:
                print_problem_specifications(graph, candidate_sinks_list, self.__sink_Y_spin_variables_2d_arr,
                                             candidate_controllers_list, self.__controller_Y_spin_variables_2d_arr)

    def execute(self):
        # Reset Temperature and Tunnling Field
        self.__tunnling_field = self.__tunnling_field_initial
        self.__temperature = self.__temperature_quantum

        # Generate replicas (Fill replicasOfSinkXSpinVariables, replicasOfControllerXSpinVariables )
        self.generateReplicasOfSolutions()
        self.generate_initial_spin_variables_and_energy()

        counter = 0

        min_energy_tuple = (sys.maxsize, sys.maxsize)

        # Do while tunnlig field is favorable
        while True:
            # For each replica
            for ro in range(self.__trotter_replicas):
                self.__temp_sink_X_spin_variables_list = self.__replicas_of_sink_X_spin_variables_2d_arr[ro].copy()
                self.__temp_controller_X_spin_variables_list = self.__replicas_of_controller_X_spin_variables_2d_arr[
                    ro].copy()
                # For each montecarlo step
                for step in range(self.__monte_carlo_steps):
                    counter += 1
                    # Generate Neighbors
                    self.generateNeighbor()
                    # Calculate energy of temp solution
                    energy_tuple = ()
                    energy_tuple = self.calculateEnergy(ro)
                    energy = self.calculateEnergyFromPair(energy_tuple)
                    prev_energy = self.calculateEnergyFromPair(self.__prev_energy_tuple)
                    min_energy = self.calculateEnergyFromPair(min_energy_tuple)

                    if energy < min_energy:
                        min_energy_tuple = energy_tuple

                    if energy_tuple[0] < self.__prev_energy_tuple[0] or energy < prev_energy:
                        # If energy has decreased: accept solution
                        self.__prev_energy_tuple = energy_tuple.copy()
                        self.__sink_X_spin_variables_list = self.__temp_sink_X_spin_variables_list.copy()
                        self.__controller_X_spin_variables_list = self.__temp_controller_X_spin_variables_list.copy()
                    else:
                        # Else with given probability decide to accept or not
                        baseProp = math.exp(-float(prev_energy - energy) / self.__temperature)

                        if DO_PRINT_STEPS:
                            print("BaseProp " + baseProp)

                        rand = random.random()

                        if rand < baseProp:
                            self.__prev_energy_tuple = energy_tuple
                            self.__sink_X_spin_variables_list = self.__temp_sink_X_spin_variables_list.copy()
                            self.__controller_X_spin_variables_list = self.__temp_controller_X_spin_variables_list.copy()

            # Update tunnling field
            self.__tunnling_field *= self.__tunnling_field_evaporation
            self.__temperature *= self.__cooling_rate

            if self.__tunnling_field > self.__tunnling_field_final:
                break

        if DO_PRINT_INSTANCES:
            # Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            print("Counter: " + counter)
            print("Accepted Energy: " + self.calculateEnergyFromPair(self.__prev_energy_tuple))
            print("Accepted Potential Energy: " + self.__prev_energy_tuple[0])
            print("Min Energy: " + self.calculateEnergyFromPair(min_energy_tuple))
            print("Final Temperature: " + self.__temperature)

        return self.__prev_energy_tuple[0]

    def generate_initial_spin_variables_and_energy(self):
        # Initialize temp lists to false
        for i in range(len(self.__candidate_controllers_list)):
            self.__controller_X_spin_variables_list[i] = False
        for i in range(len(self.__candidate_sinks_list)):
            self.__sink_X_spin_variables_list[i] = False

        self.__temp_controller_X_spin_variables_list = self.__controller_X_spin_variables_list.copy()
        self.__temp_sink_X_spin_variables_list = self.__sink_X_spin_variables_list.copy()

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
        reliability_energy = getReliabilityEnergy(self.__graph,
                                                  self.__sink_Y_spin_variables_2d_arr,
                                                  self.__controller_Y_spin_variables_2d_arr,
                                                  self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
                                                  self.__candidate_controllers_list,
                                                  self.__temp_controller_X_spin_variables_list,
                                                  self.__max_sink_coverage, self.__max_controller_coverage)

        load_balancing_energy = getLoadBalancingEnergy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_load, self.__max_sink_coverage,
            self.__max_controller_load, self.__max_controller_coverage
        )

        cost_energy = getCostEnergy(
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__cost_sink, self.__cost_controller, self.__cost_reduction_factor
        )

        potential_energy = reliability_energy + load_balancing_energy + cost_energy
        kinetic_energy = self.getKineticEnergy(currentReplicaNum)
        energy = kinetic_energy + potential_energy

        return potential_energy, kinetic_energy

    def calculatePotentialEnergy(self, currentReplicaNum):
        reliability_energy = getReliabilityEnergy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_coverage, self.__max_controller_coverage)

        load_balancing_energy = getLoadBalancingEnergy(
            self.__graph,
            self.__sink_Y_spin_variables_2d_arr, self.__controller_Y_spin_variables_2d_arr,
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__max_sink_load, self.__max_sink_coverage,
            self.__max_controller_load, self.__max_controller_coverage)

        cost_energy = getCostEnergy(
            self.__candidate_sinks_list, self.__temp_sink_X_spin_variables_list,
            self.__candidate_controllers_list, self.__temp_controller_X_spin_variables_list,
            self.__cost_sink, self.__cost_controller, self.__cost_reduction_factor
        )

        return reliability_energy + load_balancing_energy + cost_energy

    def getKineticEnergy(self, currentReplicaNum):

        if currentReplicaNum + 1 >= self.__trotter_replicas or currentReplicaNum < 0:
            return 0

        # Calculate coupling among replicas
        halfTemperatureQuantum = self.__temperature_quantum / 2
        angle = self.__tunnling_field / (self.__trotter_replicas * self.__temperature_quantum)
        coupling = - halfTemperatureQuantum * math.log((math.tanh(angle)))

        sinkReplicaCoupling = 0
        controllerReplicaCoupling = 0

        for i in range(len(self.__candidate_sinks_list)):
            areSpinVariablesTheSame = self.__replicas_of_sink_X_spin_variables_2d_arr[currentReplicaNum][i] and \
                                      self.__replicas_of_sink_X_spin_variables_2d_arr[currentReplicaNum + 1][i]
            if areSpinVariablesTheSame:
                sinkReplicaCoupling = 1
            else:
                sinkReplicaCoupling = -1

        for i in range(len(self.__candidate_controllers_list)):
            areSpinVariablesTheSame = self.__replicas_of_controller_X_spin_variables_2d_arr[currentReplicaNum][i] and \
                                      self.__replicas_of_controller_X_spin_variables_2d_arr[currentReplicaNum + 1][i]

            if areSpinVariablesTheSame:
                controllerReplicaCoupling = 1
            else:
                controllerReplicaCoupling = -1

        return coupling * (sinkReplicaCoupling + controllerReplicaCoupling)
