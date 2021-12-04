package controller;

import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICentroReparacoes;
import model.interfaces.ICliente;
import model.interfaces.IPedido;
import model.interfaces.IUtilizador;
import model.utilizadores.Funcionario;
import model.utilizadores.Gestor;
import model.utilizadores.Tecnico;
import model.clientes.Cliente;

import java.util.Map;

public class CentroReparacoesFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private Map<String, IPedido> pedidos;//possivelmente para ser uma queque fifo


    public void adicionar_utilizador(String id,String nome,int permissao) throws JaExistenteExcecao {
        if(!utilizadores.containsKey(id)){
            switch (permissao) {
                case 0 -> utilizadores.put(id, new Funcionario(id, nome, permissao));
                case 1 -> utilizadores.put(id, new Tecnico(id, nome, permissao));
                case 2 -> utilizadores.put(id, new Gestor(id, nome, permissao));
            }
        }else{throw new JaExistenteExcecao("Utilizador já existe no sistema");}
    }

    public void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao {
        if(!clientes.containsKey(nif)){
            clientes.put(nif,new Cliente(nif,nome,numTelemovel,email));
        }else{throw  new JaExistenteExcecao("model.clientes.Cliente já existe no sistema");}
    }
}
