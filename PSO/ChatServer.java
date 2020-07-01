import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

    public void registrarCliente(String nombre, ChatCliente cliente)
        throws RemoteException, IllegalArgumentException;
    public void olvidarCliente(String nombre)
        throws RemoteException;
    public void enviarMensaje(String nombre, String mensaje, String destinatario)
        throws RemoteException;
}

