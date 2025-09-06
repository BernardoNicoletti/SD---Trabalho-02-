# SD---Trabalho 02 - Sistema de Cliente-Servidor

## Integrantes

* **Bernardo Nicoletti**
* **Guilherme Pedroso**
* **Maria Eduarda Nascimento**

## Instruções de Execução

### 1. Preparação do Servidor

1. Abra o terminal e execute a classe `Main` do servidor com o seguinte comando:

   ```bash
   java Main
   ```

2. O servidor pedirá para informar a **porta** em que ele irá rodar. Por exemplo:

   ```
   Informe a porta para o servidor: 500
   ```

3. Em seguida, será solicitado que informe a **quantidade de clientes** que irão se conectar. Exemplo:

   ```
   Informe o número de clientes que vão se conectar: 2
   ```

### 2. Preparação dos Clientes

1. Para cada cliente, abra um terminal separado e execute o comando:

   ```bash
   java Cliente
   ```

2. O cliente irá pedir a **porta** do servidor, onde você deve informar a mesma porta que foi configurada no servidor (por exemplo, `500`):

   ```
   Informe a porta para o servidor: 500
   ```

3. Após conectar, será exibida a seguinte mensagem de boas-vindas:

   ```
   Bem-vindo! Conectado ao servidor em localhost:500
   ```

4. O cliente então solicitará o **horário local** no formato HH\:mm. Exemplo:

   ```
   Digite a hora local do cliente (formato HH:mm): 10:20
   ```

### 3. Logs

Após a execução, o sistema irá gerar logs para cada cliente e também para o servidor, registrando as informações de conexão e os horários enviados por cada cliente.


