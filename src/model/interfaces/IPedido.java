package model.interfaces;

import java.time.LocalDateTime;

public interface IPedido extends Carregavel {

    LocalDateTime getTempoRegisto();
    int getNumeroRegistoEquipamento();
    public String getNifCliente();
    public IPedido clone();

}
