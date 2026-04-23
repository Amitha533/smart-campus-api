/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// GlobalExceptionMapper.java — catches EVERYTHING else
package com.smartcampus.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        e.printStackTrace();

        if (e instanceof NotFoundException) {
            return Response.status(404)
                .entity(Map.of(
                    "error", "404 Not Found",
                    "message", "The requested endpoint does not exist"
                ))
                .type("application/json")
                .build();
        }

        return Response.status(500)
            .entity(Map.of(
                "error", "500 Internal Server Error",
                "message", "An unexpected error occurred."
            ))
            .type("application/json")
            .build();
    }
}