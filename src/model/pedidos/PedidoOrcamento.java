package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PedidoOrcamento implements IPedido {
    private String nif_cliente;
    private LocalDateTime data_registo;
    private LocalDateTime data_conclusao;
    private int numero_registo;
    private String descricao_pedido;

    public PedidoOrcamento(String nif_cliente, int numero_registo, String descricao_pedido){
        this.nif_cliente = nif_cliente;
        this.numero_registo = numero_registo;
        this.descricao_pedido = descricao_pedido;
        this.data_registo = LocalDateTime.now();
    }

    public PedidoOrcamento(String nif_cliente, int numero_registo, String descricao_pedido, LocalDateTime data_registo, LocalDateTime data_conclusao){
        this.nif_cliente = nif_cliente;
        this.numero_registo = numero_registo;
        this.descricao_pedido = descricao_pedido;
        this.data_registo = data_registo;
        this.data_conclusao = data_conclusao;
    }

    public PedidoOrcamento(){
        this.nif_cliente = "";
        this.numero_registo = -1;
        this.descricao_pedido = "";
        this.data_registo = null;
        this.data_conclusao = null;
    }

    public boolean valida(){
        return nif_cliente.length() > 0 && numero_registo > 0 && descricao_pedido.length() > 0 && data_registo != null;
    }

    public String get_nif_cliente() {
        return nif_cliente;
    }


    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 5) {
            this.nif_cliente = split[0];
            try{
                if(!split[1].equals("null")) this.data_registo = LocalDateTime.parse(split[1]);
                if(!split[2].equals("null")) this.data_conclusao = LocalDateTime.parse(split[2]);
                this.numero_registo = Integer.parseInt(split[3]);
            }
            catch(DateTimeParseException e){
                this.data_registo = null;
                this.data_conclusao = null;
            }
            catch(NumberFormatException e){
                this.numero_registo = -1;
            }
            this.descricao_pedido = split[4];

        }
    }


    public LocalDateTime get_tempo_registo(){
        return data_registo;
    }

    public int get_num_registo() {
        return this.numero_registo;
    }

    //tipoPedido@nifCliente;dataRegisto;dataConclusao;numeroRegistoEquipamento;descricaoPedido
    public String salvar(){
        StringBuilder sb = new StringBuilder();
        sb.append(nif_cliente).append(";");
        if(data_registo != null) sb.append(data_registo).append(";");
        else sb.append("null").append(";");
        if(data_conclusao != null) sb.append(data_conclusao).append(";");
        else sb.append("null").append(";");
        sb.append(numero_registo).append(";").append(descricao_pedido);
        return sb.toString();
    }

    public IPedido clone(){
        return new PedidoOrcamento(this.nif_cliente,this.numero_registo,this.descricao_pedido,this.data_registo,this.data_conclusao);
    }

    public void conclui_pedido(){
        this.data_conclusao = LocalDateTime.now();
    }

    public LocalDateTime get_data_conclusao() {
        return data_conclusao;
    }
}
