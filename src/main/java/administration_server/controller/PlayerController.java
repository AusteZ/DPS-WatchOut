package administration_server.controller;

import administration_server.service.PlayerService;
import dtos.PlayerInfo;
import dtos.PlayersDto;
import dtos.RegistrationResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Path("registration")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response registerPlayer(PlayerInfo player) {
        try {
            RegistrationResponse response = playerService.registerPlayer(player);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }

    }

    @Path("list")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayerList() {
        PlayersDto players = playerService.getPlayers();
        return Response.ok(players).build();
    }
}
