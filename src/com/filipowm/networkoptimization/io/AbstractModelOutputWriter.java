package com.filipowm.networkoptimization.io;
import com.filipowm.networkoptimization.network.*;
import com.filipowm.networkoptimization.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public abstract class AbstractModelOutputWriter implements OutputWriter {

    private FileWriter fileWriter;
    private NetworkData networkData;

    public AbstractModelOutputWriter(NetworkData data) {
        this.networkData = data;
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Files.write(Paths.get(filePath), content.getBytes());
    }

    public static void appendToFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.APPEND);
    }

    protected void writePathNumbers() throws IOException {
        append("set NrPaths := ");
        for (int i = 1; i < networkData.pathsNum; i++) {
            append(i);
            appendSpace();
        }
        append(networkData.pathsNum);
        appendSemicolon();
    }

    protected void writeNodes() throws IOException {
        append("set Nodes := ");
        for(String node : networkData.nodes){
            append(node);
            appendSpace();
        }
        appendSemicolon();
    }

    protected void writeLinks() throws IOException  {
        append("set Links := ");

        for(int i = 0; i < networkData.edgeEnd.size(); ++i) {
            appendLeftBrace();
            append(networkData.edgeStart.get(i));
            appendComma();
            append(networkData.edgeEnd.get(i));
            appendRightBrace();
        }
        appendSemicolon();
    }

    protected void writeDemands() throws IOException {
        append("set Demands := ");

        for(int i = 0; i < networkData.demandSource.size(); ++i) {
            appendLeftBrace();
            append(networkData.demandSource.get(i));
            appendComma();
            append(networkData.demandSink.get(i));
            appendRightBrace();
        }
        appendSemicolon();
        appendDoubleSeparator();
        writeDemandValues();
    }

    private void writeDemandValues() throws IOException {
        append("param demandValue := ");
        appendSeparator();

        for(int i = 0; i < networkData.demandSource.size(); ++i) {
            append(networkData.demandSource.get(i));
            appendSpace();
            append(networkData.demandSink.get(i));
            appendSpace();
            append(networkData.demandValue.get(i));
            appendSeparator();
        }
        appendSemicolon();
    }

    protected void writePaths() throws IOException {
        append("param Paths := ");
        appendSeparator();

        for (int i = 0; i < networkData.demandsPathsList.size(); i++) {
            for (int j = 0; j < networkData.demandsPathsList.get(i).size(); j++) {
                append("[");
                append(j+1);
                appendComma();
                append(networkData.demandSource.get(i));
                appendComma();
                append(networkData.demandSink.get(i));
                append(",*,*]: ");
                String row;

                for (String node : networkData.nodes) {
                    append(node);
                    appendSpace();
                }
                append(":= ");
                appendSeparator();

                String[][] pathMatrix = new String[networkData.nodes.size()][networkData.nodes.size()];

                for (int k = 0; k < networkData.nodes.size(); k++) {
                    for (int l = 0; l < networkData.nodes.size(); l++) {
                        pathMatrix[k][l] = "0";
                    }
                }

                List<NetworkEdge> edges = networkData.demandsPathsList.get(i).get(j).getEdgeList();
                for (NetworkEdge edge : edges) {
                    String x = edge.toString().substring(2, edge.toString().indexOf(":") - 1);
                    String y = edge.toString().substring(edge.toString().indexOf(":") + 3, edge.toString().indexOf(")"));
                    pathMatrix[Integer.parseInt(x) - 1][Integer.parseInt(y) - 1] = "1";
                }

                for (int k = 0; k < networkData.nodes.size(); k++) {
                    row = networkData.nodes.get(k) + "    ";
                    for (int l = 0; l < networkData.nodes.size(); l++) {
                        row += pathMatrix[k][l] + " ";
                    }
                    append(row);
                    appendSeparator();
                }
                appendSeparator();
            }
        }
        appendSemicolon();
    }

    protected void writeModulations() throws IOException {
        append("param Modulations :=");
        appendSeparator();
        NetworkManager manager = NetworkManager.getInstance();

        for (int i = 1; i <= networkData.pathsNum; i++) {
            for (Map.Entry<Demand, List<NetworkPath>> entry : manager.getPossiblePaths().entrySet()) {
                Demand demand = entry.getKey();
                List<NetworkPath> paths = entry.getValue();
                append("[");
                append(i);
                appendComma();
                append(demand.getSource().getName());
                appendComma();
                append(demand.getDestination().getName());
                append("] ");
                append(manager.getModulation(paths.get(i - 1).getEdgesCount()).getValue());
                appendSeparator();
            }
        }
        appendSemicolon();
    }

    protected void append(String data) throws IOException {
        if (fileWriter != null) {
            fileWriter.write(data);
        }
    }

    protected void append(int data) throws IOException {
        append(String.valueOf(data));
    }

    protected void appendSeparator() throws IOException {
        append(Main.LINE_SEPARATOR);
    }

    protected void appendDoubleSeparator() throws IOException {
        appendSeparator();
        appendSeparator();
    }

    protected void appendSemicolon() throws IOException {
        append(";");
    }

    protected void appendSpace() throws IOException {
        append(" ");
    }

    protected void appendLeftBrace() throws IOException {
        append("(");
    }

    protected void appendRightBrace() throws IOException {
        append(")");
    }

    protected void appendComma() throws IOException {
        append(",");
    }

    @Override
    public void write(String fileName) throws IOException {
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            fileWriter = new FileWriter(file);
            append("data;");
            appendDoubleSeparator();
            write();
        } finally {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }
    }

    protected abstract void write() throws IOException;
}

