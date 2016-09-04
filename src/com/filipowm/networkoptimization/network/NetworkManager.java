package com.filipowm.networkoptimization.network;

import com.filipowm.networkoptimization.annealing.AnnealingSolution;
import com.filipowm.networkoptimization.Main;
import com.filipowm.networkoptimization.annealing.AnnealingScheduler;
import com.filipowm.networkoptimization.annealing.DefaultScheduler;
import com.filipowm.networkoptimization.annealing.SimulatedAnnealingProblemSolver;
import com.filipowm.networkoptimization.network.generator.NetworkGenerator;
import com.filipowm.networkoptimization.network.generator.PathGenerator;

import java.util.*;

/**
 * Class responsible for managing whole network. It can generate network graph and manage paths and demands.
 */
public final class NetworkManager {

    private final NetworkGraph graph = new NetworkGraph();
    public static final int EDGE_BANDWIDTH = 100;

    private final Map<Demand, List<NetworkPath>> possiblePaths = new HashMap<>();
    private final List<Modulation> modulations = new ArrayList<>();
    private final Set<Demand> demands = new HashSet<>();
    private final List<Demand> demandsList = new ArrayList<>();

    public List<Demand> getDemands() {
        return demandsList;
    }

    private static final NetworkManager networkManager = new NetworkManager();

    private NetworkManager() {}

    private void initializeModulations(List<String> borders, List<String> values) {
        Iterator<String> borderIterator = borders.iterator();
        Iterator<String> valueIterator = values.iterator();
        while (borderIterator.hasNext() && valueIterator.hasNext()) {
            String border = borderIterator.next();
            String value = valueIterator.next();
            try {
                addModulation(new Modulation(Integer.parseInt(border), Integer.parseInt(value)));
            } catch (NumberFormatException e) {
                System.out.println("Unparsable modulation - " + border + " : " + value);
            }
        }
    }

    private void initializeModulaions() {
        int maxPathLength = graph.vertexSet().size() - 1;
        int value = 1;
        for (int i = 0; i <= maxPathLength + 3; i += 2, value++) {
            if (i == 1) {
                continue;
            }
            Modulation modulation = new Modulation(i, value);
            addModulation(modulation);
        }
    }

    public void addModulation(Modulation modulation) {
        modulations.add(modulation);
        Collections.sort(modulations);
    }

    public Modulation getModulation(int pathLength) {
        return modulations
                .stream()
                .filter(mod -> mod.getPathSize() <= pathLength)
                .findFirst()
                .get();
    }

    public void generateNetwork(NetworkData data) {
        createNetworkGraph(data);
        initializeModulations(data.borders, data.coefficients);
    }

    public void generateNetwork(int numOfVertexes, int numOfEdges) {
        long time = System.currentTimeMillis();
        NetworkGenerator.generateGraph(graph, numOfVertexes, numOfEdges);
        System.out.println("Network generation took: " + (System.currentTimeMillis() - time));
        initializeModulaions();
    }

    public void generateDemands(int numOfDemands) {
        long time = System.currentTimeMillis();
        Collection<Demand> gDemands = NetworkGenerator.generateDemands(graph, numOfDemands);
        demands.clear();
        System.out.println("Demands generation took: " + (System.currentTimeMillis() - time));
        demands.addAll(gDemands);
        demandsList.addAll(demands);
    }

    private void createNetworkGraph(NetworkData data) {

        addVerticesToGraph(data.nodes);
        addEdgesToGraph(data.edgeStart, data.edgeEnd);
    }

    private void addVerticesToGraph(List<String> nodeNames) {
        for (String nodeName : nodeNames) {
            NetworkVertex vertex = new NetworkVertex(nodeName);
            graph.addVertex(vertex);
        }
    }

    private void addEdgesToGraph(List<String> sourceVertices, List<String> endVertices) {
        Iterator<String> sourcesIterator = sourceVertices.iterator();
        Iterator<String> endsIterator = endVertices.iterator();

        while (sourcesIterator.hasNext() && endsIterator.hasNext()) {
            String startVertexName = sourcesIterator.next();
            String endVertexName = endsIterator.next();

            NetworkVertex startVertex = graph.findVertex(startVertexName);
            NetworkVertex endVertex = graph.findVertex(endVertexName);
            graph.addEdge(startVertex, endVertex);
        }
    }

    public void addDemands(List<String> sources, List<String> sinks, List<String> values) {
        Iterator<String> sourcesIterator = sources.iterator();
        Iterator<String> sinksIterator = sinks.iterator();
        Iterator<String> valuesIterator = values.iterator();
        while (sourcesIterator.hasNext() && sinksIterator.hasNext() && valuesIterator.hasNext()) {
            String source = sourcesIterator.next();
            String sink = sinksIterator.next();
            String value = valuesIterator.next();
            Double dValue;
            try {
                dValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                dValue = 0.0d;
            }
            Demand demand = new Demand(graph.findVertex(source), graph.findVertex(sink), dValue);
            demands.add(demand);
        }
    }

    public int generatePaths(int maxPaths) {
        possiblePaths.clear();
        System.out.println("Generating " + maxPaths + " paths");
        final PathGenerator pathGenerator = new PathGenerator(graph, maxPaths);
        long time = System.currentTimeMillis();
        for (Demand demand : demands) {
            List<NetworkPath> paths = pathGenerator.generate(demand);
            if (paths.size() < maxPaths) {
                generatePaths(maxPaths - 1);
            }
            possiblePaths.put(demand, paths);
        }
        System.out.println("Paths generation took: " + (System.currentTimeMillis() - time));
        return maxPaths;
    }

    public AnnealingSolution optimize(double coolingMultiplier) {
        NetworkSimulatedAnnealingOptimization opt = new NetworkSimulatedAnnealingOptimization();
        AnnealingScheduler scheduler = new DefaultScheduler(Main.INITIAL_TEMPERATURE, Main.END_TEMPERATURE, coolingMultiplier);
        SimulatedAnnealingProblemSolver solver = new SimulatedAnnealingProblemSolver(scheduler, opt);
        long time = System.currentTimeMillis();
        solver.solve();
        System.out.println("Optimization took: " + (System.currentTimeMillis() - time));
        return opt.getSolution();
    }

    public static NetworkManager getInstance() {
        return networkManager;
    }

    public double getCurrentFlowMaxWeight() {
        double current = 0.0;
        double next;
        for (NetworkEdge edge : graph.edgeSet()) {
            next = edge.getUsage();
            if (next > current && next > 0) {
                current = next;
            }
        }
        return current;
    }

    public Map<Demand, List<NetworkPath>> getPossiblePaths() {
        return possiblePaths;
    }

    public Collection<NetworkEdge> getEdges() {
        return graph.edgeSet();
    }

    public Collection<NetworkVertex> getVertices() {
        return graph.vertexSet();
    }

    public void writePathsToOut() {
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Demand is presented as (source -> destination : demand value)");
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Path is presented as (path : cost on single edge)");
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Possible paths for demands:");
        sb.append(Main.LINE_SEPARATOR);
        for (Map.Entry<Demand, List<NetworkPath>> entry : possiblePaths.entrySet()) {
            Demand demand = entry.getKey();
            sb.append("Demand: ");
            sb.append(demand.getSource().getName());
            sb.append(" -> ");
            sb.append(demand.getDestination().getName());
            sb.append(" : ");
            sb.append(demand.getValue());

            sb.append(Main.LINE_SEPARATOR);
            sb.append("Paths:");
            sb.append(Main.LINE_SEPARATOR);
            for (NetworkPath path : entry.getValue()) {
                for (NetworkEdge edge : path.getEdgeList()) {
                    sb.append(edge);
                }
                sb.append(" : ");
                sb.append(path.getWeight());
                sb.append(Main.LINE_SEPARATOR);
            }
        }
        System.out.println("Paths writing to out took: " + (System.currentTimeMillis() - time));
        System.out.println(sb.toString());
    }

    public NetworkData toNetworkData() {
        NetworkData data = new NetworkData();
        for (NetworkVertex vertex : graph.vertexList()) {
            data.nodes.add(vertex.getName());
        }
        for (NetworkEdge edge : graph.edgeSet()) {
            data.edgeStart.add(edge.getSource().getName());
            data.edgeEnd.add(edge.getTarget().getName());
        }
        for (Map.Entry<Demand, List<NetworkPath>> entry : possiblePaths.entrySet()) {
            Demand demand = entry.getKey();
            data.demandSource.add(demand.getSource().getName());
            data.demandSink.add(demand.getDestination().getName());
            data.demandValue.add(String.valueOf(demand.getValue()));
            data.demandsPathsList.add(entry.getValue());
            data.pathsNum = entry.getValue().size();
        }
        data.Bandwidth = EDGE_BANDWIDTH;
        for (Modulation modulation : modulations) {
            data.borders.add(String.valueOf(modulation.getPathSize()));
            data.coefficients.add(String.valueOf(modulation.getValue()));
        }
        return data;
    }
}
