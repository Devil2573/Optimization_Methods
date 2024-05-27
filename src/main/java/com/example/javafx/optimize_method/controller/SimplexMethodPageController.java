package com.example.javafx.optimize_method.controller;

import com.example.javafx.optimize_method.model.Fractional;
import com.example.javafx.optimize_method.model.SharedData;
import com.example.javafx.optimize_method.utils.Gaus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SimplexMethodPageController {

    private Boolean min;
    private SharedData sharedData;
    private Boolean auto;
    private ArrayList<SharedData> allData = new ArrayList<>();

    @FXML
    private GridPane simplexGridPane;

    private Button highlightedButton = null;

    @FXML
    private Label messageLabel;

    @FXML
    private Button back;
    @FXML
    private Button forward;
    @FXML
    private CheckBox checkBox;

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
        allData.add(sharedData);
        System.out.println("setSharedData called");
        updateTableView();
    }

    public void firstSetData(SharedData sharedData) {
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        SharedData newData = new SharedData();
        min = sharedData.getMin();
        allData.clear();
        messageLabel.setText("");
        back.setDisable(true);
        forward.setDisable(false);
        matrixOfCoef = Gaus.doAllMethods(matrixOfCoef, basis.toArray(new Integer[0]));
        auto = false;
        checkBox.setSelected(false);
        highlightedButton = null;
        Fractional[] newCoefOfFunction = new Fractional[matrixOfCoef[0].length];
        for (int i = 0; i < newCoefOfFunction.length; i++) {
            newCoefOfFunction[i] = new Fractional(0, 1);
        }
        for (Integer basisIndex : notBasis) {
            newCoefOfFunction[basisIndex - 1] = targetCoefs[basisIndex - 1];
        }
        int row = 0;
        for (Integer indexOfX : basis) {
            for (Integer col : notBasis) {
                Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][col - 1], targetCoefs[indexOfX - 1]), new Fractional(-1, 1));
                Fractional newCoef = Fractional.sum(newCoefOfFunction[col - 1], temp);
                newCoefOfFunction[col - 1] = newCoef;
            }
            Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][matrixOfCoef[0].length - 1], targetCoefs[indexOfX - 1]), new Fractional(-1, 1));
            Fractional newCoef = Fractional.sum(newCoefOfFunction[newCoefOfFunction.length - 1], temp);
            newCoefOfFunction[newCoefOfFunction.length - 1] = newCoef;
            row += 1;
        }

        newData.setCoefOfTargetFunction(newCoefOfFunction);
        newData.setMatrixOfCoef(matrixOfCoef);
        newData.setBasis(basis);
        newData.setNotBasis(notBasis);

        setSharedData(newData);
    }

    @FXML
    private void initialize() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Переход на следующий шаг");
        contextMenu.getItems().add(menuItem1);
        forward.setContextMenu(contextMenu);
        forward.setOnMouseEntered((MouseEvent event) -> {
            contextMenu.show(forward, event.getScreenX() + 5, event.getScreenY() + 5);
        });
        forward.setOnMouseExited((MouseEvent event) -> {
            contextMenu.hide();
        });

        ContextMenu contextMenu2 = new ContextMenu();
        MenuItem menuItem2 = new MenuItem("Возвращает на прошлый шаг");
        contextMenu2.getItems().add(menuItem2);
        back.setContextMenu(contextMenu2);
        back.setOnMouseEntered((MouseEvent event) -> {
            contextMenu2.show(back, event.getScreenX() + 5, event.getScreenY() + 5);
        });
        back.setOnMouseExited((MouseEvent event) -> {
            contextMenu2.hide();
        });
        ContextMenu contextMenu3 = new ContextMenu();
        MenuItem menuItem3 = new MenuItem("Автоматическое решение");
        contextMenu3.getItems().add(menuItem3);
        checkBox.setContextMenu(contextMenu3);
        checkBox.setOnMouseEntered((MouseEvent event) -> {
            contextMenu3.show(checkBox, event.getScreenX() + 5, event.getScreenY() + 5);
        });
        checkBox.setOnMouseExited((MouseEvent event) -> {
            contextMenu3.hide();
        });
    }

    @FXML
    private void handleCheckBox() {
        auto = checkBox.isSelected();
    }

    public void updateTableView() {

        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        highlightedButton = null;

        simplexGridPane.getChildren().clear();
        int index = 0;

        // Верхняя полоска
        for (Integer num : notBasis) {
            index += 1;
            Label colLabel = new Label("x" + num);
            colLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            simplexGridPane.add(colLabel, index, 0);
        }

        // Левый столбец и основные кнопки
        for (int row = 0; row < matrixOfCoef.length; row++) {
            index = 0;
            Label rowLabel = new Label("x" + basis.get(row));
            rowLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px; -fx-alignment: CENTER;");
            simplexGridPane.add(rowLabel, 0, row + 1);
            for (Integer col : notBasis) {
                index += 1;
                Fractional value = matrixOfCoef[row][col - 1];
                Button button = new Button(value.toString());
                button.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");
                button.setDisable(true);

                simplexGridPane.add(button, index, row + 1);
            }
            index += 1;
            Fractional value = matrixOfCoef[row][matrixOfCoef[0].length - 1];
            Button button = new Button(value.toString());
            button.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");
            button.setDisable(true);
            button.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");

            simplexGridPane.add(button, index, row + 1);
        }

        index = 0;
        for (Integer num : notBasis) {
            index += 1;
            Fractional value = targetCoefs[num - 1];
            Button button = new Button(value.toString());
            button.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");
            button.setDisable(true);

            simplexGridPane.add(button, index, matrixOfCoef.length + 1);
        }

        Fractional value = targetCoefs[targetCoefs.length - 1];
        Button button = new Button(value.toString());
        button.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");
        button.setDisable(true);

        simplexGridPane.add(button, index + 1, matrixOfCoef.length + 1);

        enableMinButton(matrixOfCoef, basis, notBasis);

        if (edgeToInfinity()) {
            for (Node node : simplexGridPane.getChildren()) {
                if (node instanceof Button) {
                    ((Button) node).setDisable(true);
                }
            }
            forward.setDisable(true);
            back.setDisable(false);
            messageLabel.setText("Нет решения");
        } else if (isSolution()) {
            StringBuilder solution = createAnswer();
            forward.setDisable(true);
            messageLabel.setText(solution.toString());
        } else if (auto) {
            stepForward();
        }
    }

    public StringBuilder createAnswer() {
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        ArrayList<Fractional> answer = new ArrayList<>();
        int dlina = basis.size() + notBasis.size();
        for (int i = 0; i < dlina; i++) {
            answer.add(new Fractional(0, 1));
        }
        for (int row = 0; row < matrixOfCoef.length; row++) {
            Fractional coef = matrixOfCoef[row][matrixOfCoef[0].length - 1];
            int index = basis.get(row);
            answer.set(index - 1, coef);
        }

        StringBuilder solution = new StringBuilder();
        solution.append("(");
        int count = 0;
        for (Fractional coef : answer) {
            count++;
            solution.append(coef);
            if (count != answer.size()) {
                solution.append(",");
            }
        }
        solution.append(")");
        solution.append("\n");

        if (min) {
            Fractional itog = Fractional.multiplication(targetCoefs[targetCoefs.length - 1], new Fractional(-1, 1));
            solution.append("f = ").append(itog);
        }else {
            Fractional itog = targetCoefs[targetCoefs.length - 1];
            solution.append("f = ").append(itog);
        }

        return solution;
    }

    public void stepForward() {
        if (highlightedButton != null) {
            Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
            Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
            ArrayList<Integer> basis = sharedData.getBasis();
            ArrayList<Integer> notBasis = sharedData.getNotBasis();
            stepForwardcalculations(matrixOfCoef, basis, notBasis, targetCoefs);
            back.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Выберите значение");
            alert.showAndWait();
        }
    }


    public void stepForwardcalculations(Fractional[][] oldMatrix, ArrayList<Integer> oldBasis, ArrayList<Integer> oldNotBasis, Fractional[] oldTarget) {
        Fractional[][] newMatrix = Fractional.deepCopyMatrix(oldMatrix);

        Button button = highlightedButton;
        int indexRowButton = GridPane.getRowIndex(button);
        int indexColButton = GridPane.getColumnIndex(button);

        int xRow = oldBasis.get(indexRowButton - 1);
        int xCol = oldNotBasis.get(indexColButton - 1);

        //Меняем столбцы местами
        for (int row = 0; row < oldMatrix.length; row++) {
            Fractional temp = newMatrix[row][xRow - 1];
            newMatrix[row][xRow - 1] = oldMatrix[row][xCol - 1];
            newMatrix[row][xCol - 1] = temp;
        }

        Fractional[] newTarget = Arrays.copyOf(oldTarget, oldTarget.length);
        //Меняем коэффициенты местами
        Fractional temp = newTarget[xRow - 1];
        newTarget[xRow - 1] = oldTarget[xCol - 1];
        newTarget[xCol - 1] = temp;

        //Меняем базисные и небазисные нумерации для x
        Fractional choosenCoef = oldMatrix[indexRowButton - 1][xCol - 1];
        ArrayList<Integer> newBasis = new ArrayList<>(oldBasis);
        ArrayList<Integer> newNotBasis = new ArrayList<>(oldNotBasis);
        int indexOfOldNumInNewBasis = getIndexOfNum(newBasis, xRow);
        int indexOfOldNumInNewNotBasis = getIndexOfNum(newNotBasis, xCol);
        newBasis.set(indexOfOldNumInNewBasis, xCol);
        newNotBasis.set(indexOfOldNumInNewNotBasis, xRow);

        //Меняем значения в столбце выбранного значения

        Fractional alpha_1 = Fractional.division(new Fractional(1, 1), choosenCoef);
        for (int row = 0; row < newMatrix.length; row++) {
            if (row != indexRowButton - 1) {
                temp = Fractional.multiplication(newMatrix[row][xRow - 1], alpha_1);
                temp = Fractional.multiplication(temp, new Fractional(-1, 1));

                newMatrix[row][xRow - 1] = temp;

            } else {
                newMatrix[row][xRow - 1] = alpha_1;
            }
        }
        newTarget[xRow - 1] = Fractional.multiplication(newTarget[xRow - 1], alpha_1);
        newTarget[xRow - 1] = Fractional.multiplication(newTarget[xRow - 1], new Fractional(-1, 1));
        //Целевая функция


        //Меняем значения в строке выбранного значения
        for (Integer col : newNotBasis) {
            if (col != xRow) {
                newMatrix[indexRowButton - 1][col - 1] = Fractional.multiplication(newMatrix[indexRowButton - 1][col - 1], alpha_1);
            }
        }
        newMatrix[indexRowButton - 1][newMatrix[0].length - 1] = Fractional.multiplication(newMatrix[indexRowButton - 1][newMatrix[0].length - 1], alpha_1);
        //Самое правое значение

        //Целевая функция

        //Считаем вектора
        for (Integer col : newNotBasis) {
            if (col != xRow) {
                for (int row = 0; row < newMatrix.length; row++) {
                    if (row != indexRowButton - 1) {
                        //oldMatrix[row][xCol - 1] - коэффициент из прошлой матрицы
                        //newMatrix[xRow - 1][col - 1] - значение из вектора ( например = (3, *, 6) )
                        Fractional multi = Fractional.multiplication(oldMatrix[row][xCol - 1], newMatrix[indexRowButton - 1][col - 1]);
                        Fractional razn = Fractional.subtraction(newMatrix[row][col - 1], multi);
                        newMatrix[row][col - 1] = razn;
                    }
                }
                Fractional multi = Fractional.multiplication(oldTarget[xCol - 1], newMatrix[indexRowButton - 1][col - 1]);
                Fractional razn = Fractional.subtraction(newTarget[col - 1], multi);

                newTarget[col - 1] = razn;
            }


        }


        //Для самых правых значений
        for (int row = 0; row < newMatrix.length; row++) {
            if (row != indexRowButton - 1) {
                Fractional multi = Fractional.multiplication(oldMatrix[row][xCol - 1], newMatrix[indexRowButton - 1][newMatrix[0].length - 1]);
                newMatrix[row][newMatrix[0].length - 1] = Fractional.subtraction(newMatrix[row][newMatrix[0].length - 1], multi);
            }
        }
        Fractional multi = Fractional.multiplication(oldTarget[xCol - 1], newMatrix[indexRowButton - 1][newMatrix[0].length - 1]);
        newTarget[oldTarget.length - 1] = Fractional.subtraction(newTarget[oldTarget.length - 1], multi);
        //целевая функция

        SharedData newData = new SharedData();
        newData.setNotBasis(newNotBasis);
        newData.setMatrixOfCoef(newMatrix);
        newData.setCoefOfTargetFunction(newTarget);
        newData.setBasis(newBasis);
        setSharedData(newData);

    }

    public void stepBack() {
        if (allData.size() >= 2) {
            int size = allData.size();
            SharedData oldData = allData.get(size - 2);
            allData.remove(size - 1);
            allData.remove(size - 2);
            messageLabel.setText("");
            forward.setDisable(false);
            if (allData.size() == 0) {
                back.setDisable(true);
            }
            setSharedData(oldData);
        }
    }


    public Boolean edgeToInfinity() {
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        for (int col = 0; col < matrixOfCoef[0].length; col++) {
            int count = 0;
            for (int row = 0; row < matrixOfCoef.length; row++) {
                if (!matrixOfCoef[row][col].isPositive() || matrixOfCoef[row][col].getNumerator() == 0) {
                    count++;
                }
            }
            if (!targetCoefs[col].isPositive()) {
                count++;
            }
            if (count == matrixOfCoef.length + 1) {
                return true;
            }
        }
        return false;
    }

    public Boolean isSolution() {
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();

        for (Integer col : notBasis) {
            int cal = col - 1;
            if (!targetCoefs[col - 1].isPositive() && targetCoefs[col - 1].getNumerator() != 0) {
                return false;
            }
        }

        return true;
    }

    private Integer getIndexOfNum(ArrayList<Integer> massive, Integer targetNum) {
        int index = -1;
        for (Integer num : massive) {
            index += 1;
            if (Objects.equals(num, targetNum)) {
                return index;
            }
        }
        return null;
    }


    private void handleButtonClick(Button button) {
        if (highlightedButton != null) {
            highlightedButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 60px;");
        }
        button.setStyle("-fx-background-color: green; -fx-font-size: 14px; -fx-pref-width: 60px;");
        highlightedButton = button;
    }

    public void enableMinButton(Fractional[][] matrixOfCoef, ArrayList<Integer> basis, ArrayList<Integer> notBasis) {
        int col = -1;
        int count = 0;
        for (Integer j : notBasis) {
            col += 1;
            Button button = getButtonAt(matrixOfCoef.length + 1, col + 1);
            Fractional number = sharedData.getCoefOfTargetFunction()[j - 1];
            //Проверяем является значение p(самая нижня строчка) отрицательным

            if (!number.isPositive() && number.getNumerator() != 0) {
                List<Fractional> listOfMins = new ArrayList<>();
                Fractional min = null;
                //пробегаем по столбцу
                for (int i = 0; i < matrixOfCoef.length; i++) {
                    //значение b определенной строки
                    Fractional b = matrixOfCoef[i][matrixOfCoef[0].length - 1];
                    //значение alpha определенной строки
                    Fractional alpha = matrixOfCoef[i][j - 1];
                    //Если alpha положительный, то работаем с этими значениями

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


                //Смотрим на значения которые получились при делении b/alpha, и если оно совпадает с min то активируем кнопку
                for (int i = 0; i < listOfMins.size(); i++) {
                    if (listOfMins.get(i) != null && listOfMins.get(i) == min) {
                        Button buttonTemp = getButtonAt(i + 1, col + 1);
                        buttonTemp.setDisable(false);
                        buttonTemp.setOnAction(e -> handleButtonClick(buttonTemp));
                        if (count == 0) {
                            highlightedButton = buttonTemp;
                            handleButtonClick(buttonTemp);
                        }
                        count++;
                    }
                }

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



