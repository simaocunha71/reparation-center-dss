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
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CRFacade implements ICentroReparacoes {

    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema
    private Map<String, ICliente> clientes;//map com clientes do sistema
    private Set<IPedido> pedidos_pendentes;
    private Map<Integer, IPedido> pedidos_ja_planeados;
    private Map<Integer, IPedido> pedidos_completos;
    private Map<Integer, IOrcamento> orcamentos; //numero de registo do equipamento é a key
    private Armazem armazem;
    private IUtilizador logado;
    private Map<String,LogTecnico> logs_tecnicos;
    private Map<String,LogFuncionario> logs_funcionarios;

    private final String utilizadores_FN;
    private final String clientes_FN;
    private final String armazem_FN;
    private final String pedidos_FN;
    private final String orcamentos_FN;
    private final String logs_FN;



    public CRFacade(String utilizadores_FN, String clientes_FN, String armazem_FN, String pedidos_FN, String orcamentos_FN, String logs_FN) {
        this.utilizadores = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pedidos_pendentes = new TreeSet<>(new IPedidoComparator());
        this.armazem = new Armazem();
        this.orcamentos = new HashMap<>();
        this.pedidos_ja_planeados = new HashMap<>();
        this.pedidos_completos = new HashMap<>();
        this.logs_tecnicos = new HashMap<>();
        this.logs_funcionarios = new HashMap<>();

        this.utilizadores_FN = utilizadores_FN;
        this.clientes_FN = clientes_FN;
        this.armazem_FN = armazem_FN;
        this.pedidos_FN = pedidos_FN;
        this.orcamentos_FN = orcamentos_FN;
        this.logs_FN = logs_FN;

        try{
            carregar_cp();
        }
        catch(JaExistenteExcecao | IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Procura de utilizador no map de utilizadores
     * @param id id
     * @return  IUtilizador
     */
    public IUtilizador get_utilizador(String id){
        return utilizadores.get(id);
    }

    public String get_logged_id(){
        return logado.get_id();
    }

    public void adicionar_utilizador(String id,String nome,String password,int permissao) throws JaExistenteExcecao {
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
                if (!(nome.equals(utilizador.get_nome()) && password.equals(utilizador.get_password()))) {
                    utilizadores.remove(id);
                    utilizadores.put(id,utilizador);
                    gravar_todos_utilizadores();
                }
                throw new JaExistenteExcecao("Utilizador já existe no sistema");
            }
        }
    }


    private void carregar_cliente(ICliente cliente) throws JaExistenteExcecao {
        if(!clientes.containsKey(cliente.get_nif())){
            clientes.put(cliente.get_nif(),cliente.clone());
        }else{throw  new JaExistenteExcecao("Cliente já existe no sistema");}
    }

    public boolean disponibilidade_pedido_expresso(){
        return pedidos_pendentes.size() == 0 || pedidos_pendentes.stream().findFirst().get().getClass().equals(PedidoOrcamento.class);
    }

    public void completa_pedido_expresso() {
        if(pedidos_pendentes.size() > 0){
            IPedido pedido = pedidos_pendentes.stream().findFirst().get();
            if(pedido.getClass().equals(PedidoExpresso.class)){
                pedidos_pendentes.remove(pedido);
                transferencia_seccao(pedido.get_num_registo());
                adicionar_pedido_completo(pedido);
                gravar_todos_pedidos();
            }
        }
    }

    /**
     * Adiciona um cliente ao map de clientes
     * @param nif nif do cliente que representa o seu id
     * @param nome nome do cliente
     * @param numero_telemovel número de telemóvel
     * @param email email
     * @throws JaExistenteExcecao exceção
     */

    public void adicionar_cliente(String nif, String nome, String numero_telemovel, String email) throws JaExistenteExcecao {
        if(!clientes.containsKey(nif)){
            clientes.put(nif,new Cliente(nif,nome, numero_telemovel,email));
            gravar_cliente(clientes.get(nif));
        }else {
            ICliente cliente = clientes.get(nif);
            if(!(nome.equals(cliente.get_nome()) && numero_telemovel.equals(cliente.get_num_telemovel()) && email.equals(cliente.get_email()))){
                clientes.remove(nif);
                clientes.put(nif, new Cliente(nif, nome, numero_telemovel, email));
                gravar_todos_clientes();
            }
            throw new JaExistenteExcecao("Cliente já existe no sistema, dados foram overwrited!");
        }
    }

    public void adicionar_orcamento(IOrcamento orcamento) {
        int num_ref = orcamento.get_num_registo();
        if(!orcamentos.containsKey(num_ref)){
            orcamentos.put(num_ref,orcamento.clone());
            gravar_orcamento(orcamentos.get(num_ref));
        }else {
            orcamentos.remove(num_ref);
            orcamentos.put(num_ref,orcamento.clone());
            gravar_todos_orcamentos();
        }
    }


    public void gerar_orcamento(IPlanoDeTrabalho plano) {
        int num_referencia = plano.get_pedido().get_num_registo();
        remove_pedido_orcamento(num_referencia);
        transferencia_seccao(num_referencia);
        if(!orcamentos.containsKey(num_referencia)){
            IOrcamento orcamento = new Orcamento(plano);
            orcamentos.put(num_referencia,orcamento);
            gravar_orcamento(orcamento);
        }
    }


    public List<IOrcamento> get_orcamentos_por_confirmar() {
        return orcamentos.values().stream().filter(k->!k.get_confirmado()).map(IOrcamento::clone).collect(Collectors.toList());
    }

    public List<IOrcamento> get_orcamentos_confirmados() {
        Set<IOrcamento> set = new TreeSet<>(new IOrcamentoComparator());
        orcamentos.values().stream().filter(k -> k.get_confirmado() && pedidos_ja_planeados.containsKey(k.get_num_registo())).map(IOrcamento::clone).forEach(set::add);
        return set.stream().toList();
    }

    public List<IPedido> get_pedidos_completos() {
        List<IPedido> pedidos_completos = new ArrayList<>();
        this.pedidos_completos.forEach((k, v)-> pedidos_completos.add(v.clone()));
        return pedidos_completos;
    }


    public IOrcamento get_orcamento(int num_ref) {
        IOrcamento orcamento = null;
        if(orcamentos.containsKey(num_ref)) orcamento= orcamentos.get(num_ref);
        if(orcamento != null )return orcamento.clone();
        else return null;
    }

    private void transferencia_seccao(int num_referencia) {
        armazem.transferencia_seccao(num_referencia);
        gravar_todos_equipamento();
    }

    private void remove_pedido_orcamento(int num_referencia) {
        Iterator<IPedido> iterator = pedidos_pendentes.iterator();
        boolean encontrado = false;
        while(iterator.hasNext() && !encontrado){
            IPedido pedido = iterator.next();
            if(pedido.get_num_registo() == num_referencia){
                adicionar_pedido_ja_planeado(pedido);
                iterator.remove();
                encontrado = true;
            }
        }
        if(encontrado) gravar_todos_pedidos();
    }

    private void remove_pedido_ja_planeado(int num_referencia) {
        if(pedidos_ja_planeados.containsKey(num_referencia)) {
            pedidos_ja_planeados.remove(num_referencia);
            gravar_todos_pedidos();
        }
    }

    public void concluir_reparacao(IOrcamento orcamento) {
        int num_ref = orcamento.get_num_registo();
        if(orcamentos.containsKey(num_ref)){
            orcamentos.remove(num_ref);
            orcamentos.put(num_ref,orcamento.clone());
            transferencia_seccao(num_ref);
            adicionar_pedido_completo(pedidos_ja_planeados.get(num_ref));
            remove_pedido_ja_planeado(num_ref);
            gravar_todos_orcamentos();
            gravar_todos_equipamento();
        }
    }


    private void adicionar_pedido_ja_planeado(IPedido pedido) {
        if(!pedidos_ja_planeados.containsKey(pedido.get_num_registo())){
            pedidos_ja_planeados.put(pedido.get_num_registo(),pedido);
        }
    }

    private void adicionar_pedido_completo(IPedido pedido) {
        pedido.conclui_pedido();
        if(!pedidos_completos.containsKey(pedido.get_num_registo())){
            pedidos_completos.put(pedido.get_num_registo(),pedido);
        }
    }

    public void adicionar_log(String log, String user_id) {
        IUtilizador u = get_utilizador(user_id);
        if(u.getClass().equals(Tecnico.class)){
            if(logs_tecnicos.containsKey(user_id)){
                logs_tecnicos.get(user_id).addIntervencao(log);
            }else{
                LogTecnico l = new LogTecnico(user_id);
                l.addIntervencao(log);
                logs_tecnicos.put(user_id,l);
            }
            gravar_todos_logs();
        }else if(u.getClass().equals(Funcionario.class)){
            if(logs_funcionarios.containsKey(user_id)){
                logs_funcionarios.get(user_id).addOperacao(log);
            }else{
                LogFuncionario l = new LogFuncionario(user_id);
                l.addOperacao(log);
                logs_funcionarios.put(user_id,l);
            }
            gravar_todos_logs();
        }
    }


    /**
     * Carrega utilizadores para o estado do sistema
     * @throws FileNotFoundException execão
     */

    private void carregar_utilizadores() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.utilizadores_FN));
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
            utilizadores.remove(utilizador.get_id());
            utilizadores.put(utilizador.get_id(),utilizador);
        }
    }

    private void carregar_gestor(String s) {
        IUtilizador utilizador = new Gestor();
        utilizador.carregar(s);
        if (utilizador.valida()) {
            utilizadores.remove(utilizador.get_id());
            utilizadores.put(utilizador.get_id(),utilizador);
        }
    }

    private void carregar_tecnico(String s) {
        IUtilizador utilizador = new Tecnico();
        utilizador.carregar(s);
        if (utilizador.valida()) {
            utilizadores.remove(utilizador.get_id());
            utilizadores.put(utilizador.get_id(), utilizador);
        }
    }

    private void carregar_orcamento(IOrcamento orcamento) throws JaExistenteExcecao {
        int num_referencia = orcamento.get_num_registo();
        if (!orcamentos.containsKey(num_referencia)) {
            orcamentos.put(num_referencia, orcamento);
            validade_orcamento(orcamento);
        } else throw new JaExistenteExcecao("Orcamento ja existe");
    }

    private void validade_orcamento(IOrcamento orcamento) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().plusDays(-30);
        if(orcamento.get_confirmado() && !orcamento.get_data_confirmacao().isAfter(thirtyDaysAgo)) {
            recusa_orcamento(orcamento.get_num_registo());
        }
    }

    private boolean validade_pedido(IPedido pedido) {
        boolean validade = true;
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().plusDays(-90);
        if(pedido.get_data_conclusao() != null && !pedido.get_data_conclusao().isAfter(ninetyDaysAgo)) {
            transferencia_seccao(pedido.get_num_registo());
            validade = false;
        }
        return validade;
    }


    /**
     * Carrega clientes para o estado do sistema
     * @throws FileNotFoundException  execão
     */
    private void carregar_clientes() throws IOException, JaExistenteExcecao {
        BufferedReader br = new BufferedReader(new FileReader(this.clientes_FN));
        String linha;
        while((linha = br.readLine()) != null){
            ICliente cliente = new Cliente();
            cliente.carregar(linha);
            if(cliente.valida()) carregar_cliente(cliente);
        }
        br.close();
    }

    private void carregar_armazem() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.armazem_FN));
        String linha;
        String[] split;
        while((linha = br.readLine()) != null){
            split = linha.split("@");
            if(split.length == 2){
                IEquipamento equipamento = new Equipamento();
                equipamento.carregar(split[0]);
                try {
                    int local = Integer.parseInt(split[1]);
                    if (equipamento.valida()) armazem.regista_equipamento(equipamento,local);
                }catch (NumberFormatException ignored){}
            }
        }
        br.close();
    }

    private void carregar_pedidos() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.pedidos_FN));
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

    private void carregar_pedido(IPedido pedido, int tipo) {
        switch (tipo) {
            case 1, 2 -> pedidos_pendentes.add(pedido);
            case 3 -> adicionar_pedido_ja_planeado(pedido);
            case 4, 5 -> {if(validade_pedido(pedido)) adicionar_pedido_completo(pedido);}
        }
    }

    private void carregar_orcamentos() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(orcamentos_FN));
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
                        if (pedidos_ja_planeados.containsKey(numRegisto)) {
                            pedido = pedidos_ja_planeados.get(numRegisto);
                        }else if (pedidos_completos.containsKey(numRegisto)) {
                            pedido = pedidos_completos.get(numRegisto);
                        }else valido = false;
                        if(valido) {
                            IOrcamento orcamento = new Orcamento(numRegisto, pedido,confirmacao,dataRegisto);
                            orcamento.carregar(split[1]);
                            if (orcamento.valida()) {
                                carregar_orcamento(orcamento);
                            }
                        }
                    } catch (NumberFormatException | DateTimeParseException | JaExistenteExcecao ignored) {}
                }
            }
        }
        gravar_todos_orcamentos();
        br.close();
    }


    private void carregar_logs() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(logs_FN));
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
                            logs_funcionarios.put(log.getUserId(),log);
                        }
                    } else if (permissao == 3) {
                        LogTecnico log = new LogTecnico();
                        log.carregar(split[1]);
                        if(log.valida()) {
                            logs_tecnicos.put(log.getUserId(),log);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        br.close();
    }


    private boolean valida_pedido(IPedido pedido, int tipo) {
        switch (tipo){
            case 1 -> {
                return clientes.containsKey(pedido.get_nif_cliente()) && armazem.contem_equipamento_para_orcamento(pedido.get_num_registo());}
            case 2,3 -> {
                return clientes.containsKey(pedido.get_nif_cliente()) && armazem.contem_equipamento_para_reparacao(pedido.get_num_registo());}
            case 4,5 -> {
                return clientes.containsKey(pedido.get_nif_cliente()) && armazem.contem_equipamento_pronto_a_entregar(pedido.get_num_registo());
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

    public void adicionar_pedido_orcamento(String cliente_nif, String modelo, String descricao_equipamento, String descricao_pedido) {
        if(clientes.containsKey(cliente_nif)){
            IEquipamento e = new Equipamento(cliente_nif, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo, descricao_equipamento);
            armazem.regista_equipamento(e,1);
            IPedido pedido = new PedidoOrcamento(cliente_nif, e.get_numero_registo(), descricao_pedido);
            pedidos_pendentes.add(pedido);
            String log = "0;"+pedido.get_tempo_registo();
            adicionar_log(log,get_logged_id());
            gravar_pedido(pedido);
            gravar_equipamento(e,1);
        }
    }

    public void adicionar_pedido_expresso(String nifCliente, String modelo, String descricaoEquipamento, int tipo) {
        if(clientes.containsKey(nifCliente)){
            IEquipamento e = new Equipamento(nifCliente, armazem.get_ultimo_numero_de_registo_equipamento()+1,modelo,descricaoEquipamento );
            armazem.regista_equipamento(e,2);
            IPedido pedido = new PedidoExpresso(nifCliente, e.get_numero_registo(), tipo);
            pedidos_pendentes.add(pedido);
            String log = "0;"+pedido.get_tempo_registo();
            adicionar_log(log,get_logged_id());
            gravar_pedido(pedido);
            gravar_equipamento(e,2);
        }
    }

    public IPedido get_pedido_expresso() {
        if(pedidos_pendentes.size() > 0){
            IPedido p = pedidos_pendentes.stream().findFirst().get();
            if(p.getClass().equals(PedidoExpresso.class)){
                return p.clone();
            }
        }
        return null;
    }


    public boolean login(String nomeDeUtilizador, String password) {
        if (utilizadores.containsKey(nomeDeUtilizador)){
            if(utilizadores.get(nomeDeUtilizador).get_password().equals(password)) {
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

    public boolean existe_utilizador(String id){
        return utilizadores.containsKey(id);
    }

    public boolean existe_cliente(String nif){
        return clientes.containsKey(nif);
    }

    public int get_ultimo_numero_de_registo_equipamento() {
        return armazem.get_ultimo_numero_de_registo_equipamento();
    }

    private void gravar_cliente (ICliente cliente){
        try {
            FileWriter w = new FileWriter(this.clientes_FN, true);
            w.write(cliente.salvar() + "\n");
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_todos_clientes (){
        try {
            FileWriter w = new FileWriter(this.clientes_FN);
            clientes.forEach((k, v) -> {
                try {
                    w.write(v.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            w.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_utilizador (IUtilizador utilizador) {
        try {
            FileWriter w = new FileWriter(this.utilizadores_FN, true);
            w.write(utilizador.salvar() + "\n");
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_todos_utilizadores () {
        try {
            FileWriter w = new FileWriter(this.utilizadores_FN);
            utilizadores.forEach((k, v) -> {
                try {
                    w.write(v.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            w.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_equipamento(IEquipamento equipamento, int local) {
        try {
            FileWriter w = new FileWriter(this.armazem_FN, true);
            w.write(equipamento.salvar() + "@" + local + "\n");
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_pedido(IPedido pedido) {
        try {
            FileWriter w = new FileWriter(this.pedidos_FN, true);
            int tipo = 1;
            if (pedido.getClass().equals(PedidoExpresso.class))
                tipo = 2;
            w.write(tipo + "@" + pedido.salvar() + "\n");
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_orcamento(IOrcamento orcamento) {
        try {
            FileWriter w = new FileWriter(this.orcamentos_FN, true);
            w.write(orcamento.salvar() + "\n");
            w.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_todos_pedidos() {
        try {
            FileWriter w = new FileWriter(this.pedidos_FN);
            pedidos_pendentes.forEach(k -> {
                int tipo = 1;
                if (k.getClass().equals(PedidoExpresso.class))
                    tipo = 2;
                try {
                    w.write(tipo + "@" + k.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pedidos_ja_planeados.forEach((v, k) -> {
                try {
                    w.write("3@" + k.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pedidos_completos.forEach((v, k) -> {
                int tipo = 4;
                if (k.getClass().equals(PedidoExpresso.class))
                    tipo = 5;
                try {
                    w.write(tipo + "@" + k.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_todos_equipamento() {
        try{
        FileWriter w = new FileWriter(this.armazem_FN);
        w.write(armazem.salvar());
        w.close();}
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void gravar_todos_orcamentos() {
        try {
            FileWriter w = new FileWriter(this.orcamentos_FN);
            orcamentos.forEach((k, v) -> {
                try {
                    w.write(v.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



    private void gravar_todos_logs() {
        try {
            FileWriter w = new FileWriter(this.logs_FN);
            logs_tecnicos.forEach((k, v) -> {
                try {
                    w.write(v.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            logs_funcionarios.forEach((k, v) -> {
                try {
                    w.write(v.salvar() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            w.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<IPedido> get_pedidos_orcamento(){
        List<IPedido> lista = new ArrayList<>(pedidos_pendentes.size());

        Iterator<IPedido> iterator = pedidos_pendentes.iterator();
        for(int i = 0; i < 10 && iterator.hasNext();){
            IPedido p = iterator.next();
            if(p.getClass().equals(PedidoOrcamento.class)){
                i++;
                lista.add(p.clone());
            }
        }
        return lista;
    }

    public ICliente get_cliente(String nif){
        ICliente cliente = null;
        if(clientes.containsKey(nif)) cliente = clientes.get(nif).clone();
        return cliente;
    }

    public void confirmar_orcamento(int num_reg) {
        if(orcamentos.containsKey(num_reg)){
            orcamentos.get(num_reg).confirma();
            gravar_todos_orcamentos();
        }
    }

    public void recusa_orcamento(int num_ref) {
        if(orcamentos.containsKey(num_ref)){
            IOrcamento orcamento = orcamentos.get(num_ref);
            orcamentos.remove(num_ref);
            IPedido pedido = orcamento.get_plano_de_trabalho().get_pedido();
            pedidos_completos.put(num_ref,pedido);
            pedidos_ja_planeados.remove(num_ref);
            transferencia_seccao(num_ref);
            gravar_todos_orcamentos();
            gravar_todos_pedidos();
        }
    }

    public void remover_orcamento(int num_ref) {
        orcamentos.remove(num_ref);
        pedidos_completos.remove(num_ref);
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

    public void remover_utilizador(String id) {
        if(logged_gestor()){
            utilizadores.remove(id);
            gravar_todos_utilizadores();
        }
    }

    public IEquipamento get_equipamento(int num_ref){
        return armazem.get_equipamento(num_ref);
    }

    public String get_logs_tecnicos_simples(){
        StringBuilder sb = new StringBuilder();
        logs_tecnicos.forEach((k, v)-> sb.append(v.getUserId()).append("-> Passos [").append(v.get_numero_passos_completos()).append("] ")
                .append("Pedidos Expresso [").append(v.get_numero_reparacoes_expresso()).append("] ")
                .append("Duracao Media [").append(v.get_media_duracao_real()).append("] ")
                .append("Desvio Duracao Media [").append(v.get_media_duracao_esperada()-v.get_media_duracao_real()).append("]\n"));
        return sb.toString();
    }


    public String get_logs_funcionarios(){
        StringBuilder sb = new StringBuilder();
        logs_funcionarios.forEach((k, v)-> sb.append(v.getUserId()).append("-> Rececoes [").append(v.get_numero_rececoes()).append("] ")
                .append("Entregas [").append(v.get_numero_entregas()).append("]\n"));
        return sb.toString();
    }

    public List<LogTecnico> get_logs_tecnicos_extensivos(){
        List<LogTecnico> tecnicos = new ArrayList<>();
        logs_tecnicos.forEach((k, v)->{
            if(existe_utilizador(v.getUserId())) tecnicos.add(v);
        });
        return tecnicos;
    }

}
