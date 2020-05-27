//package parallelpso.PSO;

import java.util.ArrayList;


class Function {
    static ArrayList<Monomial> polinomial = new ArrayList<>();
    /**
     * Evalua una funcion polinomica
     * @param terminos lista de monomios de una funcion polinomica
     * @return      the y component
     */
    static double function (double[] v) {
        double resultado = 0;
        for(int i = 0; i < polinomial.size(); i++) {
            resultado += polinomial.get(i).eval(v[i]);
        }
        return resultado;
    }

    static String functionToString() {
        String polinomialString = "";
        for(int i = 0; i <polinomial.size(); i++) {
            char c = (char) (i + 97);
            polinomialString += polinomial.get(i).convertToString(c) + " ";
        }
        return polinomialString;
    }
}