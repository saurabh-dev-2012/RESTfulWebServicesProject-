package drpatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "patientsList")
public class PatientsList {
    private List<Patient> patients; 
    private AtomicInteger patientId;

    public PatientsList() { 
		patients = new CopyOnWriteArrayList<Patient>(); 
		patientId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "patients") 
    public List<Patient> getPatients() { 
    	return this.patients;
    } 
    public void setPatients(List<Patient> patients) { 
    	this.patients = patients;
    }

    @Override
    public String toString() {
    	String s = "";
    	int count = 0;
    	for (Patient p : this.patients){    	
    	String name = p.getName();
    	String insuranceCN = p.getInsuranceCN();
    	s += count+": "+name+" ==> "+insuranceCN+ "\n";
    	count++;
    	}
    	return s;
    }

    public Patient find(int id) {
		Patient pred = null;
		// Search the list -- for now, the list is short enough that
		// a linear search is ok but binary search would be better if the
		// list got to be an order-of-magnitude larger in size.
		for (Patient p : patients) {
		    if (p.getId() == id) {
			pred = p;
			break;
		    }
		}	
		return pred;
    }
    public int add(String name, String insuranceCN) {
		int id = patientId.incrementAndGet();
		Patient p = new Patient();
		p.setName(name);
		p.setInsuranceCN(insuranceCN);
		p.setId(id);
		patients.add(p);
		return id;
    }
    
    public int add(String name) {
		int id = patientId.incrementAndGet();
		Patient p = new Patient();
		p.setName(name);
		p.addNewInsuranceCN();
		p.setId(id);
		patients.add(p);
		return id;
    }
}