/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import com.smartcampus.exception.GlobalExceptionMapper;
import com.smartcampus.exception.RoomNotEmptyExceptionMapper;
import com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.exception.SensorUnavailableExceptionMapper;
import com.smartcampus.filter.LoggingFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) throws Exception {

        ResourceConfig config = new ResourceConfig();
        
        config.register(DiscoveryResource.class);
        config.register(RoomResource.class);
        config.register(SensorResource.class);
        config.register(GlobalExceptionMapper.class);
        config.register(RoomNotEmptyExceptionMapper.class);
        config.register(LinkedResourceNotFoundExceptionMapper.class);
        config.register(SensorUnavailableExceptionMapper.class);
        config.register(LoggingFilter.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
            URI.create(BASE_URI), config
        );

        System.out.println("Smart Campus API running at " + BASE_URI);
        System.out.println("Press ENTER to stop the server.");
        System.in.read();
        server.shutdownNow();
    }
}