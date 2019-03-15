import math
from random import random

from Knapsack import Knapsack

temperature_initial = 100
temperature_final = 1
cooling_rate = 0.9
number_of_iterations = 100  # Equilibrium State

temperature = temperature_initial
iteration_no = 0
knapsack = Knapsack()
best_solution = knapsack.generate_initial_solution()

while temperature > temperature_final:
    while iteration_no < number_of_iterations:
        iteration_no += 1
        candidate_solution = knapsack.generate_neighbour()
        delta = knapsack.calculate_total_price(candidate_solution) - knapsack.calculate_total_price(best_solution)
        if delta > 0:
            best_solution = candidate_solution.copy()
        else:
            rand_prob = random()
            if rand_prob > math.exp(float(delta) / temperature):
                knapsack.revert_solution()
        # print("-- Total Weight: " + str(knapsack.calculate_total_weight(best_solution)))
        print("-- Total Price: " + str(knapsack.calculate_total_price(best_solution)))
        break
    temperature *= cooling_rate

knapsack.print_items()
knapsack.print_solution(best_solution)
knapsack.print_solution(best_solution)
print("Total Weight: " + str(knapsack.calculate_total_weight(best_solution)))
print("Total Price: " + str(knapsack.calculate_total_price(best_solution)))
