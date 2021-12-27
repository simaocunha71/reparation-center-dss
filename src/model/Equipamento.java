package model;

public class Equipamento {
    private String numeroRegisto;
    private String modelo;
    private String descricao;

    public Equipamento(String numeroRegisto, String modelo, String descricao){
        this.numeroRegisto = numeroRegisto;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    public String getNumeroRegisto(){return numeroRegisto;}

    public String getModelo() {
        return modelo;
    }

    public String getDescricao() {
        return descricao;
    }
}
