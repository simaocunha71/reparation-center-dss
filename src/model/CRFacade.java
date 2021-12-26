package model;

import model.excecoes.JaExistenteExcecao;
import model.excecoes.NaoExisteExcecao;
import model.interfaces.*;
import model.utilizadores.Funcionario;
import model.utilizadores.Gestor;
import model.utilizadores.Tecnico;
import model.clientes.Cliente;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CRFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private List<IPedido> pedidos; //queue fifo de pedidos
    private IUtilizador logado;


    public CRFacade(){
        utilizadores = new HashMap<>();
        clientes = new HashMap<>();
        pedidos = new LinkedList<>();
    }

    /**
     * Procura de utilizador no map de utilizadores
     * @param id id
     * @return  IUtilizador
     */
    public IUtilizador getUtilizadorByID(String id){
        return utilizadores.get(id);
    }

    /**
     * Procura cliente no map de clientes
     * @param id id
     * @return ICliente
     */
    public ICliente getClientesById(String id){
        return clientes.get(id);
    }

    /**
     *Adiciona um utilizado ao map de utilizadores
     * @param id id
     * @param nome nome
     * @param permissao nivel de permissao
     * @throws JaExistenteExcecao exceção
     */
    public void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao {
        if(!utilizadores.containsKey(id)){
            switch (permissao) {
                case 1 -> utilizadores.put(id, new Gestor(id, nome,password));
                case 2 -> utilizadores.put(id, new Funcionario(id, nome,password));
                case 3 -> utilizadores.put(id, new Tecnico(id, nome,password));
            }
        }else{throw new JaExistenteExcecao("Utilizador já existe no sistema");}
    }

    /**
     * Adiciona um cliente ao map de clientes
     * @param nif nif do cliente que representa o seu id
     * @param nome nome do cliente
     * @param numTelemovel número de telemóvel
     * @param email email
     * @throws JaExistenteExcecao exceção
     */
    public void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao {
        if(!clientes.containsKey(nif)){
            clientes.put(nif,new Cliente(nif,nome,numTelemovel,email));
        }else{throw  new JaExistenteExcecao("Cliente já existe no sistema");}
    }

    public void fazer_pedido(String idCLiente){

    }

    /**
     * Remove cliente do map de clientes
     * @param nif nif
     * @throws NaoExisteExcecao exceção
     */
    public void remover_cliente(String nif) throws NaoExisteExcecao {
        if(!clientes.containsKey(nif)){
            throw  new NaoExisteExcecao("Cliente não existe no sistema");
        }else{clientes.remove(nif);}
    }

    /* state loaders */

    /**
     * Carrega utilizadores para o estado do sistema
     * @param filename path para  ficheiro
     * @throws FileNotFoundException execão
     */
    public void carregar_utilizadores(String filename) throws IOException, JaExistenteExcecao {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split(";");
            //fazer validação da informação
            adicionar_utilizador(split[0],split[1], split[2],Integer.parseInt(split[3]));
        }
        br.close();
    }

    /**
     * Carrega clientes para o estado do sistema
     * @param filename path para o ficheiro
     * @throws FileNotFoundException  execão
     */
    public void carregar_clientes(String filename) throws IOException, JaExistenteExcecao {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split(";");
            //fazer validação da informação
            adicionar_cliente(split[0],split[1],split[2],split[3]);
        }
        br.close();

    }

    //carregar pedidos

    public void carregar_cp(String utilizadoresFN,String clientesFN,String pedidosFN) throws IOException, JaExistenteExcecao {
        carregar_utilizadores(utilizadoresFN);
        carregar_clientes(clientesFN);

    }

    public boolean login(String nomeDeUtilizador, String password) {
        if (utilizadores.containsKey(nomeDeUtilizador)){
            if(utilizadores.get(nomeDeUtilizador).getPassword().equals(password)) {
                this.logado = utilizadores.get(nomeDeUtilizador);
                return true;
            }
        }
        return false;
    }

    public void logout(){
        this.logado = null;
    }

    public boolean loggedTecnico(){
        return this.logado.getClass().equals(Tecnico.class);
    }

    public boolean loggedFuncionario(){
        return this.logado.getClass().equals(Funcionario.class);
    }

    public boolean loggedGestor(){
        return this.logado.getClass().equals(Gestor.class);
    }

    public boolean existsPlans(){
        return false;
    }


}
