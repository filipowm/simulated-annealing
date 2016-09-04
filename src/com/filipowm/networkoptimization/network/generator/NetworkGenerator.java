package com.filipowm.networkoptimization.network.generator;

import com.filipowm.networkoptimization.network.Demand;
import com.filipowm.networkoptimization.network.NetworkGraph;
import com.filipowm.networkoptimization.network.NetworkManager;
import com.filipowm.networkoptimization.network.NetworkVertex;

import java.util.*;

public class NetworkGenerator {

    public static NetworkGraph generateGraph(final NetworkGraph graph, int numOfVertexes, int numOfEdges) {
        NetworkGraphGenerator generator = new NetworkGraphGenerator(numOfVertexes, numOfEdges);
        generator.generateGraph(graph);
        return graph;
    }

    public static Collection<Demand> generateDemands(final NetworkGraph graph, int numOfDemands) {
        List<NetworkVertex> vertexList = graph.vertexList();
        int numOfVertices = vertexList.size();
        Set<Demand> demands = new HashSet<>(numOfDemands);
        Random randomizer = new Random(SeedCreator.getSeed());
        for(; demands.size() < numOfDemands; ) {
            NetworkVertex startVertex = vertexList.get(randomizer.nextInt(numOfVertices));
            NetworkVertex endVertex = vertexList.get(randomizer.nextInt(numOfVertices));
            if (startVertex == endVertex) {
                continue;
            }
            if (pathExists(graph, startVertex, endVertex)) {
                double value = 0;
                while (value == 0) {
                    value = randomizer.nextInt(NetworkManager.EDGE_BANDWIDTH/10);
                }
                Demand demand = new Demand(startVertex, endVertex, value);
                demands.add(demand);
            }
        }
        return demands;
    }

    private static  boolean pathExists(final NetworkGraph graph, NetworkVertex startVertex, NetworkVertex endVertex) {
        return PathGenerator.pathExists(graph, startVertex, endVertex);
    }
}
