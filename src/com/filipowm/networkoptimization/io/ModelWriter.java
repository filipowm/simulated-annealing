package com.filipowm.networkoptimization.io;

import com.filipowm.networkoptimization.network.NetworkData;

import java.io.IOException;

public class ModelWriter {

    public static void write(NetworkData data, String fileName) throws IOException {
        writeLinkPath(data, fileName);
        writeNodeLink(data, fileName);
    }

    public static void writeNodeLink(NetworkData data, String fileName) throws IOException{
        NodeLinkWriter nodeLinkWriter = new NodeLinkWriter(data);
        nodeLinkWriter.write(fileName + "_nl.dat");
    }

    public static void writeLinkPath(NetworkData data, String fileName) throws IOException {
        LinkPathWriter linkPathWriter = new LinkPathWriter(data);
        linkPathWriter.write(fileName + "_lp.dat");
    }
}
