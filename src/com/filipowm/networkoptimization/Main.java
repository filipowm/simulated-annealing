package com.filipowm.networkoptimization;

import com.filipowm.networkoptimization.annealing.AnnealingSolution;
import com.filipowm.networkoptimization.io.AbstractModelOutputWriter;
import com.filipowm.networkoptimization.io.ModelWriter;
import com.filipowm.networkoptimization.network.NetworkManager;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import com.filipowm.networkoptimization.network.NetworkData;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static final int INITIAL_TEMPERATURE = 10000;
    public static final float END_TEMPERATURE = 0.01f;

    private static final int DEFAULT_MAX_PATHS = 3;
    private static final int DEFAULT_VERTEXES = 20;
    private static final int DEFAULT_EDGES = 50;
    private static final int DEFAULT_DEMANDS = 10;
    private static final int DEFAULT_STEPS = 50000;
    private static final String SOLUTION_FILE_PREFIX = "solution";
    private static final String DATA_FILE_PREFIX = "data_";
    private static final String DATA_DIRECTORY = "data";

    public static final String LINE_SEPARATOR = System.lineSeparator();

    private InitialParams params = new InitialParams();
    private NetworkManager networkManager = NetworkManager.getInstance();

    private String getFileSuffix() {
        return "v" + params.numOfVertices + "e" + params.numOfEdges + "d" + params.numOfDemands + "p" + params.maxPaths;
    }

    private String getFileSuffix(TestOption testOption) {
        String suffix = getFileSuffix();
        if (testOption == TestOption.COOLING) {
            suffix += getFileCoolingSuffix();
        } else if (testOption == TestOption.COOLING_PATH) {
            suffix += getFileCoolingSuffix();
            suffix += getFilePathSuffix();
        } else if (testOption == TestOption.PATH) {
            suffix += getFilePathSuffix();
        }
        return suffix;
    }

    private String getFileCoolingSuffix() {
        return "cooling";
    }

    private String getFilePathSuffix() {
        return "pstart" + params.pathsStart +"pstep" + params.pathsStep;
    }

    private void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(params);
        parser.setUsageWidth(80);
        try {
            // parse the arguments.
            parser.parseArgument(args);

        } catch( CmdLineException e ) {
            System.err.println(e.getMessage());
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            System.err.println(" Example: java Main" + parser.printExample(ExampleMode.ALL));
        } finally {
            params.coolingMultiplier = params.getCoolingMultiplier(params.steps);
        }
    }

    private void init() {
        networkManager.generateNetwork(params.numOfVertices, params.numOfEdges);
        networkManager.generateDemands(params.numOfDemands);
    }

    private String getPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "\\" + DATA_DIRECTORY + "\\";
    }

    private void execute() throws IOException {
        AnnealingSolution solution;

        if (params.testForPathsDependence) {
            String fileSuffix = getFileSuffix(TestOption.PATH);
            StringBuilder sb = new StringBuilder();
            sb.append("paths,stepsArray,solution");
            sb.append(LINE_SEPARATOR);
            for (int i = params.pathsStart; i <= params.maxPaths; i += params.pathsStep) {
                int generatedPaths = networkManager.generatePaths(i);
                networkManager.writePathsToOut();
                solution = networkManager.optimize(params.coolingMultiplier);
                sb.append(generatedPaths);
                sb.append(",");
                sb.append(params.steps);
                sb.append(",");
                sb.append(solution.getSimpleSolution());
                sb.append(LINE_SEPARATOR);


                ModelWriter.writeLinkPath(networkManager.toNetworkData(), getPath() + fileSuffix + "\\" + DATA_FILE_PREFIX + i);
            }
            AbstractModelOutputWriter.writeToFile(getPath() + fileSuffix + "\\" + SOLUTION_FILE_PREFIX + ".txt", sb.toString());
            ModelWriter.writeNodeLink(networkManager.toNetworkData(), getPath() + fileSuffix + "\\" + DATA_FILE_PREFIX);
        }

        if (params.testForCoolingMultiplier) {
            String fileSuffix = getFileSuffix(TestOption.COOLING);
            StringBuilder sb = new StringBuilder();
            sb.append("paths,stepsArray,solution");
            sb.append(LINE_SEPARATOR);
            for (int i = 0; i < params.coolingMultipliers.length; i++) {
                int generatedPaths = networkManager.generatePaths(params.maxPaths);
                networkManager.writePathsToOut();
                solution = networkManager.optimize(params.coolingMultipliers[i]);
                sb.append(generatedPaths);
                sb.append(",");
                sb.append(params.stepsArray[i]);
                sb.append(",");
                sb.append(solution.getSimpleSolution());
                sb.append(LINE_SEPARATOR);
            }
            AbstractModelOutputWriter.writeToFile(getPath() + fileSuffix + "\\" + SOLUTION_FILE_PREFIX + ".txt", sb.toString());
        }

        if (params.testForPathsAndCooling) {
            String fileSuffix = getFileSuffix(TestOption.COOLING_PATH);
            StringBuilder sb = new StringBuilder(",");
            for (int i = 0; i < params.coolingMultipliers.length - 1; i++) {
                sb.append(params.stepsArray[i]);
                sb.append(",");
            }
            sb.append(params.stepsArray[params.coolingMultipliers.length - 1]);
            sb.append(LINE_SEPARATOR);

            for (int j = params.pathsStart; j<= params.maxPaths; j+= params.pathsStep) {
                int generatedPaths = networkManager.generatePaths(j);
                sb.append(generatedPaths);
                for (double i : params.coolingMultipliers) {
                    solution = networkManager.optimize(i);
                    sb.append(",");
                    sb.append(solution.getSimpleSolution());
                }
                sb.append(LINE_SEPARATOR);
            }
            AbstractModelOutputWriter.writeToFile(getPath() + fileSuffix + "\\" + SOLUTION_FILE_PREFIX + ".txt", sb.toString());
        }

        if (! params.isTesting()) {
            networkManager.generatePaths(params.maxPaths);
            networkManager.writePathsToOut();

            solution = networkManager.optimize(params.coolingMultiplier);
            System.out.println(solution.getFullSolution());

            String fileSuffix = getFileSuffix();

            try {
                AbstractModelOutputWriter.writeToFile(getPath() + fileSuffix + "\\" + SOLUTION_FILE_PREFIX + ".txt", solution.getFullSolution());
            } catch (IOException e) {
                e.printStackTrace();
            }

            NetworkData data = networkManager.toNetworkData();

            try {
                ModelWriter.write(data, getPath()+ fileSuffix + "\\" + DATA_FILE_PREFIX);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.parseArgs(args);
        main.init();
        try {
            main.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     private class InitialParams {
         @Option(name = "-v", usage="number of generated vertices")
         int numOfVertices = DEFAULT_VERTEXES;
         @Option(name = "-e", usage="number of generated edges")
         int numOfEdges = DEFAULT_EDGES;
         @Option(name = "-d", usage="number of generated demands")
         int numOfDemands = DEFAULT_DEMANDS;
         @Option(name = "-p", usage="number of maximum paths")
         int maxPaths = DEFAULT_MAX_PATHS;
         @Option(name = "-s", usage="annealing stepsArray")
         int steps = DEFAULT_STEPS;
         double coolingMultiplier = DEFAULT_STEPS;
         @Option(name = "-tp", usage="test solution depending on number of paths")
         boolean testForPathsDependence = false;
         @Option(name ="-tc", usage="test solution depending on cooling multiplier")
         boolean testForCoolingMultiplier = false;
         @Option(name = "-tcp", usage="test solution depending on cooling multiplier and number of paths")
         boolean testForPathsAndCooling = false;
         int[] stepsArray = {10, 100, 1000, 10000, 50000, 100000};
         @Option(name = "-tps", usage="test solution depending on number of paths - start number")
         int pathsStart = 3;
         @Option(name = "-tpst", usage="test solution depending on number of paths - step")
         int pathsStep = 1;
         double[] coolingMultipliers = new double[stepsArray.length];

         public InitialParams() {
             for (int i = 0; i < stepsArray.length; i++) {
                 int steps = this.stepsArray[i];
                 coolingMultipliers[i] = getCoolingMultiplier(steps);
             }
         }

         double getCoolingMultiplier(int numOfSteps) {
             double b = 1.0/numOfSteps;
             return Math.pow(base, b);
         }

         private double base = END_TEMPERATURE/INITIAL_TEMPERATURE;

         boolean isTesting() {
             return testForCoolingMultiplier || testForPathsAndCooling || testForPathsDependence;
         }
     }

    private enum TestOption {
        COOLING,
        PATH,
        COOLING_PATH
    }
}
