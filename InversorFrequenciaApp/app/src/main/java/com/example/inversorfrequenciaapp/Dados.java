package com.example.inversorfrequenciaapp;

public class Dados {
    private String display;
    private String leds;
    private String teclas;
    private int potenciomentro;
    private String entradasDigitais;
    private boolean saidaDigital;
    private int saidaAnalogica;
    private int entradaAnalogica;
    private String leitura;

    public Dados(String entrada){
        String[] dadosDivididos = entrada.split(";");
        display = dadosDivididos[0];
        leds = dadosDivididos[1];
        teclas = dadosDivididos[2];
        potenciomentro = Integer.parseInt(dadosDivididos[3]);
        entradasDigitais = dadosDivididos[4];
        saidaDigital = dadosDivididos[5].equals("1");
        saidaAnalogica = Integer.parseInt(dadosDivididos[6]);
        entradaAnalogica = Integer.parseInt(dadosDivididos[7]);

    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getLeds() {
        return leds;
    }

    public void setLeds(String leds) {
        this.leds = leds;
    }

    public String getTeclas() {
        return teclas;
    }

    public void setTeclas(String teclas) {
        this.teclas = teclas;
    }

    public int getPotenciomentro() {
        return potenciomentro;
    }

    public void setPotenciomentro(int potenciomentro) {
        this.potenciomentro = potenciomentro;
    }

    public String getEntradasDigitais() {
        return entradasDigitais;
    }

    public void setEntradasDigitais(String entradasDigitais) {
        this.entradasDigitais = entradasDigitais;
    }

    public boolean isSaidaDigital() {
        return saidaDigital;
    }

    public void setSaidaDigital(boolean saidaDigital) {
        this.saidaDigital = saidaDigital;
    }

    public int getSaidaAnalogica() {
        return saidaAnalogica;
    }

    public void setSaidaAnalogica(int saidaAnalogica) {
        this.saidaAnalogica = saidaAnalogica;
    }

    public int getEntradaAnalogica() {
        return entradaAnalogica;
    }

    public void setEntradaAnalogica(int entradaAnalogica) {
        this.entradaAnalogica = entradaAnalogica;
    }

    public String getLeitura() {
        return leitura;
    }

    public void setLeitura() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(display).append(";");
        stringBuilder.append(leds).append(";");
        stringBuilder.append(teclas).append(";");
        stringBuilder.append(potenciomentro).append(";");
        stringBuilder.append(entradasDigitais).append(";");
        stringBuilder.append(saidaDigital ? "1" : "0").append(";");
        stringBuilder.append(saidaAnalogica).append(";");
        stringBuilder.append(entradaAnalogica).append(";");

        leitura = stringBuilder.toString();
    }
}
