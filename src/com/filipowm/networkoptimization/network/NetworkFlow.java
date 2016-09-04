package com.filipowm.networkoptimization.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing simple network flow, that is set of paths for each demand which are currently used.
 */
public class NetworkFlow {

    private final Map<Demand, NetworkPath> runningPaths = new HashMap<>();

    public Map<Demand, NetworkPath> getRunningPaths() {
        return runningPaths;
    }

    public NetworkFlow() {
        unusePaths();
    }

    public NetworkFlow(NetworkFlow baseFlow) {
        runningPaths.putAll(baseFlow.getRunningPaths());
        unusePaths();
    }

    public void addPath(Demand demand, NetworkPath path) {
        runningPaths.put(demand, path);
    }

    public void usePaths() {
        for (NetworkPath path : runningPaths.values()) {
            path.usePath();
        }
    }

    public void unusePaths() {
        for (NetworkPath path : runningPaths.values()) {
            path.unusePath();
        }
    }


}
