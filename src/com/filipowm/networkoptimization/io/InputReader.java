package com.filipowm.networkoptimization.io;

import com.filipowm.networkoptimization.network.NetworkPath;
import com.filipowm.networkoptimization.network.NetworkData;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputReader {

    private static NetworkData loadedData = new NetworkData();

    public static NetworkData readDataFromFile(String filename) {
        try(BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
            String line = br.readLine();

            while (line != null) {
                String[] fieldsInFile = line.split(" ");
                line = br.readLine();

                pushFieldsToArrays(fieldsInFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadedData;
    }

    private static void pushFieldsToArrays(String[] fieldsInFile){
        if(fieldsInFile[0].equals("Nodes")) {
            loadedData.nodes.addAll(Arrays.asList(fieldsInFile).subList(2, fieldsInFile.length));

        }
        if(fieldsInFile[0].equals("Links")) {
            for(int i = 2; i<fieldsInFile.length; i=i+2){
                loadedData.edgeStart.add(fieldsInFile[i]);
                loadedData.edgeEnd.add(fieldsInFile[i + 1]);
            }
        }
        if(fieldsInFile[0].equals("Demands")) {
            for(int i = 2; i<fieldsInFile.length; i=i+3){
                loadedData.demandSource.add(fieldsInFile[i]);
                loadedData.demandSink.add(fieldsInFile[i+1]);
                loadedData.demandValue.add(fieldsInFile[i+2]);
            }
        }
        if(fieldsInFile[0].equals("Borders")) {
            for(int i = 2; i<fieldsInFile.length; ++i){
                loadedData.borders.add(fieldsInFile[i]);
            }
        }
        if(fieldsInFile[0].equals("Coefficients")) {
            for(int i = 2; i<fieldsInFile.length; ++i){
                loadedData.coefficients.add(fieldsInFile[i]);
            }
        }
        if(fieldsInFile[0].equals("Bandwidth")) {
            loadedData.Bandwidth = Integer.parseInt(fieldsInFile[2]);
        }
    }

    public Integer getBandwidth() {
        return loadedData.Bandwidth;
    }

    public ArrayList<String> getNodes() {
        return loadedData.nodes;
    }

    public ArrayList<String> getEdgeStart() {
        return loadedData.edgeStart;
    }

    public ArrayList<String> getEdgeEnd() {
        return loadedData.edgeEnd;
    }

    public ArrayList<String> getDemandSource() {
        return loadedData.demandSource;
    }

    public ArrayList<String> getDemandSink() {
        return loadedData.demandSink;
    }

    public ArrayList<String> getDemandValue() {
        return loadedData.demandValue;
    }

    public List<List<NetworkPath>> getdemandsPathsList() {
        return loadedData.demandsPathsList;
    }

    public void setLoadedData(NetworkData loadedData) {
        this.loadedData = loadedData;
    }

    public Integer getPathsNum() {
        return loadedData.pathsNum;
    }
}
