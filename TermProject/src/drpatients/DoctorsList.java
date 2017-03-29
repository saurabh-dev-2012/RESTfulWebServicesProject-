package drpatients;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "doctorsList")
public class DoctorsList {
    private List<Doctor> doctors; 
    private AtomicInteger doctorId;

    public DoctorsList() { 
		doctors = new CopyOnWriteArrayList<Doctor>(); 
		doctorId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "doctors") 
    public List<Doctor> getDoctors() { 
    	return this.doctors;
    } 
    public void setDoctors(List<Doctor> doctors) { 
    	this.doctors = doctors;
    }

    @Override
    public String toString() {
    	String s = "";
    	for (Doctor d : this.doctors){    	
	    	String name = d.getName();
	    	PatientsList patients = d.getPatients();
	    	s+= name+" -- "+patients.toString();
    	}
    	return s;
    }

    public Doctor find(int id) {
    	Doctor doctor = null;
		// Search the list -- for now, the list is short enough that
		// a linear search is ok but binary search would be better if the
		// list got to be an order-of-magnitude larger in size.
		for (Doctor doc : doctors) {
		    if (doc.getId() == id) {
				doctor = doc;
				break;
		    }
		}	
		return doctor;
    }
    
    public int add(String name, String[] patientNames) {
		int id = doctorId.incrementAndGet();
		Doctor d = new Doctor();
		d.setName(name);
		d.setId(id);
		PatientsList patients = new PatientsList();
		for(String patientName :patientNames){
			
			//ListIterator li = patientNames.listIterator();
			/*while(li.hasNext()){
				patients.add(li.next().toString());
			}*/
			patients.add(patientName);
			
		}
		d.setPatients(patients);
		doctors.add(d);
		return id;
    }
    
    public int add(String name, String docId) {
    	int id = 0;
    	try{
    		id = Integer.parseInt(docId);
    	}
    	catch(NumberFormatException ex){
    		id = doctorId.incrementAndGet();
    	}
		Doctor d = new Doctor();
		d.setName(name);
		d.setId(id);
    	doctors.add(d);
		return id;
    }
    
    
}