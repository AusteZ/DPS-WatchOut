package administration_server.controller;

import administration_server.exception.PlayerAlreadyExistsException;
import administration_server.exception.UninitializedPlayerException;
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

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("players")
public class PlayerController {
    private static final Logger LOGGER = Logger.getLogger(PlayerController.class.getName());

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
        } catch (UninitializedPlayerException e) {
            LOGGER.log(Level.SEVERE, "Error trying to register player. [player=%s, errorMessage=%s]".formatted(player, e.getMessage()), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (PlayerAlreadyExistsException e){
            LOGGER.log(Level.SEVERE, "Error trying to register player. [player=%s, errorMessage=%s]".formatted(player, e.getMessage()), e);
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown error while registering player. [errorMessage=%s]".formatted(e.getMessage()), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("list")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayerList() {
        try{
            PlayersDto players = playerService.getPlayers();
            return Response.ok(players).build();
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "Unknown error while getting player list. [errorMessage=%s]".formatted(e.getMessage()), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
