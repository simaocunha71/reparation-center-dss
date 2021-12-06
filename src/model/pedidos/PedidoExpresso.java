package model.pedidos;

import model.interfaces.IPedido;

public class PedidoExpresso implements IPedido {
    private String idPedido; //#p...
    private String idCliente;//nif do cliente
    private String tipo; //espeficiação do serviço expresso
    private float valorFixo;

}
