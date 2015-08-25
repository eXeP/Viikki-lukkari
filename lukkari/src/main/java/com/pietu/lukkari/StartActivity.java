package com.pietu.lukkari;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class StartActivity extends Activity {
    Spinner vuosiSpinner, jaksoSpinner;
    ListView kurssiListView;

    ArrayList<String> kurssit;
    Lukkari lukkari = null;
    EditText nameText;

    int lastVuosi = -1, lastJakso = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setTitle("Uusi lukkari");

        setContentView(R.layout.activity_start);
        vuosiSpinner = (Spinner) findViewById(R.id.vuosiSpinner);
        jaksoSpinner = (Spinner) findViewById(R.id.jaksoSpinner);
        kurssiListView = (ListView) findViewById(R.id.kurssiList);
        kurssiListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        nameText = (EditText) findViewById(R.id.nimiText);
        vuosiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                paivitaKurssit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        jaksoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                paivitaKurssit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	if(item.getTitle().equals("valmis"))
    		valmis();
    		
    	return true;
    	
    }
    public void valmis(MenuItem item){
        SparseBooleanArray checkeditems = kurssiListView.getCheckedItemPositions();
        ArrayList<String> valitutKurssit = new ArrayList<String>();
        if(checkeditems != null){
            for(int i = 0; i < checkeditems.size(); i++){
                if(checkeditems.valueAt(i) == true)
                    valitutKurssit.add(kurssiListView.getAdapter().getItem(checkeditems.keyAt(i)).toString());
            }
        }
        SparseArray<ArrayList<String>> tunnitJaAjat = null;
        try {
            tunnitJaAjat = lukkari.getTunnitJaAjat();
        }
        catch(Exception e){
            return;
        }
        ArrayList<String> tunnit = new ArrayList<String>();
        for(int i = 0; i < 24; i++){
            boolean stop = false;
            for(String tunti: tunnitJaAjat.valueAt(i)){
                for(String kurssi: valitutKurssit){
                    if(tunti.contains(kurssi)) {
                        tunnit.add(tunti);
                        stop = true;
                        break;
                    }
                }
                if(stop)
                    break;
            }
            if(!stop)
                tunnit.add("Hypäri");
        }

        String lukkariname = nameText.getText().toString();
        if(lukkariname.length() == 0){
            Toast.makeText(getApplicationContext(), "Anna lukkarille nimi", Toast.LENGTH_SHORT).show();
            return;
        }
        Lukkari.setLastLukkari(lukkariname, getApplicationContext());
        Lukkari.addLukkari(lukkariname, lastVuosi, lastJakso, tunnit, valitutKurssit, getApplication());
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("lukkariname", lukkariname);
        startActivity(mainIntent);
        finish();
    }

    private void paivitaKurssit(){
        int vuosi = vuosiSpinner.getSelectedItemPosition()+1;
        int jakso = jaksoSpinner.getSelectedItemPosition()+1;


        try {
             lukkari = new Lukkari(vuosi, jakso, StartActivity.this);
        } catch (Exception e) {
            if(vuosi != lastVuosi || jakso != lastJakso)
                Toast.makeText(getApplicationContext(), "Kyseinen jakso ei ole saatavilla", Toast.LENGTH_SHORT).show();
            kurssiListView.setAdapter(null);
            lastVuosi = vuosi;
            lastJakso = jakso;
            return;
        }
        lastVuosi = vuosi;
        lastJakso = jakso;
        kurssit = lukkari.getKurssit();
        ArrayAdapter<String> kurssiAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checkedlistitem, kurssit);
        kurssiListView.setAdapter(kurssiAdapter);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);

		return true;
	}



}
