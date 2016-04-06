package br.com.christiano.boaviagem.boaviagem.domain;

/**
 * Created by Christiano on 07/03/2016.
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gasto {

    private Integer id;
    private Date data;
    private String categoria;
    private String descricao;
    private Double valor;
    private String local;
    private Integer viagemId;

    public Gasto(){}

    public Gasto(Integer id, Date data, String categoria, String descricao,
                 Double valor, String local, Integer viagemId) {
        this.id = id;
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.local = local;
        this.viagemId = viagemId;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public Double getValor() {
        return valor;
    }
    public void setValor(Double valor) {
        this.valor = valor;
    }
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }
    public Integer getViagemId() {
        return viagemId;
    }
    public void setViagemId(Integer viagemId) {
        this.viagemId = viagemId;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("[").append(categoria).append("] ")
                .append(descricao)
                .append(" Data: ").append(new SimpleDateFormat("dd/MM/yyyy").format(data))
                .append(" ").append(local)
                .append(" Valor: $").append(valor);
        return str.toString();
    }
}
