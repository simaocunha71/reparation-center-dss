package model;

import model.interfaces.IPedido;
import model.interfaces.Loadable;

import java.util.ArrayList;
import java.util.List;

public class PlanoDeTrabalho implements Loadable {
    IPedido pedidoAssociado;
    List<Passo> passos;
    float custoEstimado;
    float custoReal;
    float duracaoEstimada; //em minutos
    float duracaoReal; //em minutos
    boolean realizado;

    public PlanoDeTrabalho(IPedido pedidoAssociado){
        this.pedidoAssociado = pedidoAssociado; //Nao sei se tem de fazer clone. mby yes
    }

    public PlanoDeTrabalho(float custoEstimado, float duracaoEstimada){
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
        this.realizado = false;
    }

    public void concluido(){
        this.realizado = true;
    }


    //custoEstimado;custoReal;tempoEstimado;tempoReal;booleanoRealizado;numeroPassos@Passos

    //Passos: Passo1->Passo2->Passo3...

    public void load(String string){
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
                        p.load(passos[i]);
                        adicionar_passo(p);
                    }
                }
            }
        }
    }

    public boolean validate() {
        return true;
    }

    //TODO: adicionar passo();
    public void adicionar_passo(String descricao, float custoEstimado, float duracaoEstimada){
        Passo novoPasso = new Passo(descricao,custoEstimado,duracaoEstimada);
        passos.add(novoPasso);
    }

    public void adicionar_passo(Passo p){
        passos.add(new Passo(p.descricao,p.custoEstimado,p.duracaoEstimada)); //secalhar nao é preciso
    }

}
