package com.marshteq.ecospotless;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toolbar;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
//        getSupportActionBar().hide();


        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://www.ecospotless.co.za/mobile-register");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        Spinner spinner = (Spinner) findViewById(R.id.city_spinner);
//        spinner.setOnItemSelectedListener(this);
//        List<String> categories = new ArrayList<String>();
//        categories.add("Johannesburg");
//        categories.add("Cape Town");
//        categories.add("Durban");
//        categories.add("East London");
//        categories.add("Port Elizabeth");
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);

    }
}
