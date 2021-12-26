import controller.CRController;
import model.CRFacade;
import model.excecoes.JaExistenteExcecao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, JaExistenteExcecao, ClassNotFoundException {

        CRController controller = new CRController();
        controller.run();
    }

}
