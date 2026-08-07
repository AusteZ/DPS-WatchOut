package administration_server.controller;

import administration_server.exception.NotFoundException;
import administration_server.service.MeasurementService;
import dtos.MeasurementAverageDto;
import dtos.MeasurementListDto;
import dtos.TimestampsDto;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("analytics")
public final class AnalyticsController {
    private static final Logger LOGGER = Logger.getLogger(AnalyticsController.class.getName());

    private final MeasurementService measurementService;

    @Inject
    public AnalyticsController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @Path("post-measurements")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response postMeasurements(MeasurementListDto measurementListDto) {
        measurementService.addMeasurements(measurementListDto);
        return Response.noContent().build();
    }

    @Path("get-last-n-measurements/{playerId}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastNMeasurements(@PathParam("playerId") int playerId,
                                         @QueryParam("lastMeasurementCount") int lastMeasurementCount) {
        try {
            List<MeasurementAverageDto> response = measurementService.calculateLatestMeasurementAverages(playerId, lastMeasurementCount);
            return Response.ok(response).build();
        } catch (ValidationException e) {
            LOGGER.log(Level.SEVERE, "Error while getting last measurements. [playerId=%s, errorMessage=%s]".formatted(playerId, e.getMessage()), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error while getting last measurements. [playerId=%s, errorMessage=%s]".formatted(playerId, e.getMessage()), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown error while getting last measurements. [errorMessage=%s]".formatted(e.getMessage()), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("get-values-between-timestamps")
    @POST
    @Produces({"application/json", "application/xml"})
    public Response getMeasurementsBetweenTimestamps(TimestampsDto timestampsDto) {
        try {
            List<MeasurementAverageDto> response = measurementService.calculateMeasurementAveragesBetweenTimestamps(timestampsDto.startTimestamp(), timestampsDto.endTimestamp());
            return Response.ok(response).build();
        } catch (ValidationException e) {
            LOGGER.log(Level.SEVERE, "Error while getting measurements between timestamps. [timestamps=%s, errorMessage=%s]".formatted(timestampsDto, e.getMessage()), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error while getting measurements between timestamps. [timestamps=%s, errorMessage=%s]".formatted(timestampsDto, e.getMessage()), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown error while getting measurements between timestamps [timestamps=%s, errorMessage=%s]".formatted(timestampsDto, e.getMessage()), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
