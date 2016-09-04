package com.filipowm.networkoptimization.network;

/**
 * Class representing modulation, which can be applied in the network for specific path.
 */
public class Modulation implements Comparable<Modulation> {

    private final Integer pathSize;
    private final Integer value;

    public Modulation(int pathSize, int value) {
        this.pathSize = pathSize;
        this.value = value;
    }

    public Integer getPathSize() {
        return pathSize;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Modulation o) {
        return o.getPathSize().compareTo(pathSize);
    }
}
