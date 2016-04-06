package br.com.christiano.boaviagem.boaviagem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Christiano on 07/03/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "BoaViagem";
    private static int VERSAO = 1;

    public DataBaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    public static class Viagem {
        public static final String TABELA = "VIAGEM";
        public static final String _ID = "_ID";
        public static final String DESTINO = "DESTINO";
        public static final String DATA_CHEGADA = "DATA_CHEGADA";
        public static final String DATA_SAIDA = "DATA_SAIDA";
        public static final String ORCAMENTO = "ORCAMENTO";
        public static final String QUANTIDADE_PESSOAS = "QUANTIDADE_PESSOAS";
        public static final String TIPO_VIAGEM = "TIPO_VIAGEM";

        public static final String[] COLUNAS = new String[]{
                _ID, DESTINO, DATA_CHEGADA, DATA_SAIDA,
                TIPO_VIAGEM, ORCAMENTO, QUANTIDADE_PESSOAS };
    }

    public static class Gasto{
        public static final String TABELA = "GASTO";
        public static final String _ID = "_ID";
        public static final String VIAGEM_ID = "VIAGEM_ID";
        public static final String CATEGORIA = "CATEGORIA";
        public static final String DATA = "DATA";
        public static final String DESCRICAO = "DESCRICAO";
        public static final String VALOR = "VALOR";
        public static final String LOCAL = "LOCAL";

        public static final String[] COLUNAS = new String[]{
                _ID, VIAGEM_ID, CATEGORIA, DATA, DESCRICAO, VALOR, LOCAL
        };
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE VIAGEM (_ID INTEGER PRIMARY KEY," +
                " DESTINO TEXT, TIPO_VIAGEM INTEGER, DATA_CHEGADA DATE," +
                " DATA_SAIDA DATE, ORCAMENTO DOUBLE, QUANTIDADE_PESSOAS INTEGER);");

        db.execSQL("CREATE TABLE GASTO (_ID INTEGER PRIMARY KEY," +
                " CATEGORIA TEXT, DATA DATE, VALOR DOUBLE, DESCRICAO TEXT," +
                " LOCAL TEXT, VIAGEM_ID INTEGER," +
                " FOREIGN KEY(VIAGEM_ID) REFERENCES VIAGEM(_ID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //implementar quando tiver atualização ex: ALTER TABLE gasto ADD column pessoa TEXT
    }
}
