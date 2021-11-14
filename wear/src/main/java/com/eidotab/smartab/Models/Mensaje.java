package com.eidotab.smartab.Models;

import java.util.Date;



public class Mensaje implements Comparable<Mensaje> {

    private String remitente;
    private String texto;
    private String estadomensaje;
    private Date   fechamensaje;
    private String _id;

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEstadomensaje() {
        return estadomensaje;
    }

    public void setEstadomensaje(String estadomensaje) {
        this.estadomensaje = estadomensaje;
    }

    public Date getFechamensaje() {
        return fechamensaje;
    }

    public void setFechamensaje(Date fechamensaje) {
        this.fechamensaje = fechamensaje;
    }

    public String get_id() {
        return _id;
    }

    @Override
    public int compareTo(Mensaje other) {
        return get_id().compareTo(other.get_id());
    }


    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Mensaje)) {
            return false;
        }
        Mensaje otherDataroot = (Mensaje) anObject;
        return otherDataroot.get_id().equals(get_id());
    }



}
