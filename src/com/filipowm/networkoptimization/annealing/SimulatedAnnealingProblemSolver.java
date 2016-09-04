package com.filipowm.networkoptimization.annealing;

public class SimulatedAnnealingProblemSolver {

    /**
     * AnnealingScheduler is required for limiting the number of tries for global search
     * and also for search in a temperature state
     */
    protected AnnealingScheduler annealingSchedule;

    /**
     * Problem is the state generator also the cost function is defined in problem
     */
    protected SimulatedAnnealingProblem simulatedAnnealingProblem;

    public SimulatedAnnealingProblemSolver(AnnealingScheduler annealingSchedule, SimulatedAnnealingProblem simulatedAnnealingProblem) {
        this.annealingSchedule = annealingSchedule;
        this.simulatedAnnealingProblem = simulatedAnnealingProblem;
    }

    public SimulatedAnnealingProblemSolver() {
    }

    public AnnealingScheduler getAnnealingSchedule() {
        return annealingSchedule;
    }

    public void setAnnealingSchedule(AnnealingScheduler annealingSchedule) {
        this.annealingSchedule = annealingSchedule;
    }

    public SimulatedAnnealingProblem getSimulatedAnnealingProblem() {
        return simulatedAnnealingProblem;
    }

    public void setSimulatedAnnealingProblem(SimulatedAnnealingProblem simulatedAnnealingProblem) {
        this.simulatedAnnealingProblem = simulatedAnnealingProblem;
    }

    private strictfp double getProbability(double deltaEnergy) {
        return Math.exp(-deltaEnergy / annealingSchedule.getCurrentTemperature());
    }

    private strictfp double getRandomDouble() {
        return Math.random();
    }

    public strictfp void solve() {
        // init scheduler and problem
        annealingSchedule.init();
        simulatedAnnealingProblem.init();
        long times = 0;
        // loop until reaching the freezing point
        while ((!simulatedAnnealingProblem.isTotalNumberOfStatesReached()) && !annealingSchedule.isFreezingPointReached()) {

            annealingSchedule.incrementIterationCount();
            double deltaEnergy = 0.0;

            // loop until reaching a limit for a certain temperature
            while (!annealingSchedule.isMarkovChainLimitReached()) {
                // create a next state
                simulatedAnnealingProblem.createNextState();
                annealingSchedule.incrementSameTemperatureIterationCount();

                // find the difference between the next state and the current state
                deltaEnergy = simulatedAnnealingProblem.getCostForNextState() - simulatedAnnealingProblem.getCostForCurrentState();

                // if the delta is less then 0 then go to next state
                // if not use Boltzman function in order to decide (Metropolis test)
                // main purpose of accepting an increment state is not to get stuck on a local minimum
                if ((deltaEnergy <= 0) || (deltaEnergy > 0 && getProbability(deltaEnergy) > getRandomDouble())) {
                    simulatedAnnealingProblem.goToNextState();
                    annealingSchedule.incrementAcceptanceCount();
                }
            }
            ++times;

            annealingSchedule.coolDown(deltaEnergy);
        }
        simulatedAnnealingProblem.setNumberOfAnnealings(times);
        System.out.println("Number of steps: " + times);
    }
}
