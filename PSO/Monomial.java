//package parallelpso.PSO;

import java.util.Random;

public class Monomial {
    public double coefficient;
    public double exponent;

    Monomial() {
        coefficient = rand(-20, 20);
        exponent = rand(1, 5);
    }

    Monomial(double coefficient, double exponent ){
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public double eval(double x) {
        return Math.pow(coefficient * x, exponent);
    }

    private static int rand (int beginRange, int endRange) {
        Random r = new java.util.Random();
        return r.nextInt(endRange - beginRange) + beginRange;
    }

    public String convertToString(char c) {
        String str = coefficient >= 0 ? "+" : "";
        return str + coefficient + c + "^" + exponent;
    }
}