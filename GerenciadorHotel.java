package hotel;

import java.time.LocalDate;
import java.util.*;

public class GerenciadorHotel {
    private ArvoreRubroNegra reservasAtivas;
    private List<Reserva> reservasCanceladas;
    private Map<String, Cliente> clientes;
    private Map<Integer, Quarto> quartos;
    private static final double TAXA_OCUPACAO_ALERTA = 0.9; // 90%

    public GerenciadorHotel() {
        reservasAtivas = new ArvoreRubroNegra();
        reservasCanceladas = new ArrayList<>();
        clientes = new HashMap<>();
        quartos = new HashMap<>();
        inicializarQuartos();
    }

    private void inicializarQuartos() {
        // Inicializa alguns quartos de exemplo
        quartos.put(100, new Quarto(100, "Econômico", 200.0));
        quartos.put(101, new Quarto(101, "Econômico", 200.0));
        quartos.put(102, new Quarto(102, "Econômico", 200.0));
        quartos.put(103, new Quarto(103, "Econômico", 200.0));
        quartos.put(104, new Quarto(104, "Econômico", 200.0));
        quartos.put(105, new Quarto(105, "Luxo", 500.0));
        quartos.put(106, new Quarto(106, "Luxo", 500.0));
        quartos.put(107, new Quarto(107, "Luxo", 500.0));
    }

    public void cadastrarCliente(Cliente cliente) {
        if (clientes.containsKey(cliente.getCpf())) {
            throw new RuntimeException("Cliente já cadastrado");
        }
        clientes.put(cliente.getCpf(), cliente);
    }

    public void fazerReserva(String cpf, int numeroQuarto, LocalDate checkIn, LocalDate checkOut) {
        Cliente cliente = clientes.get(cpf);
        if (cliente == null) {
            throw new RuntimeException("Cliente não cadastrado");
        }

        Quarto quarto = quartos.get(numeroQuarto);
        if (quarto == null) {
            throw new RuntimeException("Quarto não existe");
        }

        Reserva novaReserva = new Reserva(cpf, cliente.getNome(), numeroQuarto,
                checkIn, checkOut, quarto.getCategoria(),
                quarto.getDiaria());

        try {
            reservasAtivas.inserirReserva(novaReserva);
            verificarTaxaOcupacao();
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao fazer reserva: " + e.getMessage());
        }
    }

    private void verificarTaxaOcupacao() {
        List<Reserva> reservasAtuais = reservasAtivas.listarReservasPorPeriodo(
                LocalDate.now(), LocalDate.now().plusDays(30));

        int quartosOcupados = reservasAtuais.size();
        double taxaOcupacao = (double) quartosOcupados / quartos.size();

        if (taxaOcupacao >= TAXA_OCUPACAO_ALERTA) {
            System.out.println("ALERTA: Taxa de ocupação atingiu " +
                    (taxaOcupacao * 100) + "%");
        }
    }

    public void cancelarReserva(String cpf, int numeroQuarto) {
        List<Reserva> reservasCliente = reservasAtivas.buscarReservasPorCpf(cpf);
        for (Reserva reserva : reservasCliente) {
            if (reserva.getNumeroQuarto() == numeroQuarto && reserva.isAtiva()) {
                reserva.setAtiva(false);
                reservasCanceladas.add(reserva);
                return;
            }
        }
        throw new RuntimeException("Reserva não encontrada");
    }

    public List<Reserva> getReservasCanceladas() {
        return reservasCanceladas;
    }

    public List<Quarto> consultarQuartosDisponiveis(LocalDate checkIn,
            LocalDate checkOut,
            String categoria) {
        List<Quarto> quartosDisponiveis = new ArrayList<>();
        List<Reserva> reservasPeriodo = reservasAtivas.listarReservasPorPeriodo(checkIn, checkOut);

        Set<Integer> quartosOcupados = new HashSet<>();
        for (Reserva r : reservasPeriodo) {
            if (r.isAtiva()) {
                quartosOcupados.add(r.getNumeroQuarto());
            }
        }

        for (Quarto quarto : quartos.values()) {
            if (!quartosOcupados.contains(quarto.getNumero()) &&
                    (categoria == null || quarto.getCategoria().equals(categoria))) {
                quartosDisponiveis.add(quarto);
            }
        }

        return quartosDisponiveis;
    }

    public Map<String, Object> gerarRelatorioGerencial(LocalDate inicio, LocalDate fim) {
        Map<String, Object> relatorio = new HashMap<>();
        List<Reserva> reservasPeriodo = reservasAtivas.listarReservasPorPeriodo(inicio, fim);

        // Taxa de ocupação
        int diasPeriodo = (int) inicio.until(fim).getDays();
        double ocupacaoMedia = (double) reservasPeriodo.size() /
                (quartos.size() * diasPeriodo) * 100;

        // Contagem de reservas por categoria
        Map<String, Integer> reservasPorCategoria = new HashMap<>();
        Map<Integer, Integer> reservasPorQuarto = new HashMap<>();
        double valorTotal = 0;

        for (Reserva r : reservasPeriodo) {
            reservasPorCategoria.merge(r.getCategoriaQuarto(), 1, Integer::sum);
            reservasPorQuarto.merge(r.getNumeroQuarto(), 1, Integer::sum);
            valorTotal += r.getValorTotal();
        }

        // Quartos mais e menos reservados
        int maxReservas = 0, minReservas = Integer.MAX_VALUE;
        int quartoMaisReservado = 0, quartoMenosReservado = 0;

        for (Map.Entry<Integer, Integer> entry : reservasPorQuarto.entrySet()) {
            if (entry.getValue() > maxReservas) {
                maxReservas = entry.getValue();
                quartoMaisReservado = entry.getKey();
            }
            if (entry.getValue() < minReservas) {
                minReservas = entry.getValue();
                quartoMenosReservado = entry.getKey();
            }
        }

        // Número de cancelamentos
        long cancelamentosPeriodo = reservasCanceladas.stream()
                .filter(r -> !r.getCheckIn().isBefore(inicio) &&
                        !r.getCheckIn().isAfter(fim))
                .count();

        relatorio.put("ocupacaoMedia", ocupacaoMedia);
        relatorio.put("reservasPorCategoria", reservasPorCategoria);
        relatorio.put("quartoMaisReservado", quartoMaisReservado);
        relatorio.put("quartoMenosReservado", quartoMenosReservado);
        relatorio.put("cancelamentos", cancelamentosPeriodo);
        relatorio.put("valorTotal", valorTotal);

        return relatorio;
    }
}