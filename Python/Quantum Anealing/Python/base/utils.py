from dijkstra.dijkstra_algorithm import DijkstraAlgorithm


def print_problem_specifications(
        graph,
        candidate_sinks_list, sink__y_spin_variables_2d_arr,
        candidate_controllers_list, controller__y_spin_variables_2d_arr):
    for vertex in graph.get_vertexes():
        print("Vertex: " + vertex.__str__())

    print("\n")

    for edge in graph.get_edges():
        print("Edge: " + edge.__str__())

    print("\n")

    print("Candidate sink vertexes are:")

    for candidate_sink in candidate_sinks_list:
        print(candidate_sink.__str__() + ", ")

    print("\n")
    print("\n")

    print("Sink Y: \n")

    for i in range(len(graph.get_vertexes())):
        for j in range(len(candidate_sinks_list)):
            print(sink__y_spin_variables_2d_arr[i][j] + ", ")
        print("\n")

    print("\n")
    print("\n")

    print("Controller Y:")

    for i in range(len(graph.get_vertexes())):
        for j in range(len(candidate_controllers_list)):
            print(controller__y_spin_variables_2d_arr[i][j] + ", ")

    print("\n")


def print_generated_solution(temp_sink_x_spin_variables, temp_controller_x_spin_variables):
    print("\n")
    print("Temp Sink X:")

    temp_str = ""
    for i in range(len(temp_sink_x_spin_variables)):
        temp_str += str(temp_sink_x_spin_variables[i]) + ", "

    print(temp_str + "\n")
    print("Temp Controller X:")

    temp_str = ""
    for i in range(len(temp_controller_x_spin_variables)):
        temp_str += str(temp_controller_x_spin_variables[i]) + ", "

    print(temp_str + "\n")


def get_reliability_energy(
        graph,
        sink_y_spin_variables, controller_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_sink_coverage, max_controller_coverage
):
    sensor_numbers = get_sensors_count(
        graph=graph,
        candidate_sinks=candidate_sinks,
        candidate_controllers=candidate_controllers,
        temp_sink_x_spin_variables=temp_sink_x_spin_variables,
        temp_controller_x_spin_variables=temp_controller_x_spin_variables)
    return (
            (
                    max_sink_coverage *
                    sensor_numbers -
                    total_cover_sinks_score(
                        graph, max_sink_coverage,
                        sink_y_spin_variables,
                        candidate_sinks, temp_sink_x_spin_variables,
                        candidate_controllers, temp_controller_x_spin_variables)
            ) +
            (
                    max_controller_coverage *
                    sensor_numbers -
                    total_cover_controllers_score(
                        graph, max_controller_coverage,
                        controller_y_spin_variables,
                        candidate_sinks, temp_sink_x_spin_variables,
                        candidate_controllers, temp_controller_x_spin_variables))
    )


def get_load_balancing_energy(
        graph,
        sink_y_spin_variables, controller_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_sink_load, max_sink_coverage,
        max_controller_load, max_controller_coverage
):
    sinks_load_balancing_energy = get_sinks_load_balancing_energy(
        graph, sink_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_sink_load, max_sink_coverage
    )

    controllers_load_balancing_energy = get_controllers_load_balancing_energy(
        graph, controller_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_controller_load, max_controller_coverage
    )
    return sinks_load_balancing_energy + controllers_load_balancing_energy


def get_cost_energy(
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        cost_sink, cost_controller, cost_reduction_factor):
    for i in range(len(candidate_sinks)):
        for j in range(len(candidate_controllers)):
            if temp_sink_x_spin_variables[i] and temp_controller_x_spin_variables[j]:
                cost = (cost_sink + cost_controller) * cost_reduction_factor
                return cost
            elif temp_sink_x_spin_variables[i]:
                return cost_sink
            elif temp_controller_x_spin_variables[j]:
                return cost_controller
    return 0


def get_sensors_count(graph,
                      candidate_sinks, temp_sink_x_spin_variables,
                      candidate_controllers, temp_controller_x_spin_variables):
    sensor_count = 0
    for i in range(len(graph.get_vertexes())):
        graph_node = graph.get_vertexes()[i]

        if not is_node_selected_as_sink_or_controller(
                graph_node.get_id(),
                temp_sink_x_spin_variables=temp_sink_x_spin_variables,
                candidate_sinks=candidate_sinks,
                temp_controller_x_spin_variables=temp_controller_x_spin_variables,
                candidate_controllers=candidate_controllers):
            sensor_count += 1

    return sensor_count


def is_node_selected_as_sink_or_controller(
        _id, temp_sink_x_spin_variables, candidate_sinks,
        temp_controller_x_spin_variables, candidate_controllers):
    for i in range(len(temp_sink_x_spin_variables)):
        temp_sink_x_spin_variable = temp_sink_x_spin_variables[i]

        if temp_sink_x_spin_variable:
            sink_id = candidate_sinks[i].get_id()

            if sink_id == _id:
                return True

    for i in range(len(temp_controller_x_spin_variables)):
        temp_controller_x_spin_variable = temp_controller_x_spin_variables[i]

        if temp_controller_x_spin_variable:
            controller_id = candidate_controllers[i].get_id()

            if controller_id == _id:
                return True

    return False


def get_sinks_load_balancing_energy(
        graph, sink_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_sink_load, max_sink_coverage):
    total_sink_load_energy = 0

    for j in range(len(candidate_sinks)):
        total_load_to_jth_sink = calculate_load_to_jth_sink(
            j, graph, sink_y_spin_variables,
            temp_sink_x_spin_variables, candidate_sinks,
            temp_controller_x_spin_variables, candidate_controllers)
        best_sink_load = max_sink_load / (max_sink_coverage - 1)
        total_sink_load_energy += max(0, total_load_to_jth_sink - best_sink_load)

    return total_sink_load_energy


def get_controllers_load_balancing_energy(
        graph, controller_y_spin_variables,
        candidate_sinks, temp_sink_x_spin_variables,
        candidate_controllers, temp_controller_x_spin_variables,
        max_controller_load, max_controller_coverage):
    total_controller_load_energy = 0
    for j in range(len(candidate_controllers)):
        total_load_to_jth_controller = calculate_load_to_jth_controller(
            j, graph, controller_y_spin_variables,
            temp_sink_x_spin_variables, candidate_sinks,
            temp_controller_x_spin_variables, candidate_controllers
        )
        best_controller_load = max_controller_load / (max_controller_coverage - 1)
        total_controller_load_energy += max(0, total_load_to_jth_controller - best_controller_load)

    return total_controller_load_energy


# j is sink's index in candidateSinks (Not graph node index)
def calculate_load_to_jth_sink(
        j, graph, sink_y_spin_variables, temp_sink_x_spin_variables,
        candidate_sinks, temp_controller_x_spin_variables, candidate_controllers):
    total_load_to_jth_sink = .0
    for i in range(len(graph.get_vertexes())):
        graph_node = graph.get_vertexes()[i]

        if not is_node_selected_as_sink_or_controller(
                graph_node.get_id(), temp_sink_x_spin_variables, candidate_sinks,
                temp_controller_x_spin_variables, candidate_controllers):
            condition = sink_y_spin_variables[i][j] and temp_sink_x_spin_variables[j]
            if condition:
                total_load_to_jth_sink += float(graph_node.get_sink_load()) / float(
                    covered_sinks_count_by_node(i, candidate_sinks, sink_y_spin_variables, temp_sink_x_spin_variables))

    return float(total_load_to_jth_sink)


def calculate_load_to_jth_controller(
        j, graph, controller_y_spin_variables,
        temp_sink_x_spin_variables, candidate_sinks,
        temp_controller_x_spin_variables, candidate_controllers):
    total_load_to_jth_controller = .0
    for i in range(len(graph.get_vertexes())):
        graph_node = graph.get_vertexes()[i]
        if not is_node_selected_as_sink_or_controller(
                graph_node.get_id(), temp_sink_x_spin_variables, candidate_sinks,
                temp_controller_x_spin_variables, candidate_controllers):
            condition = controller_y_spin_variables[i][j] and temp_controller_x_spin_variables[j]
            if condition:
                total_load_to_jth_controller += float(graph_node.get_controller_load()) / float(
                    covered_controllers_count_by_node(i, candidate_controllers, controller_y_spin_variables,
                                                      temp_controller_x_spin_variables))

    return float(total_load_to_jth_controller)


def covered_nodes_count_by_controller(
        controller_index, graph, controller_y_spin_variables, temp_controller_x_spin_variables):
    covered_controllers = 0

    for j in range(len(graph.get_vertexes())):
        if controller_y_spin_variables[j][controller_index] and temp_controller_x_spin_variables[controller_index]:
            covered_controllers += 1

    return covered_controllers


def covered_sinks_count_by_node(node_index, candidate_sinks, sink_y_spin_variables, temp_sink_x_spin_variables):
    covered_sinks = 0
    for j in range(len(candidate_sinks)):
        if sink_y_spin_variables[node_index][j] and temp_sink_x_spin_variables[j]:
            covered_sinks += 1

    return covered_sinks


def covered_controllers_count_by_node(node_index, candidate_controllers, controller_y_spin_variables,
                                      temp_controller_x_spin_variables):
    covered_controllers = 0
    for i in range(len(candidate_controllers)):
        if controller_y_spin_variables[node_index][i] and temp_controller_x_spin_variables[i]:
            covered_controllers += 1

    return covered_controllers


def covered_nodes_count_by_sink(sink_index, graph, sink_y_spin_variables, temp_sink_x_spin_variables):
    covered_sinks = 0
    for j in range(len(graph.get_vertexes())):
        if sink_y_spin_variables[j][sink_index] and temp_sink_x_spin_variables[sink_index]:
            covered_sinks += 1

    return covered_sinks


def total_cover_sinks_score(graph, max_sink_coverage, sink_y_spin_variables,
                            candidate_sinks, temp_sink_x_spin_variables,
                            candidate_controllers, temp_controller_x_spin_variables):
    score = 0
    for i in range(len(graph.get_vertexes())):
        graph_node = graph.get_vertexes()[i]
        if is_node_selected_as_sink_or_controller(
                graph_node.get_id(),
                temp_sink_x_spin_variables, candidate_sinks,
                temp_controller_x_spin_variables, candidate_controllers
        ):
            score += min(
                max_sink_coverage,
                covered_sinks_count_by_node(i, candidate_sinks, sink_y_spin_variables, temp_sink_x_spin_variables))

    return score


def total_cover_controllers_score(graph, max_controller_coverage, controller_y_spin_variables,
                                  candidate_sinks, temp_sink_x_spin_variables,
                                  candidate_controllers, temp_controller_x_spin_variables):
    score = 0
    for i in range(len(graph.get_vertexes())):
        graph_node = graph.get_vertexes()[i]
        if is_node_selected_as_sink_or_controller(graph_node.get_id(),
                                                  temp_sink_x_spin_variables, candidate_sinks,
                                                  temp_controller_x_spin_variables, candidate_controllers):
            score += min(max_controller_coverage,
                         covered_controllers_count_by_node(i, candidate_controllers, controller_y_spin_variables,
                                                           temp_controller_x_spin_variables))

    return score


def is_distance_favorable(graph, first_node_index, second_node_index, max_distance):
    return get_distance(graph, first_node_index, second_node_index) <= max_distance


def get_distance(graph, first_node_index, second_node_index):
    dijkstra = DijkstraAlgorithm(graph)
    dijkstra.execute(graph.get_vertexes()[first_node_index])
    path = dijkstra.get_path(graph.get_vertexes()[second_node_index])

    if not path:
        return 0
    else:
        return len(path) - 1


## TODO: In rafte tooye Utils
def initialize_spin_variables(
        graph,
        candidate_sinks_list,
        candidate_controllers_list,
        sensor_sink_max_distance,
        sensor_controller_max_distance,
        sink__y_spin_variables_2d_arr,
        controller__y_spin_variables_2d_arr):
    for i in range(len(graph.get_vertexes())):
        for j in range(len(candidate_sinks_list)):
            sink__y_spin_variables_2d_arr[i].append(False)

    for i in range(len(graph.get_vertexes())):
        for j in range(len(candidate_controllers_list)):
            controller__y_spin_variables_2d_arr[i].append(False)

    j_upper_bound = max(len(candidate_sinks_list), len(candidate_controllers_list))

    for i in range(len(graph.get_vertexes())):
        for j in range(j_upper_bound):
            if j < len(candidate_sinks_list):
                # The following line can be replaced with vertexIndex = i - but I preferred to write this in the
                # following way for more readability
                spin_vertex_index1 = graph.get_vertex_index_by_id((graph.get_vertexes()[i]).get_id())
                spin_vertex_index2 = graph.get_vertex_index_by_id(candidate_sinks_list[j].get_id())
                sink__y_spin_variables_2d_arr[i][j] = is_distance_favorable(
                    graph, spin_vertex_index1, spin_vertex_index2, sensor_sink_max_distance
                )

            if j < len(candidate_controllers_list):
                # The following line can be replaced with vertexIndex = i - but I preferred to write this in the
                # following way for more readability
                vertex_index1 = graph.get_vertex_index_by_id(graph.get_vertexes()[i].get_id())
                vertex_index2 = graph.get_vertex_index_by_id(candidate_controllers_list[j].get_id())
                controller__y_spin_variables_2d_arr[i][j] = is_distance_favorable(
                    graph, vertex_index1, vertex_index2, sensor_controller_max_distance
                )
