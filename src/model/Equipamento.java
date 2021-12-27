package model;

public class Equipamento {
    private String nifCliente;
    private int numeroRegisto;
    private String modelo;
    private String descricao;

    public Equipamento(String nifCliente, int numeroRegisto, String modelo, String descricao){
        this.nifCliente = nifCliente;
        this.numeroRegisto = numeroRegisto;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    public String getNifCliente() {
        return nifCliente;
    }

    public int getNumeroRegisto(){return numeroRegisto;}

    public String getModelo() {
        return modelo;
    }

    public String getDescricao() {
        return descricao;
    }
}
