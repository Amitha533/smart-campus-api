/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// SensorUnavailableExceptionMapper.java
package com.smartcampus.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Provider
public class SensorUnavailableExceptionMapper 
    implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException e) {
        return Response.status(403) // 403 Forbidden
            .entity(Map.of(
                "error", "403 Forbidden",
                "message", e.getMessage()
            ))
            .type("application/json")
            .build();
    }
}