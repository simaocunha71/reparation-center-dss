package model.interfaces;

import java.time.LocalDateTime;

public interface IOrcamento extends Carregavel,Validavel {

    IOrcamento clone();

    boolean get_confirmado();

    LocalDateTime get_data_confirmacao();

    void confirma();

    void desconfirma();

    void carregar(IOrcamento orcamento);

    IPlanoDeTrabalho get_plano_de_trabalho();
    void set_plano_de_trabalho(IPlanoDeTrabalho plano_de_trabalho);

    int get_num_registo();

}
