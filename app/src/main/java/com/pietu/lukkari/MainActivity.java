package com.pietu.lukkari;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.pietu.lukkari.utils.StorageUtils;

import java.util.ArrayList;

import com.pietu.lukkari.R;

/**
 * Created by pietu on 10/16/16.
 */

public class MainActivity extends AppCompatActivity {

    private String timetableName = "";
    private ArrayList<String> timetableStrings = null;

    private GridView timetableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timetableName = StorageUtils.getLastUsedTimetable(getApplicationContext());
        if(timetableName.length() == 0){
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        timetableView = (GridView) findViewById(R.id.timetable);

        setupTimetable();
        setupListeners();
    }

    private void setupTimetable(){
        timetableStrings = StorageUtils.getTimetableItems(timetableName, getApplicationContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.timetableitem, timetableStrings);
        timetableView.setAdapter(adapter);
    }

    private void setupListeners(){
        timetableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView tv = (TextView) view;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Read Update
                alertDialog.setTitle("Muuta tuntia");

                final EditText input = new EditText(MainActivity.this);
                input.setText(tv.getText());
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Valmis", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String edited = input.getText().toString();
                        ArrayList<String> newTimetable = StorageUtils.getTimetableItems(timetableName, getApplicationContext());
                        newTimetable.remove(position);
                        newTimetable.add(position, edited);

                        StorageUtils.updateTimetable(timetableName, newTimetable, getApplicationContext());
                        setupTimetable();
                    }
                });

                alertDialog.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_new) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.action_remove){
            final String chosen = timetableName;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Read Update
            alertDialog.setTitle("Haluatko varmasti poistaa lukkarin " + chosen+"?");
            alertDialog.setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    StorageUtils.removeTimetable(chosen, getApplicationContext());
                    StorageUtils.setLastUsedTimetable("", getApplicationContext());
                    timetableView.setAdapter(null);
                }
            });
            alertDialog.setNegativeButton("En", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alertDialog.show();
            return true;
        }
        else if(item.getItemId() == R.id.action_change){
            View menuItemView = findViewById(R.id.action_change);
            PopupMenu popup = new PopupMenu(this, menuItemView);
            for(String ttName: StorageUtils.getAllTimetableNames(getApplicationContext()))
                popup.getMenu().add(ttName);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String chosen = menuItem.getTitle().toString();

                    StorageUtils.setLastUsedTimetable(chosen, getApplicationContext());
                    timetableName = chosen;
                    setupTimetable();

                    return true;
                }
            });
            popup.show();
        }
        return false;
    }
}
