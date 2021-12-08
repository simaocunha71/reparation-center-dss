package model;

import model.interfaces.ISessao;
import model.interfaces.IUtilizador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Sessao implements ISessao {

    private String id;//id do utilizador
    private int permissao;//nivel de permissao
    private LocalDateTime inicio;//registo de inicio de sessao
    private LocalDateTime fim;//registo fim de sessao


    public Sessao(){
        this.inicio = LocalDateTime.now();
    }

    /**
     * Iniciar sessao
     * @param id id
     * @param password pass
     * @return boolean
     */
    public Boolean login(String id, String password, HashMap<String,IUtilizador> utilizadores){
        IUtilizador us = utilizadores.get(id);
        if(us.getPassword().equals(password)){
            this.id = id;
            this.permissao = us.getPermissao();
            return true;
        }
        return false;
    }

    /**
     * Hora e data de fim de sessão do utilizador
     */
    public void fim_Sessao(){this.fim = LocalDateTime.now();}

    /**
     * Guarda log da sessão
     * @param filename Nome do ficheiro
     * @throws IOException exececao
     */
    public void guardar_Sessao(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(id + ";" + inicio.toString() + ";" + fim.toString());
        bw.newLine();
        bw.close();

    }

}
