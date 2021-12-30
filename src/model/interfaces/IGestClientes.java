package model.interfaces;

import model.excecoes.JaExistenteExcecao;

public interface IGestClientes extends Carregavel {
    void adicionar_cliente(String nif, String nome, String numero_telemovel, String email) throws JaExistenteExcecao;

    ICliente get_cliente(String nif);

    boolean existe_cliente(String nif);

    void carregar_cliente(ICliente clone);
}
