package com.filipowm.networkoptimization.network;

import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing single network edge connecting two vertices.
 * Network paths can be applied on network edges.
 */
public class NetworkEdge extends DefaultEdge {

    private List<NetworkPath> pathsOnEdge = new ArrayList<>();

    public NetworkVertex getSource() {
        return (NetworkVertex) super.getSource();
    }
    public NetworkVertex getTarget() {
        return (NetworkVertex) super.getTarget();
    }

    public void clearPaths() {
        pathsOnEdge.clear();
    }

    public void addPath(NetworkPath path) {
        pathsOnEdge.add(path);
    }

    public void removePath(NetworkPath path) {
        pathsOnEdge.remove(path);
    }

    public int getUsage() {
        int sum = 0;
        for (NetworkPath path : pathsOnEdge) {
            sum += path.getWeight();
        }
        return sum;
    }
}
