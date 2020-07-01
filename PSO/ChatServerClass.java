import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChatServerClass extends UnicastRemoteObject implements ChatServer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public HashMap<String, ChatCliente> clientes;
    public Consumer<String> consola;



    @Override
    public void registrarCliente(String nombre, ChatCliente cliente) throws RemoteException {
        if(clientes.containsKey(nombre))
            throw new IllegalArgumentException("Usuario ya registrado");
        synchronized(clientes) {
            clientes.put(nombre, cliente);
        }
        String mensaje = "Se ha conectado " + nombre;
        if(consola != null) consola.accept(mensaje);
        else System.out.println(mensaje);
    }

    @Override
    public void olvidarCliente(String nombre) throws RemoteException {
        synchronized(clientes) {
            clientes.remove(nombre);
        }
        String mensaje = nombre + " se ha desconectado ";
        if(consola != null) consola.accept(mensaje);
        else System.out.println(mensaje);
    }

    @Override
    public void enviarMensaje(String nombre, String mensaje, String destinatario) throws RemoteException {
        synchronized(clientes) {
            clientes.forEach((n, c) -> {
                if(destinatario == null || destinatario.isEmpty() || destinatario.equals(n)) try {
                    c.enviarMensaje(nombre, mensaje);
                } catch(RemoteException ex) {
                    String mensajeC = "No se ha alcanzado a " + nombre + ", tal vez murió.";
                    if(consola != null) consola.accept(mensajeC);
                    else System.out.println(mensajeC);
                }
            });
        }
    }



    public ChatServerClass() throws RemoteException {
        clientes = new HashMap<>();
    }

    public void loopServer() {

        HashMap<String, ChatCliente> muertos = new HashMap<>();
        while(true) {
            try {

                Thread.sleep(2000);
                for(Map.Entry<String, ChatCliente> cliente : clientes.entrySet()) {
                    try {
                        cliente.getValue().ping();
                    } catch(RemoteException ex) {
                        muertos.put(cliente.getKey(), cliente.getValue());
                    }
                }
                synchronized(clientes) {
                    muertos.forEach((n, m) -> {
                        clientes.remove(n);
                        String mensaje = n + " ha muerto.";
                        if(consola != null) consola.accept(mensaje);
                        else System.out.println(mensaje);
                    });
                }
                muertos.clear();
                
            } catch (InterruptedException e) { }
        }
    }



    public static void main(String[] args) {
        try {

            ChatServerClass server = new ChatServerClass();
            String dirServer = "//"
                + InetAddress.getLocalHost().getHostAddress()
                + ":" + args[0] + "/chat";
            new Thread(server::loopServer).start();
            Naming.rebind(dirServer, server);
            System.out.println("Server iniciado en: " + dirServer);

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

