/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// RoomNotEmptyExceptionMapper.java
package com.smartcampus.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Provider // Tells JAX-RS to use this automatically
public class RoomNotEmptyExceptionMapper 
    implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException e) {
        return Response.status(409) // 409 Conflict
            .entity(Map.of(
                "error", "409 Conflict",
                "message", e.getMessage()
            ))
            .type("application/json")
            .build();
    }
}