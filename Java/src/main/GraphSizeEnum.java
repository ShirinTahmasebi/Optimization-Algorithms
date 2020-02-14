package main;

public enum GraphSizeEnum {
    RANDOM_20_SPECIAL_NONE(1),
    RANDOM_40_SPECIAL_NONE(2),
    RANDOM_80_SPECIAL_104(3),
    RANDOM_150_SPECIAL_169(4),
    RANDOM_200_SPECIAL_195(5),
    RANDOM_NONE_SPECIAL_143(6);

    public final int number;

    GraphSizeEnum(int number) {
        this.number = number;
    }
}
