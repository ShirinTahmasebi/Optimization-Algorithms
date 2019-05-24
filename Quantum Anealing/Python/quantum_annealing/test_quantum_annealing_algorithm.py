from .quantum_annealing import QuantumAnnealing


class TestQuantumAnnealingAlgorithm:
    def __init__(self,
                 graph,
                 candidate_sinks,
                 candidate_controllers,
                 sink_y_spin_variables,
                 controller_y_spin_variables,
                 sensor_sink_max_distance,
                 sensor_controller_max_distance,
                 max_sink_coverage,
                 max_controller_coverage,
                 max_sink_load,
                 max_controller_load,
                 cost_sink,
                 cost_controller
                 ):
        self.COST_REDUCTION_FACTOR = 0.75
        self.TROTTER_REPLICAS = 50
        self.TEMPERATURE = 100.0
        self.MONTE_CARLO_STEP = 100
        self.TUNNLING_FIELD_INITIAL = 1.0
        self.TUNNLING_FIELD_FINAL = .5
        self.TUNNLING_FIELD_EVAPORATION = .95

        self.qa = QuantumAnnealing(graph,
                                   candidate_sinks,
                                   candidate_controllers,
                                   sink_y_spin_variables,
                                   controller_y_spin_variables,
                                   sensor_sink_max_distance,
                                   sensor_controller_max_distance,
                                   max_sink_coverage,
                                   max_controller_coverage,
                                   max_sink_load,
                                   max_controller_load,
                                   cost_sink,
                                   cost_controller,
                                   self.COST_REDUCTION_FACTOR,
                                   self.TROTTER_REPLICAS,
                                   self.TEMPERATURE,
                                   self.MONTE_CARLO_STEP,
                                   self.TUNNLING_FIELD_INITIAL,
                                   self.TUNNLING_FIELD_FINAL,
                                   self.TUNNLING_FIELD_EVAPORATION)

    def execute(self):
        return self.qa.execute()
