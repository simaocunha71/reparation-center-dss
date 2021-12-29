package model;

import model.armazem.Armazem;
import model.armazem.Equipamento;
import model.comparators.IOrcamentoComparator;
import model.comparators.IPedidoComparator;
import model.excecoes.JaExistenteExcecao;
import model.excecoes.NaoExisteExcecao;
import model.interfaces.*;
import model.orcamento.Orcamento;
import model.orcamento.PlanoDeTrabalho;
import model.pedidos.PedidoExpresso;
import model.pedidos.PedidoOrcamento;
import model.utilizadores.Funcionario;
import model.utilizadores.Gestor;
import model.utilizadores.Tecnico;
import model.clientes.Cliente;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CRFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private Set<IPedido> pedidosOrcamentos;
    private Map<Integer, IPedido> pedidosJaPlaneados;
    private Map<Integer, IPedido> pedidosCompletos;
    private Map<Integer, Orcamento> orcamentos; //numero de registo do equipamento é a key
    private Armazem armazem;
    private IUtilizador logado;

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";


    public CRFacade(){
        this.utilizadores = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pedidosOrcamentos = new TreeSet<IPedido>(new IPedidoComparator());
        this.armazem = new Armazem();
        this.orcamentos = new HashMap<>();
        this.pedidosJaPlaneados = new HashMap<>();
        this.pedidosCompletos = new HashMap<>();
    }

    /**
     * Procura de utilizador no map de utilizadores
     * @param id id
     * @return  IUtilizador
     */
    public IUtilizador get_utilizador_by_ID(String id){
        return utilizadores.get(id);
    }

    public String get_logged_id(){
        return logado.getId();
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
        IUtilizador utilizador = null;
        switch (permissao) {
            case 1 -> utilizador = new Gestor(id,nome,password);
            case 2 -> utilizador = new Funcionario(id,nome,password);
            case 3 -> utilizador = new Tecnico(id,nome,password);
        }
        if (utilizador != null && utilizador.valida()) {
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


    private void carregar_cliente(ICliente cliente) throws JaExistenteExcecao {
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

    public void adicionar_cliente(String nif, String nome, String numTelemovel, String email) throws JaExistenteExcecao, IOException {
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

    public void adicionar_orcamento(Orcamento orcamento) throws IOException {
        int num_ref = orcamento.get_num_ref();
        if(!orcamentos.containsKey(num_ref)){
            orcamentos.put(num_ref,orcamento.clone());
            gravar_orcamento(orcamentos.get(num_ref));
        }else {
            orcamentos.remove(num_ref);
            orcamentos.put(num_ref,orcamento.clone());
            gravar_todos_orcamentos();
        }
    }


    public void gerar_orcamento(PlanoDeTrabalho plano) throws IOException {
        int num_referencia = plano.get_num_referencia();
        remove_pedido_orcamento(num_referencia);
        transferencia_seccao(num_referencia);
        if(!orcamentos.containsKey(num_referencia)){
            Orcamento orcamento = new Orcamento(plano);
            orcamentos.put(num_referencia,orcamento);
            gravar_orcamento(orcamento);
        }
    }


    public List<Orcamento> get_orcamentos_por_confirmar() {
        return orcamentos.values().stream().filter(k->!k.getConfirmado()).map(Orcamento::clone).collect(Collectors.toList());
    }

    public List<Orcamento> get_orcamentos_confirmados() {
        Set<Orcamento> set = new TreeSet<Orcamento>(new IOrcamentoComparator());
        orcamentos.values().stream().filter(k -> k.getConfirmado() && pedidosJaPlaneados.containsKey(k.get_num_ref())).map(Orcamento::clone).forEach(set::add);
        return set.stream().toList();
    }

    public List<Orcamento> get_orcamentos_completos() {
        return orcamentos.values().stream().filter(k->k.getConfirmado()).map(Orcamento::clone).collect(Collectors.toList());
    }


    public Orcamento get_orcamento(int num_ref) {
        Orcamento orcamento = null;
        if(orcamentos.containsKey(num_ref)) orcamento= orcamentos.get(num_ref);
        return orcamento.clone();
    }

    public IEquipamento getEquipamento(int num_ref) {
        return armazem.getEquipamento(num_ref).clone();
    }




    private void transferencia_seccao(int num_referencia) throws IOException {
        armazem.transferencia_seccao(num_referencia);
        gravar_todos_equipamento();
    }

    private void remove_pedido_orcamento(int num_referencia) throws IOException {
        Iterator<IPedido> iterator = pedidosOrcamentos.iterator();
        boolean encontrado = false;
        while(iterator.hasNext() && !encontrado){
            IPedido pedido = iterator.next();
            if(pedido.getNumeroRegistoEquipamento() == num_referencia){
                adicionar_pedido_ja_planeado(pedido);
                iterator.remove();
                encontrado = true;
            }
        }
        if(encontrado) gravar_todos_pedidos();
    }

    private void remove_pedido_ja_planeado(int num_referencia) throws IOException {
        if(pedidosJaPlaneados.containsKey(num_referencia)) {
            pedidosJaPlaneados.remove(num_referencia);
            gravar_todos_pedidos();
        }
    }

    public void concluir_reparacao(Orcamento orcamento) throws IOException {
        int num_ref = orcamento.get_num_ref();
        if(orcamentos.containsKey(num_ref)){
            orcamentos.remove(num_ref);
            orcamentos.put(num_ref,orcamento.clone());
            transferencia_seccao(num_ref);
            remove_pedido_ja_planeado(num_ref);
            gravar_todos_orcamentos();
            gravar_todos_equipamento();
        }
    }


    private void adicionar_pedido_ja_planeado(IPedido pedido) {
        System.out.println("DEBUG A ADICIONAR PEDIDO PLANEADO");
        if(!pedidosJaPlaneados.containsKey(pedido.getNumeroRegistoEquipamento())){
            System.out.println("DEBUG ADIONADO PEDIDO PLANEADO: "+pedido.getNumeroRegistoEquipamento());
            pedidosJaPlaneados.put(pedido.getNumeroRegistoEquipamento(),pedido);
        }
    }

    private void adicionar_pedido_completo(IPedido pedido) {
        if(!pedidosCompletos.containsKey(pedido.getNumeroRegistoEquipamento())){
            pedidosCompletos.put(pedido.getNumeroRegistoEquipamento(),pedido);
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
            split = linha.split("@");
            if(split.length == 2){
                try {
                    int permissao = Integer.parseInt(split[0]);
                    switch (permissao){
                        case 1 -> carregar_gestor(split[1]);
                        case 2 -> carregar_funcionario(split[1]);
                        case 3 -> carregar_tecnico(split[1]);
                    }

                }catch (NumberFormatException ignored){}
            }
        }
        br.close();
    }

    private void carregar_funcionario(String s) {
        IUtilizador utilizador = new Funcionario();
        utilizador.carregar(s);
        if (utilizador.valida()) {
            if (!utilizadores.containsKey(utilizador.getId())) {
                utilizadores.put(utilizador.getId(), utilizador);
            } else {
                utilizadores.remove(utilizador.getId());
                utilizadores.put(utilizador.getId(),utilizador);
            }
        }
    }

    private void carregar_gestor(String s) {
        IUtilizador utilizador = new Gestor();
        utilizador.carregar(s);
        if (utilizador.valida()) {
            if (!utilizadores.containsKey(utilizador.getId())) {
                utilizadores.put(utilizador.getId(), utilizador);
            } else {
                utilizadores.remove(utilizador.getId());
                utilizadores.put(utilizador.getId(),utilizador);
            }
        }
    }

    private void carregar_tecnico(String s) {
        IUtilizador utilizador = new Tecnico();
        utilizador.carregar(s);
        if (utilizador.valida()) {
            if (!utilizadores.containsKey(utilizador.getId())) {
                utilizadores.put(utilizador.getId(), utilizador);
            } else {
                utilizadores.remove(utilizador.getId());
                utilizadores.put(utilizador.getId(),utilizador);
            }
        }
    }

    private void carregar_orcamento(Orcamento orcamento) throws JaExistenteExcecao {
        int num_referencia = orcamento.get_num_ref();
        if(!orcamentos.containsKey(num_referencia)){
            orcamentos.put(num_referencia,orcamento);
        }else throw new JaExistenteExcecao("Orcamento ja existe");
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
            cliente.carregar(linha);
            if(cliente.valida()) carregar_cliente(cliente);
        }
        br.close();
    }

    private void carregar_armazem(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split("@");
            if(split.length == 2){
                IEquipamento equipamento = new Equipamento();
                equipamento.carregar(split[0]);
                try {
                    int local = Integer.parseInt(split[1]);
                    if (equipamento.valida()) armazem.adicionar_equipamento(equipamento,local);
                }catch (NumberFormatException ignored){}
            }
        }
        br.close();
    }

    private void carregar_pedidos(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split("@");
            if(split.length == 2){
                try{
                    int tipo = Integer.parseInt(split[0]);
                    System.out.println("DEBUG TIPO: "+tipo);
                    IPedido pedido = null;
                    switch (tipo){
                        case 1, 3,4 -> pedido = new PedidoOrcamento();
                        case 2 -> pedido = new PedidoExpresso();
                    }
                    if(pedido != null) {
                        System.out.println("DEBUG CARREGANDO");
                        pedido.carregar(split[1]);
                    }
                    if(pedido != null && pedido.valida() && valida_pedido(pedido,tipo)){
                        System.out.println("DEBUG CARREGAR PEDIDO");
                        carregar_pedido(pedido,tipo);
                    }
                }
                catch (NumberFormatException ignored){}
            }
        }

        br.close();
    }

    private void carregar_pedido(IPedido pedido, int tipo) {
        switch (tipo){
            case 1-> pedidosOrcamentos.add(pedido);
            //TODO: case 2 -> pedidos expresso;
            case 3-> adicionar_pedido_ja_planeado(pedido);
            case 4-> adicionar_pedido_completo(pedido);
        }
    }

    private void carregar_orcamentos(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String linha;
        String[] split;
        boolean valido = false;
        while((linha = br.readLine()) != null){
            split = linha.split("#");
            if(split.length == 2) {
                String[] infos = split[0].split(";");
                if (infos.length == 3) {
                    try {
                        int numRegisto = Integer.parseInt(infos[0]);
                        boolean confirmacao = false;
                        LocalDateTime dataRegisto = null;
                        if(Integer.parseInt(infos[1]) == 0) {
                            valido = true;
                        }
                        else if(Integer.parseInt(infos[1]) == 1){
                            confirmacao = true;
                            valido = true;
                            dataRegisto = LocalDateTime.parse(infos[2]);
                        }
                        IPedido pedido = new PedidoExpresso();
                        System.out.println("DEBUG: "+numRegisto);
                        if (pedidosJaPlaneados.containsKey(numRegisto)) {
                            System.out.println("DEBUG existe chave");
                            pedido = pedidosJaPlaneados.get(numRegisto);
                        }else if (pedidosCompletos.containsKey(numRegisto)) {
                            System.out.println("DEBUG existe chave");
                            pedido = pedidosCompletos.get(numRegisto);
                        }else valido = false;
                        if(valido) {
                            System.out.println("DEBUG: IF1");
                            Orcamento orcamento = new Orcamento(numRegisto, pedido,confirmacao,dataRegisto);
                            orcamento.carregar(split[1]);
                            if (orcamento.valida()) {
                                System.out.println("DEBUG: IF2");
                                carregar_orcamento(orcamento);
                            }
                        }
                    } catch (NumberFormatException | DateTimeParseException | JaExistenteExcecao ignored) {
                    }
                }
            }
        }
        br.close();
    }



    private boolean valida_pedido(IPedido pedido, int tipo) {
        switch (tipo){
            case 1 -> {
                return clientes.containsKey(pedido.getNifCliente()) && armazem.contem_equipamento_para_orcamento(pedido.getNumeroRegistoEquipamento());}
            case 3 -> {
                return clientes.containsKey(pedido.getNifCliente()) && armazem.contem_equipamento_para_reparacao(pedido.getNumeroRegistoEquipamento());}
            case 4 -> {
                return true;
            }
            default -> {
                return false;
            }
        }


    }


    public void carregar_cp(String utilizadoresFN,String clientesFN,String armazemFN,String pedidosFN,String orcamentosFN) throws IOException, JaExistenteExcecao {
        carregar_utilizadores(utilizadoresFN);
        carregar_clientes(clientesFN);
        carregar_armazem(armazemFN);
        carregar_pedidos(pedidosFN);
        carregar_orcamentos(orcamentosFN);
    }


    public void adicionar_pedido_orcamento(String nifCliente, String modelo, String descricaoEquipamento, String descricaoPedido) throws IOException {
        if(clientes.containsKey(nifCliente)){
            IEquipamento e = new Equipamento(nifCliente, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo,descricaoEquipamento );
            armazem.regista_equipamento(e,1);
            IPedido pedido = new PedidoOrcamento(nifCliente, e.getNumeroRegisto(), descricaoPedido);
            pedidosOrcamentos.add(pedido);
            gravar_pedido(pedido);
            gravar_equipamento(e,1);
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
        w.close();
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
        w.close();
    }

    public void gravar_utilizador (IUtilizador utilizador) throws IOException {
        FileWriter w = new FileWriter("cp/utilizadores.csv",true);
        w.write(utilizador.toString()+"\n");
        w.close();
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
        w.close();
    }

    private void gravar_equipamento(IEquipamento equipamento, int local) throws IOException {
        FileWriter w = new FileWriter("cp/armazem.csv",true);
        w.write(equipamento.toString()+"@"+local+"\n");
        w.close();
    }

    private void gravar_pedido(IPedido pedido) throws IOException {
        FileWriter w = new FileWriter("cp/pedidos.csv",true);
        w.write(pedido.toString()+"\n");
        w.close();
    }

    private void gravar_orcamento(Orcamento orcamento) throws IOException {
        FileWriter w = new FileWriter("cp/orcamentos.csv",true);
        w.write(orcamento.toString()+"\n");
        w.close();
    }

    //TODO: falta os express
    private void gravar_todos_pedidos() throws IOException {
        FileWriter w = new FileWriter("cp/pedidos.csv");
        pedidosOrcamentos.forEach(k-> {
            try {
                w.write("1@"+k.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pedidosJaPlaneados.forEach((v,k)-> {
            try {
                w.write("3@"+k.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pedidosCompletos.forEach((v,k)-> {
            try {
                w.write("4@"+k.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    private void gravar_todos_equipamento() throws IOException {
        FileWriter w = new FileWriter("cp/armazem.csv");
        w.write(armazem.toString());
        w.close();
    }

    private void gravar_todos_orcamentos() throws IOException {
        FileWriter w = new FileWriter("cp/orcamentos.csv");
        orcamentos.forEach((k,v)-> {
            try {
                w.write(v.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    public List<String> get_pedidos_orcamento(){
        List<String> lista = new ArrayList<String>(pedidosOrcamentos.size());

        Iterator<IPedido> iterator = pedidosOrcamentos.iterator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        for(int i = 0; i < 10 && iterator.hasNext(); i++){
            IPedido p = iterator.next();
            lista.add("DATA:["+p.getTempoRegisto().format(formatter) + "] CLIENTE:["+p.getNifCliente()+ "] EQUIPAMENTO:[#"+p.getNumeroRegistoEquipamento()+"]");
        }
        return lista;
    }

    public IPedido get_pedido(int posicao){
        Iterator<IPedido> iterator = pedidosOrcamentos.iterator();
        for(int i = 1; i < posicao && iterator.hasNext(); i++){
            iterator.next();
        }
        IPedido pedido = null;
        if (iterator.hasNext()) pedido = iterator.next().clone();
        return pedido;
    }

    public ICliente get_cliente(String nif){
        ICliente cliente = null;
        if(clientes.containsKey(nif)) cliente = clientes.get(nif).clone();
        return cliente;
    }

    public void confirmar_orcamento(int num_ref) throws IOException {
        if(orcamentos.containsKey(num_ref)){
            orcamentos.get(num_ref).confirma();
            gravar_todos_orcamentos();
        }
    }

    public void remover_orcamento(int num_ref) throws IOException {
        orcamentos.remove(num_ref);
        pedidosCompletos.remove(num_ref);
        gravar_todos_orcamentos();
        gravar_todos_pedidos();
    }

    public Map<String,IUtilizador> get_utilizadores(){
        Map<String,IUtilizador> users = new HashMap<>();
        if(logged_gestor()){
            utilizadores.forEach((k,v)-> users.put(k,v.clone()));
        }
        return users;
    }

    public void remover_utilizador(String id) throws IOException {
        if(logged_gestor()){
            utilizadores.remove(id);
            gravar_todos_utilizadores();
        }
    }

}
