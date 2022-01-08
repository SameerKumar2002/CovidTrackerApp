package com.sameer.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.hbb20.CountryCodePicker;
import com.sameer.covidtracker.adapter.RecyclerAdapter;
import com.sameer.covidtracker.dto.Covid_Info;
import com.sameer.covidtracker.network.ApiClient;
import com.sameer.covidtracker.service.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private FrameLayout frameLayout;
    private List<Covid_Info> list;
    private CountryCodePicker codePicker;
    private TextView total_active;
    private TextView total_recovered,today_recovered;
    private TextView total_deaths,today_deaths;
    private TextView total_case,today_case;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private PieChart piechart;
    final String TAG = "MainActivity";
    private ProgressDialog progressDialog;

    BroadcastReceiver receiver;


    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        frameLayout = findViewById(R.id.id_card_one);
        codePicker = findViewById(R.id.countryCodePicker);

        total_active = findViewById(R.id.id_total_active);
        total_recovered = findViewById(R.id.id_total_recovered);
        today_recovered = findViewById(R.id.id_today_recovered);
        total_deaths = findViewById(R.id.id_total_death);
        today_deaths = findViewById(R.id.id_today_death);
        total_case = findViewById(R.id.id_total_cases);
        today_case = findViewById(R.id.id_today_cases);
        recyclerView = findViewById(R.id.id_recycler);
        piechart = findViewById(R.id.id_piechart);

        list = new ArrayList<>();

        String[] attr = {"Active","Recovered","Case","Death"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,attr);
        spinner.setAdapter(arrayAdapter);

        codePicker.setAutoDetectedCountry(true);
        codePicker.setDialogBackgroundColor(getResources().getColor(R.color.white));
        fetchData(codePicker.getSelectedCountryName());
        fetchRecyclerData(0);

        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                String country = codePicker.getSelectedCountryName();
                fetchData(country);
                fetchRecyclerData(0);
                spinner.setSelection(0);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                fetchRecyclerData(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void fetchRecyclerData(int position){

        ArrayList<Covid_Info> arrayList = new ArrayList<>();
        APIService apiService = ApiClient.getClient().create(APIService.class);
        Call<ArrayList<Covid_Info>> call = apiService.getCountryData();
        call.enqueue(new Callback<ArrayList<Covid_Info>>() {
            @Override
            public void onResponse(Call<ArrayList<Covid_Info>> call, Response<ArrayList<Covid_Info>> response) {
                if(response.isSuccessful()){

                    arrayList.addAll(response.body());
                    recyclerAdapter = new RecyclerAdapter(MainActivity.this,arrayList,position);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(recyclerAdapter);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Covid_Info>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });


    }

    private void fetchData(String country){

            receiver = new ConnectionReceiver();
            registerBroadCast();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCancelable(false);


            APIService apiService = ApiClient.getClient().create(APIService.class);
            Call<ArrayList<Covid_Info>> call = apiService.getCountryData();
            call.enqueue(new Callback<ArrayList<Covid_Info>>() {
                @Override
                public void onResponse(Call<ArrayList<Covid_Info>> call, Response<ArrayList<Covid_Info>> response) {
                   if(response.isSuccessful()) {
                       list.addAll(response.body());
                       for (Covid_Info covid_info : list) {
                           if (covid_info.getCounty().equals(country)) {

                               total_active.setText(String.valueOf(covid_info.getActive()));
                               total_recovered.setText(String.valueOf(covid_info.getRecovered()));
                               today_recovered.setText(String.valueOf(covid_info.getTodayRecovered()));
                               total_deaths.setText(String.valueOf(covid_info.getDeaths()));
                               today_deaths.setText(String.valueOf(covid_info.getTodayDeaths()));
                               total_case.setText(String.valueOf(covid_info.getCases()));
                               today_case.setText(String.valueOf(covid_info.getTodayCases()));


                               int[] color = {android.R.color.holo_green_dark,android.R.color.holo_blue_dark,android.R.color.holo_red_dark};
                               ArrayList<PieEntry> entries = new ArrayList<>();
                               entries.add(new PieEntry(covid_info.getActive()/(float)covid_info.getCases()*100));
                               entries.add(new PieEntry(covid_info.getRecovered()/(float)covid_info.getCases()*100));
                               entries.add(new PieEntry(covid_info.getDeaths()/(float)covid_info.getCases()*100));

                               PieDataSet dataSet = new PieDataSet(entries,"");
                               dataSet.setColors(Color.GREEN,Color.BLUE,Color.RED);
                               PieData data = new PieData(dataSet);
                               piechart.setData(data);
                               //piechart.setDrawSliceText(false);
                           //    piechart.setDrawMarkers(false);
                           //    piechart.setDrawEntryLabels(false);
                               piechart.getDescription().setEnabled(false);
                               piechart.getLegend().setEnabled(false);
                               piechart.invalidate();

                           }
                       }
                       list.clear();
                       progressDialog.dismiss();
                   }
                }

                @Override
                public void onFailure(Call<ArrayList<Covid_Info>> call, Throwable t) {

                    Log.d(TAG, "onFailure: "+t.getMessage());

                }
            });
    }

       protected void registerBroadCast(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

       }

       protected void unRegisterBroadCast(){
        try{
            unregisterReceiver(receiver);
        }catch (IllegalArgumentException exception){
            exception.printStackTrace();
        }
       }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadCast();
    }
}