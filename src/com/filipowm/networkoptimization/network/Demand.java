package com.filipowm.networkoptimization.network;

/**
 * Class representing single flow demand from source vertex to destination vertex.
 */
public class Demand {

    private final NetworkVertex source;
    private final NetworkVertex destination;

    private final Double value;

    public Demand(NetworkVertex source, NetworkVertex destination, double value) {
        this.source = source;
        this.destination = destination;
        this.value = value;
    }

    public NetworkVertex getSource() {
        return source;
    }

    public NetworkVertex getDestination() {
        return destination;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Demand demand = (Demand) o;

        if (!destination.equals(demand.destination) && !destination.equals(demand.source)) return false;
        if (!source.equals(demand.source) && !source.equals(demand.destination)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + destination.hashCode();
        return result;
    }
}
