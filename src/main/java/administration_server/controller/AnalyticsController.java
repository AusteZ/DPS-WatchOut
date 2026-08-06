package administration_server.controller;

import administration_server.service.MeasurementService;
import dtos.AverageDto;
import dtos.MeasurementListDto;
import dtos.TimestampsDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("analytics")
public class AnalyticsController {
    private final MeasurementService measurementService;

    public AnalyticsController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @Path("postmeasurements")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response postMeasurements(MeasurementListDto ml) {
        measurementService.addMeasurements(ml);
        return Response.ok().build();
    }

    @Path("getlastnmeasurements/{playerId}/{lastMeasurementCount}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastNMeasurements(@PathParam("playerId") int playerId, @PathParam("lastMeasurementCount") int lastMeasurementCount) {
        try {
            double average = measurementService.calculateLatestMeasurementAverage(playerId, lastMeasurementCount);
            AverageDto response = new AverageDto(average);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("getvaluesbetweentimestamps")
    @POST
    @Produces({"application/json", "application/xml"})
    public Response getMeasurementsBetweenTimestamps(TimestampsDto timestampsDto) {
        double average = measurementService.calculateMeasurementAverageBetweenTimestamps(timestampsDto.startTimestamp(), timestampsDto.endTimestamp());
        if (average <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No values between the two timestamps").build();
        }

        AverageDto response = new AverageDto(average);
        return Response.ok(response).build();
    }
}
