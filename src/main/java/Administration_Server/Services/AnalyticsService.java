package Administration_Server.Services;

import Beans.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import Administration_Server.MeasurementStorage;
import Extensions.IntegerExtension;

@Path("analytics")
public class AnalyticsService {
    @Path("postmeasurements")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response postMeasurements(MeasurementList ml){
        MeasurementStorage.addMeasurements(ml);
        return Response.ok().build();
    }
    @Path("getlastnmeasurements/{id}/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastNMeasurements(@PathParam("id") String idString, @PathParam("n") String nValues){
        int n = IntegerExtension.tryParseInt(nValues);
        int id = IntegerExtension.tryParseInt(idString);
        
        try {
            double average = MeasurementStorage.getLastNMeasurements(id, n);
            Average response = new Average();
            response.setAverage(average);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @Path("getvaluesbetweentimestamps")
    @POST
    @Produces({"application/json", "application/xml"})
    public Response getMeasurementsBetweenTimestamps(Timestamps timestamps){
        double average =  MeasurementStorage.getMeasurementsBetweenTimestamps(timestamps.getTimestamp1(), timestamps.getTimestamp2());
        Average response = new Average();
        response.setAverage(average);
        if(average <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No values between the two timestamps").build();
        }
        return Response.ok(response).build();
    }
    
}
