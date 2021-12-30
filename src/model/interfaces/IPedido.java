package model.interfaces;

import java.time.LocalDateTime;

public interface IPedido extends Carregavel {

    LocalDateTime getTempoRegisto();
    int getNumeroRegistoEquipamento();
    String getNifCliente();
    IPedido clone();
    void concluiPedido();

    LocalDateTime getDataConclusao();
}
