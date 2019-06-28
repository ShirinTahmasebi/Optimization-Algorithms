class Graph:
    def __init__(self, vertexes, edges):
        self.__vertexes = vertexes
        self.__edges = edges

    def get_vertexes(self):
        return self.__vertexes

    def get_edges(self):
        return self.__edges

    def get_vertex_by_id(self, id):
        if not id:
            return None

        for vertex in self.__vertexes:
            if vertex.get_id() == id:
                return vertex

        return None

    def get_vertex_index_by_id(self, id):
        if not id:
            return -1

        length = len(self.__vertexes)
        for i in range(length):
            if self.__vertexes[i] == id:
                return i

        return -1
