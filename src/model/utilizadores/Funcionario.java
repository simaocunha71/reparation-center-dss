package model.utilizadores;

import model.interfaces.IUtilizador;

public class Funcionario implements IUtilizador {
    private String id;
    private String nome;
    private String password;

    public Funcionario(String id,String nome,String password){
        this.id = id;
        this.nome = nome;
        this.password = password;
    }

    public Funcionario(){
        this.id = "";
        this.nome = "";
        this.password = "";
    }

    public String getName(){return this.nome;}
    public String getId(){return this.id;}
    public String getPassword(){return this.password;}

    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 3) {
            this.id = split[0];
            this.nome = split[1];
            this.password = split[2];
        }
    }

    public boolean valida() {
        return id.length()>0 && nome.length()>0 && password.length()>0;
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append("2@").append(id).append(";").append(nome).append(";").append(password);
        return sb.toString();
    }

    public IUtilizador clone(){
        return new Funcionario(id,nome,password);
    }
}
