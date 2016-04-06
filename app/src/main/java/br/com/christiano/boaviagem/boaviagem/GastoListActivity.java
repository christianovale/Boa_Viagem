package br.com.christiano.boaviagem.boaviagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toolbar;

import br.com.christiano.boaviagem.boaviagem.dao.BoaViagemDAO;
import br.com.christiano.boaviagem.boaviagem.domain.Gasto;
import br.com.christiano.boaviagem.boaviagem.domain.Viagem;
import br.com.christiano.boaviagem.boaviagem.util.DateUtil;

/**
 * Created by Christiano on 03/03/2016.
 */
public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";
    private BoaViagemDAO dao;
    private Viagem viagemSelecionada;
    int idViagemSelecionada;
    int idGastoSelecionado;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd/MMM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dao = new BoaViagemDAO(this);
        idViagemSelecionada = getIntent().getIntExtra(Constantes.VIAGEM_SELECIONADA, -1);
        viagemSelecionada = dao.buscarViagemPorId(idViagemSelecionada);

        String[] de = { "data", "descricao", "valor", "categoria" };
        int[] para = { R.id.data, R.id.descricao, R.id.valor, R.id.categoria };

        SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(), R.layout.lista_gasto, de, para);

        adapter.setViewBinder(new GastoViewBinder());

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        //registrando o menu de Contexto
        registerForContextMenu(getListView());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = gastos.get(position);
        String descricao = (String) map.get("descricaoCompleta");
        this.idGastoSelecionado = (int) map.get("id");
        Toast.makeText(this, descricao,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gastolist_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remover) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Map<String, Object> map = gastos.get(info.position);
            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            dao.removerGasto(new Long(idGastoSelecionado = (int) map.get("id")));
            return true;
        }else if(item.getItemId() == R.id.inicio){
            startActivity(new Intent(this, DashboardActivity.class));
        }else  if(item.getItemId() == R.id.selecionarViagem) {
            startActivity(new Intent(this, ViagemListActivity.class));
        }
        return super.onContextItemSelected(item);
    }

    private List<Map<String, Object>> listarGastos() {
        gastos = new ArrayList<Map<String, Object>>();
        List<Gasto> listaGastos = dao.listarGastos(viagemSelecionada);
        Map<String, Object> item;

        for(Gasto gasto : listaGastos){

            item = new HashMap<String, Object>();
            item.put("id", gasto.getId());
            item.put("data", dateFormat.format(gasto.getData()));
            item.put("descricao", !gasto.getDescricao().isEmpty() ? gasto.getDescricao() : "["+gasto.getCategoria()+"]");
            item.put("valor", "R$ "+gasto.getValor());
            item.put("descricaoCompleta", gasto.toString());

            switch (gasto.getCategoria()){
                case "Alimentação":
                    item.put("categoria", R.color.categoria_alimentacao);
                    break;
                case "Combustivel":
                    item.put("categoria", R.color.categoria_combustivel);
                    break;
                case "Transporte":
                    item.put("categoria", R.color.categoria_transporte);
                    break;
                case "Hospedagem":
                    item.put("categoria", R.color.categoria_hospedagem);
                    break;
                case "Outros":
                    item.put("categoria", R.color.categoria_outros);
                    break;
            }

            gastos.add(item);
        }

        return gastos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gastolist_options_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.enviarEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts( "mailto", "", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Boa Viagem Relatorio: "+viagemSelecionada.getDestino());
                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(this.comporEmail()));
                startActivity(Intent.createChooser(emailIntent, "Email"));
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private String comporEmail(){
        List<Gasto> listaGastos = dao.listarGastos(viagemSelecionada);
        double totalGastos = 0;
        StringBuilder str = new StringBuilder();
        str.append("<p><b>").append(viagemSelecionada.getDestino()).append("</b></p>").append("</br></br>");
        str.append("<p><b>").append(DateUtil.deUtilParaString(viagemSelecionada.getDataChegada())).append(" a ").append(DateUtil.deUtilParaString(viagemSelecionada.getDataSaida())).append("</b></p>").append("</br></br>");
        str.append("<p>Gastos</p>");

        for(Gasto gasto : listaGastos){
            str.append("<p>R$ ").append(gasto.getValor()).append(" - ").append(DateUtil.deUtilParaString(gasto.getData()))
                    .append(" - [").append(gasto.getCategoria()).append("] ")
                    .append(gasto.getLocal()).append(" - ")
                    .append(gasto.getDescricao());
            str.append("</p>");
            totalGastos += gasto.getValor();
        }
        str.append("<p><b>Orçamento inicial: R$ ").append(String.valueOf(viagemSelecionada.getOrcamento())).append("</b></p>");
        str.append("<p><b>Total de gastos: R$ ").append(String.valueOf(totalGastos)).append("</b></p>");

        return str.toString();
    }

    private class GastoViewBinder implements ViewBinder{
        private String dataAnterior = "";

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {

            if(view.getId() == R.id.data){
                if(!dataAnterior.equals(data)){
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                }else{
                    view.setVisibility(View.GONE);
                }
                return true;
            }

            if(view.getId() == R.id.categoria){
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }


}
