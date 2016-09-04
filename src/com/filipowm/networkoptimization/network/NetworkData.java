package com.filipowm.networkoptimization.network;

import java.util.ArrayList;
import java.util.List;

public class NetworkData {
    public Integer Bandwidth;
    public ArrayList<String> nodes = new ArrayList<>();
    public ArrayList<String> edgeStart = new ArrayList<>();
    public ArrayList<String> edgeEnd = new ArrayList<>();
    public ArrayList<String> demandSource = new ArrayList<>();
    public ArrayList<String> demandSink = new ArrayList<>();
    public ArrayList<String> demandValue = new ArrayList<>();
    public ArrayList<String> borders = new ArrayList<>();
    public ArrayList<String> coefficients = new ArrayList<>();
    public List<List<NetworkPath>> demandsPathsList = new ArrayList<>();
    public Integer pathsNum;
}