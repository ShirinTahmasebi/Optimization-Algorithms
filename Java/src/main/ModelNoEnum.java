package main;

public enum ModelNoEnum {
    COST_OPTIMIZATION(1),
    BUDGET_CONSTRAINED_LMAX_OPTIMIZATION(2),
    BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD(3);

    public final int number;

    ModelNoEnum(int number) {
        this.number = number;
    }
}
