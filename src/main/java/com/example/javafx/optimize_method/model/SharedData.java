package com.example.javafx.optimize_method.model;

import java.util.ArrayList;

public class SharedData {
    private Fractional[][] matrixOfCoef;
    private ArrayList<Integer> basis;
    private ArrayList<Integer> notBasis;
    private Fractional[] coefOfTargetFunction;

    public Fractional[][] getMatrixOfCoef() {
        return matrixOfCoef;
    }

    public void setMatrixOfCoef(Fractional[][] matrixOfCoef) {
        this.matrixOfCoef = matrixOfCoef;
    }

    public Fractional[] getCoefOfTargetFunction() {
        return coefOfTargetFunction;
    }

    public void setCoefOfTargetFunction(Fractional[] coefs) {
        this.coefOfTargetFunction = coefs;
    }

    public ArrayList<Integer> getBasis() {
        return basis;
    }

    public void setBasis(ArrayList<Integer> newBasis) {
        this.basis = newBasis;
    }

    public ArrayList<Integer> getNotBasis() {
        return notBasis;
    }

    public void setNotBasis(ArrayList<Integer> newNotBasis) {
        this.notBasis = newNotBasis;
    }
}
