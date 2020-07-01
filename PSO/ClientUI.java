import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.*;
import javax.swing.*;

public class ClientUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        new ClientUI().setVisible(true);
    }

    public ClientUI() {
        initialize();
        //inicializar();
    }

    public static Point derecha(JComponent c, int x) {
        return new Point(c.getX() + c.getWidth() + x, c.getY());
    }

    public static Point debajo(JComponent c, int y) {
        return new Point(c.getX(), c.getY() + c.getHeight() + y);
    }

    public JLabel lblServer;
    public JTextField txtServer;
    public JLabel lblname;
    public JTextField txtname;
    public JButton btnConectar;
    public JLabel lblmessage;
    public JTextField txtmessage;
    public JButton btnEnviar;
    public JLabel lblDestinatario;
    public JTextField txtDestinatario;
    public JTextArea txtmessages;
    ChatClientClass cliente;

    public JPanel controlesPanel;
    public JScrollPane messagesScroll;
    public JPanel serverPanel;
    public JPanel usernamePanel;
    public JPanel messagePanel;
    public JPanel destinatarioPanel;
    private void initialize() {
        controlesPanel = new JPanel(new GridLayout(2,3));

        Dimension dimVentana = new Dimension(800, 600);
        Dimension dimPantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dimVentana);
        setLocation(
            (dimPantalla.width - dimVentana.width) / 2,
            (dimPantalla.height - dimVentana.height) / 2
        );
        setLayout(new GridLayout(2, 1));
        cliente = null;
        btnConectar = new JButton("Conectar");
        btnEnviar = new JButton("Enviar");
        lblServer = new JLabel("IP del servidor");
        txtServer = new JTextField();
        txtServer.setPreferredSize(new Dimension(250, 30));
        lblname = new JLabel("Mi nombre");
        txtname = new JTextField();
        txtname.setPreferredSize(new Dimension(250, 30));
        lblmessage = new JLabel("Mensaje");
        txtmessage = new JTextField();
        txtmessage.setPreferredSize(new Dimension(250, 30));
        lblDestinatario = new JLabel("Destino");
        txtDestinatario = new JTextField();
        txtDestinatario.setPreferredSize(new Dimension(250, 30));

        serverPanel = new JPanel(new FlowLayout());
        usernamePanel = new JPanel(new FlowLayout());
        messagePanel =  new JPanel(new FlowLayout());
        destinatarioPanel =  new JPanel(new FlowLayout());

        serverPanel.add(lblServer);
        serverPanel.add(txtServer);
        usernamePanel.add(lblname);
        usernamePanel.add(txtname);
        messagePanel.add(lblmessage);
        messagePanel.add(txtmessage);
        destinatarioPanel.add(lblDestinatario);
        destinatarioPanel.add(txtDestinatario);

        controlesPanel.add(serverPanel);
        controlesPanel.add(usernamePanel);
        controlesPanel.add(btnConectar);
        controlesPanel.add(messagePanel);
        controlesPanel.add(destinatarioPanel);
        controlesPanel.add(btnEnviar);

        txtmessages = new JTextArea();
        messagesScroll = new JScrollPane(txtmessages);

        add(controlesPanel);
        add(messagesScroll);
        //pack();

        btnConectar.addActionListener(e -> {
            if(cliente == null) try {

                cliente = new ChatClientClass();
                ChatServer server = (ChatServer)Naming.lookup(
                    "//" + txtServer.getText() + "/chat"
                );
                cliente.nombre = txtname.getText();
                cliente.server = server;
                cliente.consola = m -> txtmessages.append(m + '\n');
                server.registrarCliente(cliente.nombre, cliente);
                btnConectar.setEnabled(false);
                btnEnviar.setEnabled(true);
                
            } catch(Exception ex) {
                
                cliente = null;
                txtmessages.setText(ex.getMessage() + '\n');
            }
        });

        btnEnviar.addActionListener(e -> {
            try {
                cliente.server.enviarMensaje(
                    cliente.nombre,
                    txtmessage.getText(),
                    txtDestinatario.getText()
                );
            } catch(RemoteException ex) { }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(cliente != null) try {
                    cliente.server.olvidarCliente(cliente.nombre);
                    UnicastRemoteObject.unexportObject(cliente, true);
                } catch(RemoteException ex) { }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat cliente");
        setResizable(true);
    }

    private JPanel createInput(String txt, JLabel label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout());
        label = new JLabel(txt);
        field = new JTextField();
        field.setPreferredSize(new Dimension(250, 30));
        panel.add(label);
        panel.add(field);

        return panel;
    }
   
}
