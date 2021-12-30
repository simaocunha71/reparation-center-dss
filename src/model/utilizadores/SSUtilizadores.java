package model.utilizadores;

import model.excecoes.JaExistenteExcecao;
import model.interfaces.IGestUtilizadores;
import model.interfaces.IUtilizador;

import java.util.HashMap;
import java.util.Map;

public class SSUtilizadores implements IGestUtilizadores {
    private Map<String, IUtilizador> utilizadores; //map com utilizadores do sistema

    public SSUtilizadores(){
        this.utilizadores = new HashMap<>();
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
            } else {
                if (!(nome.equals(utilizador.get_nome()) && password.equals(utilizador.get_password()))) {
                    utilizadores.remove(id);
                    utilizadores.put(id,utilizador);
                }
                throw new JaExistenteExcecao("Utilizador já existe no sistema");
            }
        }
    }

    public IUtilizador get_utilizador(String id) {
        return utilizadores.get(id).clone();
    }

    public boolean existe_utilizador(String nomeDeUtilizador) {
        return utilizadores.containsKey(nomeDeUtilizador);
    }

    public void carregar(String string) {
        String [] split = string.split("@");
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

    public String salvar() {
        StringBuilder sb = new StringBuilder();
        utilizadores.forEach((k, v)->sb.append(v.salvar()).append("\n"));
        return sb.toString();
    }

    public Map<String,IUtilizador> get_utilizadores(){
        Map<String,IUtilizador> users = new HashMap<>();
        utilizadores.forEach((k,v)-> users.put(k,v.clone()));
        return users;
    }

    public void remover_utilizador(String id) {
        utilizadores.remove(id);
    }
}
