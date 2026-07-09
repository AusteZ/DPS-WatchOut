package administration_server.Services;

import dtos.PlayerInfo;
import dtos.Players;
import dtos.RegistrationResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("players")
public class PlayersService {
    @Path("registration")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response registerPlayer(PlayerInfo player) {
        RegistrationResponse response = new RegistrationResponse();
        try {
            Players.getInstance().RegisterPlayer(player);
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        return Response.ok(response).build();
    }

    @Path("list")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getPlayerList() {
        Players players = Players.getInstance();
        return Response.ok(players).build();
    }
}
