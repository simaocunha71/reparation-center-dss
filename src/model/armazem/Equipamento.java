package model.armazem;

import model.interfaces.IEquipamento;

public class Equipamento implements IEquipamento {
    private String nif_cliente;
    private int num_registo;
    private String modelo;
    private String descricao;

    public Equipamento(String nif_cliente, int num_registo, String modelo, String descricao){
        this.nif_cliente = nif_cliente;
        this.num_registo = num_registo;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    public Equipamento(){
        this.nif_cliente = "";
        this.num_registo = -1;
        this.modelo = "";
        this.descricao = "";
    }

    public int get_numero_registo(){return num_registo;}

    public String get_modelo() {
        return modelo;
    }

    public String get_descricao() {
        return descricao;
    }


    public void carregar(String linha) {
        String []string = linha.split(";");
        if(string.length == 4){
            this.nif_cliente = string[0];
            try {
                this.num_registo = Integer.parseInt(string[1]);
            }catch (NumberFormatException e){
                this.num_registo = -1;
            }
            this.modelo = string[2];
            this.descricao = string[3];
        }
    }

    public boolean valida() {
        return valida_nif() && valida_numero_registo() && valida_length(modelo,25) && valida_length(descricao,40);
    }

    private boolean valida_nif(){
        try{
            Integer.parseInt(this.nif_cliente);
        }
        catch (NumberFormatException e){
            return false;
        }
        return this.nif_cliente.length() == 9;
    }

    private boolean valida_numero_registo(){
        return this.num_registo >= 0;
    }

    private boolean valida_length(String string, int limit){
        return string.length() < limit;
    }


    public String salvar(){
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(nif_cliente).append(";").append(num_registo).append(";").append(modelo).append(";").append(descricao);
        return sb.toString();
    }

    public IEquipamento clone(){
        return new Equipamento(this.nif_cliente,this.num_registo,this.modelo,this.descricao);
    }


}
