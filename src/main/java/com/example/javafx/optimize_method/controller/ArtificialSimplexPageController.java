package com.example.javafx.optimize_method.controller;

import com.example.javafx.optimize_method.model.Fractional;
import com.example.javafx.optimize_method.model.SharedData;
import com.example.javafx.optimize_method.utils.Gaus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArtificialSimplexPageController {


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
    private Boolean simplexTurn;


    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
        allData.add(sharedData);
        System.out.println("setSharedData called");
        System.out.println(allData);
        updateTableView();
    }

    public void firstSetData(SharedData sharedData){
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        int countOfVariables = sharedData.getCountOfVariables();
        int countOfLimitations = sharedData.getCountOfLimitations();
        Fractional[][] artificalMatrixOfCoef = new Fractional[countOfLimitations][countOfVariables + countOfLimitations + 1];
        //Создаем новую матрицу искусственного базиса
        System.out.println("Start");
        for (int row = 0; row < countOfLimitations; row++){
            for (int col = 0; col < countOfVariables; col++){
                    artificalMatrixOfCoef[row][col] = matrixOfCoef[row][col];
            }
        }
        for (int row = 0; row < countOfLimitations; row++){
            for (int col = countOfVariables; col < countOfVariables + countOfLimitations + 1; col++){
                if (col == countOfVariables + countOfLimitations){
                    artificalMatrixOfCoef[row][col] = matrixOfCoef[row][matrixOfCoef[0].length - 1];
                }else {
                    Fractional temp = Fractional.createFractional("0");
                    artificalMatrixOfCoef[row][col] = temp;
                }
            }
        }

        System.out.println("Start2");
        //Складываем значения в столбцах и умножаем на -1 и получаем коэффициент
        Fractional[] artificalTargetCoefs = new Fractional[countOfVariables + countOfLimitations + 1];
        for (int col = 0; col < countOfVariables + countOfLimitations + 1; col++){
            Fractional temp = Fractional.createFractional("0");
            for (int row = 0; row < countOfLimitations; row++){
                temp = Fractional.sum(temp, artificalMatrixOfCoef[row][col]);
            }
            temp = Fractional.multiplication(temp, Fractional.createFractional("-1"));
            artificalTargetCoefs[col] = temp;
        }

        ArrayList<Integer> basis = new ArrayList<>();
        ArrayList<Integer> notBasis = new ArrayList<>();
        System.out.println("Start3");
        for (int index = 0; index < countOfVariables; index++){
            notBasis.add(index+1);
        }
        for (int index = countOfVariables; index < countOfVariables + countOfLimitations; index++){
            basis.add(index+1);
        }

        SharedData newData = new SharedData();
        newData.setMatrixOfCoef(artificalMatrixOfCoef);
        newData.setBasis(basis);
        newData.setNotBasis(notBasis);
        newData.setArtificalCoefTargetFuntion(artificalTargetCoefs);
        newData.setCoefOfTargetFunction(sharedData.getCoefOfTargetFunction());
        newData.setSimplexTurn(false);
        newData.setCountOfLimitations(countOfLimitations);
        newData.setCountOfVariables(countOfVariables);
        allData.clear();
        messageLabel.setText("");
        back.setDisable(true);
        forward.setDisable(false);
        highlightedButton = null;
        simplexTurn = false;
        setSharedData(newData);

    }


    public void updateTableView() {

        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getCoefOfTargetFunction();
        Fractional[] artificalCoef = sharedData.getArtificalCoefTargetFuntion();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        int countOfVariables = sharedData.getCountOfVariables();
        int countOfLimitations = sharedData.getCountOfLimitations();
        highlightedButton = null;
        System.out.println("DATA DATA DATA DATA DATA");
        System.out.println("basis and not basis");
        System.out.println(sharedData.getBasis());
        System.out.println(sharedData.getNotBasis());
        System.out.println("matrix");
        for (Fractional[] row : sharedData.getMatrixOfCoef()){
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Target Coef");
        System.out.println(Arrays.toString(sharedData.getCoefOfTargetFunction()));
        System.out.println("Artific Coef");
        System.out.println(Arrays.toString(sharedData.getArtificalCoefTargetFuntion()));
        System.out.println(sharedData.getSimplexTurn());
        System.out.println("END END END END END");

//        System.out.println("ALLDATA");
//        System.out.println(allData);


        int numVariables = matrixOfCoef[0].length - 1;
        simplexGridPane.getChildren().clear();
        int index = 0;
        // Верхняя полоска
        for (Integer num : notBasis) {
            if (num <= countOfVariables) {
                index += 1;
                Label colLabel = new Label("x" + num);
                simplexGridPane.add(colLabel, index, 0);
            }
        }
        // левый столбец и основные кнопки
        for (int row = 0; row < matrixOfCoef.length; row++) {
            index = 0;
            Label rowLabel = new Label("x" + basis.get(row));
            simplexGridPane.add(rowLabel, 0, row + 1);
            for (Integer col : notBasis) {
                if (col <= countOfVariables){
                    index += 1;
                    Fractional value = matrixOfCoef[row][col - 1];
                    Button button = new Button(value.toString());
                    button.setDisable(true);
                    simplexGridPane.add(button, index, row + 1);
                }
            }
            index += 1;
            Fractional value = matrixOfCoef[row][matrixOfCoef[0].length - 1];
            Button button = new Button(value.toString());
            button.setDisable(true);

            simplexGridPane.add(button, index, row + 1);
        }

        index = 0;
        for (Integer num : notBasis) {
            if (num <= countOfVariables){
                index += 1;
                Fractional value = artificalCoef[num - 1];
                Button button = new Button(value.toString());
                button.setDisable(true);
                simplexGridPane.add(button, index, matrixOfCoef.length + 1);
            }

        }

        Fractional value = artificalCoef[artificalCoef.length - 1];
        Button button = new Button(value.toString());
        button.setDisable(true);

        simplexGridPane.add(button, index + 1, matrixOfCoef.length + 1);
//        System.out.println(Arrays.deepToString(matrixOfCoef));
//        System.out.println(Arrays.toString(targetCoefs));
        enableMinButton(matrixOfCoef, basis, notBasis);

        if (swapToSimplex(sharedData.getArtificalCoefTargetFuntion(), sharedData.getNotBasis()) && !simplexTurn){
            forward.setText("Перейти к симплексу");
        }else {
            forward.setText("Дальше");
        }
        if (newEdgeToInfinity()){
            for (Node node : simplexGridPane.getChildren()) {
                if (node instanceof Button) {
                    ((Button) node).setDisable(true);
                }
            }
            forward.setDisable(true);
            back.setDisable(false);
            messageLabel.setText("Нет решения");
        }else if (isSolution() && simplexTurn){
            StringBuilder solution = createAnswer();
            forward.setDisable(true);
            messageLabel.setText(solution.toString());
        }

    }

    public StringBuilder createAnswer(){
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getArtificalCoefTargetFuntion();
        ArrayList<Integer> basis = sharedData.getBasis();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        ArrayList<Fractional> answer = new ArrayList<>();
        int dlina = basis.size() + notBasis.size();
        for (int i = 0; i < dlina; i++){
            answer.add(Fractional.createFractional("0"));
        }
//        System.out.println("answer");
//        System.out.println(answer);
        for (int row = 0; row < matrixOfCoef.length; row++){
            Fractional coef = matrixOfCoef[row][matrixOfCoef[0].length - 1];
            int index = basis.get(row);
            answer.set(index - 1, coef);
        }

//        for (Integer index : basis){
//            int indexOfRow = getIndexOfNum(basis, index);
//            Fractional coef = matrixOfCoef[indexOfRow][matrixOfCoef[0].length - 1];
//            System.out.println("index: " + indexOfRow);
//            System.out.println("coef: " + coef);
//            answer.set(indexOfRow, coef);
//        }
//        System.out.println(answer);
        StringBuilder solution = new StringBuilder();
        solution.append("(");
        int count = 0;
        for (Fractional coef : answer){
            count ++;
            solution.append(coef);
            if (count != answer.size()){
                solution.append(",");
            }
        }
        solution.append(")");
        solution.append("\n");
        Fractional itog = Fractional.multiplication(targetCoefs[targetCoefs.length - 1], Fractional.createFractional("-1"));
        solution.append("f = ").append(itog);
        return solution;
    }

    public Boolean swapToSimplex(Fractional[] oldTarget, ArrayList<Integer> notBasis){
        for (Integer index : notBasis){
            if (index <= sharedData.getCountOfVariables()){
                Fractional coef = oldTarget[index - 1];
                if (coef.getNumerator() != 0){
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean coefOfFuncIsZero(Fractional[] oldTarget){
        return oldTarget[oldTarget.length - 1].getNumerator() == 0;
    }
    public Boolean coefOfFuncIsBiggerZero(Fractional[] oldTarget){
        return oldTarget[oldTarget.length - 1].getNumerator() > 0;
    }

    public void stepBack(){
        if (allData.size() >= 2) {
            int size = allData.size();
            SharedData oldData = allData.get(size - 2);
            allData.remove(size - 1);
            allData.remove(size - 2);
            messageLabel.setText("");
            forward.setDisable(false);
            if (allData.size() == 0){
                back.setDisable(true);
            }
            simplexTurn = oldData.getSimplexTurn();
            setSharedData(oldData);
        }
    }
    public void stepForward(){
        SharedData newData = new SharedData();
        newData.setBasis(new ArrayList<>(sharedData.getBasis()));
        newData.setNotBasis(new ArrayList<>(sharedData.getNotBasis()));
        newData.setMatrixOfCoef(sharedData.getMatrixOfCoef());
        newData.setCoefOfTargetFunction(sharedData.getCoefOfTargetFunction());
        newData.setCountOfLimitations(sharedData.getCountOfLimitations());
        newData.setCountOfVariables(sharedData.getCountOfVariables());
        boolean flag = swapToSimplex(sharedData.getArtificalCoefTargetFuntion(), sharedData.getNotBasis());
        boolean flagBasis = false;
//        System.out.println("STEP FORWARD");
//        System.out.println(sharedData.getBasis());
//        System.out.println(sharedData.getNotBasis());
        for (Fractional[] row : sharedData.getMatrixOfCoef()){
            System.out.println(Arrays.toString(row));
        }
//        System.out.println(Arrays.toString(sharedData.getCoefOfTargetFunction()));
//        System.out.println(Arrays.toString(sharedData.getArtificalCoefTargetFuntion()));
//        System.out.println("end");
        if (flag) {
//            System.out.println("flag");
            for (int index = sharedData.getCountOfVariables() + 1; index < sharedData.getCountOfVariables() + sharedData.getCountOfLimitations(); index++) {
                int indexOfNum2 = newData.getBasis().indexOf(index);
                if (indexOfNum2 != -1) {
                    flagBasis = true;
                }
                //ХОЛОСТОЙ ШАГ
                if (flagBasis){
//                    System.out.println(newData.getBasis());
//                    System.out.println(newData.getNotBasis());
//                    System.out.println("holost");
//                    System.out.println(indexOfNum2);
                    newData.getBasis().remove(indexOfNum2);
                    flagBasis = false;
//                    System.out.println(newData.getBasis());
//                    System.out.println(newData.getNotBasis());
                    Fractional[][] newMatrixCoefs = new Fractional[newData.getMatrixOfCoef().length - 1][newData.getMatrixOfCoef()[0].length];
                    int indexOfRow = -1;
                    for (int row = 0; row < newData.getMatrixOfCoef().length; row++){
                        if (row != indexOfNum2) {
                            indexOfRow++;
                            for (int col = 0; col < newData.getMatrixOfCoef()[0].length; col++) {

                                    newMatrixCoefs[indexOfRow][col] = newData.getMatrixOfCoef()[row][col];

                            }
                        }
                    }
                    newData.setMatrixOfCoef(newMatrixCoefs);
                }
            }
        }
        if (!flag){
            //Обычный шаг
            if (highlightedButton != null) {
                Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
                Fractional[] targetCoefs = sharedData.getArtificalCoefTargetFuntion();
                ArrayList<Integer> basis = sharedData.getBasis();
                ArrayList<Integer> notBasis = sharedData.getNotBasis();
                stepForwardcalculations(matrixOfCoef, basis, notBasis, targetCoefs);
                back.setDisable(false);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Выберите значение");
                alert.showAndWait();
            }
        }else {

            Fractional[][] matrixOfCoef = newData.getMatrixOfCoef();
            Fractional[] targetCoefs = newData.getCoefOfTargetFunction();
            ArrayList<Integer> basis = newData.getBasis();
            ArrayList<Integer> notBasis = newData.getNotBasis();
            Fractional[] newArtificalTargetCoefs = new Fractional[newData.getCountOfLimitations() + newData.getCountOfVariables() ];
            for (int i = 0; i < newArtificalTargetCoefs.length; i++) {
                newArtificalTargetCoefs[i] = Fractional.createFractional("0");
            }
            for (Integer basisIndex : notBasis) {
                if (basisIndex <= newData.getCountOfVariables()) {
                    newArtificalTargetCoefs[basisIndex - 1] = targetCoefs[basisIndex - 1];
                }
            }
//            System.out.println("ELSE DATA ELSE DATA");
//            System.out.println(Arrays.toString(targetCoefs));
//            targetCoefs = Arrays.copyOf(newArtificalTargetCoefs, newArtificalTargetCoefs.length);
//            int colTemp = 0;
//            for (Fractional coef : sharedData.getCoefOfTargetFunction()){
//                targetCoefs[colTemp] = coef;
//                colTemp++;
//            }
//            System.out.println(Arrays.toString(targetCoefs));
            int row = 0;
//            System.out.println("matrix");
            for (Fractional[] stroka: matrixOfCoef){
                System.out.println(Arrays.toString(stroka));
            }
//            System.out.println("basis and not");
//            System.out.println(newData.getBasis());
//            System.out.println(newData.getNotBasis());
//            System.out.println("------------------");
            for (Integer indexOfX : basis) {
                for (Integer col : notBasis) {
                    if (col<=newData.getCountOfVariables()) {
                        Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][col - 1], targetCoefs[indexOfX - 1]), Fractional.createFractional("-1"));
                        Fractional newCoef = Fractional.sum(newArtificalTargetCoefs[col - 1], temp);
//                System.out.println("temp");
//                System.out.println(matrixOfCoef[row][col - 1]);
//                System.out.println(targetCoefs[indexOfX - 1]);
//                System.out.println(newCoef);
                        newArtificalTargetCoefs[col - 1] = newCoef;
                    }
                }
                Fractional temp = Fractional.multiplication(Fractional.multiplication(matrixOfCoef[row][matrixOfCoef[0].length - 1], targetCoefs[indexOfX - 1]), Fractional.createFractional("-1"));
                Fractional newCoef = Fractional.sum(newArtificalTargetCoefs[newArtificalTargetCoefs.length - 1], temp);
                newArtificalTargetCoefs[newArtificalTargetCoefs.length - 1] = newCoef;
                row += 1;
            }

            newData.setArtificalCoefTargetFuntion(newArtificalTargetCoefs);
            newData.setSimplexTurn(true);
            simplexTurn = true;
            System.out.println("TRUE TRUE TRUE TRUE TRUE");
            System.out.println("DATA DATA DATA DATA DATA");
            System.out.println("basis and not basis");
            System.out.println(sharedData.getBasis());
            System.out.println(sharedData.getNotBasis());
            System.out.println("matrix");
            for (Fractional[] temp : sharedData.getMatrixOfCoef()){
                System.out.println(Arrays.toString(temp));
            }
            System.out.println("Target Coef");
            System.out.println(Arrays.toString(sharedData.getCoefOfTargetFunction()));
            System.out.println("Artific Coef");
            System.out.println(Arrays.toString(sharedData.getArtificalCoefTargetFuntion()));
            System.out.println(sharedData.getSimplexTurn());
            System.out.println("END END END END END");
            System.out.println("TRUE TRUE TRUE TRUE TRUE");
            System.out.println("TRUE TRUE TRUE TRUE TRUE");
            System.out.println("DATA DATA DATA DATA DATA");
            System.out.println("basis and not basis");
            System.out.println(newData.getBasis());
            System.out.println(newData.getNotBasis());
            System.out.println("matrix");
            for (Fractional[] temp : newData.getMatrixOfCoef()){
                System.out.println(Arrays.toString(temp));
            }
            System.out.println("Target Coef");
            System.out.println(Arrays.toString(newData.getCoefOfTargetFunction()));
            System.out.println("Artific Coef");
            System.out.println(Arrays.toString(newData.getArtificalCoefTargetFuntion()));
            System.out.println(newData.getSimplexTurn());
            System.out.println("END END END END END");
            System.out.println("TRUE TRUE TRUE TRUE TRUE");
            setSharedData(newData);
        }

    }


    public void stepForwardcalculations(Fractional[][] oldMatrix, ArrayList<Integer> oldBasis, ArrayList<Integer> oldNotBasis, Fractional[] oldTarget){
        Fractional[][] newMatrix = Fractional.deepCopyMatrix(oldMatrix);

        Button button = highlightedButton;
        int indexRowButton = GridPane.getRowIndex(button);
        int indexColButton = GridPane.getColumnIndex(button);
//        System.out.println("coordinate");
//        System.out.println(indexRowButton);
//        System.out.println(indexColButton);
        int xRow = oldBasis.get(indexRowButton - 1);
        int xCol = oldNotBasis.get(indexColButton - 1);
//        System.out.println("x");
//        System.out.println(xRow);
//        System.out.println(xCol);
//        System.out.println("old data");
//        System.out.println(Arrays.toString(oldTarget));
//        System.out.println(oldBasis);
//        System.out.println(oldNotBasis);
//        for (Fractional[] row : oldMatrix){
//            System.out.println(Arrays.toString(row));
//        }
        //Меняем столбцы местами
        for (int row = 0; row < oldMatrix.length; row++){
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
        if (!simplexTurn) {
//            System.out.println("DELETE DELETE");
            newNotBasis.remove(indexOfOldNumInNewNotBasis);
        }
//        System.out.println("new data");
//        System.out.println(Arrays.toString(newTarget));
//        System.out.println(newBasis);
//        System.out.println(newNotBasis);
//        for (Fractional[] row : newMatrix){
//            System.out.println(Arrays.toString(row));
//        }

        //Меняем значения в столбце выбранного значения

        Fractional alpha_1 = Fractional.division(Fractional.createFractional("1"), choosenCoef);
//        System.out.println("alpha");
        for (int row = 0; row < newMatrix.length; row++){
            if(row != indexRowButton - 1){
                temp = Fractional.multiplication(newMatrix[row][xRow - 1], alpha_1);
                temp = Fractional.multiplication(temp, Fractional.createFractional("-1"));

                newMatrix[row][xRow - 1] = temp;

            }else {
                newMatrix[row][xRow - 1] = alpha_1;
            }
        }
        newTarget[xRow - 1] = Fractional.multiplication(newTarget[xRow - 1], alpha_1);
        newTarget[xRow - 1] = Fractional.multiplication(newTarget[xRow - 1], Fractional.createFractional("-1"));
        //Целевая функция
//        System.out.println("change stolb");
//        System.out.println(Arrays.toString(newTarget));
//        for (Fractional[] row : newMatrix){
//            System.out.println(Arrays.toString(row));
//        }


        //Меняем значения в строке выбранного значения
        for (Integer col : newNotBasis){
            if (col != xRow){
                newMatrix[indexRowButton - 1][col - 1] = Fractional.multiplication(newMatrix[indexRowButton - 1][col - 1], alpha_1);
            }
        }
        newMatrix[indexRowButton - 1][newMatrix[0].length - 1] = Fractional.multiplication(newMatrix[indexRowButton - 1][newMatrix[0].length - 1], alpha_1);
        //Самое правое значение

        //Целевая функция
//        System.out.println("change stroku");
//        for (Fractional[] row : newMatrix){
//            System.out.println(Arrays.toString(row));
//        }


        //Считаем вектора
        for (Integer col : newNotBasis){
//            System.out.println("col: " + col);
            if (col != xRow && col <= sharedData.getCountOfVariables()){
                int cal = xRow -1;
//                System.out.println("good: " +col + " != " + cal);
                for (int row = 0; row < newMatrix.length; row++){
//                    System.out.println("row: " + row);
                    cal = indexRowButton -1;
                    if (row != indexRowButton - 1) {
//                        System.out.println("good: " +row + " != " + cal);
                        //oldMatrix[row][xCol - 1] - коэффициент из прошлой матрицы
                        //newMatrix[xRow - 1][col - 1] - значение из вектора ( например = (3, *, 6) )
                        Fractional multi = Fractional.multiplication(oldMatrix[row][xCol - 1], newMatrix[indexRowButton - 1][col - 1]);
                        Fractional razn = Fractional.subtraction(newMatrix[row][col - 1], multi);
//                        System.out.println("edge");
//                        System.out.println("old coef in row: " + oldMatrix[row][xCol - 1]);
//                        System.out.println("new line coef: " + newMatrix[indexRowButton - 1][col - 1]);
//                        System.out.println("multi: " + multi);
//                        System.out.println("from sub: " + newMatrix[row][col - 1]);
//                        System.out.println("razn: " + razn);
                        newMatrix[row][col - 1] = razn;
//                        System.out.println("itog: " + newMatrix[row][col - 1]);
                    }
                }
                Fractional multi = Fractional.multiplication(oldTarget[xCol - 1],newMatrix[indexRowButton - 1][col - 1]);
                Fractional razn = Fractional.subtraction(newTarget[col - 1], multi);

                newTarget[col - 1] = razn;
            }


        }
//        System.out.println("Вычисления без правого столбца");
//        System.out.println(Arrays.toString(newTarget));
//        for (Fractional[] row : newMatrix){
//            System.out.println(Arrays.toString(row));
//        }


        //Для самых правых значений
        for (int row = 0; row < newMatrix.length; row++){
            if (row != indexRowButton - 1) {
                Fractional multi = Fractional.multiplication(oldMatrix[row][xCol - 1], newMatrix[indexRowButton - 1][newMatrix[0].length - 1]);
                newMatrix[row][newMatrix[0].length - 1] = Fractional.subtraction(newMatrix[row][newMatrix[0].length - 1], multi);
            }
        }
        Fractional multi = Fractional.multiplication(oldTarget[xCol - 1], newMatrix[indexRowButton - 1][newMatrix[0].length - 1]);
        newTarget[oldTarget.length - 1] = Fractional.subtraction(newTarget[oldTarget.length - 1], multi);
        //целевая функция
//        System.out.println("Вычисления с правым столбцом");
//        System.out.println(Arrays.toString(newTarget));
//        for (Fractional[] row : newMatrix){
//            System.out.println(Arrays.toString(row));
//        }


        SharedData newData = new SharedData();
        newData.setNotBasis(newNotBasis);
        newData.setMatrixOfCoef(newMatrix);
        newData.setArtificalCoefTargetFuntion(newTarget);
        newData.setBasis(newBasis);
        newData.setCountOfLimitations(sharedData.getCountOfLimitations());
        newData.setCountOfVariables(sharedData.getCountOfVariables());
        newData.setCoefOfTargetFunction(sharedData.getCoefOfTargetFunction());
        newData.setSimplexTurn(sharedData.getSimplexTurn());
        setSharedData(newData);

    }


    private Integer getIndexOfNum(ArrayList<Integer> massive, Integer targetNum){
        int index = -1;
        for (Integer num : massive){
            index +=1;
            if (Objects.equals(num, targetNum)){
                return index;
            }
        }
        return null;
    }



    public void enableMinButton(Fractional[][] matrixOfCoef, ArrayList<Integer> basis, ArrayList<Integer> notBasis) {
        int col = -1;
//        System.out.println("ENABLE");
        for (Integer j : notBasis) {
//            System.out.println("j: " + j);
            if (j <= sharedData.getCountOfVariables()) {
//                System.out.println("hit");
                col += 1;
                Button button = getButtonAt(matrixOfCoef.length + 1, col + 1);
                String strValue = button.getText();
                Fractional number = Fractional.createFractional(strValue);
                //Проверяем является значение p(самая нижня строчка) отрицательным
//                System.out.println("stolb");
//                System.out.println(number);
                if (!number.isPositive() && number.getNumerator() != 0) {
//                System.out.println("start");
                    ArrayList<Fractional> listOfMins = new ArrayList<>();
                    Fractional min = null;
                    //пробегаем по столбцу
//                    System.out.println("LIST OF MINS");
//                    System.out.println(listOfMins);
                    for (int i = 0; i < matrixOfCoef.length; i++) {
                        int indexOfXInRow = basis.get(i);
//                        System.out.println("INDEX: " + indexOfXInRow);
                        if (!simplexTurn && indexOfXInRow > sharedData.getCountOfVariables()) {
//                            System.out.println("TRUE");
                            //значение b определенной строки
                            Fractional b = matrixOfCoef[i][matrixOfCoef[0].length - 1];
                            //значение alpha определенной строки
                            Fractional alpha = matrixOfCoef[i][j - 1];
                            //Если alpha положительный, то работаем с этими значениями
//                    System.out.println("alpha");
//                    System.out.println(alpha);
                            if (alpha.isPositive() && alpha.getNumerator() != 0) {
                                Fractional r = Fractional.division(b, alpha);
//                                System.out.println("GOOD");
                                if (min == null) {
                                    min = r;
                                    listOfMins.add(min);
//                                    System.out.println("1");
                                } else if (Fractional.isBigger(min, r) || Fractional.isEquals(min,r)) {
                                    min = r;
                                    listOfMins.add(min);
//                                    System.out.println("2");
                                }else {
//                                    System.out.println("4");
                                    listOfMins.add(null);
                                }
                            } else {
//                                System.out.println("3");
                                listOfMins.add(null);
                            }
                        }else if (!simplexTurn){
                            if (indexOfXInRow <= sharedData.getCountOfVariables()){
//                                System.out.println("HUI");
                                listOfMins.add(null);
                            }
                        }
                        if (simplexTurn){
//                            System.out.println("SIMPLEX");
                            //значение b определенной строки
                            Fractional b = matrixOfCoef[i][matrixOfCoef[0].length - 1];
                            //значение alpha определенной строки
                            Fractional alpha = matrixOfCoef[i][j - 1];
                            //Если alpha положительный, то работаем с этими значениями
//                    System.out.println("alpha");
//                    System.out.println(alpha);
                            if (alpha.isPositive() && alpha.getNumerator() != 0) {
//                                System.out.println("GOOD");
                                Fractional r = Fractional.division(b, alpha);
                                if (min == null) {
                                    min = r;
                                    listOfMins.add(min);
//                                    System.out.println("1");
                                } else if (Fractional.isBigger(min, r)) {
                                    min = r;
                                    listOfMins.add(min);
//                                    System.out.println("2");
                                }
                            } else {
                                listOfMins.add(null);
//                                System.out.println("3");
                            }
//                            System.out.println(listOfMins);
                        }
                    }
//                    System.out.println("LIST OF MINS");
//                    System.out.println(listOfMins);
                    //Смотрим на значения которые получились при делении b/alpha, и если оно совпадает с min то активируем кнопку
                    for (int i = 0; i < listOfMins.size(); i++) {
                        if (listOfMins.get(i) != null && listOfMins.get(i) == min) {

                            Button buttonTemp = getButtonAt(i + 1, col + 1);
                            buttonTemp.setDisable(false);
                            buttonTemp.setOnAction(e -> handleButtonClick(buttonTemp));
                        }
                    }
//                System.out.println("circle");
                }
            }
        }
    }

    private void handleButtonClick(Button button) {
        if (highlightedButton != null) {
            highlightedButton.setStyle("");
        }
        button.setStyle("-fx-background-color: green;");
        highlightedButton = button;
    }
    private Button getButtonAt(int row, int col) {
        for (Node node : simplexGridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;
    }


    public Boolean newEdgeToInfinity(){
        Fractional[][] matrixOfCoef = sharedData.getMatrixOfCoef();
        Fractional[] targetCoefs = sharedData.getArtificalCoefTargetFuntion();
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
        for (Integer col : notBasis){
            int count = 0;
            for (int row = 0; row < matrixOfCoef.length; row++) {
                if (!matrixOfCoef[row][col -1].isPositive() || matrixOfCoef[row][col -1].getNumerator() == 0) {
                    count++;
                }
            }
            if (!targetCoefs[col - 1].isPositive()) {
                count++;
            }
            if (count == matrixOfCoef.length + 1) {
//                System.out.println("infinity");
//                for (int row = 0; row < matrixOfCoef.length; row++) {
//                    System.out.println(matrixOfCoef[row][col - 1]);
//                }
//                System.out.println(targetCoefs[col - 1]);
//                System.out.println("infinity");
                return true;
            }
        }
        return false;
    }


    public Boolean isSolution(){
        Fractional[] targetCoefs = sharedData.getArtificalCoefTargetFuntion();
        Fractional last = targetCoefs[targetCoefs.length - 1];
        ArrayList<Integer> notBasis = sharedData.getNotBasis();
//        System.out.println("Is Solution");
        for (Integer col : notBasis){
            int cal = col - 1;
//            System.out.println("Col: " + cal);
            if (!targetCoefs[col - 1].isPositive() && targetCoefs[col - 1].getNumerator() != 0){
//                System.out.println("FALSE: " + targetCoefs[col - 1]);
                return false;
            }
        }
        return true;
    }

    @FXML
    private void initialize() {

    }
}
