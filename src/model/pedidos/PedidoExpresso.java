package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;

public class PedidoExpresso implements IPedido {
    private String idPedido; //#p...
    private String nifCliente;//nif do cliente
    private String tipo; //espeficiação do serviço expresso
    private float valorFixo;
    private LocalDateTime dataRegisto;
    private int numeroRegistoEquipamento;

    public PedidoExpresso(){
        this.idPedido = "";
        this.nifCliente = "";
        this.tipo = "";
        this.valorFixo = 0;
        this.dataRegisto = null;
        this.numeroRegistoEquipamento = -1;
    }

    public String getNifCliente() {
        return nifCliente;
    }

    //TODO: valida_pedido
    public boolean valida(){
        return true;
    }


    //TODO:
    public void carregar(String linha) {
    }

    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }

    public int getNumeroRegistoEquipamento() {
        return this.numeroRegistoEquipamento;
    }

}
