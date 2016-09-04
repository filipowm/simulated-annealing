package com.filipowm.networkoptimization.network;

import org.jgrapht.*;
import org.jgrapht.Graph;

import java.util.List;

/**
 * Class representing path for specific demand.
 */
public class NetworkPath implements GraphPath<NetworkVertex, NetworkEdge> {

    private final NetworkVertex startVertex;
    private final NetworkVertex endVertex;
    private final List<NetworkEdge> edges;
    private final NetworkGraph graph;
    private final Demand demand;
    private final Modulation modulation;

    public NetworkPath(NetworkVertex startVertex, NetworkVertex endVertex, GraphPath<NetworkVertex, NetworkEdge> path, NetworkGraph graph, Demand demand) {
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.graph = graph;
        this.demand = demand;
        edges = path.getEdgeList();
        this.modulation = NetworkManager.getInstance().getModulation(edges.size());
    }

    @Override
    public Graph<NetworkVertex, NetworkEdge> getGraph() {
        return graph;
    }

    @Override
    public NetworkVertex getStartVertex() {
        return startVertex;
    }

    @Override
    public NetworkVertex getEndVertex() {
        return endVertex;
    }

    @Override
    public List<NetworkEdge> getEdgeList() {
        return edges;
    }

    @Override
    public double getWeight() {
        return demand.getValue() * modulation.getValue();
    }

    public void usePath() {
        for (NetworkEdge edge : edges) {
            edge.addPath(this);
        }
    }

    public void unusePath() {
        for (NetworkEdge edge : edges) {
            edge.removePath(this);
        }
    }

    public int getEdgesCount() {
        return edges.size();
    }
}
