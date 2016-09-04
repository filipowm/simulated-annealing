package com.filipowm.networkoptimization.network;

import com.filipowm.networkoptimization.Main;
import com.filipowm.networkoptimization.annealing.AnnealingSolution;
import com.filipowm.networkoptimization.annealing.SimulatedAnnealingProblem;
import pl.elka.pstkm.annealing.SimulatedAnnealingProblem;

import java.util.*;

/**
 * Class representing network optimization by application of simulated annealing algorithm.
 */
public class NetworkSimulatedAnnealingOptimization implements SimulatedAnnealingProblem {

    Random random = new Random();
    NetworkFlow currentFlow;
    NetworkFlow nextFlow;
    NetworkManager manager = NetworkManager.getInstance();
    long numberOfAnnealings;

    @Override
    public void init() {
        //initialize first flow - get first paths for demands
        currentFlow = new NetworkFlow();
        for (Map.Entry<Demand, List<NetworkPath>> entry : manager.getPossiblePaths().entrySet()) {
            List<NetworkPath> paths = entry.getValue();
            NetworkPath min = paths.get(0);
            for (NetworkPath path : paths) {
                if (path.getEdgesCount() < min.getEdgesCount()) {
                    min = path;
                }
            }
            currentFlow.addPath(entry.getKey(), min);
        }
    }

    private double getCostForFlow(NetworkFlow flow) {
        if (flow != null) {
            flow.usePaths();
            double cost = manager.getCurrentFlowMaxWeight();
            flow.unusePaths();
            return cost;
        }
        return 0.0;
    }

    @Override
    public double getCostForCurrentState() {
        return getCostForFlow(currentFlow);
    }

    @Override
    public void createNextState() {
        nextFlow = new NetworkFlow(currentFlow);
        //for each demand randomly choose next used path
        int i1 = random.nextInt(manager.getDemands().size());
        Demand demand = manager.getDemands().get(i1);
        List<NetworkPath> possiblePaths = manager.getPossiblePaths().get(demand);

        int i2 = random.nextInt(possiblePaths.size());
        NetworkPath path = possiblePaths.get(i2);
        NetworkPath currentPath = currentFlow.getRunningPaths().get(demand);
        if (possiblePaths.size() > 1) {
            while (path == currentPath) {
                i2 = random.nextInt(possiblePaths.size());
                path = possiblePaths.get(i2);
            }
        }
        nextFlow.addPath(demand, path);
    }

    @Override
    public double getCostForNextState() {
        return getCostForFlow(nextFlow);
    }

    @Override
    public void goToNextState() {
        if (currentFlow != null) {
            currentFlow.unusePaths();
        }
        if (nextFlow != null) {
            nextFlow.unusePaths();
        }
        currentFlow = nextFlow;
        nextFlow = null;
    }

    @Override
    public boolean isTotalNumberOfStatesReached() {
        return false;
    }

    @Override
    public void setNumberOfAnnealings(long numberOfAnnealings) {
        this.numberOfAnnealings = numberOfAnnealings;
    }

    public long getNumberOfAnnealings() {
        return numberOfAnnealings;
    }

    @Override
    public AnnealingSolution getSolution() {
        AnnealingSolution solution = new AnnealingSolution();
        solution.setFullSolution(getSolutionString());
        solution.setSimpleSolution(getSimpleSolutionString());
        solution.setNumberOfAnnealings(numberOfAnnealings);
        return solution;
    }

    @Override
    public String getSimpleSolutionString() {
        return String.valueOf(getCostForCurrentState());
    }

    @Override
    public String getSolutionString() {
        StringBuilder sb = new StringBuilder("Solution:");
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Total demands: ");
        sb.append(manager.getDemands().size());
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Total possible paths: ");
        int possiblePaths = 0;
        for (List<NetworkPath> paths : manager.getPossiblePaths().values()) {
            possiblePaths += paths.size();
        }
        sb.append(possiblePaths);
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Total vertexes:" );
        sb.append(manager.getVertices().size());
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Total edges: ");
        sb.append(manager.getEdges().size());
        sb.append(Main.LINE_SEPARATOR);
        sb.append("Total problem size: ");
        sb.append(possiblePaths * manager.getDemands().size());
        sb.append(Main.LINE_SEPARATOR);
        for (Map.Entry<Demand, NetworkPath> entry : currentFlow.getRunningPaths().entrySet()) {
            Demand d = entry.getKey();
            sb.append("demand: ");
            sb.append(d.getSource().getName());
            sb.append(" -> ");
            sb.append(d.getDestination().getName());
            sb.append(" : ");
            sb.append(d.getValue());

            NetworkPath path = entry.getValue();
            sb.append(Main.LINE_SEPARATOR);
            sb.append("path: ");
            for (NetworkEdge edge : path.getEdgeList()) {
                sb.append(edge);
            }
            sb.append(" : ");
            sb.append(path.getWeight());
            sb.append(Main.LINE_SEPARATOR).append(Main.LINE_SEPARATOR);
        }
        sb.append("Edges occupation:");
        sb.append(Main.LINE_SEPARATOR);
        currentFlow.usePaths();
        for (NetworkEdge edge : manager.getEdges()) {
            sb.append(edge);
            sb.append(" ");
            sb.append(edge.getUsage());
            sb.append(Main.LINE_SEPARATOR);
        }
        currentFlow.unusePaths();
        sb.append(Main.LINE_SEPARATOR);
        sb.append("maximum edge occupation: ");
        sb.append(getCostForCurrentState());
        return sb.toString();
    }
}
