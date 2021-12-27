package model;

import model.interfaces.IPedido;

import java.util.ArrayList;
import java.util.List;

public class PlanoDeTrabalho {
    IPedido pedidoAssociado;
    List<Passo> passos;
    float custoEstimado;
    float custoReal;
    float duracaoEstimada; //em minutos
    float duracaoReal; //em minutos

    public PlanoDeTrabalho(float custoEstimado, float duracaoEstimada){
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
    }

    //TODO: adicionar passo();
}
