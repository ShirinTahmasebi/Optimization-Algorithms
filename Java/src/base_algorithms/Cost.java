package base_algorithms;

import main.ModelNoEnum;
import main.Parameters;
import problem_modelings.budget_constrained_lmax_optimization.Utils;

public class Cost {
    private double lMaxCost;
    private double summationOfLMaxCost;
    private double synchronizationCost;
    private double reliabilityCost;
    private double loadBalancingCost;
    private double kineticEnergy;
    private double budgetCostEnergy;

    public Cost() {

    }

    public Cost setLmaxCost(double lmaxCost) {
        this.lMaxCost = lmaxCost;
        return this;
    }

    public Cost setSummationOfLMaxCost(double summationOfLMaxCost) {
        this.summationOfLMaxCost = summationOfLMaxCost;
        return this;
    }

    public Cost setSynchronizationCost(double synchronizationDelayCost) {
        this.synchronizationCost = synchronizationDelayCost;
        return this;
    }

    public Cost setReliabilityCost(double reliabilityCost) {
        this.reliabilityCost = reliabilityCost;
        return this;
    }

    public Cost setLoadBalancingCost(double loadBalancingCost) {
        this.loadBalancingCost = loadBalancingCost;
        return this;
    }

    public Cost setKineticEnergy(double kineticEnergy) {
        this.kineticEnergy = kineticEnergy;
        return this;
    }

    public Cost setBudgetCostEnergy(double budgetCostEnergy) {
        this.budgetCostEnergy = budgetCostEnergy;
        return this;
    }

    public double getReliabilityCost() {
        return reliabilityCost;
    }

    public double getLoadBalancingCost() {
        return loadBalancingCost;
    }

    public double getSynchronizationCost() {
        return synchronizationCost;
    }


    public double getSummationOfLMaxCost() {
        return summationOfLMaxCost;
    }

    public double getlMaxCost() {
        return lMaxCost;
    }

    public double getKineticEnergy() {
        return kineticEnergy;
    }

    public double getBudgetCostEnergy() {
        return budgetCostEnergy;
    }

    public double getPotentialEnergy() throws Exception {
        if (Parameters.Common.MODEL_NO == ModelNoEnum.COST_OPTIMIZATION) {
            return Parameters.Common.PENALTY_COEFFICIENT * (reliabilityCost + loadBalancingCost) + budgetCostEnergy;
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_LMAX_OPTIMIZATION) {
            double lMaxCost = Utils.getMaxLCost((int) getlMaxCost());
            double distanceToNearestControllerEnergy = Utils.getSummationOfMaxLCost((int) summationOfLMaxCost);
            return Parameters.Common.PENALTY_COEFFICIENT * (reliabilityCost + loadBalancingCost) + lMaxCost + distanceToNearestControllerEnergy;
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD) {
            // LMax scaled cost
            double lMaxCost = Utils.getMaxLCost((int) getlMaxCost());

            // Summation of LMax scaled cost
            double distanceToNearestControllerEnergy = Utils.getSummationOfMaxLCost((int) summationOfLMaxCost);

            // Synchronization scaled cost
            double controllerSynchronizationOverheadEnergy = Utils.getControllerSynchronizationOverheadEnergy(synchronizationCost);

            return Parameters.Common.PENALTY_COEFFICIENT * (reliabilityCost + loadBalancingCost) +
                    lMaxCost +
                    Parameters.SynchronizationOverheadModel.LMAX_COEFFICIENT * distanceToNearestControllerEnergy +
                    Parameters.SynchronizationOverheadModel.INTER_CONTROLLER_SYNC_COEFFICIENT * controllerSynchronizationOverheadEnergy;
        } else {
            throw new Exception("Error occurred");
        }
    }
}
