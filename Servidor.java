import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Servidor {
    private ServerSocket servidor;
    private LocalTime horaServidor = LocalTime.now();
    private List<Socket> clientes = new ArrayList<>();
    private List<LogCliente> logClientes = new ArrayList<>();

    public Servidor() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe a porta para o servidor: ");
        int porta = scanner.nextInt();
        servidor = new ServerSocket(porta);
        System.out.println("[Servidor] Servidor iniciado na porta " + porta);
    }

    public int getNumeroClientes() {
        return clientes.size();
    }

    public void aceitarClientes() throws IOException {
        Socket cliente = servidor.accept();
        clientes.add(cliente);
        System.out.println("[Servidor] Cliente conectado: " + cliente.getInetAddress().getHostAddress());
    }

    public List<Socket> sincronizarRelogio() throws IOException {
        List<ClienteDiff> diferencas = capturarDiferencaEntreServidorEClientes();
        List<Long> diferencasRelogio = diferencas.stream()
            .map(ClienteDiff::getDiferenca)
            .collect(Collectors.toList());

        long media = calcularMediaDosRelogios(diferencasRelogio);
        ajustarHoraServidor(media);
        enviarAjustesParaOsClientes(diferencas, media);
        salvarLogServidor(diferencas, media);

        return clientes;
    }

    private void ajustarHoraServidor(long media) {
        horaServidor = media >= 0 ? horaServidor.plusSeconds(media) : horaServidor.minusSeconds(-media);
        System.out.println("[Servidor] Hora do servidor ajustada: " + horaServidor);
    }

    private void enviarAjustesParaOsClientes(List<ClienteDiff> diferencas, long media) throws IOException {
        for (ClienteDiff clienteDiff : diferencas) {
            Socket cliente = clienteDiff.getSocket();
            long diff = clienteDiff.getDiferenca();
            long ajuste = Math.negateExact(diff - media);

            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            out.println(ajuste);

            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String novaHoraCliente = in.readLine();

            logClientes.add(new LogCliente(cliente.getInetAddress().toString(), diff, ajuste, novaHoraCliente));
        }
    }

    private long calcularMediaDosRelogios(List<Long> diferencas) {
        long soma = diferencas.stream().reduce(0L, Long::sum);
        return soma / (diferencas.size() + 1); // inclui o servidor
    }

    private List<ClienteDiff> capturarDiferencaEntreServidorEClientes() {
        List<ClienteDiff> diferencas = new ArrayList<>();
        for (Socket cliente : clientes) {
            try {
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

                out.println(horaServidor);
                long diferenca = Long.parseLong(in.readLine());

                diferencas.add(new ClienteDiff(cliente, diferenca));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return diferencas;
    }

    private void salvarLogServidor(List<ClienteDiff> diferencas, long media) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
        String nomeArquivo = "log_servidor_" + timestamp + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("Hora inicial do servidor: " + horaServidor.minusSeconds(media));
            writer.println("Número de clientes conectados: " + clientes.size());
            writer.println("Média das diferenças: " + media + " segundos");
            writer.println();

            int index = 1;
            for (LogCliente log : logClientes) {
                writer.println("Cliente " + index++);
                writer.println("Endereço IP: " + log.enderecoIP);
                writer.println("Diferença enviada pelo cliente: " + log.diferenca + " segundos");
                writer.println("Ajuste enviado: " + log.ajuste + " segundos");
                writer.println("Hora final do cliente: " + log.horaFinalCliente);
                writer.println();
            }

            writer.println("Hora final do servidor: " + horaServidor);
        } catch (IOException e) {
            System.out.println("[Servidor] Erro ao salvar o log: " + e.getMessage());
        }
    }

    public LocalTime getHoraServidor() {
        return horaServidor;
    }

    public ServerSocket getServerSocket() {
        return servidor;
    }

    private static class LogCliente {
        String enderecoIP;
        long diferenca;
        long ajuste;
        String horaFinalCliente;

        public LogCliente(String enderecoIP, long diferenca, long ajuste, String horaFinalCliente) {
            this.enderecoIP = enderecoIP;
            this.diferenca = diferenca;
            this.ajuste = ajuste;
            this.horaFinalCliente = horaFinalCliente;
        }
    }
}
