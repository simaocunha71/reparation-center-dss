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

import java.io.*;
import java.util.*;

public class CRFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private Set<IPedido> pedidosOrcamentos;
    private Map<String,PlanoDeTrabalho> planos; //numero de registo do equipamento é a key
    private Armazem armazem;
    private IUtilizador logado;


    public CRFacade(){
        this.utilizadores = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pedidosOrcamentos = new TreeSet<IPedido>(new IPedidoComparator());
        this.armazem = new Armazem();
        this.planos = new HashMap<>();
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


    public void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao, IOException {
        String linha = new StringBuilder().append(id).append(";").append(nome).append(";").append(password).toString();
        IUtilizador utilizador = null;
        switch (permissao) {
            case 1 -> {
                utilizador = new Gestor();
            }
            case 2 -> {
                utilizador = new Funcionario();
            }
            case 3 -> {
                utilizador = new Tecnico();
            }
        }
        utilizador.load_utilizador(linha);
        if (utilizador.valida_utilizador()) {
            if (!utilizadores.containsKey(id)) {
                utilizadores.put(id, utilizador);
                gravar_utilizador(utilizador);
            } else {
                if (!(nome.equals(utilizador.getName()) && password.equals(utilizador.getPassword()))) {
                    utilizadores.remove(id);
                    utilizadores.put(id,utilizador);
                    gravar_todos_utilizadores();
                }
                throw new JaExistenteExcecao("Utilizador já existe no sistema");
            }
        }
    }


    public void adicionar_cliente(ICliente cliente) throws JaExistenteExcecao {
        if(!clientes.containsKey(cliente.getNif())){
            clientes.put(cliente.getNif(),cliente.clone());
        }else{throw  new JaExistenteExcecao("Cliente já existe no sistema");}
    }

    /**
     * Adiciona um cliente ao map de clientes
     * @param nif nif do cliente que representa o seu id
     * @param nome nome do cliente
     * @param numTelemovel número de telemóvel
     * @param email email
     * @throws JaExistenteExcecao exceção
     */

    public void adicionar_cliente(String nif,String nome,String numTelemovel,String email) throws JaExistenteExcecao, IOException {
        if(!clientes.containsKey(nif)){
            clientes.put(nif,new Cliente(nif,nome,numTelemovel,email));
            gravar_cliente(clientes.get(nif));
        }else {
            ICliente cliente = clientes.get(nif);
            if(!(nome.equals(cliente.getNome()) && numTelemovel.equals(cliente.getNumTelemovel()) && email.equals(cliente.getEmail()))){
                clientes.remove(nif);
                clientes.put(nif, new Cliente(nif, nome, numTelemovel, email));
                gravar_todos_clientes();
            }
            throw new JaExistenteExcecao("cliente já existe no sistema, overwrited");
        }
    }

    private void adicionar_equipamento(Equipamento equipamento,int local) throws IOException {
        switch (local){
            case 1:
            {
                if(!armazem.contem_para_orcamento(equipamento.getNumeroRegisto())){
                    armazem.regista_para_orcamento(equipamento);
                    gravar_equipamento(equipamento,1);
                }

            }
            case 2:
            {
                if(!armazem.contem_para_reparacao(equipamento.getNumeroRegisto())) {
                    armazem.regista_para_reparacao(equipamento);
                    gravar_equipamento(equipamento,2);
                }
            }
            case 3:
            {
                if(!armazem.contem_pronto_entregar(equipamento.getNumeroRegisto())) {
                    armazem.regista_prontos_entregar(equipamento);
                    gravar_equipamento(equipamento,3);
                }
            }
        }
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
            if(split.length == 4){
                try {
                    int permissao = Integer.parseInt(split[3]);
                    adicionar_utilizador(split[0], split[1], split[2], permissao);
                }catch (NumberFormatException ignored){
                }
            }
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
        while((linha = br.readLine()) != null){
            ICliente cliente = new Cliente();
            cliente.load_cliente(linha);
            if(cliente.valida_cliente()) adicionar_cliente(cliente);
        }
        br.close();
    }

    private void carregar_armazem(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        while((linha = br.readLine()) != null){
            if(linha.split(";").length ==4){
                Equipamento equipamento = new Equipamento();
                StringBuilder sb = new StringBuilder();
                sb.append(linha.split(";")[0]).append(";")
                        .append(linha.split(";")[1]).append(";")
                        .append(linha.split(";")[2]).append(";");
                equipamento.load_equipamento(sb.toString());
                try {
                    int local = Integer.parseInt(linha.split(";")[3]);
                    if (equipamento.valida_equipamento()) adicionar_equipamento(equipamento,local);
                }catch (NumberFormatException ignored){}
            }
        }
        br.close();
    }



    //carregar pedidos

    public void carregar_cp(String utilizadoresFN,String clientesFN,String armazemFN,String pedidosFN) throws IOException, JaExistenteExcecao {
        carregar_utilizadores(utilizadoresFN);
        carregar_clientes(clientesFN);
        carregar_armazem(armazemFN);
    }




    public void adicionar_pedido_orcamento(String nifCliente, String modelo, String descricaoEquipamento, String descricaoPedido) {
        if(clientes.containsKey(nifCliente)){
            Equipamento e = new Equipamento(nifCliente, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo,descricaoEquipamento );
            armazem.regista_para_orcamento(e);
            IPedido pedido = new PedidoOrcamento(nifCliente, e.getNumeroRegisto(), descricaoPedido);
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

    public int get_ultimo_numero_de_registo_equipamento() {
        return armazem.get_ultimo_numero_de_registo_equipamento();
    }

    public void gravar_cliente (ICliente cliente) throws IOException {
        FileWriter w = new FileWriter("cp/clientes.csv",true);
        w.write(cliente.toString()+"\n");
    }

    public void gravar_todos_clientes () throws IOException {
        FileWriter w = new FileWriter("cp/clientes.csv");
        clientes.forEach((k,v)-> {
            try {
                w.write(v.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void gravar_utilizador (IUtilizador utilizador) throws IOException {
        FileWriter w = new FileWriter("cp/utilizadores.csv",true);
        w.write(utilizador.toString()+"\n");
    }

    public void gravar_todos_utilizadores () throws IOException {
        FileWriter w = new FileWriter("cp/utilizadores.csv");
        utilizadores.forEach((k,v)-> {
            try {
                w.write(v.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void gravar_equipamento(Equipamento equipamento, int local) throws IOException {
        FileWriter w = new FileWriter("cp/armazem.csv",true);
        w.write(equipamento.toString()+";"+local+"\n");
    }
}
