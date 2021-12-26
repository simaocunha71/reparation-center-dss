package model.utilizadores;

import model.interfaces.IUtilizador;

public class Gestor implements IUtilizador {
    private String id;
    private String nome;
    private String password;

    public Gestor(String id,String nome,String password){
        this.id = id;
        this.nome = nome;
        this.password = password;
    }

    public String getName(){return this.nome;}
    public String getId(){return this.id;}
    public String getPassword(){return this.password;}
}
