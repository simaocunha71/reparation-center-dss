package model.utilizadores;

import model.interfaces.IUtilizador;

public class Tecnico implements IUtilizador {
    private String id;
    private String nome;
    private int permissoes;

    public Tecnico(String id,String nome,int permissoes){
        this.id = id;
        this.nome = nome;
        this.permissoes = permissoes;
    }
}
