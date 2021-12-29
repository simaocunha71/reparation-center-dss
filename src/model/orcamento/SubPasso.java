package model.orcamento;

import model.interfaces.Carregavel;

public class SubPasso implements Carregavel {
    private String descricao;
    private float custoEstimado;
    private float custoReal;
    private float duracaoEstimada; //em minutos
    private float duracaoReal; //em minutos
    private boolean realizado;
    private String idTecnico;
    private int numero_subpasso;

    public SubPasso(String descricao, float custoEstimado, float duracaoEstimada){
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.realizado = false;
        this.idTecnico = null;
        this.numero_subpasso = -1;
    }

    public SubPasso() {
        this.descricao = "";
        this.custoEstimado = 0;
        this.duracaoEstimada = 0;
        this.custoReal = 0;
        this.duracaoReal = 0;
        this.realizado = false;
        this.idTecnico = null;
        this.numero_subpasso = -1;
    }

    public SubPasso(String descricao, float custoEstimado, float duracaoEstimada, float custoReal, float duracaoReal, boolean realizado, String idTecnico, int numero_subpasso) {
        this.descricao = descricao;
        this.custoEstimado = custoEstimado;
        this.duracaoEstimada = duracaoEstimada;
        this.custoReal = custoReal;
        this.duracaoReal = duracaoReal;
        this.realizado = realizado;
        this.idTecnico = idTecnico;
        this.numero_subpasso = numero_subpasso;
    }

    public void carrega(SubPasso passo) {
        this.descricao = passo.descricao;
        this.custoEstimado = passo.custoEstimado;
        this.duracaoEstimada = passo.duracaoEstimada;
        this.custoReal = passo.custoReal;
        this.duracaoReal = passo.duracaoReal;
        this.realizado = passo.realizado;
        this.idTecnico = passo.idTecnico;
        this.numero_subpasso = passo.numero_subpasso;
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

    public String getDescricao() {
        return descricao;
    }

    public String getIdTecnico() {
        return idTecnico;
    }

    public void setCustoReal(float custoReal) {
        this.custoReal = custoReal;
    }

    public void setDuracaoReal(float duracaoReal) {
        this.duracaoReal = duracaoReal;
    }

    public void setNumero_subpasso(int numero_subpasso) {
        this.numero_subpasso = numero_subpasso;
    }

    //descriçao;custoEstimado;custoReal;duracaoEstimada;duracaoReal;booleanRealizado;idTecnico
    public void carregar(String string) {
       String[] infos = string.split(";");
       if (infos.length == 7){
           try {
               this.descricao = infos[0];
               this.custoEstimado = Float.parseFloat(infos[1]);
               this.custoReal = Float.parseFloat(infos[2]);
               this.duracaoEstimada = Float.parseFloat(infos[3]);
               this.duracaoReal = Float.parseFloat(infos[4]);
               int b = Integer.parseInt(infos[5]);
               if(b == 1) this.realizado = true;
               this.idTecnico = infos[6];
               if(idTecnico.equals("null")) idTecnico = null;
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

    public void setDuracaoEstimada(float duracaoEstimada) {
        this.duracaoEstimada = duracaoEstimada;
    }

    public void setCustoEstimado(float custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean valida() {
        return descricao.length()>0 && custoEstimado >= 0 && duracaoEstimada >= 0 && ((!realizado && idTecnico == null)||(realizado && idTecnico != null));
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


    public float getCustoEstimado() {
        return custoEstimado;
    }

    public float getDuracaoEstimada() {
        return duracaoEstimada;
    }

    public int getNumero_subpasso() {
        return numero_subpasso;
    }

    public void recalcula_estimativas() {
        if(realizado){
            this.custoEstimado = custoReal;
            this.duracaoEstimada = duracaoReal;
        }
    }

    public float calcula_custo_gasto() {
        float custo_gasto = 0;
        if (realizado) custo_gasto = this.custoReal;
        return  custo_gasto;
    }

    public float calcula_tempo_gasto() {
        float tempo_gasto = 0;
        if (realizado) tempo_gasto = this.duracaoReal;
        return  tempo_gasto;
    }

    public SubPasso clone(){
        return new SubPasso(this.descricao,this.custoEstimado,this.duracaoEstimada,this.custoReal,this.duracaoReal,this.realizado,this.idTecnico, this.numero_subpasso);
    }


}

