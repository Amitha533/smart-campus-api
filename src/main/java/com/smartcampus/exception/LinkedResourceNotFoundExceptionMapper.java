/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// LinkedResourceNotFoundExceptionMapper.java
package com.smartcampus.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper 
    implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        return Response.status(422) // 422 Unprocessable Entity
            .entity(Map.of(
                "error", "422 Unprocessable Entity",
                "message", e.getMessage()
            ))
            .type("application/json")
            .build();
    }
}