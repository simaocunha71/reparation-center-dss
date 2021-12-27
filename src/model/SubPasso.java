package model;

import model.interfaces.Carregavel;

public class SubPasso implements Carregavel {
    String descricao;
    float custoEstimado;
    float custoReal;
    float duracaoEstimada; //em minutos
    float duracaoReal; //em minutos
    boolean realizado;
    String idTecnico;

    public SubPasso(String descricao, float custoEstimado, float duracaoEstimada){
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.realizado = false;
        this.idTecnico = null;
    }

    public SubPasso() {
        this.descricao = "";
        this.custoEstimado = -1;
        this.duracaoEstimada = -1;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.realizado = false;
        this.idTecnico = null;
    }

    public void concluir(String idTecnico, float custoReal, float duracaoReal){
        this.custoReal = custoReal;
        this.duracaoReal = duracaoReal;
        this.idTecnico = idTecnico;
        this.realizado = true;
    }

    public boolean concluido(){return this.realizado;}

    public float getCustoReal() {
        return custoReal;
    }

    public float getDuracaoReal() {
        return duracaoReal;
    }

    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanRealizado;idTecnico
    public void carregar(String string) {
       String[] infos = string.split(";");
       if (infos.length == 7){
           try {
               this.descricao = infos[0];
               this.custoEstimado = Integer.parseInt(infos[1]);
               this.custoReal = Integer.parseInt(infos[2]);
               this.duracaoEstimada = Integer.parseInt(infos[3]);
               this.duracaoReal = Integer.parseInt(infos[4]);
               int b = Integer.parseInt(infos[5]);
               if(b == 1) this.realizado = true;
               this.idTecnico = infos[6];
           }
           catch(NumberFormatException ignored){
               this.descricao = "";
               this.custoEstimado = -1;
               this.duracaoEstimada = -1;
               this.custoReal = 0;
               this.duracaoReal = 0;
               this.realizado = false;
               this.idTecnico = null;
           }
       }
    }

    public boolean valida() {
        return true;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(descricao).append(";").append(custoEstimado).append(";").append(custoReal).append(";");
        sb.append(duracaoEstimada).append(";").append(duracaoReal).append(";");
        if(realizado) sb.append("1;");
        else sb.append("0;");
        sb.append(idTecnico);
        return sb.toString();
    }
}

