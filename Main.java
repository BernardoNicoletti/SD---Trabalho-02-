import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Servidor servidor = new Servidor();

        int numeroClientes = askNumeroDeClientes(scanner);
        System.out.println("[Servidor] Aguardando conexões de " + numeroClientes + " cliente(s)...");

        while (servidor.getNumeroClientes() < numeroClientes) {
            servidor.aceitarClientes(); // aceita um cliente por vez
        }

        List<Socket> clientes = servidor.sincronizarRelogio();
        System.out.println("[Servidor] Hora final do servidor: " + servidor.getHoraServidor());

        for (Socket cliente : clientes) {
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            System.out.println("[Servidor] Hora final do cliente: " + in.readLine());
            cliente.close();
        }

        servidor.getServerSocket().close();
        scanner.close();
    }

    private static int askNumeroDeClientes(Scanner scanner) {
        System.out.println("Informe o número de clientes que vão se conectar:");
        return scanner.nextInt();
    }
}
