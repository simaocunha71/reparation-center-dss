package model.pedidos;

import model.interfaces.IPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PedidoExpresso implements IPedido {
    private String nifCliente;
    private LocalDateTime dataRegisto;
    private int numeroRegistoEquipamento;
    private int tipo;
    private int custoFixo;
    private String descricaoPedido;


    public PedidoExpresso(){
        this.nifCliente = "";
        this.tipo = -1;
        this.custoFixo = 0;
        this.numeroRegistoEquipamento = -1;
        this.dataRegisto = null;
        this.descricaoPedido = "";
    }

    public PedidoExpresso(String nifCliente, int numeroRegisto, int tipo) {
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegisto;
        set_tipo(tipo);
        this.dataRegisto = LocalDateTime.now();
    }

    public PedidoExpresso(String nifCliente, int numeroRegistoEquipamento, LocalDateTime dataRegisto, int tipo) {
        this.nifCliente = nifCliente;
        this.numeroRegistoEquipamento = numeroRegistoEquipamento;
        set_tipo(tipo);
        this.dataRegisto = dataRegisto;
    }

    public String getNifCliente() {
        return nifCliente;
    }

    public IPedido clone() {
        return new PedidoExpresso(this.nifCliente,this.numeroRegistoEquipamento,this.dataRegisto,this.tipo);
    }

    public boolean valida(){
        return nifCliente.length() == 9 && dataRegisto != null && numeroRegistoEquipamento > 0 && tipo > 0 && tipo < 5;
    }

    //123456789;2021-12-29T00:12:19.751437100;5;1
    //idCliente;data;num_reg;tipo
    public void carregar(String string) {
        String[]split = string.split(";");
        if(split.length == 4) {
            this.nifCliente = split[0];
            try{
                this.dataRegisto = LocalDateTime.parse(split[1]);
                this.numeroRegistoEquipamento = Integer.parseInt(split[2]);
                set_tipo(Integer.parseInt(split[3]));
            }
            catch(DateTimeParseException e){
                this.dataRegisto = null;
            }
            catch(NumberFormatException e){
                this.numeroRegistoEquipamento = -1;
                this.tipo = -1;
            }
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(nifCliente).append(";").append(dataRegisto.toString()).append(";");
        sb.append(numeroRegistoEquipamento).append(";").append(tipo);
        return sb.toString();
    }

    private void set_tipo(int tipo) {
        if(tipo > 0 && tipo < 5) this.tipo = tipo;
        else this.tipo = -1;
        switch (tipo){
            case 1 ->{this.descricaoPedido = "trocar ecrã"; this.custoFixo = 50;}
            case 2 ->{this.descricaoPedido = "instalar sistema operativo"; this.custoFixo = 20;}
            case 3 ->{this.descricaoPedido = "trocar bateria"; this.custoFixo = 25;}
            case 4 ->{this.descricaoPedido = "limpar equipamento"; this.custoFixo = 10;}
            default -> {this.descricaoPedido = ""; this.custoFixo = 0;}
        }
    }

    public int getTipo() {
        return tipo;
    }

    public String getDescricaoPedido() {
        return descricaoPedido;
    }

    public int getCustoFixo() {
        return custoFixo;
    }

    public LocalDateTime getTempoRegisto(){
        return dataRegisto;
    }

    public int getNumeroRegistoEquipamento() {
        return this.numeroRegistoEquipamento;
    }

}
