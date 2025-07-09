package com.gjbmloslos.markovchainsimulator;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Simulation {

    private final int cores = Runtime.getRuntime().availableProcessors();

    private int simulationAttempts;
    private int stepLimit;
    private ArrayList<State> stateList;
    private TextArea simulationResultTextArea;

    private State startState;
    private List<StringBuilder> simulationResult;

    private ScheduledExecutorService simulationExecutorService;

    public Simulation(int simulationAttempts, int stepLimit, ArrayList<State> stateList, TextArea simulationResultTextArea) {
        this.simulationAttempts = simulationAttempts;
        this.stepLimit = stepLimit;
        this.stateList = stateList;
        this.simulationResultTextArea = simulationResultTextArea;

        int usedCores = Math.min(cores, simulationAttempts);

        startState = this.stateList.getFirst();
        simulationResult = Collections.synchronizedList(new ArrayList<>());
        simulationExecutorService = Executors.newScheduledThreadPool(usedCores, Executors.defaultThreadFactory());
    }

    public void run () {
        for (int i = 0; i < simulationAttempts; i++) {
            int simIndex = i;
            int len = (simulationAttempts+"").length();
            Runnable sim = () -> {
                int len2 = ((simIndex+1)+"").length();
                StringBuilder sb = new StringBuilder("Sim#"+"0".repeat(len-len2)+(simIndex+1)+": ");
                State st = startState;
                int step = 1;
                while (st.hasNext() && step < stepLimit) {
                    if (step != 1) sb.append(" -> ");
                    sb.append(st.getName());
                    st = st.next();
                    step++;
                }
                if (st.hasNext()) {
                    sb.append(" (Exceeded Limit)");
                } else {
                    sb.append(" -> ").append(st.getName());
                }
                simulationResult.add(sb);
            };
            simulationExecutorService.execute(sim);
        }
        simulationExecutorService.shutdown();

        try {
            boolean finished = simulationExecutorService.awaitTermination(60, TimeUnit.SECONDS);
            if (finished) {
                List<String> resultList = simulationResult
                        .stream()
                        .map(StringBuilder::toString)
                        .sorted()
                        .toList();
                Platform.runLater(() -> {
                    for (String s : resultList) {
                        System.out.println(s);
                        simulationResultTextArea.appendText(s+"\n");
                    }
                });
            } else {
                System.err.println("Simulation timeout exceeded.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
