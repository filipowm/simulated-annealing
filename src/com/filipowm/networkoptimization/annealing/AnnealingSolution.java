package com.filipowm.networkoptimization.annealing;

public class AnnealingSolution {

    private String fullSolution;
    private String simpleSolution;
    private long numberOfAnnealings;

    public String getFullSolution() {
        return fullSolution;
    }

    public String getSimpleSolution() {
        return simpleSolution;
    }

    public void setFullSolution(String fullSolution) {
        this.fullSolution = fullSolution;
    }

    public void setSimpleSolution(String simpleSolution) {
        this.simpleSolution = simpleSolution;
    }

    public long getNumberOfAnnealings() {
        return numberOfAnnealings;
    }

    public void setNumberOfAnnealings(long numberOfAnnealings) {
        this.numberOfAnnealings = numberOfAnnealings;
    }
}
