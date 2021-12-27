package model;

import model.comparators.IPedidoComparator;
import model.excecoes.JaExistenteExcecao;
import model.excecoes.NaoExisteExcecao;
import model.interfaces.*;
import model.pedidos.PedidoOrcamento;
import model.utilizadores.Funcionario;
import model.utilizadores.Gestor;
import model.utilizadores.Tecnico;
import model.clientes.Cliente;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CRFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private Set<IPedido> pedidosOrcamentos;
    private Map<String,PlanoDeTrabalho> planos; //numero de registo do equipamento é a key
    private Armazem armazem;
    private IUtilizador logado;


    public CRFacade(){
        utilizadores = new HashMap<>();
        clientes = new HashMap<>();
        pedidosOrcamentos = new TreeSet<IPedido>(new IPedidoComparator());
    }

    /**
     * Procura de utilizador no map de utilizadores
     * @param id id
     * @return  IUtilizador
     */
    public IUtilizador get_utilizador_by_ID(String id){
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


    public void adicionar_pedido_orcamento(String nifCliente, Equipamento equipamento, String descricao) {
        if(clientes.containsKey(nifCliente)){
            armazem.adiciona_para_orcamento(equipamento);
            IPedido pedido = new PedidoOrcamento(nifCliente, equipamento.getNumeroRegisto(), descricao);
            pedidosOrcamentos.add(pedido);
        }
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

    public boolean logged_tecnico(){
        return this.logado.getClass().equals(Tecnico.class);
    }

    public boolean logged_funcionario(){
        return this.logado.getClass().equals(Funcionario.class);
    }

    public boolean logged_gestor(){
        return this.logado.getClass().equals(Gestor.class);
    }

    public boolean exists_plan(){
        return false;
    }

    public boolean exists_user(String id){
        return utilizadores.containsKey(id);
    }

    public boolean exists_cliente(String nif){
        return clientes.containsKey(nif);
    }

    public String novo_numero_registo(){
        int i = 0;
        boolean valido = pedidosOrcamentos.size() == 0;
        Iterator<IPedido> iterator = pedidosOrcamentos.iterator();
        while(!valido && iterator.hasNext()){
            if(iterator.next().getNumeroRegistoEquipamento().equals(String.valueOf(i))) i++;
            else valido = true;
        }
        return String.valueOf(i);
    }

}
