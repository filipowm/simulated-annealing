package com.filipowm.networkoptimization.network.generator;

import java.util.ArrayList;
import java.util.List;

import com.filipowm.networkoptimization.network.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;

public class PathGenerator {

    private final NetworkGraph graph;
    private final int maxPaths;

    public PathGenerator(final NetworkGraph graph, int maxPaths){
        this.graph = graph;
        this.maxPaths = maxPaths;
    }

    public List<NetworkPath> generate(Demand demand) {
        List<GraphPath<NetworkVertex, NetworkEdge>> paths = generate(demand.getSource(), demand.getDestination());

        List<NetworkPath> networkPaths = new ArrayList<>();
        if (paths == null) {
            return networkPaths;
        }
        for (GraphPath<NetworkVertex, NetworkEdge> path : paths) {
            NetworkPath networkPath = new NetworkPath(demand.getSource(), demand.getDestination(), path, graph, demand);
            networkPaths.add(networkPath);
        }

        return networkPaths;
    }

    public static boolean pathExists(NetworkGraph graph, NetworkVertex startNode, NetworkVertex endNode) {
        PathGenerator generator = new PathGenerator(graph, 1);
        Demand demand = new Demand(startNode, endNode, 0);
        return generator.generate(demand).size() > 0;
    }

    private List<GraphPath<NetworkVertex, NetworkEdge>> generate(NetworkVertex startNode, NetworkVertex endNode){
        KShortestPaths<NetworkVertex, NetworkEdge> kPathsGraph = new KShortestPaths<>(graph, startNode, maxPaths);
        return kPathsGraph.getPaths(endNode);
    }
}
