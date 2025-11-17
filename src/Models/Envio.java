/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */
public class Envio extends Base{
    //Definimos los atributos:
    private String tracking;
    private BigDecimal costo;
    private java.time.LocalDate fechaDespacho;
    private java.time.LocalDate fechaEstimada;
    private Empresa empresa;
    private EstadoEnvio estado;
    private TipoEnvio tipo;
    private long idPedido;
    
    //Constructor completo:

    public Envio(String tracking, BigDecimal costo, LocalDate fechaDespacho, LocalDate fechaEstimada, Empresa empresa, EstadoEnvio estado, TipoEnvio tipo, long id) {
        super(id, false);
        this.tracking = tracking;
        this.costo = costo;
        this.fechaDespacho = fechaDespacho;
        this.fechaEstimada = fechaEstimada;
        this.empresa = empresa;
        this.estado = estado;
        this.tipo = tipo;
    }
    
    //Constructor por defecto:
    public Envio() {
        super();
    }
    
    //Getters y Setters

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDate getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDate fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public TipoEnvio getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnvio tipo) {
        this.tipo = tipo;
    }

    public long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(long idPedido) {
        this.idPedido = idPedido;
    }
    
  
    @Override
    public String toString() {
        return "Envio{" + " ID=" + getId() +
                "tracking=" + tracking + 
                ", costo=" + costo + 
                ", fechaDespacho=" + fechaDespacho + 
                ", fechaEstimada=" + fechaEstimada + 
                ", empresa=" + empresa + 
                ", estado=" + estado + 
                ", tipo=" + tipo +
                ", eliminado=" + isEliminado() + '}';
    }

    
}
