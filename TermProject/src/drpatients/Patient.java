package drpatients;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "patient")
public class Patient implements Comparable<Patient> {
    private String name;   // person
    private String insuranceCN;  // his/her prediction
    private int    id;    // identifier used as lookup-key

    public Patient() { }

    @Override
    public String toString() {
	return String.format("%2d: ", id) + name + " ==> " + insuranceCN + "\n";
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
	public String getInsuranceCN() {
		return insuranceCN;
	}

	public void setInsuranceCN(String insuranceCN) {
		this.insuranceCN = insuranceCN;
	}


	public int getId() {
	return this.id;
    }   
    

    // implementation of Comparable interface
    public int compareTo(Patient other) {
	return this.id - other.id;
    }	
    
    public void addNewInsuranceCN(){
    	this.insuranceCN = randomStringOfLength(10);
    }
    
    public static String randomStringOfLength(int length) {
        StringBuffer buffer = new StringBuffer();
        while (buffer.length() < length) {
            buffer.append(uuidString());
        }

	    //this part controls the length of the returned string
	    return buffer.substring(0, length).toUpperCase();  
    }


	private static String uuidString() {
	    return UUID.randomUUID().toString().replaceAll("-", "");
	}
}