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


    //TODO: ver o clone
    public PlanoDeTrabalho(IPedido pedidoAssociado){
        this.pedidoAssociado = pedidoAssociado; //Nao sei se tem de fazer clone. mby yes
        this.custoEstimado = -1;
        this.duracaoEstimada = -1;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
        this.realizado = false;
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
                    this.custoEstimado = Integer.parseInt(infos[0]);
                    this.custoReal = Integer.parseInt(infos[1]);
                    this.duracaoEstimada = Integer.parseInt(infos[2]);
                    this.duracaoReal = Integer.parseInt(infos[3]);
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

    //TODO: valida
    public boolean valida() {
        return true;
    }

    //TODO: adicionar passo();
    public void adicionar_passo(String descricao, float custoEstimado, float duracaoEstimada){
        Passo novoPasso = new Passo(descricao,custoEstimado,duracaoEstimada);
        passos.add(novoPasso);
    }

    public void adicionar_passo(Passo p){
        passos.add(p); //TODO:clone
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

    //TODO: meter um clone direito
    public PlanoDeTrabalho clone(){
        return this;
    }

}
