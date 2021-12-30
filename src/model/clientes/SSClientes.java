package model.clientes;

import model.excecoes.JaExistenteExcecao;
import model.interfaces.ICliente;
import model.interfaces.IGestClientes;

import java.util.HashMap;
import java.util.Map;

public class SSClientes implements IGestClientes {
    private Map<String, ICliente> clientes;//map com clientes do sistema

    public SSClientes(){
        this.clientes = new HashMap<>();
    }

    public void adicionar_cliente(String nif, String nome, String numero_telemovel, String email) throws JaExistenteExcecao {
        if(!clientes.containsKey(nif)){
            clientes.put(nif,new Cliente(nif,nome, numero_telemovel,email));
        }else {
            ICliente cliente = clientes.get(nif);
            if(!(nome.equals(cliente.get_nome()) && numero_telemovel.equals(cliente.get_num_telemovel()) && email.equals(cliente.get_email()))){
                clientes.remove(nif);
                clientes.put(nif, new Cliente(nif, nome, numero_telemovel, email));
            }
            throw new JaExistenteExcecao("Cliente já existe no sistema!");
        }
    }


    public ICliente get_cliente(String nif) {
        return clientes.get(nif).clone();
    }

    public boolean existe_cliente(String nif) {
        return clientes.containsKey(nif);
    }

    public void carregar_cliente(ICliente cliente) {
        clientes.put(cliente.get_nif(),cliente.clone());
    }


    public void carregar(String string) {

    }

    public String salvar() {
        StringBuilder sb = new StringBuilder();
        clientes.forEach((k, v)->sb.append(v.salvar()).append("\n"));
        return sb.toString();
    }




}
