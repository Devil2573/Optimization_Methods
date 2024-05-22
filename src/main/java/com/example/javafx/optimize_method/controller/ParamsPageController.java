package com.example.javafx.optimize_method.controller;

import com.example.javafx.optimize_method.model.Fractional;
import com.example.javafx.optimize_method.model.SharedData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ParamsPageController {

    private MainPageController mainPageController;
    @FXML
    private TextField fieldVariables;
    @FXML
    private TextField fieldLimitations;
    @FXML
    private VBox vbox;
    @FXML
    private VBox targetFunction;
    @FXML
    private HBox checkboxContainer;

    private SharedData sharedData = new SharedData();


    public void setMainPageController(MainPageController mainPageControllerNew) {
        this.mainPageController = mainPageControllerNew;
    }

    @FXML
    private void initialize() {
        // Устанавливаем маску ввода, разрешающую только цифры
        fieldVariables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));
        fieldLimitations.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        fieldVariables.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                updatePanes();
                updateCheckBoxes();
            }
        });

        // Обработчик событий для поля fieldLimitations
        fieldLimitations.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                updatePanes();
            }
        });
        setDefaultData();

    }

    public void setDefaultData() {
        fieldVariables.setText("2");
        fieldLimitations.setText("1");
        updatePanes();
        updateCheckBoxes();
    }

    @FXML
    private void VariableAdd() {
        int count = Integer.parseInt(fieldVariables.getText());
        int newCount = count + 1;
        if (newCount >= 2 && newCount <= 16) {
            fieldVariables.setText(String.valueOf(newCount));
            updatePanes();
            updateCheckBoxes();
        }
    }

    @FXML
    private void VariableRemove() {
        int count = Integer.parseInt(fieldVariables.getText());
        int newCount = count - 1;
        if (newCount >= 2 && newCount <= 16) {
            fieldVariables.setText(String.valueOf(newCount));
            updatePanes();
            updateCheckBoxes();
        }
    }

    @FXML
    private void LimitationsAdd() {


        int count = Integer.parseInt(fieldLimitations.getText());
        int countOfVariable = Integer.parseInt(fieldVariables.getText());
        int newCount = count + 1;
        if ((newCount >= 1 && newCount <= 15) && (countOfVariable > newCount)) {
            fieldLimitations.setText(String.valueOf(newCount));
            updatePanes();
        }
    }

    @FXML
    private void LimitationsRemove() {
        int count = Integer.parseInt(fieldLimitations.getText());
        int countOfVariable = Integer.parseInt(fieldVariables.getText());
        int newCount = count - 1;
        if ((newCount >= 1 && newCount <= 15) && (countOfVariable > newCount)) {
            fieldLimitations.setText(String.valueOf(newCount));
            updatePanes();
        }
    }

    @FXML
    private void GetData() {
        try {
            int countOfLimitations = Integer.parseInt(fieldLimitations.getText());
            int countOfVariable = Integer.parseInt(fieldVariables.getText());
            Fractional[][] matrixOfCoef = new Fractional[countOfLimitations][countOfVariable + 1];
            Fractional[] coefOfTargetFunction = new Fractional[countOfVariable];
            List<HBox> hboxes = new ArrayList<>();

            for (Node node : targetFunction.getChildren()) {
                if (node instanceof HBox) {
                    hboxes.add((HBox) node);
                }
            }
            int row = 0;
            for (HBox hBox : hboxes) {
                ArrayList<Integer> tempCoefTarget = getCoefficientsFromHBox(hBox);
                if (tempCoefTarget.size() != countOfVariable) {
                    throw new RuntimeException("Количество коэффициентов в целевой функции не совпадает с количеством переменных.");
                }
                row = 0;
                for (Integer coef : tempCoefTarget) {
                    coefOfTargetFunction[row] = Fractional.createFractional(String.valueOf(coef));
                    row += 1;
                }
            }
            hboxes = new ArrayList<>();
            for (Node node : vbox.getChildren()) {
                if (node instanceof HBox) {
                    hboxes.add((HBox) node);
                }
            }
            row = 0;
            for (HBox hBox : hboxes) {
                ArrayList<Integer> tempListCoef = getCoefficientsFromHBox(hBox);
                System.out.println(tempListCoef);
                System.out.println(countOfVariable);
                if (tempListCoef.size() - 1 != countOfVariable) {
                    int rowError = row + 1;
                    throw new RuntimeException("Количество коэффициентов в " + rowError + " уравнении не совпадает с количеством переменных.");
                }
                int col = 0;
                for (Integer coef : tempListCoef) {
                    matrixOfCoef[row][col] = Fractional.createFractional(String.valueOf(coef));
                    col += 1;
                }
                row += 1;
            }
            if (countCheckedCheckboxes() != countOfLimitations) {
                throw new RuntimeException("Количество выбранных галочек не совпадает с количеством ограничений.");
            }
            ArrayList<Integer> basis = listCheckedCheckboxes();
            ArrayList<Integer> notBasis = listNotCheckedCheckboxes();
            System.out.println(basis);
            System.out.println(notBasis);
            sharedData.setMatrixOfCoef(matrixOfCoef);
            sharedData.setBasis(basis);
            sharedData.setNotBasis(notBasis);
            sharedData.setCoefOfTargetFunction(coefOfTargetFunction);
            SimplexMethodPageController simplexMethodPageController = mainPageController.getSimplexPageController();
            simplexMethodPageController.setSharedData(sharedData);


        } catch (Exception e) {
            showError(e.getMessage());
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePanes() {

        int numVariables = Integer.parseInt(fieldVariables.getText());
        int numLimitations = Integer.parseInt(fieldLimitations.getText());
        if ((numVariables >= 2 && numVariables <= 16) && (numLimitations >= 1 && numLimitations <= 15)) {
            vbox.getChildren().clear();
            targetFunction.getChildren().clear();
            for (int i = 0; i < numLimitations; i++) {
                HBox newHBox = createNewHBox(numVariables);
                vbox.getChildren().add(newHBox);
            }
            HBox newTargetFunction = createTargetFunction(numVariables);
            targetFunction.getChildren().add(newTargetFunction);
        }
    }

    private void updateCheckBoxes() {
        checkboxContainer.getChildren().clear();
        int numVariables = Integer.parseInt(fieldVariables.getText());

        for (int i = 1; i <= numVariables; i++) {
            Text variableText = new Text("x" + i);
            CheckBox checkBox = new CheckBox();
            HBox variableBox = new HBox(variableText, checkBox);
            checkboxContainer.getChildren().add(variableBox);
        }
    }

    private HBox createNewHBox(int numVariables) {
        HBox hbox = new HBox();
        double fieldWidth = 1080.0 / (numVariables * 2 + 2) + 5;

        for (int i = 1; i <= numVariables; i++) {
            TextField variableField = new TextField();
            variableField.setTextFormatter(new TextFormatter<>(change ->
                    (change.getControlNewText().matches("-?\\d*")) ? change : null));
            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            TextField variableLabel = new TextField("x" + i);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);
            hbox.getChildren().addAll(variableField, variableLabel);
        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("=", "<=", ">="));
        comboBox.setPrefWidth(fieldWidth + 30);
        comboBox.getSelectionModel().selectFirst();
        TextField resultField = new TextField();
        resultField.setPrefWidth(fieldWidth);

        hbox.getChildren().addAll(comboBox, resultField);

        return hbox;
    }

    private HBox createTargetFunction(int numVariables) {
        HBox hbox = new HBox();
        double fieldWidth = 1080.0 / (numVariables * 2 + 2) + 5;

        for (int i = 1; i <= numVariables; i++) {
            TextField variableField = new TextField();
            variableField.setTextFormatter(new TextFormatter<>(change ->
                    (change.getControlNewText().matches("-?\\d*")) ? change : null));
            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            TextField variableLabel = new TextField("x" + i);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);
            hbox.getChildren().addAll(variableField, variableLabel);
        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("min", "max"));
        comboBox.setPrefWidth(fieldWidth + 30);
        comboBox.getSelectionModel().selectFirst();


        hbox.getChildren().addAll(comboBox);

        return hbox;
    }

    private String getSign(HBox hbox) {
        String sign = null;
        for (Node node : hbox.getChildren()) {
            if (node instanceof ComboBox) {
                ComboBox<String> comboBox = (ComboBox<String>) node;
                sign = comboBox.getValue();
            }
        }
        return sign;
    }

    private ArrayList<Integer> getCoefficientsFromHBox(HBox hbox) {
        ArrayList<Integer> coefficients = new ArrayList<>();

        for (Node node : hbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                String text = textField.getText();
                try {
                    int coefficient = Integer.parseInt(text);
                    coefficients.add(coefficient);
                } catch (NumberFormatException e) {

                }
            }
        }

        return coefficients;
    }


    private Integer countCheckedCheckboxes() {
        int checkedCount = 0;

        for (Node node : checkboxContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (checkBox.isSelected()) {
                            checkedCount++;
                        }
                    }
                }
            }
        }
        return checkedCount;
    }

    private ArrayList<Integer> listCheckedCheckboxes() {
        ArrayList<Integer> basis = new ArrayList<>();
        for (Node node : checkboxContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (checkBox.isSelected()) {
                            int variableIndex = checkboxContainer.getChildren().indexOf(hbox);
                            basis.add(variableIndex + 1);

                        }
                    }
                }
            }
        }
        return basis;
    }

    private ArrayList<Integer> listNotCheckedCheckboxes() {
        ArrayList<Integer> basis = new ArrayList<>();
        for (Node node : checkboxContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (!checkBox.isSelected()) {
                            int variableIndex = checkboxContainer.getChildren().indexOf(hbox);
                            basis.add(variableIndex + 1);

                        }
                    }
                }
            }
        }
        return basis;
    }
}
