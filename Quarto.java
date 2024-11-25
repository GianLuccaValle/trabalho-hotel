package hotel;

public class Quarto {
    private int numero;
    private String categoria;
    private double diaria;
    private boolean disponivel;

    public Quarto(int numero, String categoria, double diaria) {
        this.numero = numero;
        this.categoria = categoria;
        this.diaria = diaria;
        this.disponivel = true;
    }

    public int getNumero() {
        return numero;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getDiaria() {
        return diaria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}