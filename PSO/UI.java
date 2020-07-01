import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UI {
    final int DEFAULT_PARTICLES = 20;
    final int DEFAULT_EPOCH = 30;
    final int DEFAULT_DIMENTIONS = 100000;

    private JFrame main;
    private JPanel buttonsPanel;

    private JPanel particlesPanel;
    private JLabel particlesLabel;
    private JTextField particlesTextField;

    private JPanel epochPanel;
    private JLabel epochLabel;
    private JTextField epochTextField;

    private JPanel dimentionsPanel;
    private JLabel dimentionsLabel;
    private JTextField dimentionsTextField;

    private JPanel inertiaPanel;
    private JLabel inertiaLabel;
    private JTextField inertiaTextField;

    private JPanel socialPanel;
    private JLabel socialLabel;
    private JTextField socialTextField;

    private JPanel cognitivePanel;
    private JLabel cognitiveLabel;
    private JTextField cognitiveTextField;

    private JButton startButton;
    private JLabel secuentialLabel;
    private JLabel concurrentLabel;

    private JPanel statusPanel;
    private JScrollPane secuentialParticlesScrollPane;
    private JPanel secuentialParticlesStatePanel;


    private JScrollPane concurrentParticlesScrollPane;
    private JPanel concurrentParticlesStatePanel;
    public static void main(String[] args) {
        UI ui = new UI();
    }

    public UI() {
        initComponents();
    }

    public void initComponents() {
        particlesTextField = new JTextField(String.valueOf(DEFAULT_PARTICLES));
        particlesTextField.setPreferredSize(new Dimension(250, 30));
        particlesPanel = new JPanel(new FlowLayout());
        particlesLabel = new JLabel("Particulas");
        particlesLabel.setPreferredSize(new Dimension(250, 30));
        particlesPanel.add(particlesLabel);
        particlesPanel.add(particlesTextField);
        
        epochTextField = new JTextField(String.valueOf(DEFAULT_EPOCH));
        epochTextField.setPreferredSize(new Dimension(250, 30));
        epochPanel = new JPanel(new FlowLayout());
        epochLabel = new JLabel("Iteraciones");
        epochLabel.setPreferredSize(new Dimension(250, 30));
        epochPanel.add(epochLabel);
        epochPanel.add(epochTextField);
        
        dimentionsTextField = new JTextField(String.valueOf(DEFAULT_DIMENTIONS));
        dimentionsTextField.setPreferredSize(new Dimension(250, 30));
        dimentionsPanel = new JPanel(new FlowLayout());
        dimentionsLabel = new JLabel("Dimensiones");
        dimentionsLabel.setPreferredSize(new Dimension(250, 30));
        dimentionsPanel.add(dimentionsLabel);
        dimentionsPanel.add(dimentionsTextField);
        
        inertiaTextField = new JTextField(String.valueOf(Swarm.DEFAULT_INERTIA));
        inertiaTextField.setPreferredSize(new Dimension(250, 30));
        inertiaPanel = new JPanel(new FlowLayout());
        inertiaLabel = new JLabel("Inercia");
        inertiaLabel.setPreferredSize(new Dimension(250, 30));
        inertiaPanel.add(inertiaLabel);
        inertiaPanel.add(inertiaTextField);
        
        socialTextField = new JTextField(String.valueOf(Swarm.DEFAULT_SOCIAL));
        socialTextField.setPreferredSize(new Dimension(250, 30));
        socialPanel = new JPanel(new FlowLayout());
        socialLabel = new JLabel("Componente social");
        socialLabel.setPreferredSize(new Dimension(250, 30));
        socialPanel.add(socialLabel);
        socialPanel.add(socialTextField);
        
        cognitiveTextField = new JTextField(String.valueOf(Swarm.DEFAULT_COGNITIVE));
        cognitiveTextField.setPreferredSize(new Dimension(250, 30));
        cognitivePanel = new JPanel(new FlowLayout());
        cognitiveLabel = new JLabel("Componente cognitivo");
        cognitiveLabel.setPreferredSize(new Dimension(250, 30));
        cognitivePanel.add(cognitiveLabel);
        cognitivePanel.add(cognitiveTextField);
        
        startButton = new JButton("Correr");
        startButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                runSwarm();
            }
        });

        
        secuentialParticlesStatePanel = new JPanel(new GridLayout(0, 1));
        secuentialParticlesScrollPane = new JScrollPane(secuentialParticlesStatePanel);
        secuentialLabel = new JLabel("Secuencial: En espera");
        secuentialParticlesStatePanel.add(secuentialLabel);

        concurrentParticlesStatePanel = new JPanel(new GridLayout(0, 1));
        concurrentParticlesScrollPane = new JScrollPane(concurrentParticlesStatePanel);
        concurrentLabel = new JLabel("Concurrente: En espera");
        concurrentParticlesStatePanel.add(concurrentLabel);

        statusPanel = new JPanel( new GridLayout(1,2));
        statusPanel.add(secuentialParticlesScrollPane);
        statusPanel.add(concurrentParticlesScrollPane);
        
        buttonsPanel =  new JPanel(new GridLayout(7,1));
        buttonsPanel.add(particlesPanel);
        buttonsPanel.add(epochPanel);
        buttonsPanel.add(dimentionsPanel);
        buttonsPanel.add(inertiaPanel);
        buttonsPanel.add(socialPanel);
        buttonsPanel.add(cognitivePanel);
        buttonsPanel.add(startButton);

        main = new JFrame("PSO");
        main.setLayout(new GridLayout(2,1));

        main.add(buttonsPanel);
        main.add(statusPanel);

        main.pack();
        main.setVisible(true);
        main.setPreferredSize(new Dimension(800, 800));
		main.setResizable(true);
		main.setLocationRelativeTo(null);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void runSwarm() {
        int particles = Integer.valueOf(particlesTextField.getText());
        int epochs = Integer.valueOf(epochTextField.getText());
        int dimentions = Integer.valueOf(dimentionsTextField.getText());
        double inertia = Double.valueOf(inertiaTextField.getText());
        double cognitive = Double.valueOf(cognitiveTextField.getText());
        double social = Double.valueOf(socialTextField.getText());

        ArrayList<JLabel> secuentialLabels = generateSecuentialLabels(particles);
        ArrayList<JLabel> concurrentLabels = generateConcurrentLabels(particles);
        initFunction(dimentions);
        Swarm swarm = new Swarm(particles, epochs, inertia, cognitive, social, dimentions, secuentialLabels, concurrentLabels, secuentialLabel, concurrentLabel);
        swarm.run();
        swarm.reset();
        swarm.runConcurrent();
    }

    public ArrayList<JLabel> generateSecuentialLabels(int particles) {
        ArrayList<JLabel> labels = new ArrayList<>();
        secuentialParticlesStatePanel.removeAll();
        secuentialParticlesStatePanel.add(secuentialLabel);
        for(int i = 0; i < particles; i++) {
            JLabel label = new JLabel();
            labels.add(label);
            secuentialParticlesStatePanel.add(label);
        }
        secuentialParticlesStatePanel.revalidate();
        return labels;
    }

    public ArrayList<JLabel> generateConcurrentLabels(int particles) {
        ArrayList<JLabel> labels = new ArrayList<>();
        concurrentParticlesStatePanel.removeAll();
        concurrentParticlesStatePanel.add(concurrentLabel);
        for(int i = 0; i < particles; i++) {
            JLabel label = new JLabel("Bloqueado");
            labels.add(label);
            concurrentParticlesStatePanel.add(label);
        }
        concurrentParticlesStatePanel.revalidate();
        return labels;
    }

    private static void initFunction (int dimentions) {
        Function.polinomial = new ArrayList<>();
        for(int i = 0; i < dimentions; i++) {
            Function.polinomial.add(new Monomial());
        }
    }
}