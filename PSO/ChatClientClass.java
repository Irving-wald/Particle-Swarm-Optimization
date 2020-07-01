import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.function.Consumer;

public class ChatClientClass extends UnicastRemoteObject implements ChatCliente {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String nombre;
    public ChatServer server;
    public Consumer<String> consola;



    @Override
    public void ping() throws RemoteException { }

    @Override
    public void enviarMensaje(String nombre, String mensaje) throws RemoteException {
        String salida = nombre + ": " + mensaje;
        if(consola != null) consola.accept(salida);
        else System.out.println(salida);
    }



    public ChatClientClass() throws RemoteException { }

    public void loopCliente() {

        Scanner teclado = new Scanner(System.in);
        try {
            while(true) {

                String mensaje = teclado.nextLine();
                if(mensaje.equals("/leave")) break;
                server.enviarMensaje(nombre, mensaje, null);
            }
        } catch(RemoteException ex) {

        }
        teclado.close();
    }

    public static void main(String[] args) {
        
        try {
            
            ChatClientClass cliente = new ChatClientClass();
            ChatServer server = (ChatServer)Naming.lookup("//" + args[0] + "/chat");
            cliente.nombre = args[1];
            cliente.server = server;
            server.registrarCliente(cliente.nombre, cliente);
            System.out.println("Estás conectado como " + cliente.nombre);
            System.out.println("Escribe un mensaje y envíalo a los demás con <Enter>...");
            cliente.loopCliente();
            server.olvidarCliente(cliente.nombre);
            UnicastRemoteObject.unexportObject(cliente, true);
            System.out.println("Adiós!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
