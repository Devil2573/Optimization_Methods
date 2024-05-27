package com.example.javafx.optimize_method.model;

import java.util.ArrayList;

public class SharedData {
    private Fractional[][] matrixOfCoef;
    private ArrayList<Integer> basis;
    private ArrayList<Integer> notBasis;
    private Fractional[] coefOfTargetFunction;

    private Integer countOfVariables;
    private Integer countOfLimitations;

    private Boolean simplexTurn;
    private Fractional[] artificalCoefTargetFuntion;

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

    public Integer getCountOfVariables() {
        return countOfVariables;
    }

    public void setCountOfVariables(Integer countOfVariables) {
        this.countOfVariables = countOfVariables;
    }

    public Integer getCountOfLimitations() {
        return countOfLimitations;
    }

    public void setCountOfLimitations(Integer countOfLimitations) {
        this.countOfLimitations = countOfLimitations;
    }

    public Fractional[] getArtificalCoefTargetFuntion() {
        return artificalCoefTargetFuntion;
    }

    public void setArtificalCoefTargetFuntion(Fractional[] artificalCoefTargetFuntion) {
        this.artificalCoefTargetFuntion = artificalCoefTargetFuntion;
    }

    public Boolean getSimplexTurn() {
        return simplexTurn;
    }

    public void setSimplexTurn(Boolean simplexTurn) {
        this.simplexTurn = simplexTurn;
    }
}
