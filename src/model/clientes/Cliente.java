package model.clientes;

import model.interfaces.ICliente;

public class Cliente implements ICliente {
    private String nif;
    private String nome;
    private String numTelemovel;
    private String email;

    public Cliente(String nif,String nome,String numTelemovel,String email){
        this.nif = nif;
        this.nome = nome;
        this.numTelemovel = numTelemovel;
        this.email = email;
    }

    public Cliente(){
        this.nif = "";
        this.nome = "";
        this.numTelemovel = "";
        this.email = "";
    }

    public String getNif() {
        return nif;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getNumTelemovel() {
        return numTelemovel;
    }

    public void load(String string){
        String[]split = string.split(";");
        if(split.length == 4) {
            this.nif = split[0];
            this.nome = split[1];
            this.numTelemovel = split[2];
            this.email = split[3];
        }
    }

    private boolean valida_nif(){
        try{
            Integer.parseInt(this.nif);
        }
        catch (NumberFormatException e){
            return false;
        }
        return this.nif.length() == 9;
    }

    private boolean valida_telemovel(){
        try{
            Integer.parseInt(this.numTelemovel);
        }
        catch (NumberFormatException e){
            return false;
        }
        return this.numTelemovel.length() == 9;
    }

    private boolean valida_email(){
        String[] splitEmail = this.email.split("@");
        try{
            return splitEmail[0].length() > 0 && splitEmail[1].length() > 0;
        }
        catch(ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    private boolean valida_nome(){
        return this.nome.length() > 0;
    }


    public boolean validate(){
        return valida_nif() && valida_telemovel() && valida_email() && valida_nome();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(nif).append(";").append(nome).append(";").append(numTelemovel).append(";").append(email);
        return sb.toString();
    }

    public ICliente clone(){
        return new Cliente(this.nif,this.nome,this.numTelemovel,this.email);
    }
}
