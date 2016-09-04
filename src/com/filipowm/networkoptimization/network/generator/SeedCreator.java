package com.filipowm.networkoptimization.network.generator;

public class SeedCreator {
    private static long seedUniquifier = 8682522807148012L;

    public static long getSeed() {
        return ++seedUniquifier + System.nanoTime();
    }
}
