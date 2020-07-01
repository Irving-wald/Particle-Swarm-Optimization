//package parallelpso.PSO;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

//import parallelpso.PSO.Particle.FunctionType;

/**
 * Represents a swarm of particles from the Particle Swarm Optimization
 * algorithm.
 */
public class Swarm {

    private int dimentions;
    private int numOfParticles, epochs;
    private double inertia, cognitiveComponent, socialComponent;
    private Vector bestPosition;
    private double bestEval;
    public static final double DEFAULT_INERTIA = 0.729844;
    public static final double DEFAULT_COGNITIVE = 1.496180; // Cognitive component.
    public static final double DEFAULT_SOCIAL = 1.496180; // Social component.
    ArrayList<Particle> particles;
    public ArrayList<JLabel> secuentialLabels;
    public ArrayList<JLabel> concurrentLabels;
    public JLabel concurrentLabel;
    public JLabel secuentialLabel;

    /**
     * When Particles are created they are given a random position. The random
     * position is selected from a specified range. If the begin range is 0 and the
     * end range is 10 then the value will be between 0 (inclusive) and 10
     * (exclusive).
     */
    private int beginRange, endRange;
    private static final int DEFAULT_BEGIN_RANGE = -100;
    private static final int DEFAULT_END_RANGE = 101;

    /**
     * Construct the Swarm with default values.
     * 
     * @param particles the number of particles to create
     * @param epochs    the number of generations
     */
    public Swarm(int particles, int epochs, int dimentions) {
        this(particles, epochs, DEFAULT_INERTIA, DEFAULT_COGNITIVE, DEFAULT_SOCIAL, dimentions, null, null, null, null);
    }

    /**
     * Construct the Swarm with custom values.
     * 
     * @param particles the number of particles to create
     * @param epochs    the number of generations
     * @param inertia   the particles resistance to change
     * @param cognitive the cognitive component or introversion of the particle
     * @param social    the social component or extroversion of the particle
     */
    public Swarm(int noParticles, int epochs, double inertia, double cognitive, double social, int dimentions, ArrayList<JLabel> secuentialLabels, ArrayList<JLabel> concurrentLabels, JLabel secuentialLabel, JLabel concurrentLabel) {
        this.secuentialLabels = secuentialLabels;
        this.concurrentLabels = concurrentLabels;
        this.concurrentLabel = concurrentLabel;
        this.secuentialLabel = secuentialLabel;
        this.numOfParticles = noParticles;
        this.epochs = epochs;
        this.inertia = inertia;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        this.dimentions = dimentions;
        double infinity = Double.POSITIVE_INFINITY;
        double[] infinityVector = new double[dimentions];
        for (int i = 0; i < dimentions; i++) {
            infinityVector[i] = infinity;
        }
        bestPosition = new Vector(infinityVector);
        bestEval = Double.POSITIVE_INFINITY;
        beginRange = DEFAULT_BEGIN_RANGE;
        endRange = DEFAULT_END_RANGE;

        particles = initialize();
    }

    public void reset() {
        double infinity = Double.POSITIVE_INFINITY;
        double[] infinityVector = new double[dimentions];
        for (int i = 0; i < dimentions; i++) {
            infinityVector[i] = infinity;
        }
        bestPosition = new Vector(infinityVector);
        bestEval = Double.POSITIVE_INFINITY;
    }

    /**
     * Execute the algorithm.
     */
    public void runConcurrent() {
        long startTime = System.nanoTime();
        ArrayList<Particle> cparticles = cloneParticles(concurrentLabels);
        double oldEval = bestEval;
        for (int i = 0; i < epochs; i++) {
            if (bestEval < oldEval) {
                oldEval = bestEval;
            }
            updateLabelText(concurrentLabel,"epoch :" + i + " " + bestEval);
            try {
                updateConcurrentParticlesPersonalBestAndGlobal(cparticles);
                updateParticlesVelocityAndPositions(cparticles);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Final Best Evaluation: " + bestEval);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("runConcurrent time elapsed:   " + timeElapsed );
        updateLabelText(concurrentLabel, "Tiempo concurrente :" + timeElapsed / 1000000 + "mili - Mejor eval:" + bestEval);
    }

    public void updateConcurrentParticlesPersonalBestAndGlobal(ArrayList<Particle> particles) throws InterruptedException, ExecutionException{
        ExecutorService pool = Executors.newFixedThreadPool(particles.size());
        CompletionService<Particle> ecs = new ExecutorCompletionService<Particle>(pool);
        for(Particle particle: particles) {
            ecs.submit(new Runnable() {

                @Override
                public void run() {
                    particle.updatePersonalBest();
                }
                
            }, particle);
        }
        int n = particles.size();
        for (int i = 0; i < n; ++i) {
            Particle p = ecs.take().get();
            if (p != null) {
                updateGlobalBest(p);
            }
        }
        pool.shutdown();
    }

    public void updateParticlesVelocityAndPositions(ArrayList<Particle> particles) throws InterruptedException, ExecutionException{
        ExecutorService pool = Executors.newFixedThreadPool(particles.size());
        CompletionService<Integer> ecs = new ExecutorCompletionService<Integer>(pool);
        for(Particle particle: particles) {
            ecs.submit(new Runnable() {

                @Override
                public void run() {
                    updateVelocity(particle);
                    particle.updatePosition();
                }
                
            }, 0);
        }
        int n = particles.size();
        for (int i = 0; i < n; ++i) {
            Integer r = ecs.take().get();
        }
        pool.shutdown();
    }

    /**
     * Execute the algorithm.
     */
    public void run () {
        long startTime = System.nanoTime();
        ArrayList<Particle> cparticles = cloneParticles(secuentialLabels);
        double oldEval = bestEval;
        for (int i = 0; i < epochs; i++) {
            
            if (bestEval < oldEval) {
                oldEval = bestEval;
            }
            updateLabelText(secuentialLabel,"epoch :" + i + " " + bestEval);
            for (Particle p : cparticles) {
                p.updatePersonalBest();
                updateGlobalBest(p);
            }

            for (Particle p : cparticles) {
                updateVelocity(p);
                p.updatePosition();
            }
        }
        System.out.println("Final Best Evaluation: " + bestEval);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("runSecuential time elapsed:   " + timeElapsed );
        updateLabelText(secuentialLabel, "Tiempo secuencial :" + timeElapsed / 1000000 + "mili - Mejor eval:" + bestEval);
    }

    /**
     * Create a set of particles, each with random starting positions.
     * @return  an array of particles
     */
    private ArrayList<Particle> initialize () {
        ArrayList<Particle> newParticles = new ArrayList<>();
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(beginRange, endRange, dimentions, null);
            newParticles.add(particle);
            updateGlobalBest(particle);
        }
        return newParticles;
    }

    private ArrayList<Particle> cloneParticles(ArrayList<JLabel> labels) {
        ArrayList<Particle> newParticles = new ArrayList<>();
        for(int i = 0; i < particles.size(); i++) {
            newParticles.add(particles.get(i).clone(labels.get(i)));
        }
        return newParticles;
    }

    /**
     * Update the global best solution if a the specified particle has
     * a better solution
     * @param particle  the particle to analyze
     */
    private void updateGlobalBest (Particle particle) {
        if (particle.getBestEval() < bestEval) {
            bestPosition = particle.getBestPosition();
            bestEval = particle.getBestEval();
        }
    }

    /**
     * Update the velocity of a particle using the velocity update formula
     * @param particle  the particle to update
     */
    private void updateVelocity (Particle particle) {
        particle.updateLabelText("Ejecutando");
        Vector oldVelocity = particle.getVelocity();
        Vector pBest = particle.getBestPosition();
        Vector gBest = bestPosition.clone();
        Vector pos = particle.getPosition();

        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        // The first product of the formula.
        Vector newVelocity = oldVelocity.clone();
        newVelocity.mul(inertia);

        // The second product of the formula.
        pBest.sub(pos);
        pBest.mul(cognitiveComponent);
        pBest.mul(r1);
        newVelocity.add(pBest);

        // The third product of the formula.
        gBest.sub(pos);
        gBest.mul(socialComponent);
        gBest.mul(r2);
        newVelocity.add(gBest);

        particle.setVelocity(newVelocity);
        particle.updateLabelText("Finalizado");
    }

    public void updateLabelText(JLabel label, String str) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                label.setText(str);
            }

        });
    }
}