package com.example.javafx.optimize_method.controller;

import com.example.javafx.optimize_method.model.Fractional;
import com.example.javafx.optimize_method.model.SharedData;
import com.example.javafx.optimize_method.utils.Gaus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplexMethodPageController {


    private SharedData sharedData;
    private Boolean auto;
    private ArrayList<SharedData> allData = new ArrayList<>();

    @FXML
    private GridPane simplexGridPane;

    private Button highlightedButton = null;

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
        allData.add(sharedData);
        System.out.println("setSharedData called");
        updateTableView();
    }

    @FXML
    private void initialize() {

        System.out.println("initialize called");
    }


    public void updateTableView() {

        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();

        matrixOfCoef = Gaus.doAllMethods(matrixOfCoef, basis.toArray(new Integer[0]));
        System.out.println(basis);
        System.out.println(notBasis);

        int numVariables = matrixOfCoef[0].length - 1;
        simplexGridPane.getChildren().clear();
        int index = 0;
        // Верхняя полоска
        for (Integer num : notBasis) {
            index += 1;
            Label colLabel = new Label("x" + num);

            simplexGridPane.add(colLabel, index, 0);
        }
        // левый столбец и основные кнопки
        for (int row = 0; row < matrixOfCoef.length; row++) {
            index = 0;
            Label rowLabel = new Label("x" + basis.get(row));
            simplexGridPane.add(rowLabel, 0, row + 1);
            for (Integer col : notBasis) {
                index += 1;
                Fractional value = matrixOfCoef[row][col - 1];
                Button button = new Button(value.toString());
                button.setDisable(true);

                simplexGridPane.add(button, index, row + 1);
            }
            index += 1;
            Fractional value = matrixOfCoef[row][matrixOfCoef[0].length - 1];
            Button button = new Button(value.toString());
            button.setDisable(true);

            simplexGridPane.add(button, index, row + 1);
        }

        Fractional[] newCoefOfFunction = new Fractional[matrixOfCoef[0].length];
        for (int i = 0; i < newCoefOfFunction.length; i++) {
            newCoefOfFunction[i] = Fractional.createFractional("0");
        }
        for (Integer basisIndex : notBasis) {
            System.out.println("gogo");
            System.out.println(targetCoefs[basisIndex - 1]);
            newCoefOfFunction[basisIndex - 1] = targetCoefs[basisIndex - 1];
        }
        System.out.println(Arrays.toString(newCoefOfFunction));
        int row = 0;
        for (Integer indexOfX : basis) {
            for (Integer col : notBasis) {
                Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][col - 1], targetCoefs[indexOfX - 1]), Fractional.createFractional("-1"));
                Fractional newCoef = Fractional.sum(newCoefOfFunction[col - 1], temp);
                System.out.println("temp");
                System.out.println(matrixOfCoef[row][col - 1]);
                System.out.println(targetCoefs[indexOfX - 1]);
                System.out.println(newCoef);
                newCoefOfFunction[col - 1] = newCoef;
            }
            Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][matrixOfCoef[0].length - 1], targetCoefs[indexOfX - 1]), Fractional.createFractional("-1"));
            Fractional newCoef = Fractional.sum(newCoefOfFunction[newCoefOfFunction.length - 1], temp);
            newCoefOfFunction[newCoefOfFunction.length - 1] = newCoef;
            row += 1;
        }

        System.out.println(Arrays.toString(targetCoefs));
        System.out.println(Arrays.toString(newCoefOfFunction));
        index = 0;
        for (Integer num : notBasis) {
            index += 1;
            Fractional value = newCoefOfFunction[num - 1];
            Button button = new Button(value.toString());
            button.setDisable(true);

            simplexGridPane.add(button, index, matrixOfCoef.length + 1);
        }

        Fractional value = newCoefOfFunction[newCoefOfFunction.length - 1];
        Button button = new Button(value.toString());
        button.setDisable(true);

        simplexGridPane.add(button, index + 1, matrixOfCoef.length + 1);
        System.out.println(Arrays.deepToString(matrixOfCoef));
        System.out.println(Arrays.toString(newCoefOfFunction));
        enableMinButton(matrixOfCoef, basis, notBasis);


    }

    private void handleButtonClick(Button button) {
        if (highlightedButton != null) {
            highlightedButton.setStyle("");
        }
        button.setStyle("-fx-background-color: green;");
        highlightedButton = button;
    }

    public void enableMinButton(Fractional[][] matrixOfCoef, ArrayList<Integer> basis, ArrayList<Integer> notBasis) {
        int col = -1;
        for (Integer j : notBasis) {
            col += 1;
            Button button = getButtonAt(matrixOfCoef.length + 1, col + 1);
            String strValue = button.getText();
            Fractional number = Fractional.createFractional(strValue);
            //Проверяем является значение p(самая нижня строчка) отрицательным
            System.out.println("stolb");
            System.out.println(number);
            if (!number.isPositive() && number.getNumerator() != 0) {
                System.out.println("start");
                List<Fractional> listOfMins = new ArrayList<>();
                Fractional min = null;
                //пробегаем по столбцу

                for (int i = 0; i < matrixOfCoef.length; i++) {
                    //значение b определенной строки
                    Fractional b = matrixOfCoef[i][matrixOfCoef[0].length - 1];
                    //значение alpha определенной строки
                    Fractional alpha = matrixOfCoef[i][j - 1];
                    //Если alpha положительный, то работаем с этими значениями
                    System.out.println("alpha");
                    System.out.println(alpha);
                    if (alpha.isPositive() && alpha.getNumerator() != 0) {
                        Fractional r = Fractional.division(b, alpha);
                        if (min == null) {
                            min = r;
                            listOfMins.add(r);
                        } else if (Fractional.isBigger(min, r)) {
                            min = r;
                            listOfMins.add(r);
                        }
                    } else {
                        listOfMins.add(null);
                    }
                }
                System.out.println(listOfMins);
                //Смотрим на значения которые получились при делении b/alpha, и если оно совпадает с min то активируем кнопку
                for (int i = 0; i < listOfMins.size(); i++) {
                    if (listOfMins.get(i) != null && listOfMins.get(i) == min) {
                        Button buttonTemp = getButtonAt(i + 1, col + 1);
                        buttonTemp.setDisable(false);
                        buttonTemp.setOnAction(e -> handleButtonClick(buttonTemp));
                    }
                }
                System.out.println("circle");
            }

        }
    }

    private Button getButtonAt(int row, int col) {
        for (Node node : simplexGridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;
    }
}



