package com.miralak.basicaccelerometer.api;

import com.miralak.basicaccelerometer.model.Acceleration;
import com.miralak.basicaccelerometer.model.TrainingAcceleration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CassandraRestApi {

    @POST("/acceleration")
    Call<Void> sendAccelerationValues(@Body Acceleration acceleration);


    @POST("/training")
    Call<Void> sendTrainingAccelerationValues(@Body TrainingAcceleration trainingAcceleration);

}
