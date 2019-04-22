package com.ttsbco.pmmonitoring;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivitySettings extends AppCompatActivity
{

    TextView txtServerPath, txtServerPort;
    Button btnSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        txtServerPath = (TextView) findViewById(R.id.txtServerPath);
        txtServerPort = (TextView) findViewById(R.id.txtServerPort);
        btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);

        txtServerPort.setText(String.valueOf(prefs.getInt("port", 80)));
        txtServerPath.setText(prefs.getString("server", ""));

        btnSaveSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = prefs.edit();
                int port = 80;
                try
                {
                    port = Integer.valueOf(txtServerPort.getText().toString());
                }
                catch (Exception ex)
                {
                }
                editor.putInt("port", port);
                editor.putString("server", txtServerPath.getText().toString());

                editor.apply();
                ActivitySettings.this.finish();
            }
        });
    }
}
