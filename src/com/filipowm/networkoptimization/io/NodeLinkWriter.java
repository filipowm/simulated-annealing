package com.filipowm.networkoptimization.io;

import com.filipowm.networkoptimization.network.NetworkData;

import java.io.IOException;

class NodeLinkWriter extends AbstractModelOutputWriter {
    public NodeLinkWriter(NetworkData data) {
        super(data);
    }

    @Override
    protected void write() throws IOException {
        writeNodes();
        appendDoubleSeparator();

        writeLinks();
        appendDoubleSeparator();

        writeDemands();
    }
}
