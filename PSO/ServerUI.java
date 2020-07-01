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

public class ServerUI extends JFrame {

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat server");
        Dimension dimVentana = new Dimension(500, 300);
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
        

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable( false );
        consoleScrollPane = new JScrollPane(consoleTextArea);

        getContentPane().setLayout( new BorderLayout());
        add(controlsPanel, BorderLayout.NORTH);
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
    /* JLabel lblPuerto;
    JTextField txtPuerto;
    JButton btnConectar;
    JLabel lblConsola;
    JTextArea txtConsola;
    ChatServerClass server;
    Process rmiregistry;
    Thread hiloServer;
    
    private void inicializar() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Consola del servidor de chat");
        setResizable(false);
        Dimension dimVentana = new Dimension(800, 600);
        Dimension dimPantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dimVentana);
        setLocation(
            (dimPantalla.width - dimVentana.width) / 2,
            (dimPantalla.height - dimVentana.height) / 2
        );

        lblPuerto = new JLabel("Puerto:");
        Point posLblPuerto = new Point(10, 10);
        lblPuerto.setLocation(posLblPuerto);
        lblPuerto.setSize(lblPuerto.getPreferredSize());

        txtPuerto = new JTextField("1234");
        Point posTxtPuerto = nextTo(lblPuerto, 20);
        txtPuerto.setLocation(posTxtPuerto);
        txtPuerto.setSize(80, 20);

        btnConectar = new JButton("Encender");
        Point posBtnConectar = nextTo(txtPuerto, 50);
        btnConectar.setLocation(posBtnConectar);
        btnConectar.setSize(btnConectar.getPreferredSize());

        lblConsola = new JLabel("Consola:");
        Point posLblConsola = below(lblPuerto, 50);
        lblConsola.setLocation(posLblConsola);
        lblConsola.setSize(lblConsola.getPreferredSize());

        txtConsola = new JTextArea();
        Point posTxtConsola = below(lblConsola, 20);
        txtConsola.setLocation(posTxtConsola);
        txtConsola.setSize(770, 450);


        server = null;
        rmiregistry = null;
        hiloServer = null;
        btnConectar.addActionListener(e -> {
            if(server == null) try {

                server = new ChatServerClass();
                server.consola = m -> txtConsola.append(m + '\n');
                rmiregistry = Runtime.getRuntime().exec("rmiregistry " + txtPuerto.getText());
                Thread.sleep(1000);
                String dirServer = "//"
                    + InetAddress.getLocalHost().getHostAddress()
                    + ":" + txtPuerto.getText() + "/chat";
                hiloServer = new Thread(server::loopServer);
                hiloServer.start();
                Naming.rebind(dirServer, server);
                txtConsola.setText("Server iniciado en: " + dirServer + '\n');
                btnConectar.setEnabled(false);
                
            } catch(Exception ex) {
                
                server = null;
                if(rmiregistry != null && rmiregistry.isAlive()) rmiregistry.destroy();
                rmiregistry = null;
                if(hiloServer != null) hiloServer.interrupt();
                hiloServer = null;
                txtConsola.setText(ex.getMessage() + '\n');
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(rmiregistry != null && rmiregistry.isAlive()) rmiregistry.destroy();
            }
        });


        setLayout(null);
        add(lblPuerto);
        add(txtPuerto);
        add(btnConectar);
        add(lblConsola);
        add(txtConsola);
    } */
}
