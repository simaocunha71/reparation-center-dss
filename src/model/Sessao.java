package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

public class Sessao {

    private String id;//id do utilizador
    private int permissao;//nivel de permissao
    private LocalDateTime inicio;//registo de inicio de sessao
    private LocalDateTime fim;//registo fim de sessao


    public Sessao(String id, int permissao){
        this.inicio = LocalDateTime.now();
        this.id = id;
        this.permissao = permissao;
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
