package com.pietu.lukkari;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	ArrayList<String> tunnit;
	GridView lukkarigrid;
    String lukkariname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent from = getIntent();
        Log.d("main", "taalla ollaan");

        lukkariname = Lukkari.getLastLukkari(getApplicationContext());
        
        if(lukkariname == null || Lukkari.getLukkarinames(this).size() == 0){
            Intent startIntent = new Intent(this, StartActivity.class);
            startActivity(startIntent);
            finish();
            return;
        }
		tunnit = Lukkari.getTunnitByName(lukkariname, this);
		
		if(tunnit.size() == 0){
			Intent startIntent = new Intent(this, StartActivity.class);
			startActivity(startIntent);
			finish();
		}
		
		setContentView(R.layout.activity_main);
		
		lukkarigrid = (GridView) findViewById(R.id.lukkariGrid);  
        muodostaLukkariGrid();
        // Create adapter to set value for grid view
          
	}

	public void muodostaLukkariGrid(){
		ArrayList<String> uudetTunnit = Lukkari.getTunnitByName(lukkariname, this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.lukkariitem, uudetTunnit.toArray(new String[uudetTunnit.size()]));
        
        lukkarigrid.setAdapter(adapter);
 
        lukkarigrid.setOnItemClickListener(new OnItemClickListener() {

           

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
					long id) {
				Log.d("Muokkaus", Integer.toString(pos));
				TextView t = (TextView) arg1;
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Read Update
		        alertDialog.setTitle("Muuta tuntia");
		        
		        final EditText input = new EditText(MainActivity.this);
                input.setText(t.getText());
		        alertDialog.setView(input);

		        alertDialog.setPositiveButton("valmis", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	String value = input.getText().toString();
		        	ArrayList<String> uusiLukkari = Lukkari.getTunnitByName(lukkariname, MainActivity.this);
		        	uusiLukkari.remove(pos);
		        	uusiLukkari.add(pos, value);

		        	Lukkari.removeLukkari(lukkariname, MainActivity.this);
		        	Lukkari.addLukkari(lukkariname, uusiLukkari, Lukkari.getKurssitByName(lukkariname, getApplicationContext()), MainActivity.this);
		        	
		        	muodostaLukkariGrid();
		          }
		        });

		        alertDialog.setNegativeButton("peruuta", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int whichButton) {
		            // Canceled.
		          }
		        });
		        
		        
		        alertDialog.show();
				
			}
        });
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_change:
                View menuItemView = findViewById(R.id.action_change);
                PopupMenu popup = new PopupMenu(this, menuItemView);

                popup.getMenu().add("Uusi lukkari");
                for(String lname: Lukkari.getLukkarinames(getApplicationContext()))
                    popup.getMenu().add(lname);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String valittu = menuItem.getTitle().toString();
                        if(valittu.equals("Uusi lukkari")){
                            Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(startIntent);
                            finish();
                        }
                        else{
                            Lukkari.setLastLukkari(valittu, getApplicationContext());
                            lukkariname = valittu;
                            muodostaLukkariGrid();
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            case R.id.action_remove:
                View menuItemView2 = findViewById(R.id.action_remove);
                PopupMenu popup2 = new PopupMenu(this, menuItemView2);


                for(String lname: Lukkari.getLukkarinames(getApplicationContext()))
                    popup2.getMenu().add(lname);
                popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        final String valittu = menuItem.getTitle().toString();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Read Update
                        alertDialog.setTitle("Haluatko varmasti poistaa lukkarin " + valittu+"?");

                        alertDialog.setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Lukkari.removeLukkari(valittu, getApplicationContext());
                            }
                        });

                        alertDialog.setNegativeButton("En", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });


                        alertDialog.show();
                        return true;
                    }
                });
                popup2.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	private int HorizontalIndexToVertical(int HI){
		int VI = 0;
		if(HI < 5)
			VI = HI * 5;
		else if(HI < 10)
			VI = (HI - 5) * 5 + 1;
		else if(HI == 10)
			VI = 2;
		else if(HI == 11)
			VI = 7;
		else if(HI == 13)
			VI = 17;
		else if(HI == 14)
			VI = 22;
		else if(HI == 15)
			VI = 3;
		else if(HI == 16)
			VI = 8;
		else if(HI == 17)
			VI = 13;
		else if(HI == 18)
			VI = 18;
		else if(HI == 19)
			VI = 23;
		else if(HI == 20)
			VI = 4;
		else if(HI == 21)
			VI = 9;
		else if(HI == 22)
			VI = 14;
		else if(HI == 23)
			VI = 19;
		else
			VI = HI;
		return VI;
	}
}
