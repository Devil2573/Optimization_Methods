package com.example.javafx.optimize_method.utils;

import com.example.javafx.optimize_method.model.Fractional;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Gaus {


    /**
     * HashMap<String, Object> hashMap = readFile("data.txt");
     * int matrixColumns = 0;
     * int matrixRows = 0;
     * String[] baseXtemp = new String[0];
     * Fractional[][] matrixOfCoef = new Fractional[0][];
     * if (hashMap != null) {
     * matrixColumns = (int) hashMap.get("matrixColumns");
     * matrixRows = (int) hashMap.get("matrixRows");
     * baseXtemp = (String[]) hashMap.get("baseXtemp");
     * matrixOfCoef = (Fractional[][]) hashMap.get("matrixOfCoef");
     * } else {
     * System.exit(0);
     * }
     * Integer[] baseX = new Integer[baseXtemp.length];
     * Integer[] startColums = new Integer[baseXtemp.length];
     * for (int i = 0; i < baseXtemp.length; i++) {
     * baseX[i] = Integer.parseInt(baseXtemp[i]);
     * startColums[i] = i + 1;
     * }
     * <p>
     * for (int i = 0; i != matrixOfCoef.length; i++) {
     * System.out.println(Arrays.toString(matrixOfCoef[i]));
     * }
     * System.out.println(Arrays.toString(baseXtemp));
     * System.out.println("\n");
     * <p>
     * System.out.println("Меняем столбцы местами для удобства");
     * matrixOfCoef = swapColumns(matrixOfCoef, baseX);-----------------------------------------------------------------------------------
     * <p>
     * System.out.println("\n");
     * for (int t = 0; t != matrixOfCoef.length; t++) {
     * System.out.println(Arrays.toString(matrixOfCoef[t]));
     * }
     * System.out.println();
     * System.out.println("Прямой ход");---------------------------------------
     * //Прямой ход
     * matrixOfCoef = straightRunning(matrixOfCoef, matrixRows, matrixColumns);---------------------------------------------------------------------------
     * if (matrixOfCoef == null) {
     * <p>
     * System.exit(0);
     * }
     * <p>
     * System.out.println("Обратный ход");-------------------------------------
     * //Обратный ход
     * matrixOfCoef = backRunning(matrixOfCoef, matrixRows, matrixColumns);--------------------------------------------------
     * <p>
     * System.out.println("Final");
     * for (int t = 0; t != matrixOfCoef.length; t++) {
     * System.out.println(Arrays.toString(matrixOfCoef[t]));
     * }
     * System.out.println("\n");
     * <p>
     * System.out.println("Возвращаем на место столбцы");-------------------------------------
     * matrixOfCoef = swapColumnsBack(matrixOfCoef, baseX);------------------------------------------------------------------------
     * for (int t = 0; t != matrixOfCoef.length; t++) {
     * System.out.println(Arrays.toString(matrixOfCoef[t]));
     * }
     * <p>
     * // Запись в файл
     * writeMatrixInFile("Итоговая матрица\n", matrixOfCoef);
     */

    public static Fractional[][] doAllMethods(Fractional[][] matrixOfCoef, Integer[] base) {
        matrixOfCoef = swapColumns(matrixOfCoef, base);
        matrixOfCoef = straightRunning(matrixOfCoef);
        matrixOfCoef = backRunning(matrixOfCoef);
        matrixOfCoef = swapColumnsBack(matrixOfCoef, base);
        return matrixOfCoef;
    }

    static Fractional[][] backRunning(Fractional[][] matrixOfCoef) {
        boolean flag = true;
        int i = matrixOfCoef.length - 1;
        int matrixColumns = matrixOfCoef[0].length;
        while (flag) {
            //проверяем что число не равно нулю, чтобы сделать из него единицу
            if (matrixOfCoef[i][i].getNumerator() != 0) {
                //пробегаем строчки ниже нашей
                for (int j = i - 1; j != -1; j--) {// j = 2 - 1 = 1
                    if (matrixOfCoef[j][i].getNumerator() != 0) {
                        Fractional coef = Fractional.division(matrixOfCoef[j][i], matrixOfCoef[i][i]);
                        Fractional[] newRowCoef = matrixOfCoef[j];
                        for (int k = matrixColumns - 1; k != i - 1; k--) {//k = 5 - 1 = 4
                            Fractional subtractible = Fractional.multiplication(matrixOfCoef[i][k], coef);

                            newRowCoef[k] = Fractional.subtraction(matrixOfCoef[j][k], subtractible);
                        }
                        matrixOfCoef[j] = newRowCoef;
                    }
                }
            }
            System.out.println();
            for (int t = 0; t != matrixOfCoef.length; t++) {
                System.out.println(Arrays.toString(matrixOfCoef[t]));
            }
            System.out.println();
            i--;
            if (i == -1) {
                flag = false;
            }
        }
        return matrixOfCoef;
    }

    static Fractional[][] swapColumns(Fractional[][] matrix, Integer[] base) {
        Fractional[][] tempMatrix = new Fractional[matrix.length][matrix[0].length];
        ArrayList<Integer> baseList = new ArrayList<>(Arrays.asList(base));
        for (int i = 1; i != matrix[0].length + 1; i++) {
            if (!baseList.contains(i)) {
                baseList.add(i);
            }
        }
        base = baseList.toArray(new Integer[0]);
        System.out.println(Arrays.toString(base));

        for (int i = 0; i != matrix[0].length; i++) {

            for (int j = 0; j != matrix.length; j++) {
                tempMatrix[j][i] = matrix[j][base[i] - 1];
            }

            for (int t = 0; t != matrix.length; t++) {
                System.out.println(Arrays.toString(tempMatrix[t]));
            }
            System.out.println();
        }

        return tempMatrix;
    }

    static Fractional[][] swapColumnsBack(Fractional[][] matrix, Integer[] base) {
        Fractional[][] tempMatrix = new Fractional[matrix.length][matrix[0].length];
        ArrayList<Integer> baseList = new ArrayList<>(Arrays.asList(base));
        ArrayList<Integer> startColumnsList = new ArrayList<>();
        for (int i = 1; i != matrix[0].length + 1; i++) {
            if (!baseList.contains(i)) {
                baseList.add(i);
            }

        }
        for (int i = 1; i != matrix[0].length + 1; i++) {
            startColumnsList.add(i);
        }
        base = baseList.toArray(new Integer[0]);
        Integer[] startColumns = startColumnsList.toArray(new Integer[0]);
        System.out.println(Arrays.toString(base));
        System.out.println(Arrays.toString(startColumns));

        for (int i = 0; i != matrix[0].length; i++) {

            for (int j = 0; j != matrix.length; j++) {
                tempMatrix[j][base[i] - 1] = matrix[j][startColumns[i] - 1];
            }

            for (int t = 0; t != matrix.length; t++) {
                System.out.println(Arrays.toString(tempMatrix[t]));
            }
            System.out.println();
        }

        return tempMatrix;
    }

    static Fractional[][] straightRunning(Fractional[][] matrixOfCoef) {
        int matrixRows = matrixOfCoef.length;
        int matrixColums = matrixOfCoef[0].length;
        boolean flag = true;
        //текущая строчка
        int i = 0;
        //индекс сттроки с которой надо будет поменять текущую строку
        int coefSwap = 0;
        Fractional CoefMinusOne = new Fractional(-1, 1);
        Fractional CoefOne = new Fractional(1, 1);
        Fractional CoefTakeOut = new Fractional(1, 1);

        Fractional[][] matrixDeterminant = new Fractional[matrixRows][matrixRows];
        for (int t = 0; t != matrixRows; t++) {
            for (int j = 0; j != matrixRows; j++) {
                matrixDeterminant[t][j] = matrixOfCoef[t][j];
            }
        }
        //Прямой ход
        while (flag) {

            //проверяем что число не равно нулю, чтобы сделать из него единицу
            if (matrixOfCoef[i][i].getNumerator() != 0) {
                coefSwap = i;

                //если отрицательное число, то умножаем на -1
                if (!matrixOfCoef[i][i].isPositive()) {
                    Fractional[] newRowCoef = matrixOfCoef[i];
                    CoefTakeOut = Fractional.multiplication(CoefTakeOut, CoefMinusOne);
                    for (int k = i; k != matrixColums; k++) {

                        newRowCoef[k] = Fractional.multiplication(matrixOfCoef[i][k], CoefMinusOne);
                    }
                    //заменяем старые коэффициенты на новые
                    matrixOfCoef[i] = newRowCoef;
                }
                //если число не равно единичке, то делаем его единичкой
                if (!matrixOfCoef[i][i].isOne()) {
                    Fractional inverseCoef = Fractional.division(CoefOne, matrixOfCoef[i][i]);
                    Fractional[] newRowCoef = matrixOfCoef[i];
                    CoefTakeOut = Fractional.multiplication(CoefTakeOut, matrixOfCoef[i][i]);
                    for (int k = i; k != matrixColums; k++) {
                        newRowCoef[k] = Fractional.multiplication(matrixOfCoef[i][k], inverseCoef);
                    }
                    //заменяем старые коэффициенты на новые
                    matrixOfCoef[i] = newRowCoef;
                }

                //пробегаем строчки ниже нашей
                for (int j = i + 1; j != matrixRows; j++) {
                    //проверяем что число в столбце не равно нулю (то есть надо ли вычитать из строки)
                    if (matrixOfCoef[j][i].getNumerator() != 0) {
                        //коэффициент на который надо умножить нашу строку, чтобы вычесть из другой строки и получить нулевой столбец
                        Fractional coef = Fractional.division(matrixOfCoef[j][i], matrixOfCoef[i][i]);
                        //строка коэффициентов из которой вычитаем
                        Fractional[] newRowCoef = matrixOfCoef[j];
                        for (int k = i; k != matrixColums; k++) {
                            //число, которое вычитается
                            Fractional subtractible = Fractional.multiplication(matrixOfCoef[i][k], coef);
                            //заменяем число на разность
                            newRowCoef[k] = Fractional.subtraction(matrixOfCoef[j][k], subtractible);
                        }
                        //заменяем старые коэффициенты на новые
                        matrixOfCoef[j] = newRowCoef;
                    }
                }
                System.out.println();
                for (int t = 0; t != matrixOfCoef.length; t++) {
                    System.out.println(Arrays.toString(matrixOfCoef[t]));
                }
                i++;
                if (i == matrixRows) {
                    flag = false;
                }
                //если число все же равно нулю, то меняем строку на другую
            } else {
                coefSwap++;
                if (coefSwap == matrixRows) {
                    flag = false;
                } else {
                    Fractional[] swap = matrixOfCoef[coefSwap];
                    matrixOfCoef[coefSwap] = matrixOfCoef[i];
                    matrixOfCoef[i] = swap;
                }
            }

        }

        Fractional Determinant = CoefTakeOut;
        for (int t = 0; t != matrixRows; t++) {
            if (matrixOfCoef[t][t].getNumerator() == 0) {
                Determinant.setNumerator(0);
                break;
            }
        }
        System.out.println("Определитель");
        System.out.println(Determinant);
        if (Determinant.getNumerator() != 0) {
            return matrixOfCoef;
        } else {
            System.out.println("Решения нет");
            for (int t = 0; t != matrixRows; t++) {
                System.out.println(Arrays.toString(matrixDeterminant[t]));
            }
            writeMatrixInFile("Решения нет\n Определитель равен " + Determinant, matrixDeterminant);
            return null;
        }
    }

    static void writeMatrixInFile(String firstRow, Fractional[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            for (Fractional[] row : matrix) {
                for (Fractional element : row) {
                    writer.write(element.toString());
                    writer.write(" ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    static HashMap<String, Object> readFile(String fileName) throws FileNotFoundException {

        HashMap<String, Object> hashMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] matrixSize = br.readLine().split(" ");
            System.out.println(Arrays.toString(matrixSize));

            int matrixColumns = Integer.parseInt(matrixSize[1]);
            int matrixRows = Integer.parseInt(matrixSize[0]);

            Fractional[][] matrixOfCoef = new Fractional[matrixRows][matrixColumns];

            for (int i = 0; i != matrixRows; i++) {
                String[] vectorOfCoef = br.readLine().split(" ");
                for (int j = 0; j != matrixColumns; j++) {
                    matrixOfCoef[i][j] = Fractional.createFractional(vectorOfCoef[j]);
                }
            }

            String[] baseXtemp = br.readLine().split(" ");

            hashMap.put("baseXtemp", baseXtemp);
            hashMap.put("matrixOfCoef", matrixOfCoef);
            hashMap.put("matrixColumns", matrixColumns);
            hashMap.put("matrixRows", matrixRows);

            return hashMap;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
