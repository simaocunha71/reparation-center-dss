package model.interfaces;

import model.orcamento.Passo;

public interface IPlanoDeTrabalho extends Planeavel{

    IPlanoDeTrabalho clone();

    void recalcula_estimativas();

    void adicionar_passo(Passo p);
}
