/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private DataStore store = DataStore.getInstance();

    // GET /api/v1/rooms — return all rooms
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = store.getRooms().values();
        return Response.ok(rooms).build();
    }

    // POST /api/v1/rooms — create a new room
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(400)
                .entity(Map.of("error", "Room ID is required"))
                .build();
        }
        store.addRoom(room);
        return Response.status(201) // 201 = Created
            .entity(room)
            .build();
    }

    // GET /api/v1/rooms/{roomId} — get one specific room
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return Response.status(404)
                .entity(Map.of("error", "Room not found: " + roomId))
                .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} — delete a room (only if no sensors)
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);

        if (room == null) {
            return Response.status(404)
                .entity(Map.of("error", "Room not found: " + roomId))
                .build();
        }

        // BUSINESS LOGIC: cannot delete if room has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room " + roomId + " cannot be deleted — it has active sensors assigned."
            );
        }

        store.deleteRoom(roomId);
        return Response.noContent().build(); // 204 = deleted successfully
    }
}