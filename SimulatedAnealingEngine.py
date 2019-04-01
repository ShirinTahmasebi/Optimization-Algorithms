import math
from random import random

from Knapsack import Knapsack

temperature_initial = 100
temperature_final = 1
cooling_rate = 0.9
number_of_iterations = 100  # Equilibrium State

temperature = temperature_initial
iteration_no = 0
knapsack = Knapsack(items_count=60, knapsack_size=1000, do_generate_items_random=True)
current_solution = knapsack.generate_initial_solution()

while temperature > temperature_final:
    while iteration_no < number_of_iterations:
        break_inner_loop = False
        iteration_no += 1
        previous_solution = current_solution.copy()
        current_solution = knapsack.generate_neighbour(current_solution)
        delta = knapsack.calculate_sa_total_price(current_solution) - knapsack.calculate_sa_total_price(
            previous_solution)
        if delta > 0:
            # Accept Solution (It has better price)
            break_inner_loop = True
        else:
            rand_prob = random()
            if rand_prob < math.exp(float(delta) / temperature):
                # Accept Worse Solution
                pass
            else:
                # Reverse Selection (Reject)
                current_solution = previous_solution.copy()
        if break_inner_loop:
            break
    print("Current Solution Price: " + str(knapsack.calculate_sa_total_price(current_solution)))
    temperature *= cooling_rate

knapsack.print_items()
knapsack.print_solution(current_solution)
print("Final Solution Weight: " + str(knapsack.calculate_total_weight(current_solution)))
print("Final Solution Price: " + str(knapsack.calculate_sa_total_price(current_solution)))
