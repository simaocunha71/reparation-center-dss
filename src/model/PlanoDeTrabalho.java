package model;

import model.interfaces.IPedido;
import model.interfaces.Carregavel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlanoDeTrabalho implements Carregavel {
    private IPedido pedidoAssociado;
    private List<Passo> passos;
    private float custoEstimado;
    private float custoReal;
    private float duracaoEstimada; //em minutos
    private float duracaoReal; //em minutos
    private boolean realizado;


    public PlanoDeTrabalho(IPedido pedidoAssociado){
        this.pedidoAssociado = pedidoAssociado.clone();
        this.custoEstimado = 0;
        this.duracaoEstimada = 0;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
        this.realizado = false;
    }

    public PlanoDeTrabalho(IPedido pedidoAssociado,float custoEstimado, float duracaoEstimada, float custoReal, float duracaoReal, boolean realizado,List<Passo> passos) {
        this.pedidoAssociado = pedidoAssociado.clone();
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = custoReal;
        this.duracaoReal = duracaoReal;
        this.realizado = realizado;
        this.passos = new ArrayList<>();
        for(Passo p : passos){
            this.passos.add(p.clone());
        }
    }

    public boolean getRealizado() {
        return realizado;
    }

    public int get_num_referencia(){
        return pedidoAssociado.getNumeroRegistoEquipamento();
    }

    public void concluir(){
        boolean passosConcluidos = true;
        for(Passo p : passos){
            if(!p.concluido()) passosConcluidos = false;
        }
        if(passosConcluidos) {
            this.custoReal = 0;
            this.duracaoReal = 0;
            for (Passo p : passos) {
                this.custoReal += p.getCustoReal();
                this.duracaoReal += p.getDuracaoReal();
            }
            realizado = true;
        }

        this.realizado = true;
    }


    //custoEstimado;custoReal;tempoEstimado;tempoReal;booleanoRealizado;numeroPassos@Passos

    //Passos: Passo1->Passo2->Passo3...

    public void carregar(String string){
        String[] split = string.split("@");
        if(split.length == 2){
            String[] infos = split[0].split(";");
            if(infos.length == 6) {
                int nP = 0;
                try {
                    this.custoEstimado = Float.parseFloat(infos[0]);
                    this.custoReal = Float.parseFloat(infos[1]);
                    this.duracaoEstimada = Float.parseFloat(infos[2]);
                    this.duracaoReal = Float.parseFloat(infos[3]);
                    int b = Integer.parseInt(infos[4]);
                    if (b == 1) this.realizado = true;
                    nP = Integer.parseInt(infos[5]);
                } catch (NumberFormatException ignored) {
                    this.custoEstimado = -1;
                    this.duracaoEstimada = -1;
                    this.custoReal = 0;
                    this.duracaoReal = 0;
                    this.realizado = false;
                }
                String[] passos = split[1].split("->");
                if(passos.length == nP) {
                    for (int i = 0; i < nP; i++) {
                        Passo p = new Passo();
                        p.carregar(passos[i]);
                        adicionar_passo(p);
                    }
                }
            }
        }
    }

    public IPedido get_pedido(){
        return pedidoAssociado.clone();
    }

    public float getCustoEstimado() {
        return custoEstimado;
    }

    public float getDuracaoEstimada() {
        return duracaoEstimada;
    }

    public boolean valida() {
        boolean valido = true;
        for(Passo p : passos){
            if(!p.valida()) valido = false;
        }
        return valido && duracaoEstimada >= 0 && custoEstimado >= 0 && passos.size() > 0;
    }

    public void adicionar_passo(String descricao, float custoEstimado, float duracaoEstimada){
        Passo novoPasso = new Passo(descricao,custoEstimado,duracaoEstimada);
        passos.add(novoPasso);
        this.custoEstimado += custoEstimado;
        this.duracaoEstimada += duracaoEstimada;
    }

    public void adicionar_passo(Passo p){
        passos.add(p.clone());
        this.custoEstimado += p.getCustoEstimado();
        this.duracaoEstimada += p.getDuracaoEstimada();
    }

    //custoEstimado;custoReal;tempoEstimado;tempoReal;booleanoRealizado;numeroPassos@Passos

    //Passos: Passo1->Passo2->Passo3...
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(custoEstimado).append(";")
                .append(custoReal).append(";")
                .append(duracaoEstimada).append(";")
                .append(duracaoReal).append(";");
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(passos.size()).append("@");
        int i = 0;
        for(; i < passos.size()-1; i++){
            sb.append(passos.get(i).toString()).append("->");
        }
        if(passos.size() != 0) sb.append(passos.get(passos.size()-1).toString());
        return sb.toString();
    }

    public PlanoDeTrabalho clone(){
        return new PlanoDeTrabalho(this.pedidoAssociado,this.custoEstimado,this.duracaoEstimada,this.custoReal,this.duracaoReal,this.realizado,this.passos);
    }

    public void recalcula_estimativas() {
        if(passos.size() > 0) {
            this.custoEstimado = 0;
            this.duracaoEstimada = 0;
            for (Passo p : passos) {
                p.recalcula_estimativas();

                this.custoEstimado += p.getCustoEstimado();
                this.duracaoEstimada += p.getDuracaoEstimada();
            }
        }
        else if (realizado){
            this.custoEstimado = custoReal;
            this.duracaoEstimada = duracaoReal;
        }
    }

    public float calcula_custo_gasto(){
        float custo_gasto = 0;
        if(realizado){
            custo_gasto = this.custoReal;
        }
        else {
            for(Passo p : passos){
                custo_gasto += p.calcula_custo_gasto();
            }
        }
        return custo_gasto;
    }

    public float calcula_tempo_gasto(){
        float tempo_gasto = 0;
        if(realizado){
            tempo_gasto = this.duracaoReal;
        }
        else {
            for(Passo p : passos){
                tempo_gasto += p.calcula_tempo_gasto();
            }
        }
        return tempo_gasto;
    }

    public boolean ultrapassou120PorCentoOrcamento(){
        return this.custoEstimado*1.2 < calcula_custo_gasto();
    }

    public float orcamento_gasto(){
        return calcula_custo_gasto()*100/this.custoEstimado;
    }

    public Passo getProximoPasso(){
        boolean stop = false;
        Passo prox = null;
        if(existe_proximo_passo()) {
            for (Passo p : passos) {
                if (stop) break;
                if (!p.concluido()) {
                    stop = true;
                    prox = p;
                }
            }
        }
        return prox;
    }

    public boolean existe_proximo_passo(){
        boolean existe = false;
        for(Passo p : passos){
            if(existe) break;
            if(!p.concluido()){
                existe = true;
            }
        }
        return existe;
    }
}
