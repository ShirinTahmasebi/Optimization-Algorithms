# Optimization-Algorithms

## Goal
Compare simulated annealing and quantum annealing algorithm in node placement problem

## Paper Details
Title: A quantum annealing based approach to optimize the deployment cost of a multi-sink multi-contoller WSN

Conference Name: 16th International Conference on Mobile Systems and Pervasive Computing


## Implementation Details
Quantum annealing algorithm implementation is available in java and python

Simulated annealing algorithm implementation is available in java

### How To Execute Python Version

Initialize graph (nodes and edges). Then select a candidate list for controllers and sinks. Tune parameters (sink and controller max distance, max sink and controller coverage, etc.). Then execute an instance of `TestQuantumAnnealingAlgorithm`. A sample of this procedure exists [here](https://github.com/ShirinTahmasebi/Optimization-Algorithms/blob/master/Quantum%20Anealing/Python/base/main.py).

```
qaTest = TestQuantumAnnealingAlgorithm(
        graph,
        main.candidate_sinks_list,
        main.candidate_controllers_list,
        main.sink_Y_spin_variables_2d_arr,
        main.controller_Y_spin_variables_2d_arr,
        main.SENSOR_SINK_MAX_DISTANCE,
        main.SENSOR_CONTROLLER_MAX_DISTANCE,
        main.MAX_SINK_COVERAGE,
        main.MAX_CONTROLLER_COVERAGE,
        main.MAX_SINK_LOAD,
        main.MAX_CONTROLLER_LOAD,
        main.COST_SINK,
        main.COST_CONTROLLER
    )
    
 qaTest.execute()
```

### How To Execute Java Version

The procedure is exactly the same as python version. The sample exists [here](https://github.com/ShirinTahmasebi/Optimization-Algorithms/blob/master/Quantum%20Anealing/Java/src/main/Main.java).

```
 TestQuantumAnnealingAlgorithm qaTest = new TestQuantumAnnealingAlgorithm(
                graph,
                m.candidateSinks,
                m.candidateControllers,
                m.sinkYSpinVariables,
                m.controllerYSpinVariables,
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE,
                MAX_SINK_LOAD,
                MAX_CONTROLLER_LOAD,
                COST_SINK,
                COST_CONTROLLER
        );
        
 qaTest.execute();
```


