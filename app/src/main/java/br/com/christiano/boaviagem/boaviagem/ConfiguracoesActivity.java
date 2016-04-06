package br.com.christiano.boaviagem.boaviagem;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Christiano on 03/03/2016.
 */
public class ConfiguracoesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
