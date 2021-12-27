package model.interfaces;

import java.time.LocalDateTime;

public interface IPedido extends Loadable{

    LocalDateTime getTempoRegisto();
    int getNumeroRegistoEquipamento();
    public String getNifCliente();


}
