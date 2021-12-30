package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PedidoExpresso implements IPedido {
    private String nif_cliente;
    private LocalDateTime data_registo;
    private LocalDateTime data_conclusao;
    private int numero_registo;
    private int tipo;
    private int custo_fixo;
    private String descricao_pedido;


    public PedidoExpresso(){
        this.nif_cliente = "";
        this.tipo = -1;
        this.custo_fixo = 0;
        this.numero_registo = -1;
        this.data_registo = null;
        this.data_conclusao = null;
        this.descricao_pedido = "";
    }

    public PedidoExpresso(String nif_cliente, int numeroRegisto, int tipo) {
        this.nif_cliente = nif_cliente;
        this.numero_registo = numeroRegisto;
        set_tipo(tipo);
        this.data_registo = LocalDateTime.now();
    }

    public PedidoExpresso(String nif_cliente, int numero_registo, LocalDateTime data_registo, LocalDateTime data_conclusao, int tipo) {
        this.nif_cliente = nif_cliente;
        this.numero_registo = numero_registo;
        set_tipo(tipo);
        this.data_registo = data_registo;
        this.data_conclusao = data_conclusao;
    }

    public String get_nif_cliente() {
        return nif_cliente;
    }

    public IPedido clone() {
        return new PedidoExpresso(this.nif_cliente,this.numero_registo,this.data_registo,this.data_conclusao,this.tipo);
    }

    public boolean valida(){
        return nif_cliente.length() == 9 && data_registo != null && numero_registo > 0 && tipo > 0 && tipo < 5;
    }

    public String get_descricao_pedido() {
        return descricao_pedido;
    }

    public int get_custo_fixo() {
        return custo_fixo;
    }

    //123456789;2021-12-29T00:12:19.751437100;5;1
    //idCliente;data;num_reg;tipo
    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 5) {
            this.nif_cliente = split[0];
            try{
                if(!split[1].equals("null")) this.data_registo = LocalDateTime.parse(split[1]);
                if(!split[2].equals("null")) this.data_conclusao = LocalDateTime.parse(split[2]);
                this.numero_registo = Integer.parseInt(split[3]);
                set_tipo(Integer.parseInt(split[4]));
            }
            catch(DateTimeParseException e){
                this.data_registo = null;
                this.data_conclusao = null;
            }
            catch(NumberFormatException e){
                this.numero_registo = -1;
                this.tipo = -1;
            }
        }
    }

    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(nif_cliente).append(";");
        if(data_registo != null) sb.append(data_registo).append(";");
        else sb.append("null").append(";");
        if(data_conclusao != null) sb.append(data_conclusao).append(";");
        else sb.append("null").append(";");
        sb.append(numero_registo).append(";").append(tipo);
        return sb.toString();
    }

    private void set_tipo(int tipo) {
        if(tipo > 0 && tipo < 5) this.tipo = tipo;
        else this.tipo = -1;
        switch (tipo){
            case 1 ->{this.descricao_pedido = "trocar ecrã"; this.custo_fixo = 50;}
            case 2 ->{this.descricao_pedido = "instalar sistema operativo"; this.custo_fixo = 20;}
            case 3 ->{this.descricao_pedido = "trocar bateria"; this.custo_fixo = 25;}
            case 4 ->{this.descricao_pedido = "limpar equipamento"; this.custo_fixo = 10;}
            default -> {this.descricao_pedido = ""; this.custo_fixo = 0;}
        }
    }

    public LocalDateTime get_tempo_registo(){
        return data_registo;
    }

    public int get_num_registo() {
        return this.numero_registo;
    }

    public void conclui_pedido(){
        this.data_conclusao = LocalDateTime.now();
    }

    public LocalDateTime get_data_conclusao() {
        return data_conclusao;
    }
}
