package model.utilizadores;

import model.interfaces.IUtilizador;

public class Tecnico implements IUtilizador {
    private String id;
    private String nome;
    private String password;
    private int permissoes;

    public Tecnico(String id,String nome,String password,int permissoes){
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.permissoes = permissoes;
    }

    public String getName(){return this.nome;}
    public String getId(){return this.id;}
    public String getPassword(){return this.password;}
    public int getPermissao(){return this.permissoes;}
}
