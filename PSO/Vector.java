//package parallelpso.PSO;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Can represent a position as well as a velocity.
 */
class Vector {

    private double[] vector;
    private double limit = Double.MAX_VALUE;

    Vector (int dimentions) {
        vector = new double[dimentions];
    }

    Vector (double[] other) {
        vector = other.clone();
    }

    double[] getVector() {
        return vector;
    }

    double getElement (int i) {
        return vector[i];
    }

    void set (double[] other) {
        vector = other.clone();
    }

    private void set (double x, int i) {
        vector[i] = x;
    }

    void add (Vector v) {
        for(int i = 0; i < vector.length; i++) {
            vector[i] += v.getVector()[i];
        }
        limit();
    }

    void sub (Vector v) {
        for(int i = 0; i < vector.length; i++) {
            vector[i] -= v.getVector()[i];
        }
        limit();
    }

    void mul (double s) {
        for(int i = 0; i < vector.length; i++) {
            vector[i] *= s;
        }
        limit();
    }

    void div (double s) {
        for(int i = 0; i < vector.length; i++) {
            vector[i] /= s;
        }
        limit();
    }

    /* void normalize () {
        double m = mag();
        if (m > 0) {
            x /= m;
            y /= m;
            z /= m;
        }
    } */

    private double mag () {
        double result = 0;
        for(int i = 0; i < vector.length - 1; i++) {
            result += vector[i] * vector[i];
        }
        return Math.sqrt(result);
    }

    void limit (double l) {
        limit = l;
        limit();
    }

    private void limit () {
        double m = mag();
        if (m > limit) {
            double ratio = m / limit;
            for(int i = 0; i < vector.length - 1; i++) {
                vector[i] /= ratio;
            }
        }
    }

    public Vector clone () {
        return new Vector(vector);
    }

    public String toString () {
        String str = "(";
        for(int i = 0; i < vector.length; i++) {
            str += vector[i] + (i < vector.length - 1 ? ", " : "");
        }
        return str += ")";
    }

}
