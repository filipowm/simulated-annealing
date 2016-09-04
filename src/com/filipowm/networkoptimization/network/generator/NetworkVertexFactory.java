package com.filipowm.networkoptimization.network.generator;

import org.jgrapht.VertexFactory;
import com.filipowm.networkoptimization.network.NetworkVertex;

public class NetworkVertexFactory implements VertexFactory<NetworkVertex> {

    private int currentVertexNum = 0;

    @Override
    public NetworkVertex createVertex() {
        int vertexNum = ++currentVertexNum;
        return  new NetworkVertex("K", vertexNum);
    }
}
