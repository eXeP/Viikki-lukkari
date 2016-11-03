package com.pietu.lukkari.utils;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by pietu on 10/15/16.
 */

public class AssetUtils {

    public static ArrayList<ArrayList<String>> fetchTimeTableFromAssets(AssetManager assets, String year, String period){
        BufferedReader reader = null;
        String filename = year+period+".txt";
        ArrayList<ArrayList<String>> classes = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(assets.open(filename)));

            ArrayList<String> oneClass = new ArrayList<>();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if(mLine.length() == 0){
                    classes.add(oneClass);
                    oneClass = new ArrayList<>();
                }
                else{
                    oneClass.add(mLine);
                }
            }
        } catch (IOException e) {
            Log.d("AssetUtils", "Couldn't find in assets: " + filename);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
        return classes;
    }
}
