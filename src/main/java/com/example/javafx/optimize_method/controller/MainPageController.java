package com.example.javafx.optimize_method.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;


public class MainPageController {

    @FXML
    private TabPane tabPane;


    @FXML
    private ParamsPageController ParamPageController;

    @FXML
    private SimplexMethodPageController SimplexPageController;

    @FXML
    private ArtificialSimplexPageController ArtificalSimplexPageController;

    public TabPane getTabPane() {
        return tabPane;
    }

    @FXML
    private void initialize() {


        ParamPageController.setMainPageController(this);

    }

    public ParamsPageController getParamPageController() {
        return ParamPageController;
    }

    public SimplexMethodPageController getSimplexPageController() {
        return SimplexPageController;
    }

    public ArtificialSimplexPageController getArtificalSimplexPageController() {
        return ArtificalSimplexPageController;
    }
}


