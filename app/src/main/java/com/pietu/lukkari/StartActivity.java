package com.pietu.lukkari;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.pietu.lukkari.utils.AssetUtils;
import com.pietu.lukkari.utils.StorageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class StartActivity extends AppCompatActivity {

    ListView courseListView;
    Spinner yearSpinner, periodSpinner;
    String[] yearStrings = {"ykkonen", "kakkonen", "abi"};
    String[] periodStrings;
    String selectedYear = null, selectedPeriod = null;
    ArrayList<ArrayList<String>> classes = null;
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        courseListView = (ListView) findViewById(R.id.courseList);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        periodSpinner = (Spinner) findViewById(R.id.periodSpinner);
        nameText = (EditText) findViewById(R.id.nameText);

        courseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        periodStrings = getResources().getStringArray(R.array.periods);
        setupListeners();

    }

    private void setupListeners(){
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = yearStrings[position];
                if(selectedYear != null && selectedPeriod != null){
                    fetchAndDisplayTimeTable(selectedYear, selectedPeriod);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = null;
            }
        });
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeriod = periodStrings[position];
                if(selectedYear != null && selectedPeriod != null){
                    fetchAndDisplayTimeTable(selectedYear, selectedPeriod);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPeriod = null;
            }
        });
    }

    public void fetchAndDisplayTimeTable(String year, String period) {
        classes = AssetUtils.fetchTimeTableFromAssets(getAssets(), year, period);
        HashSet<String> courses = new HashSet<>();
        for(ArrayList<String>  list:  classes){
            for(String str : list){
                courses.add(str);
            }
        }
        ArrayList<String> sortedCourses = new ArrayList<>(courses);
        Collections.sort(sortedCourses);
        setCourseListViewContent(sortedCourses);
    }

    void setCourseListViewContent(ArrayList<String> content){
        ArrayAdapter<String> contentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, content);
        courseListView.setAdapter(contentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.start, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    private ArrayList<String> getSelectedCourses(){
        SparseBooleanArray positions = courseListView.getCheckedItemPositions();
        ArrayList<String> selectedCourses = new ArrayList<>();
        for(int i = 0; i < courseListView.getAdapter().getCount(); ++i){
            if(positions.get(i)){
                selectedCourses.add((String)courseListView.getAdapter().getItem(i));
            }
        }
        return selectedCourses;
    }

    private boolean generateAndSaveTimetable(String name, ArrayList<String> selectedCourses){
        ArrayList<String> finalClasses = new ArrayList<>();
        for(ArrayList<String> classSpot : classes){
            String chosenClass = "";
            for(String classCandidate : classSpot){
                if(selectedCourses.contains(classCandidate)){
                    chosenClass = classCandidate;
                    break;
                }
            }
            finalClasses.add(chosenClass);
        }
        if(finalClasses.size() == 0)
            return false;
        if(!StorageUtils.addTimetable(name, finalClasses, getApplicationContext()))
            return false;
        StorageUtils.setLastUsedTimetable(name, getApplicationContext());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_done) {
            if(nameText.getText().length() == 0){
                Toast.makeText(StartActivity.this, "Nimi on liian lyhyt", Toast.LENGTH_SHORT).show();
                return true;
            }
            String timetableName = nameText.getText().toString();
            ArrayList<String> selectedCourses = getSelectedCourses();
            if(!generateAndSaveTimetable(timetableName, selectedCourses)){
                Toast.makeText(StartActivity.this, "Lukkari ei kelpaa", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }
}
