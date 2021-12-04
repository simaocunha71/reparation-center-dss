package model.utilizadores;

import model.interfaces.IUtilizador;

public class Gestor implements IUtilizador {
    private String id;
    private String nome;
    private int permissoes;

    public Gestor(String id,String nome,int permissoes){
        this.id = id;
        this.nome = nome;
        this.permissoes = permissoes;
    }
}
