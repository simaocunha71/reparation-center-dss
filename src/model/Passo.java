package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Passo {
    List<Passo> passos;
    String descricao;
    float custoEstimado;
    float custoReal;
    float duracaoEstimada; //em minutos
    float duracaoReal; //em minutos

    public Passo(String descricao, float custoEstimado, float duracaoEstimada){
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.passos = new ArrayList<>();
    }

    //TODO: adicionar sub-passo();

}
