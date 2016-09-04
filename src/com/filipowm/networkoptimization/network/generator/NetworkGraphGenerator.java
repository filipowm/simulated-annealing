package com.filipowm.networkoptimization.network.generator;

import com.filipowm.networkoptimization.network.NetworkEdge;
import com.filipowm.networkoptimization.network.NetworkVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NetworkGraphGenerator {

    protected int numOfVertexes;
    protected int numOfEdges;
    protected Random randomizer;
    private long randomizerSeed;

    public void generateGraph(Graph<NetworkVertex, NetworkEdge> target) {
        this.resetRandomSeed();
        HashMap orderToVertexMap = new HashMap(this.numOfVertexes);
        NetworkVertexFactory vertexFactory = new NetworkVertexFactory();

        for(int edgesFactory = 0; edgesFactory < this.numOfVertexes; ++edgesFactory) {
            NetworkVertex currVertex = vertexFactory.createVertex();
            target.addVertex(currVertex);
            orderToVertexMap.put(Integer.valueOf(edgesFactory), currVertex);
        }

        if(target.vertexSet().size() != this.numOfVertexes) {
            throw new IllegalArgumentException("Vertex factory did not produce " + this.numOfVertexes + " distinct vertices.");
        } else {
            NetworkGraphGenerator.EdgeTopologyFactory var7 = this.edgeTopologyFactoryChooser();
            if(!var7.isNumberOfEdgesValid(target, this.numOfEdges)) {
                throw new IllegalArgumentException("numOfEdges is not valid for the graph type \n-> Invalid number Of Edges=" + this.numOfEdges + " for:" + " graph type=" + target.getClass() + " ,number Of Vertexes=" + this.numOfVertexes + "\n-> Advice: For the Max value , check the javadoc for" + " org.jgrapht.generate.NetworkGraphGenerator.DefaultEdgeTopologyFactory");
            } else {
                var7.createEdges(target, orderToVertexMap, this.numOfEdges, this.randomizer);
            }
        }
    }

    public NetworkGraphGenerator(int aNumOfVertexes, int aNumOfEdges) {
        if(aNumOfVertexes >= 0 && aNumOfEdges >= 0) {
            this.numOfVertexes = aNumOfVertexes;
            this.numOfEdges = aNumOfEdges;
            this.randomizerSeed = chooseRandomSeedOnce();
            this.randomizer = new Random(this.randomizerSeed);
        } else {
            throw new IllegalArgumentException("must be non-negative");
        }
    }

    private static synchronized long chooseRandomSeedOnce() {
        return SeedCreator.getSeed();
    }

    private void resetRandomSeed() {
        this.randomizer.setSeed(this.randomizerSeed);
    }

    private NetworkGraphGenerator.EdgeTopologyFactory<NetworkVertex, NetworkEdge> edgeTopologyFactoryChooser() {
        return new NetworkGraphGenerator.DefaultEdgeTopologyFactory();
    }

    public class DefaultEdgeTopologyFactory implements NetworkGraphGenerator.EdgeTopologyFactory<NetworkVertex, NetworkEdge> {
        public DefaultEdgeTopologyFactory() {
        }

        public void createEdges(Graph<NetworkVertex, NetworkEdge> targetGraph, Map<Integer, NetworkVertex> orderToVertexMap, int numberOfEdges, Random randomizer) {

            for(int edgesCounter = 0; edgesCounter < numberOfEdges; ) {
                NetworkVertex startVertex = orderToVertexMap.get(randomizer.nextInt(NetworkGraphGenerator.this.numOfVertexes));
                NetworkVertex endVertex = orderToVertexMap.get(randomizer.nextInt(NetworkGraphGenerator.this.numOfVertexes));
                if (startVertex == endVertex
                        || targetGraph.containsEdge(endVertex, startVertex)
                        || targetGraph.containsEdge(startVertex, endVertex)) {
                    continue;
                }
                try {
                    NetworkEdge e = targetGraph.addEdge(startVertex, endVertex);
                    if(e != null) {
                        ++edgesCounter;
                    }
                } catch (Exception var10) {
                    var10.printStackTrace();
                }
            }

        }

        public boolean isNumberOfEdgesValid(Graph<NetworkVertex, NetworkEdge> targetGraph, int numberOfEdges) {
            boolean infinite = false;
            int maxAllowedEdges = this.getMaxEdgesForVertexNum(targetGraph);
            if (maxAllowedEdges == -1) {
                infinite = true;
            }

            boolean result;
            result = infinite || numberOfEdges <= maxAllowedEdges;

            return result;
        }

        public int getMaxEdgesForVertexNum(Graph<NetworkVertex, NetworkEdge> targetGraph) {
            int maxAllowedEdges1;
            if(targetGraph instanceof SimpleGraph) {
                maxAllowedEdges1 = NetworkGraphGenerator.this.numOfVertexes * (NetworkGraphGenerator.this.numOfVertexes - 1) / 2;
            } else if(targetGraph instanceof SimpleDirectedGraph) {
                maxAllowedEdges1 = NetworkGraphGenerator.this.numOfVertexes * (NetworkGraphGenerator.this.numOfVertexes - 1);
            } else if(targetGraph instanceof DefaultDirectedGraph) {
                maxAllowedEdges1 = NetworkGraphGenerator.this.numOfVertexes * NetworkGraphGenerator.this.numOfVertexes;
            } else {
                maxAllowedEdges1 = -1;
            }

            return maxAllowedEdges1;
        }
    }

    public interface EdgeTopologyFactory<VV, EE> {
        void createEdges(Graph<VV, EE> var1, Map<Integer, VV> var2, int var3, Random var4);

        boolean isNumberOfEdgesValid(Graph<VV, EE> var1, int var2);
    }
}
