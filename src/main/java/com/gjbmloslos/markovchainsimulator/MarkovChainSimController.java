package com.gjbmloslos.markovchainsimulator;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarkovChainSimController {

    @FXML Button startSimulationButton;
    @FXML Button resetSimulationButton;
    @FXML Button addStateButton;
    @FXML Button clearStateButton;

    @FXML TextField addStateTextField;
    @FXML TextField simulationNumberTextField;
    @FXML TextField stepLimitTextField;

    @FXML TextArea simulationResultTextArea;

    @FXML GridPane stateGridPane;

    ArrayList<String> stateNameList;
    ArrayList<State> stateList;

    private final Map<Pair<Integer, Integer>, TextField> cellMap = new HashMap<>();
    final double cellWidth = 60;
    final double cellHeight = 60;

    public void initialize () {
        enableFields(true);

        stateNameList = new ArrayList<>();
        stateList = new ArrayList<>();

        stateGridPane.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPrefHeight(cellHeight);
        rowConstraints.setMinHeight(cellHeight);
        rowConstraints.setMaxHeight(cellHeight);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPrefWidth(cellWidth);
        columnConstraints.setMinWidth(cellWidth);
        columnConstraints.setMaxWidth(cellWidth);

        stateGridPane.getRowConstraints().clear();
        stateGridPane.getColumnConstraints().clear();
        stateGridPane.getRowConstraints().add(rowConstraints);
        stateGridPane.getColumnConstraints().add(columnConstraints);
    }

    @FXML
    public void addState () {
        String stateName = addStateTextField.getText();
        if (stateName.isBlank() || stateNameList.contains(stateName)) {
            return;
        } else {
            stateNameList.add(stateName);
        }

        State state = new State(stateName);
        stateList.add(state);

        int index = stateList.size();

        Label rowHeader = new Label(stateName);
        rowHeader.setPrefWidth(cellWidth);
        rowHeader.setPrefHeight(cellHeight);
        rowHeader.setAlignment(Pos.CENTER);
        GridPane.setMargin(rowHeader, new Insets(4));
        GridPane.setHalignment(rowHeader, HPos.CENTER);
        GridPane.setValignment(rowHeader, VPos.CENTER);
        rowHeader.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        Label colHeader = new Label(stateName);
        colHeader.setPrefWidth(cellWidth);
        colHeader.setPrefHeight(cellHeight);
        colHeader.setAlignment(Pos.CENTER);
        GridPane.setMargin(colHeader, new Insets(4));
        GridPane.setHalignment(colHeader, HPos.CENTER);
        GridPane.setValignment(colHeader, VPos.CENTER);
        colHeader.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        stateGridPane.add(colHeader, 0, index);
        stateGridPane.add(rowHeader, index, 0);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPrefHeight(cellHeight);
        rowConstraints.setMinHeight(cellHeight);
        rowConstraints.setMaxHeight(cellHeight);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPrefWidth(cellWidth);
        columnConstraints.setMinWidth(cellWidth);
        columnConstraints.setMaxWidth(cellWidth);

        stateGridPane.getRowConstraints().add(rowConstraints);
        stateGridPane.getColumnConstraints().add(columnConstraints);

        for (int row = 1; row <= index; row++) {
            for (int col = 1; col <= index; col++) {
                Pair<Integer, Integer> coord = new Pair<>(row, col);
                if (!cellMap.containsKey(coord)) {
                    TextField dataCell = new TextField();
                    dataCell.setAlignment(Pos.CENTER);
                    dataCell.setMaxWidth(Double.MAX_VALUE);
                    dataCell.setMaxHeight(Double.MAX_VALUE);
                    dataCell.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                    GridPane.setMargin(dataCell, new Insets(4));
                    GridPane.setHalignment(dataCell, HPos.CENTER);
                    GridPane.setValignment(dataCell, VPos.CENTER);
                    stateGridPane.add(dataCell, col, row);
                    cellMap.put(coord, dataCell);
                }
            }
        }

        System.out.println("State " + state.getName() + " added");

    }

    @FXML
    public void clearStates () {
        stateNameList.clear();
        stateList.clear();
        cellMap.clear();

        stateGridPane.getChildren().clear();
        Region emptyCell = new Region();
        emptyCell.setPrefSize(cellWidth, cellHeight);
        stateGridPane.add(emptyCell, 0 ,0);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPrefHeight(cellHeight);
        rowConstraints.setMinHeight(cellHeight);
        rowConstraints.setMaxHeight(cellHeight);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPrefWidth(cellWidth);
        columnConstraints.setMinWidth(cellWidth);
        columnConstraints.setMaxWidth(cellWidth);

        stateGridPane.getRowConstraints().clear();
        stateGridPane.getColumnConstraints().clear();
        stateGridPane.getRowConstraints().add(rowConstraints);
        stateGridPane.getColumnConstraints().add(columnConstraints);

        System.out.println("Cleared all states");
    }

    @FXML
    public void startSimulation () {

        enableFields(false);

        int simulationAttempts;
        int stepLimit;
        try {
            simulationAttempts = Integer.parseInt(simulationNumberTextField.getText());
            stepLimit = Integer.parseInt(stepLimitTextField.getText());
        } catch (Exception e) {
            throw e;
        }

        int row = 1;
        //System.out.println("States:");
        //simulationResultTextArea.appendText("States:\n");
        for (State s : stateList) {
            s.clearTransition();
            for (int col = 1; col < stateGridPane.getColumnCount(); col++) {
                float trans = 0;
                try {
                    TextField tf = cellMap.get(new Pair<>(row, col));
                    if (tf.getText().isEmpty()) {
                        trans = 0f;
                    } else {
                        trans = Float.parseFloat(tf.getText());
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Transition must be float between 0.0 and 1.0");
                }
                s.addTransition(stateList.get(col-1), trans);
            }
            s.verifyTransitionProbabilities();
            System.out.println(s);
            simulationResultTextArea.appendText(s+"\n");
            row++;
        }

        System.out.println("Started Simulation");
        System.out.println("Simulation Count: " + simulationAttempts);
        System.out.println("Max Step Limit: " + stepLimit);

        simulationResultTextArea.appendText("Started Simulation\n");
        simulationResultTextArea.appendText("Simulation Count: " + simulationAttempts+"\n");
        simulationResultTextArea.appendText("Max Step Limit: " + stepLimit+"\n");

        Simulation simulation = new Simulation(simulationAttempts, stepLimit, stateList, simulationResultTextArea);
        simulation.run();

    }

    @FXML
    public void resetSimulation (){
        enableFields(true);
        simulationResultTextArea.clear();
    }

    private void enableFields (Boolean b) {
        startSimulationButton.setDisable(!b);
        resetSimulationButton.setDisable(b);
        addStateTextField.setDisable(!b);
        addStateButton.setDisable(!b);
        clearStateButton.setDisable(!b);
        simulationNumberTextField.setDisable(!b);
        stepLimitTextField.setDisable(!b);

        stateGridPane.setVisible(b);
        stateGridPane.setManaged(b);
        simulationResultTextArea.setVisible(!b);
        simulationResultTextArea.setManaged(!b);
    }

}
