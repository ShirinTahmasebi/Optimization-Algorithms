def print_problem_specifications():
    pass


def print_generated_solution(tempSinkXSpinVariables, tempControllerXSpinVariables):
    pass


def getReliabilityEnergy(
        graph,
        sinkYSpinVariables, controllerYSpinVariables,
        candidateSinks, tempSinkXSpinVariables,
        candidateControllers, tempControllerXSpinVariables,
        maxSinkCoverage, maxControllerCoverage
):
    pass


def getLoadBalancingEnergy(
        graph,
        sinkYSpinVariables, controllerYSpinVariables,
        candidateSinks, tempSinkXSpinVariables,
        candidateControllers, tempControllerXSpinVariables,
        maxSinkLoad, maxSinkCoverage,
        maxControllerLoad, maxControllerCoverage
):
    pass


def getCostEnergy(
        candidateSinks, tempSinkXSpinVariables,
        candidateControllers, tempControllerXSpinVariables,
        costSink, costController, costReductionFactor):
    pass
