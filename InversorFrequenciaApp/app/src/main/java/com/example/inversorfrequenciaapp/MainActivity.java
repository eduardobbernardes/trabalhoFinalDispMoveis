package com.example.inversorfrequenciaapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_REDE_LOCAL = 1001;
    private final String[] nomesAlvos = {
            "Simulador deste notebook", "Laboratório — máquina 1 (.60)",
            "Laboratório — máquina 2 (.61)", "Laboratório — máquina 4 (.63)",
            "Laboratório — máquina 5 (.64)"
    };
    private final String[] enderecos = {
            "http://192.168.1.67:8080/",
            //"http://localhost:8080/",
            "http://192.168.20.60/api/ihm/",
            "http://192.168.20.61/api/ihm/",
            "http://192.168.20.63/api/ihm/",
            "http://192.168.20.64/api/ihm/",
    };
    private final int[] idsLeds = {
            R.id.ledHz, R.id.ledA, R.id.ledV, R.id.ledRun,
            R.id.ledLocal, R.id.ledFwd, R.id.ledTune
    };
    private final int[] idsTeclas = {
            R.id.button4, R.id.button3, R.id.button5, R.id.buttonUp,
            R.id.button8, R.id.button6, R.id.buttonDown, R.id.button9
    };
    private final int[] idsEntradas = {
            R.id.entradaDigital0, R.id.entradaDigital1,
            R.id.entradaDigital2, R.id.entradaDigital3
    };

    private Spinner seletorAlvo;
    private TextView visor, valorPot, valorEntradaAnalogica;
    private TextView valorSaidaDigital, valorSaidaAnalogica, status;
    private Slider sliderPot, sliderEntradaAnalogica;
    private CheckBox[] entradasDigitais;
    private Dados dadosAtuais;
    private int alvoSelecionado = 0;
    private boolean atualizandoTela = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seletorAlvo = findViewById(R.id.seletorAlvo);
        visor = findViewById(R.id.textView);
        valorPot = findViewById(R.id.valorPotenciometroLabel);
        sliderPot = findViewById(R.id.frequenccySlider);
        sliderEntradaAnalogica = findViewById(R.id.sliderEntradaAnalogica);
        valorEntradaAnalogica = findViewById(R.id.valorEntradaAnalogica);
        valorSaidaDigital = findViewById(R.id.valorSaidaDigital);
        valorSaidaAnalogica = findViewById(R.id.valorSaidaAnalogica);
        status = findViewById(R.id.statusConexao);

        entradasDigitais = new CheckBox[] {
                findViewById(idsEntradas[0]), findViewById(idsEntradas[1]),
                findViewById(idsEntradas[2]), findViewById(idsEntradas[3])
        };

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nomesAlvos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seletorAlvo.setAdapter(adaptador);
        seletorAlvo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                alvoSelecionado = position;
                dadosAtuais = null;
                verificaPermissaoRedeLocal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        findViewById(R.id.botaoAtualizar).setOnClickListener(v -> verificaPermissaoRedeLocal());

        sliderPot.addOnChangeListener((slider, valor, doUsuario) -> {
            if (doUsuario) valorPot.setText("POTENCIÔMETRO: " + Math.round(valor));
        });
        sliderPot.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override public void onStartTrackingTouch(Slider slider) { }
            @Override public void onStopTrackingTouch(Slider slider) {
                enviarPotenciometro(Math.round(slider.getValue()));
            }
        });

        sliderEntradaAnalogica.addOnChangeListener((slider, valor, doUsuario) -> {
            if (doUsuario) valorEntradaAnalogica.setText(
                    "Entrada analógica: " + Math.round(valor));
        });
        sliderEntradaAnalogica.addOnSliderTouchListener(
                new Slider.OnSliderTouchListener() {
                    @Override public void onStartTrackingTouch(Slider slider) { }
                    @Override public void onStopTrackingTouch(Slider slider) {
                        if (!atualizandoTela && dadosAtuais != null) {
                            dadosAtuais.setEntradaAnalogica(
                                    Math.round(slider.getValue()));
                            enviarEstadoAtual(MainActivity.this::carregarLeitura);
                        }
                    }
                });

        for (int i = 0; i < entradasDigitais.length; i++) {
            final int indice = i;
            entradasDigitais[i].setOnCheckedChangeListener((button, marcado) -> {
                if (!atualizandoTela && dadosAtuais != null) {
                    atualizarBitEntrada(indice, marcado);
                    enviarEstadoAtual(this::carregarLeitura);
                }
            });
        }

        // Ordem do protocolo: RUN, STOP, PRG/ESC, cima, DATA/ENT,
        // QUICK/JOG, baixo, SHIFT.
        for (int i = 0; i < idsTeclas.length; i++) {
            final int indice = i;
            findViewById(idsTeclas[i]).setOnClickListener(
                    v -> enviarTeclaMomentanea(indice));
        }
    }

    private InversorApi apiAtual() {
        return RetrofitClient.criarApi(enderecos[alvoSelecionado]);
    }

    private boolean simuladorSelecionado() {
        return alvoSelecionado == 0;
    }

    private void carregarLeitura() {
        status.setText("Conectando...");
        apiAtual().listar().enqueue(new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    status.setText("Falha HTTP " + response.code());
                    System.out.println(response.code());
                    return;
                }
                try {
                    dadosAtuais = new Dados(response.body());
                    System.out.println(response.body());
                    mostrarDados(dadosAtuais);
                    status.setText("Conectado — " + nomesAlvos[alvoSelecionado]);
                } catch (RuntimeException erro) {
                    status.setText("Resposta fora do formato String esperado");
                }
            }

            @Override public void onFailure(Call<String> call, Throwable erro) {
                //status.setText("Sem resposta. Confira rede e máquina selecionada.");
                status.setText("Sem resposta: " + erro);
            }
        });
    }

    private void mostrarDados(Dados dados) {
        atualizandoTela = true;
        visor.setText(dados.getDisplay().isEmpty() ? "(Vazio)" : dados.getDisplay());
        sliderPot.setValue(dados.getPotenciometro());
        valorPot.setText("POTENCIÔMETRO: " + dados.getPotenciometro());
        sliderEntradaAnalogica.setValue(dados.getEntradaAnalogica());
        valorEntradaAnalogica.setText(
                "Entrada analógica: " + dados.getEntradaAnalogica());
        valorSaidaDigital.setText(dados.isSaidaDigital() ? "Ligada" : "Desligada");
        valorSaidaAnalogica.setText(String.valueOf(dados.getSaidaAnalogica()));

        for (int i = 0; i < 4; i++) {
            entradasDigitais[i].setChecked(
                    dados.getEntradasDigitais().charAt(i) == '1');
        }
        atualizarLeds(dados.getLeds());
        atualizandoTela = false;
    }

    private void atualizarLeds(String bits) {
        for (int i = 0; i < idsLeds.length; i++) {
            View led = findViewById(idsLeds[i]);
            boolean ligado = bits.charAt(i) == '0'; // zero significa aceso
            int cor = Color.parseColor(ligado ? "#00FF70" : "#555555");
            led.setBackgroundTintList(ColorStateList.valueOf(cor));
        }
    }

    private void atualizarBitEntrada(int indice, boolean ligado) {
        char[] bits = dadosAtuais.getEntradasDigitais().toCharArray();
        bits[indice] = ligado ? '1' : '0';
        dadosAtuais.setEntradasDigitais(new String(bits));
    }

    private void enviarPotenciometro(int valor) {
        if (dadosAtuais == null) { mensagem("Faça uma leitura primeiro"); return; }
        dadosAtuais.setPotenciometro(valor);
        enviarEstadoAtual(this::carregarLeitura);
    }

    private void enviarTeclaMomentanea(int indice) {
        if (dadosAtuais == null) { mensagem("Faça uma leitura primeiro"); return; }
        char[] bits = dadosAtuais.getTeclas().toCharArray();
        bits[indice] = '1';
        dadosAtuais.setTeclas(new String(bits));
        enviarEstadoAtual(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            char[] soltas = dadosAtuais.getTeclas().toCharArray();
            soltas[indice] = '0';
            dadosAtuais.setTeclas(new String(soltas));
            enviarEstadoAtual(this::carregarLeitura);
        }, 300));
    }

    private String montarMensagem() {
        if (simuladorSelecionado()) {
            return dadosAtuais.getDisplay() + ";" + dadosAtuais.getLeds() + ";"
                    + dadosAtuais.getTeclas() + ";" + dadosAtuais.getPotenciometro() + ";"
                    + dadosAtuais.getEntradasDigitais() + ";"
                    + (dadosAtuais.isSaidaDigital() ? "1" : "0") + ";"
                    + dadosAtuais.getSaidaAnalogica() + ";"
                    + dadosAtuais.getEntradaAnalogica();
        }
        // Formato de escrita do laboratório: teclas;potenciômetro;
        // entradas digitais;entrada analógica.
        return dadosAtuais.getTeclas() + ";" + dadosAtuais.getPotenciometro()
                + ";" + dadosAtuais.getEntradasDigitais()
                + ";" + dadosAtuais.getEntradaAnalogica();
    }

    private void enviarEstadoAtual(Runnable depois) {
        if (dadosAtuais == null) { mensagem("Faça uma leitura primeiro"); return; }
        apiAtual().escrever(montarMensagem()).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call,
                                             Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (depois != null) depois.run();
                } else {
                    mensagem("Falha no envio: HTTP " + response.code());
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable erro) {
                mensagem("Não foi possível enviar. Confira a conexão.");
            }
        });
    }

    public void verificaPermissaoRedeLocal(){

        if (Build.VERSION.SDK_INT >= 37 &&
                checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK")
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{"android.permission.ACCESS_LOCAL_NETWORK"}, REQUEST_REDE_LOCAL);
        } else {
            carregarLeitura();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );

        if (requestCode == REQUEST_REDE_LOCAL) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                carregarLeitura();

            } else {
                status.setText("Permissão de rede local negada");
            }
        }
    }

    private void mensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}
