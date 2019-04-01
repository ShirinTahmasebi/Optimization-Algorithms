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
        iteration_no += 1
        ##### Generate a neighbor
        previous_solution = current_solution.copy()
        current_solution = knapsack.generate_neighbour(current_solution)
        ##### Generate correspondent spin variables
        ##### Calculate cost (Hamiltonian)
        ##### If dH is positive
        ####### Accept neighbour as current solution
        ##### Else
        ####### With probability math.exp(float(delta) / temperature) accept neighbour
        ##### End this loop when the process repeated for # of iterations
        pass
    ### Update temperature

knapsack.print_items()
knapsack.print_solution(current_solution)
print("Final Solution Weight: " + str(knapsack.calculate_total_weight(current_solution)))
print("Final Solution Price: " + str(knapsack.calculate_total_price(current_solution)))
