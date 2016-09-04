package com.filipowm.networkoptimization.annealing;

interface SimulatedAnnealingProblem {
    /**
     * initializes the problem
     */
    void init();


    /**
     * should calculate the cost for the current state
     *
     * @return the result of the current state (also called current Energy)
     */
    double getCostForCurrentState();


    /**
     * creates a new state but doesn't change the current state,
     * next state is usually created randomly in SA applications
     */
    void createNextState();


    /**
     * should calculate the cost for the next state
     * @return the result of the next generated state which is created in createNextState
     */
    double getCostForNextState();


    /**
     * sets the current state as the next state
     * current state <--- next state
     */
    void goToNextState();


    /**
     * problem solve operation will be terminated if this function returns true
     * in essence this function is used if one wants to put a limitation besides freezing temperature
     *
     * @return true if there is a desired number of solutions to limit the solution
     */
    boolean isTotalNumberOfStatesReached();


    /**
     * should generate a result set string
     * @return result set as a string
     */
    String getSolutionString();

    /**
     * should generate a simple result set string
     * @return result set as a string
     */
    String getSimpleSolutionString();

    /**
     * should generate result set
     * @return result set
     */
    AnnealingSolution getSolution();

    void setNumberOfAnnealings(long numberOfAnnealings);
}
