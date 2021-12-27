package model.interfaces;

import java.time.LocalDateTime;

public interface IPedido {

    LocalDateTime getTempoRegisto();
    int getNumeroRegistoEquipamento();

    boolean valida_pedido();
    public String getNifCliente();

    void load_pedido(String linha);
}
