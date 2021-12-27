package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;

public class PedidoExpresso implements IPedido {
    private String idPedido; //#p...
    private String idCliente;//nif do cliente
    private String tipo; //espeficiação do serviço expresso
    private float valorFixo;
    private LocalDateTime dataRegisto;


    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }
}
