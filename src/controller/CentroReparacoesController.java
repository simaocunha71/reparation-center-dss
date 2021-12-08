package controller;

import model.Sessao;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import model.interfaces.ISessao;

import java.io.IOException;

public class CentroReparacoesController {

    private ICentroReparacoes centro;
    private ISessao sessaoAtual;

    public void run() throws IOException, JaExistenteExcecao {
        centro.carregar_cp("../cp/utilizadores.csv","../cp/clientes.csv","../cp/pedidos.csv");

    }



}
