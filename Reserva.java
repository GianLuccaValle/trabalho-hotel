package hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reserva {
    private String cpfCliente;
    private String nomeCliente;
    private int numeroQuarto;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String categoriaQuarto;
    private boolean ativa;
    private double valorTotal;

    public Reserva(String cpfCliente, String nomeCliente, int numeroQuarto,
            LocalDate checkIn, LocalDate checkOut, String categoriaQuarto,
            double diaria) {
        this.cpfCliente = cpfCliente;
        this.nomeCliente = nomeCliente;
        this.numeroQuarto = numeroQuarto;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.categoriaQuarto = categoriaQuarto;
        this.ativa = true;
        this.valorTotal = calcularValorTotal(diaria);
    }

    private double calcularValorTotal(double diaria) {
        long dias = ChronoUnit.DAYS.between(checkIn, checkOut);
        return dias * diaria;
    }

    // Getters e setters
    public String getCpfCliente() {
        return cpfCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public int getNumeroQuarto() {
        return numeroQuarto;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getCategoriaQuarto() {
        return categoriaQuarto;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    @Override
    public String toString() {
        return String.format("Reserva: Cliente=%s, Quarto=%d, Check-in=%s, Check-out=%s, Valor=R$%.2f",
                nomeCliente, numeroQuarto, checkIn, checkOut, valorTotal);
    }
}
