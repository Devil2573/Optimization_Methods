package com.example.javafx.optimize_method.model;


public class Fractional {
    private long denominator;
    private long numerator;

    public Fractional(long numerator, long denominator) {
        this.denominator = denominator;
        this.numerator = numerator;
    }

    public Fractional() {

    }

    static void commonDenominator(Fractional fractionalFirst, Fractional fractionalSecond) {
        if (fractionalFirst.getDenominator() != fractionalSecond.getDenominator()) {

            long firstDenominator = fractionalFirst.getDenominator();
            long secondDenominator = fractionalSecond.getDenominator();

            long lcm = findLCM(firstDenominator, secondDenominator);

            if (lcm != 0) {
                fractionalFirst.setDenominator(lcm);
                fractionalFirst.setNumerator(fractionalFirst.getNumerator() * (lcm / firstDenominator));

                fractionalSecond.setDenominator(lcm);
                fractionalSecond.setNumerator(fractionalSecond.getNumerator() * (lcm / secondDenominator));
            } else {
                fractionalFirst.setDenominator(firstDenominator * secondDenominator);
                fractionalFirst.setNumerator(fractionalFirst.getNumerator() * secondDenominator);

                fractionalSecond.setDenominator(secondDenominator * firstDenominator);
                fractionalSecond.setNumerator(fractionalSecond.getNumerator() * firstDenominator);

            }
        }
    }

    static long findLCM(long a, long b) {
        return Math.abs(a * b) / findGCD(a, b);
    }

    static long findGCD(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public static Fractional sum(Fractional fractionalFirst, Fractional fractionalSecond) {
        Fractional.commonDenominator(fractionalFirst, fractionalSecond);
        return new Fractional(
                fractionalFirst.getNumerator() + fractionalSecond.getNumerator(),
                fractionalSecond.getDenominator()
        );
    }

    public static Fractional multiplication(Fractional fractionalFirst, Fractional fractionalSecond) {

        return new Fractional(
                fractionalFirst.getNumerator() * fractionalSecond.getNumerator(),
                fractionalFirst.getDenominator() * fractionalSecond.getDenominator()
        );
    }

    public static Fractional subtraction(Fractional fractionalFirst, Fractional fractionalSecond) {

        Fractional.commonDenominator(fractionalFirst, fractionalSecond);
        return new Fractional(
                fractionalFirst.getNumerator() - fractionalSecond.getNumerator(),
                fractionalSecond.getDenominator()
        );
    }

    public static Fractional division(Fractional fractionalFirst, Fractional fractionalSecond) {

        return new Fractional(
                fractionalFirst.getNumerator() * fractionalSecond.getDenominator(),
                fractionalFirst.getDenominator() * fractionalSecond.getNumerator()
        );
    }

    static boolean isNumber(String number) {
        try {
            int a = Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Fractional createFractional(String number) {
        if (number.contains("/")) {
            String[] fractionalSplit = number.split("/");
            if (isNumber(String.valueOf(fractionalSplit[0]))
                    && isNumber(String.valueOf(fractionalSplit[1])) && !fractionalSplit[1].equals("0")) {
                Fractional fractional = new Fractional();
                fractional.setNumerator(Integer.parseInt(fractionalSplit[0]));
                fractional.setDenominator(Integer.parseInt(fractionalSplit[1]));
                return fractional;
            } else {
                System.out.printf("%s не является числом\n", number);
                return null;
            }
        } else {

            if (isNumber(number)) {
                Fractional fractional = new Fractional();

                fractional.setNumerator(Integer.parseInt(number));
                fractional.setDenominator(1);
                return fractional;
            } else {
                System.out.printf("%s не является числом\n", number);
                return null;
            }
        }
    }

    public static boolean isBigger(Fractional num1, Fractional num2) {
        Fractional.commonDenominator(num1, num2);
        if (num1.getNumerator() > num2.getNumerator()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEquals(Fractional num1, Fractional num2) {
        Fractional.commonDenominator(num1, num2);
        if (num1.getNumerator() == num2.getNumerator()) {
            return true;
        } else {
            return false;
        }
    }

    public long getDenominator() {
        return denominator;
    }

    public void setDenominator(long denominator) {
        this.denominator = denominator;
    }

    public long getNumerator() {
        return numerator;
    }

    public void setNumerator(long numerator) {
        this.numerator = numerator;
    }

    private void simplify() {

        long gcd = findGCD(numerator, denominator);
        if (gcd != 0) {
            this.numerator = numerator / gcd;
            this.denominator = denominator / gcd;
        }
    }

    @Override
    public String toString() {
        this.simplify();
        if (denominator != 0 && numerator % denominator == 0) {
            return String.valueOf(numerator / denominator);
        } else {
            return numerator + "/" + denominator;
        }

    }

    public boolean isPositive() {
        if ((this.numerator > 0 && this.denominator > 0) || (this.numerator < 0 && this.denominator < 0)) {
            return true;
        }
        return false;
    }

    public boolean isOne() {
        if (this.numerator == this.denominator) {
            return true;
        }
        return false;
    }


    public static Fractional[][] deepCopyMatrix(Fractional[][] original) {
        if (original == null) {
            return null;
        }

        final Fractional[][] result = new Fractional[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = new Fractional[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                result[i][j] = new Fractional(original[i][j].getNumerator(), original[i][j].getDenominator());
            }
        }
        return result;
    }
}
