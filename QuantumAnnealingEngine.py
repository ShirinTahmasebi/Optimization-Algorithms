import math
from random import random

from Knapsack import Knapsack

# Get inputs
temperature_initial = 100
temperature_final = 1
cooling_rate = 0.99
number_of_iterations = 1000  # Equilibrium State

# Initialize variables & generate an initial solution
temperature = temperature_initial
iteration_no = 0
knapsack = Knapsack()
current_solution = knapsack.generate_initial_solution()

# Repeat until reach a final temperature
while temperature > temperature_final:
    ### Repeat the process in for a given number in each temperature
    while iteration_no < number_of_iterations:
        break_inner_loop = False
        iteration_no += 1
        current_solution_weight_arr = []
        previous_solution_weight_arr = []
        for i in range(knapsack.get_knapsack_size()):
            current_solution_weight_arr.append(0)
            previous_solution_weight_arr.append(0)
        ##### Generate a neighbor & correspondent spin variables
        previous_solution = current_solution.copy()
        current_solution = knapsack.generate_neighbour(current_solution)

        ##### Set weight of current solution
        current_solution_weight_arr[knapsack.calculate_total_weight(current_solution)] = 1
        previous_solution_weight_arr[knapsack.calculate_total_weight(previous_solution)] = 1

        ##### Calculate cost (Hamiltonian)
        dE = knapsack.calculate_qa_total_energy(current_solution, current_solution_weight_arr) - \
             knapsack.calculate_qa_total_energy(previous_solution, previous_solution_weight_arr)
        if dE < 0:
            # Accept solution (It has better price)
            break_inner_loop = True
        else:
            rand_prob = random()
            if rand_prob < math.exp(-float(dE) / temperature):
                # Accept worse solution with probability math.exp(float(delta) / temperature)
                pass
            else:
                # Reverse selection (Reject)
                current_solution = previous_solution.copy()
        if break_inner_loop:
            break

        print("Current Solution Price: " + str(knapsack.calculate_sa_total_price(current_solution)))

        ### Update temperature
        temperature *= cooling_rate

knapsack.print_items()
knapsack.print_solution(current_solution)
print("Final Solution Weight: " + str(knapsack.calculate_total_weight(current_solution)))
print("Final Solution Price: " + str(knapsack.calculate_sa_total_price(current_solution)))
