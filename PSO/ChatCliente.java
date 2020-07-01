import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatCliente extends Remote {

    public void ping() throws RemoteException;
    public void enviarMensaje(String nombre, String mensaje) throws RemoteException;
}

