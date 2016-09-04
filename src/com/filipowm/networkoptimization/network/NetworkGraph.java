package com.filipowm.networkoptimization.network;

import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing whole network graph, all vertices and edges.
 */
public class NetworkGraph extends DefaultDirectedGraph<NetworkVertex, NetworkEdge> {

    public NetworkGraph() {
        super(NetworkEdge.class);
        setAllowingLoopsFalse();
    }

    private void setAllowingLoopsFalse() {
        try {
            Field field = AbstractBaseGraph.class.getDeclaredField("allowingLoops");
            field.setAccessible(true);
            field.setBoolean(this, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find vertex by name
     * @param name vertex name
     * @return vertex
     */
    public NetworkVertex findVertex(String name) {
        return vertexSet()
                .stream()
                .filter(vertex -> vertex.getName().equals(name))
                .findFirst()
                .get();
    }

    public List<NetworkVertex> vertexList() {
        List<NetworkVertex> list = new ArrayList<>(vertexSet());
        Collections.sort(list, (o1, o2) -> o1.getVertexNum().compareTo(o2.getVertexNum()));
        return list;
    }
}
