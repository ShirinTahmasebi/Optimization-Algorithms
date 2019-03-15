class Item:
    def __init__(self, weight, price):
        self.__weight = weight
        self.__price = price

    def get_weight(self):
        return self.__weight

    def get_price(self):
        return self.__price
