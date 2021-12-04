package model.clientes;

import model.interfaces.ICliente;

public class Cliente implements ICliente {
    private String nif;
    private String nome;
    private String numTelemovel;
    private String email;

    public  Cliente(String nif,String nome,String numTelemovel,String email){
        this.nif = nif;
        this.nome = nome;
        this.numTelemovel = numTelemovel;
        this.email = email;
    }
}
