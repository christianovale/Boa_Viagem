package br.com.christiano.boaviagem.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.christiano.boaviagem.boaviagem.dao.BoaViagemDAO;
import br.com.christiano.boaviagem.boaviagem.domain.Gasto;
import br.com.christiano.boaviagem.boaviagem.domain.Viagem;
import br.com.christiano.boaviagem.boaviagem.provider.BoaViagemContract;

/**
 * Created by Christiano on 02/03/2016.
 */
public class GastoActivity extends Activity {
    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;
    private TextView destino;
    private EditText valor;
    private EditText descricao;
    private EditText local;
    private Date data;
    private BoaViagemDAO dao;
    private int idViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        destino = (TextView) findViewById(R.id.destino);
        valor = (EditText) findViewById(R.id.valor);
        descricao = (EditText) findViewById(R.id.descricao);
        local = (EditText) findViewById(R.id.local);
        dao = new BoaViagemDAO(this);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataGasto = (Button) findViewById(R.id.data);
        dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        idViagem = getIntent().getIntExtra(Constantes.VIAGEM_SELECIONADA, -1);
        if(idViagem == -1) {
            Viagem viagem = dao.buscarViagemPorDataAtual();
            if(viagem == null){
                Toast.makeText(this, "Selecione uma viagem na lista.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ViagemListActivity.class));
            }
            idViagem = viagem.getId();
            destino.setText(viagem.getDestino());
            Toast.makeText(this, "Viagem selecionada pela data atual: "+viagem.getDestino(), Toast.LENGTH_LONG).show();
        }else {
            String viagemDestino = getIntent().getExtras().getString(Constantes.VIAGEM_DESTINO);
            destino.setText(viagemDestino);
        }
    }

    private void prepararEdicao() {
        Gasto gasto = (Gasto) getIntent().getSerializableExtra(Constantes.GASTO_SELECIONADO);
        //categoria.setSelection(position);
        descricao.setText(gasto.getDescricao());
        local.setText(gasto.getLocal());
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.data == id) {
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.selecionarViagem){
            startActivity(new Intent(this, ViagemListActivity.class));
        }else if(item.getItemId() == R.id.inicio){
            startActivity(new Intent(this, DashboardActivity.class));
        }

        return true;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);
            data = criarData(ano, mes, dia);
        }
    };

    public void registrarGasto(View view){

        Gasto gasto = new Gasto();
        gasto.setData(data != null ? data : new Date());
        gasto.setCategoria(categoria.getSelectedItem().toString());
        gasto.setDescricao(descricao.getText().toString());
        gasto.setLocal(local.getText().toString());
        if(!valor.getText().toString().isEmpty()) {
            gasto.setValor(new Double(valor.getText().toString()));
        }
        gasto.setViagemId(idViagem);

        List<String> camposSolicitados = this.camposSolicitados(gasto);
        if(camposSolicitados.isEmpty()){
            long resultado = dao.inserir(gasto);
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

    private List<String> camposSolicitados(Gasto gasto){
        List<String> camposSolicitados = new ArrayList<>();
        if(gasto.getValor() == null){
            camposSolicitados.add(getString(R.string.gasto));
        }
        return camposSolicitados;
    }

    private Date criarData(int anoSelecionado, int mesSelecionado, int diaSelecionado) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anoSelecionado, mesSelecionado, diaSelecionado);
        return calendar.getTime();
    }
}