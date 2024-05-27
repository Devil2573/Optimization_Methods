package com.example.javafx.optimize_method.controller;

import com.example.javafx.optimize_method.model.Fractional;
import com.example.javafx.optimize_method.model.SharedData;
import com.example.javafx.optimize_method.utils.Gaus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    @FXML
    private CheckBox checkBox;
    private Boolean decimalFractionals;
    private SharedData sharedData = new SharedData();
    private ComboBox<String> comboBoxAspiration;

    public void setMainPageController(MainPageController mainPageControllerNew) {
        this.mainPageController = mainPageControllerNew;
    }

    @FXML
    private void handleCheckBox() {
        decimalFractionals = checkBox.isSelected();
        Fractional.setDecimalFractionals(decimalFractionals);
    }

    @FXML
    private void initialize() {
        Fractional.setDecimalFractionals(false);
        fieldVariables.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d+")) ? change : null));
        fieldLimitations.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d+")) ? change : null));

        fieldVariables.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                updatePanes();
                updateCheckBoxes();
            }
        });

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


    public void GetData() {
        try {
            int countOfLimitations = Integer.parseInt(fieldLimitations.getText());
            int countOfVariable = Integer.parseInt(fieldVariables.getText());
            String selectedItem = (String) comboBoxAspiration.getSelectionModel().getSelectedItem();
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
                ArrayList<Fractional> tempCoefTarget = getCoefficientsFromHBox(hBox);
                if (tempCoefTarget.size() != countOfVariable) {
                    throw new RuntimeException("Количество коэффициентов в целевой функции не совпадает с количеством переменных.");
                }
                row = 0;

                for (Fractional coef : tempCoefTarget) {
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
                ArrayList<Fractional> tempListCoef = getCoefficientsFromHBox(hBox);


                if (tempListCoef.size() - 1 != countOfVariable) {
                    int rowError = row + 1;
                    throw new RuntimeException("Количество коэффициентов в " + rowError + " уравнении не совпадает с количеством переменных.");
                }
                int count = 0;
                int index = 0;
                for (Fractional coef : tempListCoef) {
                    if (index != tempListCoef.size() - 1) {
                        if (coef.getNumerator() == 0) {
                            count++;
                        }
                    }
                    index++;
                }
                if (count == tempListCoef.size() - 1) {
                    int rowError = row + 1;
                    throw new RuntimeException("Уравнении " + rowError + " состоит только из 0.");
                }
                int col = 0;
                for (Fractional coef : tempListCoef) {
                    matrixOfCoef[row][col] = Fractional.createFractional(String.valueOf(coef));
                    col += 1;
                }
                row += 1;
            }
            if (countCheckedCheckboxes() != countOfLimitations) {
                throw new RuntimeException("Количество выбранных галочек не совпадает с количеством ограничений.");
            }

            if (Objects.equals(selectedItem, "max")) {
                int count = 0;
                for (Fractional coef : coefOfTargetFunction) {
                    coefOfTargetFunction[0] = Fractional.multiplication(coef, Fractional.createFractional("-1"));
                    count++;
                }

            }
            ArrayList<Integer> basis = listCheckedCheckboxes();
            ArrayList<Integer> notBasis = listNotCheckedCheckboxes();

            sharedData.setMatrixOfCoef(matrixOfCoef);
            sharedData.setBasis(basis);
            sharedData.setNotBasis(notBasis);
            sharedData.setCoefOfTargetFunction(coefOfTargetFunction);
            sharedData.setCountOfVariables(countOfVariable);
            sharedData.setCountOfLimitations(countOfLimitations);


        } catch (Exception e) {
            showError(e.getMessage());
            throw new RuntimeException("Ошибка");
        }
    }

    @FXML
    public void startDecision() {
        try {
            GetData();
            SimplexMethodPageController simplexMethodPageController = mainPageController.getSimplexPageController();
            simplexMethodPageController.firstSetData(sharedData);
            ArtificialSimplexPageController artificialSimplexPageController = mainPageController.getArtificalSimplexPageController();
            artificialSimplexPageController.firstSetData(sharedData);
            mainPageController.enableTabs();
        } catch (Exception e) {
            showError(e.getMessage());
        }


    }

    public void writeInFile() {
        try {
            GetData();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить файл");

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showSaveDialog(vbox.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    Gaus.writeFile(selectedFile.getAbsolutePath(), sharedData);
                } catch (IOException e) {
                    showError("Ошибка записи файла: " + e.getMessage());
                }
            }
        } catch (Exception e) {

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

            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            TextField variableLabel = new TextField("x" + i);
            variableLabel.setDisable(true);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);

            variableField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            variableLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            hbox.getChildren().addAll(variableField, variableLabel);
        }

        TextField equalLabel = new TextField("=");
        equalLabel.setPrefWidth(fieldWidth);
        equalLabel.setEditable(false);
        equalLabel.setDisable(true);
        TextField resultField = new TextField();
        resultField.setPrefWidth(fieldWidth);
        resultField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
        equalLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");

        hbox.getChildren().addAll(equalLabel, resultField);

        return hbox;
    }

    private HBox createTargetFunction(int numVariables) {
        HBox hbox = new HBox();
        double fieldWidth = 1080.0 / (numVariables * 2 + 2) + 5;

        for (int i = 1; i <= numVariables; i++) {
            TextField variableField = new TextField();

            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            TextField variableLabel = new TextField("x" + i);
            variableLabel.setDisable(true);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);

            variableField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            variableLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            hbox.getChildren().addAll(variableField, variableLabel);

        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("min", "max"));
        comboBox.setPrefWidth(fieldWidth + 30);
        comboBox.getSelectionModel().selectFirst();
        this.comboBoxAspiration = comboBox;

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

    private ArrayList<Fractional> getCoefficientsFromHBox(HBox hbox) {
        ArrayList<Fractional> coefficients = new ArrayList<>();

        for (Node node : hbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                String text = textField.getText();
                Fractional coefficient = Fractional.createFractional(text);
                if (coefficient != null) {
                    coefficients.add(coefficient);
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


    @FXML
    private void openFilePicker() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        File selectedFile = fileChooser.showOpenDialog(vbox.getScene().getWindow());
        if (selectedFile != null) {
            try {
                HashMap<String, Object> fileData = Gaus.readFile(selectedFile.getAbsolutePath());
                if (fileData != null) {
                    updateUI(fileData);
                } else {
                    showError("Ошибка чтения файла: некорректный формат файла");
                }
            } catch (Exception e) {
                showError("Ошибка чтения файла");
            }
        }
    }

    private void updateUI(HashMap<String, Object> fileData) {

        int variables = (int) fileData.get("matrixColumns") - 1;
        int limitations = (int) fileData.get("matrixRows");
        String[] baseXtemp = (String[]) fileData.get("baseXtemp");
        String[] targetCoefsTemp = (String[]) fileData.get("targetCoefs");
        Fractional[][] matrixOfCoef = (Fractional[][]) fileData.get("matrixOfCoef");
        Integer[] basis = new Integer[baseXtemp.length];
        Integer[] targetCoefs = new Integer[targetCoefsTemp.length];
        for (int i = 0; i < baseXtemp.length; i++) {
            basis[i] = Integer.parseInt(baseXtemp[i]);
        }
        for (int i = 0; i < targetCoefsTemp.length; i++) {
            targetCoefs[i] = Integer.parseInt(targetCoefsTemp[i]);
        }
        fieldVariables.setText(String.valueOf(variables));
        fieldLimitations.setText(String.valueOf(limitations));
        updatePanesWithData(matrixOfCoef, targetCoefs);
        updateCheckBoxesWithData(basis);
    }

    private void updatePanesWithData(Fractional[][] matrixOfCoef, Integer[] targetCoefs) {

        int numVariables = Integer.parseInt(fieldVariables.getText());
        int numLimitations = Integer.parseInt(fieldLimitations.getText());
        if ((numVariables >= 2 && numVariables <= 16) && (numLimitations >= 1 && numLimitations <= 15)) {
            vbox.getChildren().clear();
            targetFunction.getChildren().clear();
            for (int i = 0; i < numLimitations; i++) {
                HBox newHBox = createNewHBoxWithData(matrixOfCoef[i]);
                vbox.getChildren().add(newHBox);
            }
            HBox newTargetFunction = createTargetFunctionWithData(targetCoefs);
            targetFunction.getChildren().add(newTargetFunction);
        }
    }

    private HBox createNewHBoxWithData(Fractional[] coefs) {
        HBox hbox = new HBox();
        int numVariables = Integer.parseInt(fieldVariables.getText());
        double fieldWidth = 1080.0 / (numVariables * 2 + 2) + 5;

        for (int i = 1; i <= numVariables; i++) {
            TextField variableField = new TextField();

            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            variableField.setText(String.valueOf(coefs[i - 1]));
            TextField variableLabel = new TextField("x" + i);
            variableLabel.setDisable(true);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);
            variableField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            variableLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            hbox.getChildren().addAll(variableField, variableLabel);
        }

        TextField equalLabel = new TextField("=");
        equalLabel.setPrefWidth(fieldWidth);
        equalLabel.setEditable(false);
        equalLabel.setDisable(true);
        TextField resultField = new TextField();
        resultField.setPrefWidth(fieldWidth);
        resultField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
        resultField.setText(String.valueOf(coefs[coefs.length - 1]));
        equalLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");

        hbox.getChildren().addAll(equalLabel, resultField);

        return hbox;
    }

    private void updateCheckBoxesWithData(Integer[] basis) {
        checkboxContainer.getChildren().clear();
        int numVariables = Integer.parseInt(fieldVariables.getText());
        ArrayList<Integer> tempBasis = new ArrayList<>(List.of(basis));
        for (int i = 1; i <= numVariables; i++) {
            Text variableText = new Text("x" + i);
            CheckBox checkBox = new CheckBox();
            if (tempBasis.contains(i)) {
                checkBox.setSelected(true);
            }
            HBox variableBox = new HBox(variableText, checkBox);
            checkboxContainer.getChildren().add(variableBox);
        }
    }

    private HBox createTargetFunctionWithData(Integer[] coefs) {
        HBox hbox = new HBox();
        int numVariables = Integer.parseInt(fieldVariables.getText());
        double fieldWidth = 1080.0 / (numVariables * 2 + 2) + 5;

        for (int i = 1; i <= numVariables; i++) {
            TextField variableField = new TextField();

            variableField.setPrefWidth(fieldWidth);
            variableField.setEditable(true);
            variableField.setText(String.valueOf(coefs[i - 1]));

            TextField variableLabel = new TextField("x" + i);
            variableLabel.setDisable(true);
            variableLabel.setEditable(false);
            variableLabel.setPrefWidth(fieldWidth);

            variableField.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            variableLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            hbox.getChildren().addAll(variableField, variableLabel);
        }

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList("min", "max"));
        comboBox.setPrefWidth(fieldWidth + 30);
        comboBox.getSelectionModel().selectFirst();

        this.comboBoxAspiration = comboBox;

        hbox.getChildren().addAll(comboBox);

        return hbox;
    }
}
