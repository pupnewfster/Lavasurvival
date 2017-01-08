package me.eddiep.minecraft.ls.system.util;

import java.util.Random;

public class RandomHelper {
    private static final Random RANDOM = new Random();
    private static final double LAMBDA = 5;

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static int random(int max) {
        return random(0, max, RandomDistribution.UNIFORM);
    }

    public static int random(int min, int max) {
        return random(min, max, RandomDistribution.UNIFORM);
    }

    public static int random(int min, int max, RandomDistribution distribution) {
        double u;
        switch (distribution) {
            case UNIFORM:
                u = uniformRandom();
                break;
            case NEGATIVE_EXPONENTIAL:
                u = negativeExponentialRandom();
                break;
            case POSITIVE_EXPONENTIAL:
                u = positiveExponentialRandom();
                break;
            default:
                u = uniformRandom();
                break;
        }

        return min + (int)(((max - min) * u));
    }

    public static double uniformRandom() {
        return RANDOM.nextDouble();
    }

    public static double negativeExponentialRandom() {
        return negativeExponentialRandom(LAMBDA);
    }

    public static double positiveExponentialRandom() {
        return positiveExponentialRandom(LAMBDA);
    }

    public static double negativeExponentialRandom(double lambda) {
        return -Math.log(1 - (1 - Math.exp(-lambda)) * RANDOM.nextDouble()) / lambda;
    }

    public static double positiveExponentialRandom(double lambda) {
        return Math.log(1 - (1 - Math.exp(lambda)) * RANDOM.nextDouble()) / lambda;
    }
}
