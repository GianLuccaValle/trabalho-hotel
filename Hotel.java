package hotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Hotel {
    private static GerenciadorHotel gerenciador;
    private static Scanner scanner;
    private static DateTimeFormatter dateFormatter;

    public static void main(String[] args) {
        gerenciador = new GerenciadorHotel();
        scanner = new Scanner(System.in);
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        while (true) {
            exibirMenu();
            int opcao = scanner.nextInt();
            scanner.nextLine(); 
            
            try {
                processarOpcao(opcao);
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n=== Sistema de Gerenciamento de Hotel ===");
        System.out.println("1. Cadastrar Cliente");
        System.out.println("2. Fazer Reserva");
        System.out.println("3. Cancelar Reserva");
        System.out.println("4. Consultar Quartos Disponíveis");
        System.out.println("5. Gerar Relatório Gerencial");
        System.out.println("6. Visualizar Reservas Canceladas");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                cadastrarCliente();
                break;
            case 2:
                fazerReserva();
                break;
            case 3:
                cancelarReserva();
                break;
            case 4:
                consultarQuartosDisponiveis();
                break;
            case 5:
                gerarRelatorio();
                break;
            case 6:
                visualizarReservasCanceladas();
                break;
            case 0:
                System.out.println("Sistema finalizado.");
                System.exit(0);
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void cadastrarCliente() {
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Cliente cliente = new Cliente(cpf, nome, telefone, email);
        gerenciador.cadastrarCliente(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    private static void fazerReserva() {
        System.out.print("CPF do cliente: ");
        String cpf = scanner.nextLine();
        System.out.print("Número do quarto: ");
        int numeroQuarto = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Data de check-in (dd/MM/yyyy): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        System.out.print("Data de check-out (dd/MM/yyyy): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine(), dateFormatter);

        gerenciador.fazerReserva(cpf, numeroQuarto, checkIn, checkOut);
        System.out.println("Reserva realizada com sucesso!");
    }

    private static void cancelarReserva() {
        System.out.print("CPF do cliente: ");
        String cpf = scanner.nextLine();
        System.out.print("Número do quarto: ");
        int numeroQuarto = scanner.nextInt();
        
        gerenciador.cancelarReserva(cpf, numeroQuarto);
        System.out.println("Reserva cancelada com sucesso!");
    }

    private static void consultarQuartosDisponiveis() {
        System.out.print("Data de check-in (dd/MM/yyyy): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        System.out.print("Data de check-out (dd/MM/yyyy): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        System.out.print("Categoria (ou enter para todas): ");
        String categoria = scanner.nextLine();
        if (categoria.isEmpty()) categoria = null;

        List<Quarto> disponiveis = gerenciador.consultarQuartosDisponiveis(
            checkIn, checkOut, categoria);
        
        System.out.println("\nQuartos disponíveis:");
        for (Quarto quarto : disponiveis) {
            System.out.printf("Quarto %d - %s - R$%.2f/dia\n", 
                quarto.getNumero(), quarto.getCategoria(), quarto.getDiaria());
        }
    }

    private static void gerarRelatorio() {
        System.out.print("Data inicial (dd/MM/yyyy): ");
        LocalDate inicio = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        System.out.print("Data final (dd/MM/yyyy): ");
        LocalDate fim = LocalDate.parse(scanner.nextLine(), dateFormatter);

        Map<String, Object> relatorio = gerenciador.gerarRelatorioGerencial(inicio, fim);
        
        System.out.println("\n=== Relatório Gerencial ===");
        System.out.printf("Taxa de Ocupação: %.2f%%\n", 
            (double) relatorio.get("ocupacaoMedia"));
        System.out.println("Reservas por Categoria: " + 
            relatorio.get("reservasPorCategoria"));
        System.out.println("Quarto Mais Reservado: " + 
            relatorio.get("quartoMaisReservado"));
        System.out.println("Quarto Menos Reservado: " + 
            relatorio.get("quartoMenosReservado"));
        System.out.println("Número de Cancelamentos: " + 
            relatorio.get("cancelamentos"));
        System.out.printf("Valor Total: R$%.2f\n", 
            (double) relatorio.get("valorTotal"));
    }

    private static void visualizarReservasCanceladas() {
        System.out.println("\n=== Reservas Canceladas ===");
        for (Reserva reserva : gerenciador.getReservasCanceladas()) {
            System.out.println(reserva);
        }
    }
}