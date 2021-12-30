package model.clientes;

import model.interfaces.ICliente;

public class Cliente implements ICliente {
    private String nif;
    private String nome;
    private String numero_telemovel;
    private String email;

    public Cliente(String nif, String nome, String numero_telemovel, String email){
        this.nif = nif;
        this.nome = nome;
        this.numero_telemovel = numero_telemovel;
        this.email = email;
    }

    public Cliente(){
        this.nif = "";
        this.nome = "";
        this.numero_telemovel = "";
        this.email = "";
    }

    public String get_nif() {
        return nif;
    }

    public String get_email() {
        return email;
    }

    public String get_nome() {
        return nome;
    }

    public String get_num_telemovel() {
        return numero_telemovel;
    }

    public void carregar(String string){
        String[]split = string.split(";");
        if(split.length == 4) {
            this.nif = split[0];
            this.nome = split[1];
            this.numero_telemovel = split[2];
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
            Integer.parseInt(this.numero_telemovel);
        }
        catch (NumberFormatException e){
            return false;
        }
        return this.numero_telemovel.length() == 9;
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


    public boolean valida(){
        return valida_nif() && valida_telemovel() && valida_email() && valida_nome();
    }

    public String salvar(){
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(nif).append(";").append(nome).append(";").append(numero_telemovel).append(";").append(email);
        return sb.toString();
    }

    public ICliente clone(){
        return new Cliente(this.nif,this.nome,this.numero_telemovel,this.email);
    }
}
