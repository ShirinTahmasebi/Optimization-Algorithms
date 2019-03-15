import random

import numpy as np

from Item import Item


class Knapsack:
    def __init__(self,
                 items_count=10,
                 items_weight_lower_bound=1,
                 items_weight_upper_bound=100,
                 items_price_lower_bound=1,
                 items_price_upper_bound=20,
                 knapsack_size=100,
                 ):
        self.__items_count = items_count
        self.__items_weight_lower_bound = items_weight_lower_bound
        self.__items_weight_upper_bound = items_weight_upper_bound
        self.__items_price_lower_bound = items_price_lower_bound
        self.__items_price_upper_bound = items_price_upper_bound
        self.__knapsack_size = knapsack_size
        self.__items = []
        self.__current_solution = []
        self.__prev_solution = []
        self.generate_items()
        return

    def generate_items(self):
        for i in range(self.__items_count):
            item = Item(
                random.randrange(self.__items_weight_lower_bound, self.__items_weight_upper_bound),
                random.randrange(self.__items_price_lower_bound, self.__items_price_upper_bound),
            )
            self.__items.append(item)
        return

    def print_items(self, items=None):
        if items is None:
            items = self.__items

        for i in range(self.__items_count):
            print("Item #" + str(i + 1) + " Weight: " + str(items[i].get_weight()) + " Price: " + str(
                items[i].get_price()))

        return

    def print_solution(self, solution=None):
        if solution is None:
            solution = self.__current_solution.copy()

        t_str = ""
        for i in range(self.__items_count):
            t_str += "#" + str(i + 1) + ":" + str(solution[i]) + ", "

        print("Soulution is: [" + t_str + "]")
        return

    def generate_initial_solution(self):
        while True:
            selected_items_count = random.randrange(self.__items_count)
            self.__current_solution = np.array([1] * selected_items_count +
                                               [0] * (self.__items_count - selected_items_count))
            np.random.shuffle(self.__current_solution)
            if self.is_solution_valid():
                break

        return self.__current_solution.copy()

    def generate_neighbour(self):
        self.__prev_solution = self.__current_solution.copy()
        while True:
            flip_index = random.randrange(self.__items_count)
            self.__current_solution[flip_index] ^= 1
            if self.is_solution_valid():
                break

        return self.__current_solution.copy()

    def revert_solution(self):
        self.__current_solution = self.__prev_solution.copy()
        return self.__current_solution.copy()

    def calculate_total_price(self, solution=None):
        if solution is None:
            solution = self.__current_solution.copy()
        x = 0
        for i in range(self.__items_count):
            x += (self.__items[i]).get_price() if solution[i] else 0
        return x

    def calculate_total_weight(self, solution=None):
        if solution is None:
            solution = self.__current_solution.copy()
        x = 0
        for i in range(self.__items_count):
            x += (self.__items[i]).get_weight() if solution[i] else 0
        return x

    def is_solution_valid(self):
        if self.calculate_total_weight() < self.__knapsack_size:
            return True
        return False


if __name__ == "__main__":
    knapsack = Knapsack()
    knapsack.print_items()
    knapsack.print_solution(knapsack.generate_initial_solution())
    knapsack.print_solution(knapsack.generate_neighbour())
    print(knapsack.calculate_total_price())
