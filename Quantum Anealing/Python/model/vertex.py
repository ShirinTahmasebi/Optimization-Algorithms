class Vertex:
    def __init__(self, id, name, sink_load, controller_load):
        self.__id = id
        self.__name = name
        self.__sink_load = sink_load
        self.__controller_load = controller_load

    def get_id(self):
        return self.__id

    def get_name(self):
        return self.__name

    def get_sink_load(self):
        return self.__sink_load

    def get_controller_load(self):
        return self.__controller_load

    def set_id(self, id):
        self.__id = id

    def set_name(self, name):
        self.__name = name

    def set_sink_load(self, sink_load):
        self.__sink_load = sink_load

    def set_controller_load(self, controller_load):
        self.__controller_load = controller_load

    def __str__(self):
        return self.__name

    def __hash__(self):
        prime = 31
        if self.__id:
            return prime + hash(self.__id)
        else:
            return prime

    def __eq__(self, other):
        # if self == other:
        #     return True

        if not other:
            return False

        if not isinstance(other, Vertex):
            # Don't attempt to compare against unrelated types
            return NotImplemented

        if not self.__id:
            if other.get_id():
                return False
        elif self.__id != other.get_id():
            return False

        return True