package drpatients;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "doctor")
public class Doctor implements Comparable<Doctor> {
    private String name;   // person
    private PatientsList patients;
    private int    id;    // identifier used as lookup-key

    public Doctor() {
    	patients = new PatientsList();
    }

    @Override
    public String toString() {
    	return name + " -- " + patients.toString()+ "\n";
    }
    
    //** properties
    

    public void setId(int id) {
	this.id = id;
    }
    
    @XmlElement
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlElement
	public PatientsList getPatients() {
		return patients;
	}

	public void setPatients(PatientsList patients) {
		this.patients = patients;
	}


	public int getId() {
	return this.id;
    }   
    

    // implementation of Comparable interface
    public int compareTo(Doctor other) {
	return this.id - other.id;
    }	
}