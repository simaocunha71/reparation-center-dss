package model.interfaces;

import java.time.LocalDateTime;

public interface IOrcamento extends Planeavel {

    IOrcamento clone();

    boolean getConfirmado();

    LocalDateTime getDataConfirmacao();

    void confirma();

    void desconfirma();

    void carregar(IOrcamento orcamento);

    IPlanoDeTrabalho getPlanoDeTrabalho();

}
