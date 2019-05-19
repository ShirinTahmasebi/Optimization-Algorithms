class Edge:
    def __init__(self, id, source, destination, weight):
        self.__id = id
        self.__source = source
        self.__destination = destination
        self.__weight = weight

    def get_id(self):
        return self.__id

    def get_source(self):
        return self.__source

    def get_destination(self):
        return self.__destination

    def get_weight(self):
        return self.__weight

    def set_id(self, id):
        self.__id = id

    def set_source(self, source):
        self.__source = source

    def set_destination(self, destination):
        self.__destination = destination

    def set_weight(self, weight):
        self.__weight = weight

    def __str__(self):
        return self.__source + " " + self.__destination
