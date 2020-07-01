import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.net.InetAddress;
import java.rmi.Naming;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ServerUI extends JFrame {
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
    

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public static void main(String[] args) {
        new ServerUI().setVisible(true);
    }

    public ServerUI() {
        initializeUI();
    }

    public static Point nextTo(JComponent c, int x) {
        return new Point(c.getX() + c.getWidth() + x, c.getY());
    }

    public static Point below(JComponent c, int y) {
        return new Point(c.getX(), c.getY() + c.getHeight() + y);
    }

    JPanel controlsPanel;
    JLabel portLabel;
    JTextField portField;
    JButton connectButton;
    JTextArea consoleTextArea;
    JScrollPane consoleScrollPane;
    ChatServerClass server;
    Process rmiregistry;
    Thread hiloServer;

    private void initializeUI() {
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

        startButton.addActionListener(e -> {
            runSwarm();
        });
        
        buttonsPanel =  new JPanel(new GridLayout(8,1));
        buttonsPanel.add(particlesPanel);
        buttonsPanel.add(epochPanel);
        buttonsPanel.add(dimentionsPanel);
        buttonsPanel.add(inertiaPanel);
        buttonsPanel.add(socialPanel);
        buttonsPanel.add(cognitivePanel);
        buttonsPanel.add(startButton);

        secuentialParticlesStatePanel = new JPanel(new GridLayout(0, 1));
        concurrentParticlesStatePanel = new JPanel(new GridLayout(0, 1));
        /////
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat server");
        Dimension dimVentana = new Dimension(800, 1000);
        Dimension dimPantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dimVentana);
        setLocation(
            (dimPantalla.width - dimVentana.width) / 2,
            (dimPantalla.height - dimVentana.height) / 2
        );
        server = null;
        rmiregistry = null;
        hiloServer = null;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(rmiregistry != null && rmiregistry.isAlive()) rmiregistry.destroy();
            }
        });

        portLabel = new JLabel("Puerto :");
        portField = new JTextField("1234");
        connectButton = new JButton("Arrancar");
        connectButton.addActionListener(e -> {
            initChatServer();
        });

        controlsPanel = new JPanel();
        controlsPanel.setLayout( new GridLayout(1, 3) );
        controlsPanel.add(portLabel);
        controlsPanel.add(portField);
        controlsPanel.add(connectButton);
        buttonsPanel.add(controlsPanel);

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable( false );
        consoleScrollPane = new JScrollPane(consoleTextArea);

        getContentPane().setLayout( new BorderLayout());
        add(buttonsPanel, BorderLayout.NORTH);
        add(consoleScrollPane, BorderLayout.CENTER);
        setVisible(true);
        setResizable(true);
    }

    private void initChatServer() {
        if(server == null) try {

            server = new ChatServerClass();
            server.consola = m -> consoleTextArea.append(m + '\n');
            rmiregistry = Runtime.getRuntime().exec("rmiregistry " + portField.getText());
            Thread.sleep(1000);
            String dirServer = "//"
                + InetAddress.getLocalHost().getHostAddress()
                + ":" + portField.getText() + "/chat";
            hiloServer = new Thread(server::loopServer);
            hiloServer.start();
            Naming.rebind(dirServer, server);
            consoleTextArea.setText("Corriendo servidor en: " + dirServer + '\n');
            connectButton.setEnabled(false);
            
        } catch(Exception ex) {
            
            server = null;
            if(rmiregistry != null && rmiregistry.isAlive()) rmiregistry.destroy();
            rmiregistry = null;
            if(hiloServer != null) hiloServer.interrupt();
            hiloServer = null;
            consoleTextArea.setText(ex.getMessage() + '\n');
        }
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
