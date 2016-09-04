package com.filipowm.networkoptimization.network;

/**
 * Class representing single network vertex.
 */
public class NetworkVertex {

    private final String name;
    private final int vertexNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkVertex that = (NetworkVertex) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public NetworkVertex(String argName, int vertexNum) {
        name = argName + vertexNum;
        this.vertexNum = vertexNum;
    }

    public NetworkVertex(String argName) {
        vertexNum = 0;
        name = argName;
    }
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Integer getVertexNum() {
        return vertexNum;
    }
}
