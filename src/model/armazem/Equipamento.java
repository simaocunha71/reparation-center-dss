package model.armazem;

import model.interfaces.IEquipamento;

public class Equipamento implements IEquipamento {
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

    public Equipamento(){
        this.nifCliente = "";
        this.numeroRegisto = -1;
        this.modelo = "";
        this.descricao = "";
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


    public void carregar(String linha) {
        String []string = linha.split(";");
        if(string.length == 4){
            this.nifCliente = string[0];
            try {
                this.numeroRegisto = Integer.parseInt(string[1]);
            }catch (NumberFormatException e){
                this.numeroRegisto = -1;
            }
            this.modelo = string[2];
            this.descricao = string[3];
        }
    }

    public boolean valida() {
        return valida_nif() && valida_numero_registo() && valida_length(modelo,25) && valida_length(descricao,25);
    }

    private boolean valida_nif(){
        try{
            Integer.parseInt(this.nifCliente);
        }
        catch (NumberFormatException e){
            return false;
        }
        return this.nifCliente.length() == 9;
    }

    private boolean valida_numero_registo(){
        return this.numeroRegisto >= 0;
    }

    private boolean valida_length(String string, int limit){
        return string.length() < limit;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(nifCliente).append(";")
                .append(numeroRegisto).append(";")
                .append(modelo).append(";")
                .append(descricao);
        return sb.toString();
    }

    public Equipamento clone(){
        return new Equipamento(this.nifCliente,this.numeroRegisto,this.modelo,this.descricao);
    }


}
