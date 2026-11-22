package com.example.inversorfrequenciaapp;

/** Converte a resposta de texto do inversor nos oito campos do protocolo. */
public class Dados {
    private String display;
    private String leds;
    private String teclas;
    private int potenciometro;
    private String entradasDigitais;
    private boolean saidaDigital;
    private int saidaAnalogica;
    private int entradaAnalogica;

    public Dados(String resposta) {
        if (resposta == null) {
            throw new IllegalArgumentException("Resposta vazia");
        }
        String texto = resposta.trim();
        if (texto.startsWith("{")) texto = texto.substring(1);
        if (texto.endsWith("}")) texto = texto.substring(0, texto.length() - 1);

        String[] partes = texto.split(";", -1);
        if (partes.length != 8) {
            throw new IllegalArgumentException("Esperava 8 campos; recebeu " + partes.length);
        }

        display = partes[0].trim();
        leds = partes[1].trim();
        teclas = partes[2].trim();
        potenciometro = Integer.parseInt(partes[3].trim());
        entradasDigitais = partes[4].trim();
        saidaDigital = partes[5].trim().equals("1");
        saidaAnalogica = Integer.parseInt(partes[6].trim());
        entradaAnalogica = Integer.parseInt(partes[7].trim());

        if (leds.length() != 7 || teclas.length() != 8
                || entradasDigitais.length() != 4) {
            throw new IllegalArgumentException("Tamanho inválido nos campos binários");
        }
        if (potenciometro < 0 || potenciometro > 255
                || entradaAnalogica < 0 || entradaAnalogica > 255) {
            throw new IllegalArgumentException("Valor fora da faixa de 0 a 255");
        }
    }

    public String getDisplay() { return display; }
    public String getLeds() { return leds; }
    public String getTeclas() { return teclas; }
    public void setTeclas(String valor) { teclas = valor; }
    public int getPotenciometro() { return potenciometro; }
    public void setPotenciometro(int valor) { potenciometro = valor; }
    public String getEntradasDigitais() { return entradasDigitais; }
    public void setEntradasDigitais(String valor) { entradasDigitais = valor; }
    public boolean isSaidaDigital() { return saidaDigital; }
    public int getSaidaAnalogica() { return saidaAnalogica; }
    public int getEntradaAnalogica() { return entradaAnalogica; }
    public void setEntradaAnalogica(int valor) { entradaAnalogica = valor; }
}
