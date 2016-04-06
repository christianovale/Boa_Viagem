package br.com.christiano.boaviagem.boaviagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.com.christiano.boaviagem.boaviagem.dao.BoaViagemDAO;
import br.com.christiano.boaviagem.boaviagem.domain.Viagem;

/**
 * Created by Christiano on 02/03/2016.
 */
public class ViagemActivity extends Activity {

    private Date dataChegada, dataSaida;
    private int ano, mes, dia;
    private Button dataChegadaButton, dataSaidaButton;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private int id;
    private BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viagem);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegadaButton = (Button) findViewById(R.id.dataChegada);
        dataSaidaButton = (Button) findViewById(R.id.dataSaida);

        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas = (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        //prepara acesso a base de dados
        dao = new BoaViagemDAO(this);

        id = getIntent().getIntExtra(Constantes.VIAGEM_SELECIONADA, -1);
        //id = id != -1 ? id : 4;//TODO arruamr isso

        if(id != -1){
            prepararEdicao();
        }
    }



    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

    private void prepararEdicao() {
        Viagem viagem = dao.buscarViagemPorId(id);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
            radioGroup.check(R.id.lazer);
        }else{
            radioGroup.check(R.id.negocios);
        }

        destino.setText(viagem.getDestino());
        dataChegada = viagem.getDataChegada();
        dataSaida = viagem.getDataSaida();
        dataChegadaButton.setText(dateFormat.format(dataChegada));
        dataSaidaButton.setText(dateFormat.format(dataSaida));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(viagem.getOrcamento().toString());
    }

    public void salvarViagem(View view){
        Viagem viagem = new Viagem();
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada);
        viagem.setDataSaida(dataSaida);
        if(!orcamento.getText().toString().isEmpty()) {
            viagem.setOrcamento(Double.valueOf(orcamento.getText().toString()));
        }
        if(!quantidadePessoas.getText().toString().isEmpty()) {
            viagem.setQuantidadePessoas(Integer.valueOf(quantidadePessoas.getText().toString()));
        }
        int tipo = radioGroup.getCheckedRadioButtonId();

        if(tipo == R.id.lazer){
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        }else{
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        List<String> camposSolicitados = this.camposSolicitados(viagem);

        if(camposSolicitados.isEmpty()){
            long resultado;

            if(id == -1){
                resultado = dao.inserir(viagem);
                //new Task().execute(viagem);
            }else{
                viagem.setId(id);
                resultado = dao.atualizar(viagem);
            }

            if (resultado != -1) {// em caso de falha o banco de dados retorna -1, se der certo retorna o id do registro
                Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, R.string.erro_salvar, Toast.LENGTH_SHORT).show();
            }

            startActivity(new Intent(this, ViagemListActivity.class));
        }else{
            String campos = "";
            int contador = 0;
            for(String campo : camposSolicitados){
                if(contador==1){
                    campos = campo;
                }else {
                    campos += ", "+ campo;
                }
                contador++;
            }

            if(contador==1){
                campos = "Preencha o campo: "+campos;
            }else{
                campos = "Preencha os campos: "+campos;
            }
            Toast.makeText(this, campos, Toast.LENGTH_LONG).show();
        }

    }

    private List<String> camposSolicitados(Viagem viagem){
        List<String> camposSolicitados = new ArrayList<>();

        if(viagem.getDestino() == null || viagem.getDestino().isEmpty()){
            camposSolicitados.add(getString(R.string.destino));
        }
        if(viagem.getDataChegada() == null){
            camposSolicitados.add(getString(R.string.dt_chegada));
        }
        if(viagem.getDataSaida()==null){
            camposSolicitados.add(getString(R.string.dt_saida));
        }
        if(viagem.getOrcamento() == null){
            camposSolicitados.add(getString(R.string.quantidade));
        }
        if(viagem.getQuantidadePessoas() == null || viagem.getQuantidadePessoas().intValue() == 0){
            camposSolicitados.add(getString(R.string.qtd_pessoas)
            );
        }

        return camposSolicitados;
    }

    public void selecionarData(View view) {

        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case R.id.dataChegada:
                return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);

            case R.id.dataSaida:
                return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);
        }
        return null;
    }

    private OnDateSetListener dataChegadaListener = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataChegada = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataChegadaButton.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);
        }
    };

    private OnDateSetListener dataSaidaListener = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataSaida = criarData(anoSelecionado, mesSelecionado, diaSelecionado);
            dataSaidaButton.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);
        }
    };

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.novo_gasto:
                startActivity(new Intent(this, GastoActivity.class));
                return true;
            case R.id.remover:
                //remover do BD
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private class Task extends AsyncTask<Viagem, Void, Void> {
        @Override
        protected Void doInBackground(Viagem... viagens) {
            Viagem viagem = viagens[0];
            //calendarService.criarEvento(viagem);
            return null;
        }
    }
}
