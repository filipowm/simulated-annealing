package com.filipowm.networkoptimization.io;

import com.filipowm.networkoptimization.network.NetworkData;

import java.io.IOException;

class LinkPathWriter extends AbstractModelOutputWriter {

    public LinkPathWriter(NetworkData data) {
        super(data);
    }

    @Override
    protected void write() throws IOException {
        writePathNumbers();
        appendDoubleSeparator();

        writeNodes();
        appendDoubleSeparator();

        writeLinks();
        appendDoubleSeparator();

        writeDemands();
        appendDoubleSeparator();

        writePaths();
        appendDoubleSeparator();

        writeModulations();
    }
}
