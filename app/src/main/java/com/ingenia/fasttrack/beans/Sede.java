package com.ingenia.fasttrack.beans;

/**
 * Created by FABiO on 19/09/2016.
 */
public class Sede
{

    private String codSede;
    private String codCliente;
    private String nomSede;
    private String dirSede;
    private String telSede;
    private String corSede;
    private String lonSede;
    private String latSede;
    private String imgSede;
    private String valTurno;
    private String skuPago;

    public String getCodPrecio() {
        return codPrecio;
    }

    public void setCodPrecio(String codPrecio) {
        this.codPrecio = codPrecio;
    }

    public String getValPrecio() {
        return valPrecio;
    }

    public void setValPrecio(String valPrecio) {
        this.valPrecio = valPrecio;
    }

    public String getSkuPrecio() {
        return skuPrecio;
    }

    public void setSkuPrecio(String skuPrecio) {
        this.skuPrecio = skuPrecio;
    }

    //DATOS PAGOS MULTIPLES
    private String codPrecio;
    private String valPrecio;
    private String skuPrecio;




    private String valorTurno;

    public Sede()
    {

    }

    public String getValorTurno() {
        return valorTurno;
    }

    public void setValorTurno(String valorTurno) {
        this.valorTurno = valorTurno;
    }

    public String getImgSede() {
        return imgSede;
    }

    public void setImgSede(String imgSede) {
        this.imgSede = imgSede;
    }

    public String getCodSede()
    {
        return codSede;
    }

    public void setCodSede(String codSede)
    {
        this.codSede = codSede;
    }

    public String getCodCliente()
    {
        return codCliente;
    }

    public void setCodCliente(String codCliente)
    {
        this.codCliente = codCliente;
    }

    public String getNomSede()
    {
        return nomSede;
    }

    public void setNomSede(String nomSede)
    {
        this.nomSede = nomSede;
    }

    public String getDirSede()
    {
        return dirSede;
    }

    public void setDirSede(String dirSede)
    {
        this.dirSede = dirSede;
    }

    public String getTelSede()
    {
        return telSede;
    }

    public void setTelSede(String telSede)
    {
        this.telSede = telSede;
    }

    public String getCorSede()
    {
        return corSede;
    }

    public void setCorSede(String corSede)
    {
        this.corSede = corSede;
    }

    public String getLonSede()
    {
        return lonSede;
    }

    public void setLonSede(String lonSede)
    {
        this.lonSede = lonSede;
    }

    public String getLatSede()
    {
        return latSede;
    }

    public void setLatSede(String latSede)
    {
        this.latSede = latSede;
    }

    public String getValTurno() {
        return valTurno;
    }

    public void setValTurno(String valTurno) {
        this.valTurno = valTurno;
    }

    public String getSkuPago() {
        return skuPago;
    }

    public void setSkuPago(String skuPago) {
        this.skuPago = skuPago;
    }



}
