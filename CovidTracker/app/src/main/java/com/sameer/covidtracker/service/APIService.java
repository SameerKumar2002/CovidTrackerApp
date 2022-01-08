package com.sameer.covidtracker.service;

import com.sameer.covidtracker.dto.Covid_Info;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("countries")
    Call<ArrayList<Covid_Info>> getCountryData();

}
