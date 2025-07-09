package com.gjbmloslos.markovchainsimulator;

import java.util.*;

public class State {

    private static final Random r = new Random();

    private final String name;
    private final LinkedHashMap<State, Float> transitions;

    private boolean endState;

    private State[] cumulativeStateList;
    private float[] cumulativeTransitionProbabilityList;

    public State(String name) {
        this.name = name;

        this.transitions = new LinkedHashMap<>();
    }

    public State(State toState) {
        this.name = toState.getName();
        this.transitions = toState.getTransitions();
        this.endState = toState.isEndState();
        this.cumulativeStateList = toState.getCumulativeStateList();
        this.cumulativeTransitionProbabilityList = toState.getCumulativeTransitionProbabilityList();
    }

    public void addTransition (State toState, float probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be between 0 and 1");
        }
        transitions.put(toState, probability);
    }

    public void clearTransition () {
        transitions.clear();
    }

    public void verifyTransitionProbabilities () {
        float sum = 0;
        for (float f : transitions.values()) {
            //System.out.println(f);
            sum += f;
        }

        if (sum != 1) {
            throw new IllegalStateException("Sum of transition probabilities must equal 1");
        }

        for (Map.Entry<State, Float> m : transitions.entrySet()) {
            String name = m.getKey().name;
            Float prob = m.getValue();
            if (getName().equals(name) && prob == 1f) endState = true;
        }

        cumulativeStateList = new State[transitions.size()];
        cumulativeTransitionProbabilityList = new float[transitions.size()];
        int i = 0;
        float cumulative = 0f;
        for (Map.Entry<State, Float> entry : transitions.entrySet()) {
            cumulativeStateList[i] = entry.getKey();
            cumulative += entry.getValue();
            cumulativeTransitionProbabilityList[i] = cumulative;
            i++;
        }
    }

    public boolean hasNext () {
        return !endState;
    }

    public State next () {
        State toState = null;
        float rng = r.nextFloat();
        for (int i = 0; i < cumulativeTransitionProbabilityList.length; i++) {
            if (rng <= cumulativeTransitionProbabilityList[i]) {
                toState = cumulativeStateList[i];
                break;
            }
        }
        return toState != null ? toState : cumulativeStateList[cumulativeStateList.length - 1];
    }

    @Override
    public String toString() {
        String s = "State{name='" + name + "'";
        s = s + ", transitions={";
        boolean first = true;
        for (Map.Entry<State, Float> m : transitions.entrySet()) {
            String name = m.getKey().name;
            Float prob = m.getValue();
            s = s + ((first)?"":", ") + name + " -> " + prob;
            first = false;
        }
        s = s + "}}";
        return s;
    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<State, Float> getTransitions() {
        return transitions;
    }

    public boolean isEndState() {
        return endState;
    }

    public void setEndState(boolean endState) {
        this.endState = endState;
    }

    public State[] getCumulativeStateList() {
        return cumulativeStateList;
    }

    public void setCumulativeStateList(State[] cumulativeStateList) {
        this.cumulativeStateList = cumulativeStateList;
    }

    public float[] getCumulativeTransitionProbabilityList() {
        return cumulativeTransitionProbabilityList;
    }

    public void setCumulativeTransitionProbabilityList(float[] cumulativeTransitionProbabilityList) {
        this.cumulativeTransitionProbabilityList = cumulativeTransitionProbabilityList;
    }
}
