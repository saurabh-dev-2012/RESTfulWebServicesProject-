package drpatients;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class DoctorsRS {
    @Context 
    private ServletContext sctx;          // dependency injection
    private static DoctorsList dlist; // set in populate()

    public DoctorsRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
		checkContext();
		return Response.ok(toJson(dlist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
		checkContext();
		return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
		checkContext();
		return dlist.toString();
    }
    
    @GET  
    @Path("/plain/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})    
    public String getPlain(@PathParam("id") int id) {
		checkContext();
		Doctor doctor = dlist.find(id);
		return doctor.toString();
    }

    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("name") String name, @FormParam("patients") String patientNames) {
		checkContext();
		String msg = null;
		// Require both properties to create.
		if (name == null) {
		    msg = "Property 'name' is missing.\n";
		    return Response.status(Response.Status.BAD_REQUEST).
			                                   entity(msg).
			                                   type(MediaType.TEXT_PLAIN).
			                                   build();
		}	    
		// Otherwise, create the Prediction and add it to the collection.
		String[] patients = patientNames.split(",");
		int id = addDoctor(name, patients);
		msg = "Doctor " + id + " created: (name = " + name +").\n";
		return Response.ok(msg, "text/plain").build();
    }

    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id,
			   @FormParam("name") String name) {
	checkContext();

	// Check that sufficient data are present to do an edit.
	String msg = null;
	if (name == null) 
	    msg = "No name is given: nothing to edit.\n";

	Doctor d = dlist.find(id);
	if (d == null)
	    msg = "There is no patient with ID " + id + "\n";

	if (msg != null)
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	// Update.
	if (name != null) d.setName(name);
	
	msg = "Doctor " + name + " has been updated.\n";
	return Response.ok(msg, "text/plain").build();
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
		checkContext();
		String msg = null;
		Doctor d = dlist.find(id);
		if (d == null) {
		    msg = "There is no doctor with ID " + id + ". Cannot delete.\n";
		    return Response.status(Response.Status.BAD_REQUEST).
			                                   entity(msg).
			                                   type(MediaType.TEXT_PLAIN).
			                                   build();
		}
		dlist.getDoctors().remove(d);
		msg = "Doctor " + id + " deleted.\n";
	
		return Response.ok(msg, "text/plain").build();
    }

    //** utilities
    private void checkContext() {
	if (dlist == null) populate();
    }

    private void populate() {
		dlist = new DoctorsList();
	
		String drFilename = "/WEB-INF/data/drs.db";
		String ptFilename = "/WEB-INF/data/patients.db";
		InputStream drIn = sctx.getResourceAsStream(drFilename);
		InputStream ptIn = sctx.getResourceAsStream(ptFilename);
		PatientsList patients = new PatientsList();
		try{
			//load data into patients list.
	    	BufferedReader ptReader = new BufferedReader(new InputStreamReader(ptIn));
			String ptRecord = null;
			
			//Scanner src = new Scanner(new FileReader(ptFilename));
			while ((ptRecord = ptReader.readLine()) != null/*src.hasNext()*/) {						
				//String line = src.next();
				String[] parts2 = ptRecord.split("!");
				patients.add(parts2[0], parts2[1]);
			}
			//src.close();
			//ptIn.close();
			ptReader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		
		// Read the data into the array of Predictions. 
		if (drIn != null) {
		    try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(drIn));
				int i = 0;
				String record = null;
				while ((record = reader.readLine()) != null) {
				    String[] parts = record.split("!");
				    int docId = addDoctor(parts[0], parts[1]);
				    Doctor newDoctor = dlist.find(docId);
				    if(newDoctor != null){
						newDoctor.setPatients(patients);			    	
				    }	    
				    
				}
		    }
		    catch (Exception e) { 
		    	System.out.println("Exception = "+e.getMessage());
		    	e.printStackTrace();
		    	
		    	//throw new RuntimeException("I/O failed!"); 
		    }
		}
    }

    // Add a new prediction to the list.
    private int addDoctor(String name, String[] patients) {
		int id = dlist.add(name, patients);
		return id;
    }
    
    private int addDoctor(String name, String docId) {
		int id = dlist.add(name, docId);
		return id;
    }

    private String toJson(Doctor doctor) {
		String json = "If you see this, there's a problem.";
		try {
		    json = new ObjectMapper().writeValueAsString(doctor);
		}
		catch(Exception e) { }
		return json;
    }

    // PredictionsList --> JSON document
    private String toJson(DoctorsList dList) {
		String json = "If you see this, there's a problem.";
		try {
		    json = new ObjectMapper().writeValueAsString(dList);
		}
		catch(Exception e) { }
		return json;
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
		Doctor doctor = dlist.find(id);
		if (doctor == null) {
		    String msg = id + " is a bad ID.\n";
		    return Response.status(Response.Status.BAD_REQUEST).
			                                   entity(msg).
			                                   type(MediaType.TEXT_PLAIN).
			                                   build();
		}
		else if (type.contains("json"))
		    return Response.ok(toJson(doctor), type).build();
		else
		    return Response.ok(doctor, type).build(); // toXml is automatic
    }
    
}



