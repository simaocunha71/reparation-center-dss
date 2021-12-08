package model.interfaces;

import java.io.IOException;
import java.util.HashMap;

public interface ISessao {

     Boolean login(String id, String password, HashMap<String,IUtilizador> utilizadores);

    void fim_Sessao();

    void guardar_Sessao(String filename) throws IOException;
}
