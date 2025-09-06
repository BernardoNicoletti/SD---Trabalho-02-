import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Random;

public class Cliente implements Runnable {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Informe a porta para o servidor: ");
        int porta = scanner.nextInt();
        
        String host = "localhost";
        Socket socket = null;

        try {
            socket = new Socket(host, porta);
            System.out.println("Bem-vindo! Conectado ao servidor em " + host + ":" + porta);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String horaServidorStr = in.readLine();
            LocalTime horaServidor = LocalTime.parse(horaServidorStr);

            LocalTime horaCliente = gerarHoraLocal();

            long diff = getDiffBetweenServerAndClient(horaServidor, horaCliente);
            out.println(diff);

            long ajuste = Long.parseLong(in.readLine());
            LocalTime novaHora = horaCliente.plusSeconds(ajuste);

            out.println(novaHora.toString());

            salvarEmArquivo(horaServidor, horaCliente, diff, ajuste, novaHora);

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void salvarEmArquivo(LocalTime horaServidor, LocalTime horaCliente, long diff, long ajuste, LocalTime novaHora) {
        Random random = new Random();
        int timestamp = random.nextInt(100) + 1;
        String nomeArquivo = "cliente_ID" + String.valueOf(timestamp) + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("Hora do servidor: " + horaServidor);
            writer.println("Hora do cliente: " + horaCliente);
            writer.println("Diferença (s): " + diff);
            writer.println("Ajuste (s): " + ajuste);
            writer.println("Hora final ajustada: " + novaHora);
            System.out.println("[Cliente] Log salvo em: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("[Cliente] Erro ao salvar o log: " + e.getMessage());
        }
    }

    private long getDiffBetweenServerAndClient(LocalTime horaServidor, LocalTime horaCliente) {
        return horaCliente.toSecondOfDay() - horaServidor.toSecondOfDay();
    }

    private LocalTime gerarHoraLocal() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Digite a hora local do cliente (formato HH:mm): ");
                String entrada = reader.readLine();
                return LocalTime.parse(entrada + ":00");
            } catch (Exception e) {
                System.out.println("Formato inválido. Tente novamente (exemplo: 14:30).");
            }
        }
    }

    public static void main(String[] args) {
        new Cliente().run();
    }
}
