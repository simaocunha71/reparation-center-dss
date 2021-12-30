package model.interfaces;

import java.time.LocalDateTime;

public interface IPedido extends Carregavel,Validavel {

    LocalDateTime get_tempo_registo();

    int get_num_registo();

    String get_nif_cliente();

    IPedido clone();

    void conclui_pedido();

    LocalDateTime get_data_conclusao();
}
