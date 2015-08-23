package com.pietu.lukkari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.CheckBox;

public class Lukkari {
	
	private ArrayList<String> Tunnit = new ArrayList<String>();
	
	public static String EkanTunninPituus = "8:10 - 9.25";
	public static String TokanTunninPituus = "9.35 - 11.00";
	public static String KolmannenTunninPituus = "11.15 - 12.30";
	public static String NeljannenTunninPituus = "13.15 - 14.30";
	public static String ViidennenTunninPituus = "14.45 - 16.00";
	private SparseArray<ArrayList<String>> tunnitJaAjat = new SparseArray< ArrayList<String>>();
	private Set<String> kurssit = new HashSet<String>();

	ArrayList<Integer> selectedItems = new ArrayList<Integer>();
	ArrayList<String> KurssiList;

    private static String padding = "%&^$%(#**(((!@(#*()*@#()!&)(&@#*)!^(#^&!@(&#";

	public Lukkari(int vuosi, int jakso, Activity m) throws Exception{
		
		String filename = null;
		if(vuosi == 1){
			filename = "ykkosetjakso";
		}
		else if(vuosi == 2){
			filename = "kakkosetjakso";
		}
		else if(vuosi == 3){
			filename = "kolmosetjakso";
		}

		filename = filename + Integer.toString(jakso) + ".txt";
		try {
			loadLukkari(filename, m.getAssets());
		}
		catch(Exception e){
			Log.d("lukkariinit", "ei onnistunut");
			throw e;
		}
		
	}
	
	public void loadLukkari(String fileName, AssetManager assetManager) throws  Exception{
		tunnitJaAjat.clear();
		
		try {
			InputStream inputStream = assetManager.open("lukkarit/"+fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		    String s;
			int i = 0;
			while((s=reader.readLine())!= null){
				ArrayList<String> tunnit = new ArrayList<String>();
				while(s.contains("fin") == false){
					
					//Log.d("TUNTI", s);
					tunnit.add(s);
					kurssit.add(s);
					s = reader.readLine();
					
				}
				tunnitJaAjat.put(i, tunnit);
				i++;
				
			}
		 
		} catch (IOException e) {
			Log.d("kurssit", "Lukeminen ei onnistunut");
			throw e;
		}
		kurssit.remove("fin");
        HashSet<String> aidotKurssit = new HashSet<String>();
        for(String kurssi: kurssit){
            int i = kurssi.length()-1;
            for(; i >= 0; i--){
                if(kurssi.charAt(i) == ' ')
                    break;
            }
            aidotKurssit.add(kurssi.substring(0, i));
        }

		KurssiList = new ArrayList<String>(aidotKurssit);
		Collections.sort(KurssiList);
		
	}
	


	public ArrayList<String> getKurssit(){
		return KurssiList;
	}
	
	public SparseArray<ArrayList<String>> getTunnitJaAjat(){
		return tunnitJaAjat;
	}

    public static ArrayList<String> getTunnitByName(String lukkariname, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        ArrayList<String> tunnit = new ArrayList<String>();
        for(int i = 0; i < 24; i++)
            tunnit.add(sp.getString(lukkariname+"__%%$$##1337"+i, null));
        return tunnit;
    }

    public static ArrayList<String> getValitutKurssitByName(String lukkariname, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        ArrayList<String> kurssit = new ArrayList<String>();
        for(int i = 0; i < sp.getInt(lukkariname+"kurssiSize_785408*", 0); i++)
            kurssit.add(sp.getString(lukkariname+"kurssi$$%%_"+i, null));
        return kurssit;
    }

	public static ArrayList<String> getLukkarinames(Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);


        int lukkariN = sp.getInt("_lukkariN", 0);
        ArrayList<String> lukkariNames = new ArrayList<String>();
        for(int i = 0; i < lukkariN; i++){
            lukkariNames.add(sp.getString("_lukkariList"+i, null));
        }

        return lukkariNames;
    }


	public static void removeLukkari(String lukkariname, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor editor = sp.edit();
        for(int i = 0; i < sp.getInt(lukkariname+"kurssiSize_785408*", 0); i++)
            editor.remove(lukkariname+"kurssi$$%%_"+i);
        editor.remove(lukkariname+"kurssiSize_785408*");
        for(int i = 0; i < 24; i++)
            editor.remove(lukkariname+i);

        ArrayList<String> newLukkarinames = getLukkarinames(con);
        newLukkarinames.remove(lukkariname);
        editor.remove(lukkariname+"_vuosi%^^&");
        editor.remove(lukkariname+"_jakso%^^&");
        editor.putInt("_lukkariN", newLukkarinames.size());
        editor.remove("_lukkariList" + newLukkarinames.size());
        for(int i = 0; i < newLukkarinames.size(); i++)
            editor.putString("_lukkariList"+i, newLukkarinames.get(i));


        editor.commit();
	}

	public static void addLukkari(String lukkariname, int vuosi, int jakso, ArrayList<String> tunnit, ArrayList<String> kurssit, Context con)
	{
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
	    SharedPreferences.Editor editor = sp.edit();
        removeLukkari(lukkariname, con);

        editor.putInt(lukkariname + "_vuosi%^^&", vuosi);
        editor.putInt(lukkariname + "_jakso%^^&", jakso);

        editor.putInt("_lukkariN", sp.getInt("_lukkariN", 0) + 1);
        editor.putInt(lukkariname + "kurssiSize_785408*", kurssit.size());
        editor.putString("_lukkariList" + sp.getInt("_lukkariN", 0), lukkariname);
        for(int i = 0; i < kurssit.size(); i++){
            editor.putString(lukkariname+"kurssi$$%%_"+i, kurssit.get(i));
        }
        for(int i = 0; i < 24; i++)
            editor.putString(lukkariname+"__%%$$##1337" +i, tunnit.get(i));

	    editor.commit();
	}

    public static int getVuosiByName(String lukkariname, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getInt(lukkariname+"_vuosi%^^&", 0);
    }

    public static int getJaksoByName(String lukkariname, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getInt(lukkariname+"_jakso%^^&", 0);
    }

    public static void setLastLukkari(String lukkariname, Context con)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("_lastLukkariebineslol", lukkariname);
        editor.commit();
    }

    public static String getLastLukkari(Context con)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getString("_lastLukkariebineslol", null);
    }



}

