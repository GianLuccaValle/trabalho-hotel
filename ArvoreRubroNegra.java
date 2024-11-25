package hotel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArvoreRubroNegra {
    private enum Cor {
        VERMELHO,
        PRETO
    }

    private class Nodo {
        Reserva reserva;
        Cor cor;
        Nodo esquerdo, direito, pai;

        public Nodo(Reserva reserva) {
            this.reserva = reserva;
            this.cor = Cor.VERMELHO;
            esquerdo = direito = pai = null;
        }
    }

    private Nodo raiz;

    public ArvoreRubroNegra() {
        raiz = null;
    }

    public void inserirReserva(Reserva reserva) {
        if (!verificarConflito(reserva)) {
            Nodo novoNodo = new Nodo(reserva);
            raiz = inserirNodo(raiz, novoNodo);
            corrigirInsercao(novoNodo);
        } else {
            throw new RuntimeException("Conflito de reserva detectado para o quarto " +
                    reserva.getNumeroQuarto());
        }
    }

    private Nodo inserirNodo(Nodo atual, Nodo novoNodo) {
        if (atual == null) {
            return novoNodo;
        }

        if (compararReservas(novoNodo.reserva, atual.reserva) < 0) {
            atual.esquerdo = inserirNodo(atual.esquerdo, novoNodo);
            atual.esquerdo.pai = atual;
        } else if (compararReservas(novoNodo.reserva, atual.reserva) > 0) {
            atual.direito = inserirNodo(atual.direito, novoNodo);
            atual.direito.pai = atual;
        }

        return atual;
    }

    private int compararReservas(Reserva r1, Reserva r2) {
        int compareData = r1.getCheckIn().compareTo(r2.getCheckIn());
        if (compareData != 0)
            return compareData;

        return r1.getCpfCliente().compareTo(r2.getCpfCliente());
    }

    private boolean verificarConflito(Reserva novaReserva) {
        return verificarConflitoRecursivo(raiz, novaReserva);
    }

    private boolean verificarConflitoRecursivo(Nodo nodo, Reserva novaReserva) {
        if (nodo == null)
            return false;

        Reserva reservaAtual = nodo.reserva;

        if (reservaAtual.getNumeroQuarto() == novaReserva.getNumeroQuarto() &&
                reservaAtual.isAtiva()) {

            boolean conflito = !(novaReserva.getCheckOut().isBefore(reservaAtual.getCheckIn()) ||
                    novaReserva.getCheckIn().isAfter(reservaAtual.getCheckOut()));

            if (conflito)
                return true;
        }

        return verificarConflitoRecursivo(nodo.esquerdo, novaReserva) ||
                verificarConflitoRecursivo(nodo.direito, novaReserva);
    }

    private void corrigirInsercao(Nodo nodo) {
        while (nodo != raiz && nodo.pai != null && nodo.pai.cor == Cor.VERMELHO) {
            Nodo pai = nodo.pai;
            Nodo avo = pai.pai;

            if (pai == avo.esquerdo) {
                Nodo tio = avo.direito;
                if (tio != null && tio.cor == Cor.VERMELHO) {
                    pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    avo.cor = Cor.VERMELHO;
                    nodo = avo;
                } else {
                    if (nodo == pai.direito) {
                        nodo = pai;
                        rotacaoEsquerda(nodo);
                    }
                    pai.cor = Cor.PRETO;
                    avo.cor = Cor.VERMELHO;
                    rotacaoDireita(avo);
                }
            } else {
                Nodo tio = avo.esquerdo;
                if (tio != null && tio.cor == Cor.VERMELHO) {
                    pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    avo.cor = Cor.VERMELHO;
                    nodo = avo;
                } else {
                    if (nodo == pai.esquerdo) {
                        nodo = pai;
                        rotacaoDireita(nodo);
                    }
                    pai.cor = Cor.PRETO;
                    avo.cor = Cor.VERMELHO;
                    rotacaoEsquerda(avo);
                }
            }
        }
        raiz.cor = Cor.PRETO;
    }

    private void rotacaoEsquerda(Nodo nodo) {
        Nodo novoNodo = nodo.direito;
        nodo.direito = novoNodo.esquerdo;

        if (novoNodo.esquerdo != null) {
            novoNodo.esquerdo.pai = nodo;
        }

        novoNodo.pai = nodo.pai;

        if (nodo.pai == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.pai.esquerdo) {
            nodo.pai.esquerdo = novoNodo;
        } else {
            nodo.pai.direito = novoNodo;
        }

        novoNodo.esquerdo = nodo;
        nodo.pai = novoNodo;
    }

    private void rotacaoDireita(Nodo nodo) {
        Nodo novoNodo = nodo.esquerdo;
        nodo.esquerdo = novoNodo.direito;

        if (novoNodo.direito != null) {
            novoNodo.direito.pai = nodo;
        }

        novoNodo.pai = nodo.pai;

        if (nodo.pai == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.pai.direito) {
            nodo.pai.direito = novoNodo;
        } else {
            nodo.pai.esquerdo = novoNodo;
        }

        novoNodo.direito = nodo;
        nodo.pai = novoNodo;
    }

    public List<Reserva> buscarReservasPorCpf(String cpf) {
        List<Reserva> reservas = new ArrayList<>();
        buscarReservasRecursivo(raiz, cpf, reservas);
        return reservas;
    }

    private void buscarReservasRecursivo(Nodo nodo, String cpf, List<Reserva> reservas) {
        if (nodo == null)
            return;

        buscarReservasRecursivo(nodo.esquerdo, cpf, reservas);

        if (nodo.reserva.getCpfCliente().equals(cpf) && nodo.reserva.isAtiva()) {
            reservas.add(nodo.reserva);
        }

        buscarReservasRecursivo(nodo.direito, cpf, reservas);
    }

    public List<Reserva> listarReservasPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Reserva> reservas = new ArrayList<>();
        listarReservasPorPeriodoRecursivo(raiz, inicio, fim, reservas);
        return reservas;
    }

    private void listarReservasPorPeriodoRecursivo(Nodo nodo, LocalDate inicio,
            LocalDate fim, List<Reserva> reservas) {
        if (nodo == null)
            return;

        listarReservasPorPeriodoRecursivo(nodo.esquerdo, inicio, fim, reservas);

        if (nodo.reserva.isAtiva() &&
                !nodo.reserva.getCheckIn().isAfter(fim) &&
                !nodo.reserva.getCheckOut().isBefore(inicio)) {
            reservas.add(nodo.reserva);
        }

        listarReservasPorPeriodoRecursivo(nodo.direito, inicio, fim, reservas);
    }
}