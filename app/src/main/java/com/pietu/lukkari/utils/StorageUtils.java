package com.pietu.lukkari.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by pietu on 10/16/16.
 */

public class StorageUtils {
    private static String pad = "lol&&&@@@@*";
    private static String arrPad = "&*(&^()";
    private static String sizePad = "sz&&^^";

    private static String allSavedNamesListStr = "savedNamesLol";
    private static String lastUsedTimetableStr = "lastusedtt%$$@";

    public static boolean addTimetable(String name, ArrayList<String> classes, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        if(name.equals(allSavedNamesListStr))
            return false;
        HashSet<String> allSavedNames = getAllTimetableNames(con);
        if(allSavedNames.contains(name))
            return false;
        Log.d("sotrageutils", "ppastiin ohi");
        allSavedNames.add(name);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(allSavedNamesListStr+arrPad+(allSavedNames.size()-1), name);
        editor.putInt(allSavedNamesListStr+sizePad, allSavedNames.size());

        editor.putInt(name+sizePad, classes.size());
        for(int i = 0; i < classes.size(); ++i){
            editor.putString(name+arrPad+i, classes.get(i));
        }

        editor.commit();
        return true;
    }

    public static void updateTimetable(String name, ArrayList<String> classes, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);

        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(name+sizePad, classes.size());
        for(int i = 0; i < classes.size(); ++i){
            editor.putString(name+arrPad+i, classes.get(i));
        }

        editor.commit();
    }

    public static void removeTimetable(String name, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        HashSet<String> allSavedNames = getAllTimetableNames(con);
        allSavedNames.remove(name);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(allSavedNamesListStr+sizePad, allSavedNames.size());
        int idx = 0;
        for(String str : allSavedNames) {
            editor.putString(allSavedNamesListStr+arrPad+idx, str);
            ++idx;
        }
        editor.commit();
    }

    public static ArrayList<String> getTimetableItems(String name, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        int itemCount = sp.getInt(name+sizePad, 0);
        ArrayList<String> items = new ArrayList<>();
        for(int i = 0; i < itemCount; ++i){
            items.add(sp.getString(name+arrPad+i, ""));
        }
        return items;
    }

    public static HashSet<String> getAllTimetableNames(Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        int nameCount = sp.getInt(allSavedNamesListStr+sizePad, 0);
        HashSet<String> allSavedNames = new HashSet<>();
        for(int i = 0; i < nameCount; ++i){
            allSavedNames.add(sp.getString(allSavedNamesListStr+arrPad+i, ""));
        }
        return allSavedNames;
    }

    public static void setLastUsedTimetable(String name, Context con){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(lastUsedTimetableStr+pad, name);
        editor.commit();
    }

    public static String getLastUsedTimetable(Context con) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getString(lastUsedTimetableStr+pad, "");
    }

}
