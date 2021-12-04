package model.utilizadores;

import model.interfaces.IUtilizador;

public class Funcionario implements IUtilizador {
    private String id;
    private String nome;
    private int permissoes;

    public Funcionario(String id,String nome,int permissoes){
        this.id = id;
        this.nome = nome;
        this.permissoes = permissoes;
    }
}
