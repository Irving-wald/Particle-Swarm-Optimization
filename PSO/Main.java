//package parallelpso.PSO;

import java.util.Scanner;

public class Main {

    public static void main (String[] args) {
        if (args.length == 1 && args[0].equals("-p")) {
            menu(true);
        } else {
            System.out.print("Use the parameter '-p' to change the inertia, ");
            System.out.println("cognitive and social components.");
            System.out.println("Otherwise the default values will be: ");
            System.out.println("Inertia:             " + Swarm.DEFAULT_INERTIA);
            System.out.println("Cognitive Component: " + Swarm.DEFAULT_COGNITIVE);
            System.out.println("Social Component:    " + Swarm.DEFAULT_SOCIAL);
            menu(false);
        }
    }

    private static void menu (boolean flag) {
        Swarm swarm;
        int particles, epochs;
        double inertia, cognitive, social;
        int dimentions;
        particles =     getUserInt("Particles: ");
        epochs =        getUserInt("Epochs:    ");
        dimentions =    getUserInt("Dimentions:");
        initFunction(dimentions);

        if (flag) {
            inertia = getUserDouble("Inertia:   ");
            cognitive = getUserDouble("Cognitive: ");
            social = getUserDouble("Social:    ");
            swarm = new Swarm(particles, epochs, inertia, cognitive, social, dimentions);
        } else {
            swarm = new Swarm(particles, epochs, dimentions);

        }

        swarm.run();
        swarm.reset();
        swarm.runConcurrent();
    }

    private static void initFunction (int dimentions) {
        for(int i = 0; i < dimentions; i++) {
            Function.polinomial.add(new Monomial());
        }
    }

    private static int getUserInt (String msg) {
        int input;
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(msg);

            if (sc.hasNextInt()) {
                input = sc.nextInt();

                if (input <= 0) {
                    System.out.println("Number must be positive.");
                } else {
                    break;
                }

            } else {
                System.out.println("Invalid input.");
            }
        }
        return input;
    }

    private static double getUserDouble (String msg) {
        double input;
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(msg);

            if (sc.hasNextDouble()) {
                input = sc.nextDouble();

                if (input <= 0) {
                    System.out.println("Number must be positive.");
                } else {
                    break;
                }

            } else {
                System.out.println("Invalid input.");
            }
        }
        return input;
    }

}
