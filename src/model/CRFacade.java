package model;

import model.armazem.Armazem;
import model.armazem.Equipamento;
import model.comparators.IOrcamentoComparator;
import model.comparators.IPedidoComparator;
import model.excecoes.JaExistenteExcecao;
import model.interfaces.*;
import model.orcamento.Orcamento;
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
    private Set<IPedido> pedidosPendentes;
    private Map<Integer, IPedido> pedidosJaPlaneados;
    private Map<Integer, IPedido> pedidosCompletos;
    private Map<Integer, IOrcamento> orcamentos; //numero de registo do equipamento é a key
    private Armazem armazem;
    private IUtilizador logado;
    private Map<String,LogTecnico> logsTecnicos;
    private Map<String,LogFuncionario> logsFuncionarios;

    private String utilizadoresFN;
    private String clientesFN;
    private String armazemFN;
    private String pedidosFN;
    private String orcamentosFN;
    private String logsFN;

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";


    public CRFacade(String utilizadoresFN, String clientesFN, String armazemFN, String pedidosFN, String orcamentosFN, String logsFN) throws IOException {
        this.utilizadores = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pedidosPendentes = new TreeSet<IPedido>(new IPedidoComparator());
        this.armazem = new Armazem();
        this.orcamentos = new HashMap<>();
        this.pedidosJaPlaneados = new HashMap<>();
        this.pedidosCompletos = new HashMap<>();
        this.logsTecnicos = new HashMap<>();
        this.logsFuncionarios = new HashMap<>();

        this.utilizadoresFN = utilizadoresFN;
        this.clientesFN = clientesFN;
        this.armazemFN = armazemFN;
        this.pedidosFN = pedidosFN;
        this.orcamentosFN = orcamentosFN;
        this.logsFN = logsFN;

        try{
            carregar_cp();
        }
        catch(JaExistenteExcecao ignored){}
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

    public boolean disponibilidade_pedido_expresso(){
        return pedidosPendentes.size() == 0 || pedidosPendentes.stream().findFirst().get().getClass().equals(PedidoOrcamento.class);
    }

    public void completa_pedido_expresso() throws IOException {
        if(pedidosPendentes.size() > 0){
            IPedido pedido = pedidosPendentes.stream().findFirst().get();
            if(pedido.getClass().equals(PedidoExpresso.class)){
                pedidosPendentes.remove(pedido);
                transferencia_seccao(pedido.getNumeroRegistoEquipamento());
                adicionar_pedido_completo(pedido);
                gravar_todos_pedidos();
            }
        }
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

    public void adicionar_orcamento(IOrcamento orcamento) throws IOException {
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


    public void gerar_orcamento(IPlanoDeTrabalho plano) throws IOException {
        int num_referencia = plano.get_num_ref();
        remove_pedido_orcamento(num_referencia);
        transferencia_seccao(num_referencia);
        if(!orcamentos.containsKey(num_referencia)){
            IOrcamento orcamento = new Orcamento(plano);
            orcamentos.put(num_referencia,orcamento);
            gravar_orcamento(orcamento);
        }
    }


    public List<IOrcamento> get_orcamentos_por_confirmar() {
        return orcamentos.values().stream().filter(k->!k.getConfirmado()).map(IOrcamento::clone).collect(Collectors.toList());
    }

    public List<IOrcamento> get_orcamentos_confirmados() {
        Set<IOrcamento> set = new TreeSet<IOrcamento>(new IOrcamentoComparator());
        orcamentos.values().stream().filter(k -> k.getConfirmado() && pedidosJaPlaneados.containsKey(k.get_num_ref())).map(IOrcamento::clone).forEach(set::add);
        return set.stream().toList();
    }

    public List<IPedido> get_pedidos_completos() {
        List<IPedido> pedidos_completos = new ArrayList<>();
        pedidosCompletos.forEach((k,v)->{
            pedidos_completos.add(v.clone());
        });
        return pedidos_completos;
    }


    public IOrcamento get_orcamento(int num_ref) {
        IOrcamento orcamento = null;
        if(orcamentos.containsKey(num_ref)) orcamento= orcamentos.get(num_ref);
        if(orcamento != null )return orcamento.clone();
        else return null;
    }

    public IEquipamento getEquipamento(int num_ref) {
        return armazem.getEquipamento(num_ref).clone();
    }




    private void transferencia_seccao(int num_referencia) throws IOException {
        armazem.transferencia_seccao(num_referencia);
        gravar_todos_equipamento();
    }

    private void remove_pedido_orcamento(int num_referencia) throws IOException {
        Iterator<IPedido> iterator = pedidosPendentes.iterator();
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

    public void concluir_reparacao(IOrcamento orcamento) throws IOException {
        int num_ref = orcamento.get_num_ref();
        if(orcamentos.containsKey(num_ref)){
            orcamentos.remove(num_ref);
            orcamentos.put(num_ref,orcamento.clone());
            transferencia_seccao(num_ref);
            adicionar_pedido_completo(pedidosJaPlaneados.get(num_ref));
            remove_pedido_ja_planeado(num_ref);
            gravar_todos_orcamentos();
            gravar_todos_equipamento();
        }
    }


    private void adicionar_pedido_ja_planeado(IPedido pedido) {
        if(!pedidosJaPlaneados.containsKey(pedido.getNumeroRegistoEquipamento())){
            pedidosJaPlaneados.put(pedido.getNumeroRegistoEquipamento(),pedido);
        }
    }

    private void adicionar_pedido_completo(IPedido pedido) {
        pedido.concluiPedido();
        if(!pedidosCompletos.containsKey(pedido.getNumeroRegistoEquipamento())){
            pedidosCompletos.put(pedido.getNumeroRegistoEquipamento(),pedido);
        }
    }

    public void adicionar_log(String log, String user_id) throws IOException {
        IUtilizador u = get_utilizador_by_ID(user_id);
        if(u.getClass().equals(Tecnico.class)){
            if(logsTecnicos.containsKey(user_id)){
                logsTecnicos.get(user_id).addIntervencao(log);
            }else{
                LogTecnico l = new LogTecnico(user_id);
                l.addIntervencao(log);
                logsTecnicos.put(user_id,l);
            }
            gravar_todos_logs();
        }else if(u.getClass().equals(Funcionario.class)){
            if(logsFuncionarios.containsKey(user_id)){
                logsFuncionarios.get(user_id).addOperacao(log);
            }else{
                LogFuncionario l = new LogFuncionario(user_id);
                l.addOperacao(log);
                logsFuncionarios.put(user_id,l);
            }
            gravar_todos_logs();
        }
    }


    /**
     * Carrega utilizadores para o estado do sistema
     * @throws FileNotFoundException execão
     */

    private void carregar_utilizadores() throws IOException, JaExistenteExcecao {
        BufferedReader br = new BufferedReader(new FileReader(this.utilizadoresFN));
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

    private void carregar_orcamento(IOrcamento orcamento) throws JaExistenteExcecao, IOException {
        int num_referencia = orcamento.get_num_ref();
        if (!orcamentos.containsKey(num_referencia)) {
            orcamentos.put(num_referencia, orcamento);
            validade_orcamento(orcamento);
        } else throw new JaExistenteExcecao("Orcamento ja existe");
    }

    private void validade_orcamento(IOrcamento orcamento) throws IOException {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
        if(orcamento.getConfirmado() && !orcamento.getDataConfirmacao().isAfter(thirtyDaysAgo)) {
            recusa_orcamento(orcamento.get_num_ref());
        }
    }

    private boolean validade_pedido(IPedido pedido) throws IOException {
        boolean validade = true;
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().plusDays(-90);
        if(pedido.getDataConclusao() != null && !pedido.getDataConclusao().isAfter(ninetyDaysAgo)) {
            transferencia_seccao(pedido.getNumeroRegistoEquipamento());
            validade = false;
        }
        return validade;
    }


    /**
     * Carrega clientes para o estado do sistema
     * @throws FileNotFoundException  execão
     */
    private void carregar_clientes() throws IOException, JaExistenteExcecao {
        BufferedReader br = new BufferedReader(new FileReader(this.clientesFN));
        String linha;
        while((linha = br.readLine()) != null){
            ICliente cliente = new Cliente();
            cliente.carregar(linha);
            if(cliente.valida()) carregar_cliente(cliente);
        }
        br.close();
    }

    private void carregar_armazem() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.armazemFN));
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

    private void carregar_pedidos() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.pedidosFN));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split("@");
            if(split.length == 2){
                try{
                    int tipo = Integer.parseInt(split[0]);
                    IPedido pedido = null;
                    switch (tipo){
                        case 1, 3,4 -> pedido = new PedidoOrcamento();
                        case 2, 5 -> pedido = new PedidoExpresso();
                    }
                    if(pedido != null) {
                        pedido.carregar(split[1]);
                    }
                    if(pedido != null && pedido.valida() && valida_pedido(pedido,tipo)){
                        carregar_pedido(pedido,tipo);
                    }
                }
                catch (NumberFormatException ignored){}
            }
        }
        gravar_todos_pedidos();

        br.close();
    }

    private void carregar_pedido(IPedido pedido, int tipo) throws IOException {
        switch (tipo) {
            case 1, 2 -> pedidosPendentes.add(pedido);
            case 3 -> adicionar_pedido_ja_planeado(pedido);
            case 4, 5 -> {if(validade_pedido(pedido)) adicionar_pedido_completo(pedido);}
        }
    }

    private void carregar_orcamentos() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(orcamentosFN));
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
                        if (pedidosJaPlaneados.containsKey(numRegisto)) {
                            pedido = pedidosJaPlaneados.get(numRegisto);
                        }else if (pedidosCompletos.containsKey(numRegisto)) {
                            pedido = pedidosCompletos.get(numRegisto);
                        }else valido = false;
                        if(valido) {
                            IOrcamento orcamento = new Orcamento(numRegisto, pedido,confirmacao,dataRegisto);
                            orcamento.carregar(split[1]);
                            if (orcamento.valida()) {
                                carregar_orcamento(orcamento);
                            }
                        }
                    } catch (NumberFormatException | DateTimeParseException | JaExistenteExcecao ignored) {
                    }
                }
            }
        }
        gravar_todos_orcamentos();
        br.close();
    }


    private void carregar_logs() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(logsFN));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split("@");
            if(split.length == 2) {
                try {
                    int permissao = Integer.parseInt(split[0]);
                    if (permissao == 2) {
                        LogFuncionario log = new LogFuncionario();
                        log.carregar(split[1]);
                        if(log.valida()) {
                            logsFuncionarios.put(log.getUserId(),log);
                        }
                    } else if (permissao == 3) {
                        LogTecnico log = new LogTecnico();
                        log.carregar(split[1]);
                        if(log.valida()) {
                            logsTecnicos.put(log.getUserId(),log);
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        br.close();
    }


    private boolean valida_pedido(IPedido pedido, int tipo) {
        switch (tipo){
            case 1 -> {
                return clientes.containsKey(pedido.getNifCliente()) && armazem.contem_equipamento_para_orcamento(pedido.getNumeroRegistoEquipamento());}
            case 2,3 -> {
                return clientes.containsKey(pedido.getNifCliente()) && armazem.contem_equipamento_para_reparacao(pedido.getNumeroRegistoEquipamento());}
            case 4,5 -> {
                return clientes.containsKey(pedido.getNifCliente()) && armazem.contem_equipamento_pronto_a_entregar(pedido.getNumeroRegistoEquipamento());
            }
            default -> {
                return false;
            }
        }
    }

    public void carregar_cp() throws IOException, JaExistenteExcecao {
        carregar_utilizadores();
        carregar_clientes();
        carregar_armazem();
        carregar_pedidos();
        carregar_orcamentos();
        carregar_logs();
    }

    public void adicionar_pedido_orcamento(String nifCliente, String modelo, String descricaoEquipamento, String descricaoPedido) throws IOException {
        if(clientes.containsKey(nifCliente)){
            IEquipamento e = new Equipamento(nifCliente, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo,descricaoEquipamento );
            armazem.regista_equipamento(e,1);
            IPedido pedido = new PedidoOrcamento(nifCliente, e.getNumeroRegisto(), descricaoPedido);
            pedidosPendentes.add(pedido);
            String log = "0;"+pedido.getTempoRegisto();
            adicionar_log(log,get_logged_id());
            gravar_pedido(pedido);
            gravar_equipamento(e,1);
        }
    }

    public void adicionar_pedido_expresso(String nifCliente, String modelo, String descricaoEquipamento, int tipo) throws IOException {
        if(clientes.containsKey(nifCliente)){
            IEquipamento e = new Equipamento(nifCliente, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo,descricaoEquipamento );
            armazem.regista_equipamento(e,2);
            IPedido pedido = new PedidoExpresso(nifCliente, e.getNumeroRegisto(), tipo);
            pedidosPendentes.add(pedido);
            String log = "0;"+pedido.getTempoRegisto();
            adicionar_log(log,get_logged_id());
            gravar_pedido(pedido);
            gravar_equipamento(e,2);
        }
    }

    public IPedido get_pedido_expresso() {
        if(pedidosPendentes.size() > 0){
            IPedido p = pedidosPendentes.stream().findFirst().get();
            if(p.getClass().equals(PedidoExpresso.class)){
                return p.clone();
            }
        }
        return null;
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

    private void gravar_cliente (ICliente cliente) throws IOException {
        FileWriter w = new FileWriter(this.clientesFN,true);
        w.write(cliente.salvar()+"\n");
        w.close();
    }

    private void gravar_todos_clientes () throws IOException {
        FileWriter w = new FileWriter(this.clientesFN);
        clientes.forEach((k,v)-> {
            try {
                w.write(v.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    private void gravar_utilizador (IUtilizador utilizador) throws IOException {
        FileWriter w = new FileWriter(this.utilizadoresFN,true);
        w.write(utilizador.salvar()+"\n");
        w.close();
    }

    private void gravar_todos_utilizadores () throws IOException {
        FileWriter w = new FileWriter(this.utilizadoresFN);
        utilizadores.forEach((k,v)-> {
            try {
                w.write(v.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    private void gravar_equipamento(IEquipamento equipamento, int local) throws IOException {
        FileWriter w = new FileWriter(this.armazemFN,true);
        w.write(equipamento.salvar()+"@"+local+"\n");
        w.close();
    }

    private void gravar_pedido(IPedido pedido) throws IOException {
        FileWriter w = new FileWriter(this.pedidosFN,true);
        int tipo = 1;
        if(pedido.getClass().equals(PedidoExpresso.class))
            tipo = 2;
        w.write(tipo+"@"+pedido.salvar()+"\n");
        w.close();
    }

    private void gravar_orcamento(IOrcamento orcamento) throws IOException {
        FileWriter w = new FileWriter(this.orcamentosFN,true);
        w.write(orcamento.salvar()+"\n");
        w.close();
    }

    private void gravar_todos_pedidos() throws IOException {
        FileWriter w = new FileWriter(this.pedidosFN);
        pedidosPendentes.forEach(k-> {
            int tipo = 1;
            if(k.getClass().equals(PedidoExpresso.class))
                tipo = 2;
            try {
                w.write(tipo+"@"+k.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pedidosJaPlaneados.forEach((v,k)-> {
            try {
                w.write("3@"+k.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pedidosCompletos.forEach((v,k)-> {
            int tipo = 4;
            if(k.getClass().equals(PedidoExpresso.class))
                tipo = 5;
            try {
                w.write(tipo+"@"+k.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    private void gravar_todos_equipamento() throws IOException {
        FileWriter w = new FileWriter(this.armazemFN);
        w.write(armazem.salvar());
        w.close();
    }

    private void gravar_todos_orcamentos() throws IOException {
        FileWriter w = new FileWriter(this.orcamentosFN);
        orcamentos.forEach((k,v)-> {
            try {
                w.write(v.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }



    private void gravar_todos_logs() throws IOException {
        FileWriter w = new FileWriter(this.logsFN);
        logsTecnicos.forEach((k,v)-> {
            try {
                w.write(v.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logsFuncionarios.forEach((k,v)-> {
            try {
                w.write(v.salvar()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        w.close();
    }

    public List<String> get_pedidos_orcamento(){
        List<String> lista = new ArrayList<String>(pedidosPendentes.size());

        Iterator<IPedido> iterator = pedidosPendentes.iterator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        for(int i = 0; i < 10 && iterator.hasNext();){
            IPedido p = iterator.next();
            if(p.getClass().equals(PedidoOrcamento.class)){
                i++;
                lista.add("DATA:["+p.getTempoRegisto().format(formatter) + "] CLIENTE:["+p.getNifCliente()+ "] EQUIPAMENTO:[#"+p.getNumeroRegistoEquipamento()+"]");
            }
        }
        return lista;
    }

    public IPedido get_pedido_orcamento(int posicao){
        Iterator<IPedido> iterator = pedidosPendentes.iterator();
        for(int i = 1; i < posicao && iterator.hasNext();){
            IPedido pedido = iterator.next();
            if(pedido.getClass().equals(PedidoOrcamento.class)) i++;
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

    public void recusa_orcamento(int num_ref) throws IOException {
        if(orcamentos.containsKey(num_ref)){
            IOrcamento orcamento = orcamentos.get(num_ref);
            orcamentos.remove(num_ref);
            IPedido pedido = orcamento.get_pedido();
            pedidosCompletos.put(num_ref,pedido);
            pedidosJaPlaneados.remove(num_ref);
            transferencia_seccao(num_ref);
            gravar_todos_orcamentos();
            gravar_todos_pedidos();
        }
    }

    public void remover_orcamento(int num_ref) throws IOException {
        orcamentos.remove(num_ref);
        pedidosCompletos.remove(num_ref);
        String log = "1;"+LocalDateTime.now();
        adicionar_log(log,get_logged_id());
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

    public IEquipamento get_equipamento(int num_ref){
        return armazem.getEquipamento(num_ref);
    }

    public String get_logs_tecnicos_simples(){
        StringBuilder sb = new StringBuilder();
        logsTecnicos.forEach((k,v)->{
            sb.append(v.getUserId()).append("-> Passos [").append(v.get_numero_passos_completos()).append("] ")
                    .append("Pedidos Expresso [").append(v.get_numero_reparacoes_expresso()).append("] ")
                    .append("Duracao Media [").append(v.get_media_duracao_real()).append("] ")
                    .append("Desvio Duracao Media [").append(v.get_media_duracao_esperada()-v.get_media_duracao_real()).append("]\n");
        });
        return sb.toString();
    }


    public String get_logs_funcionarios(){
        StringBuilder sb = new StringBuilder();
        logsFuncionarios.forEach((k,v)->{
            sb.append(v.getUserId()).append("-> Rececoes [").append(v.get_numero_rececoes()).append("] ")
                    .append("Entregas [").append(v.get_numero_entregas()).append("]\n");
        });
        return sb.toString();
    }

    public List<LogTecnico> get_logs_tecnicos_simples_extensivos(){
        List<LogTecnico> tecnicos = new ArrayList<>();
        logsTecnicos.forEach((k,v)->{
            if(exists_user(v.getUserId())) tecnicos.add(v);
        });
        return tecnicos;
    }

}
