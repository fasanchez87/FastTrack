package com.ingenia.fasttrack.beans;

import java.util.ArrayList;

/**
 * Created by FABiO on 19/09/2016.
 */
public class Cliente
{
    private String codCliente;
    private String nomCliente;
    private String dirCliente;
    private String telCliente;
    private String corCliente;
    private String imgCliente;
    private String nomEncargado;
    private String valturno;
    private String indicaActivo;

    public ArrayList<Sede> getSedes()
    {
        return sedes;
    }

    public void setSedes(ArrayList<Sede> sedes)
    {
        this.sedes = sedes;
    }

    private ArrayList<Sede> sedes;

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getNomCliente() {
        return nomCliente;
    }

    public void setNomCliente(String nomCliente) {
        this.nomCliente = nomCliente;
    }

    public String getDirCliente() {
        return dirCliente;
    }

    public void setDirCliente(String dirCliente) {
        this.dirCliente = dirCliente;
    }

    public String getTelCliente() {
        return telCliente;
    }

    public void setTelCliente(String telCliente) {
        this.telCliente = telCliente;
    }

    public String getCorCliente() {
        return corCliente;
    }

    public void setCorCliente(String corCliente) {
        this.corCliente = corCliente;
    }

    public String getImgCliente() {
        return imgCliente;
    }

    public void setImgCliente(String imgCliente) {
        this.imgCliente = imgCliente;
    }

    public String getNomEncargado() {
        return nomEncargado;
    }

    public void setNomEncargado(String nomEncargado) {
        this.nomEncargado = nomEncargado;
    }

    public String getValturno() {
        return valturno;
    }

    public void setValturno(String valturno) {
        this.valturno = valturno;
    }

    public String getIndicaActivo() {
        return indicaActivo;
    }

    public void setIndicaActivo(String indicaActivo) {
        this.indicaActivo = indicaActivo;
    }




}
