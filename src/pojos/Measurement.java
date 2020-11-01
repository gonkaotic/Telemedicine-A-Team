package pojos;

import java.io.Serializable;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class Measurement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149809256669406163L;
	
	
	private Integer id;
	private Patient patient;
	private Date date;
	private List<Float> ecg;
	private Float spO2;
	private Integer bpm;
	private Float temperature;
	private Symptom symptomChecklist[];
	
	public enum Symptom {FEVER, DRY_COUGH, TIREDNESS, ANOSMIA, AUGEUSIA, DIFF_BREATH, CHEST_PAIN};
	
	public Measurement() {
		super();
	}

	public Measurement(Integer id, Patient patient, Date date, List<Float> ecg, Float spO2, Integer bpm,
						Float temperature, Symptom[] symptomChecklist) {
		super();
		this.id = id;
		this.patient = patient;
		this.date = date;
		this.ecg = ecg;
		this.spO2 = spO2;
		this.bpm = bpm;
		this.temperature = temperature;
		this.symptomChecklist = symptomChecklist;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Measurement other = (Measurement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "Measurements [id=" + id + ", patientId=" + patient + ", date=" + date + ", ECG=" + ecg + ", SpO2="
				+ spO2 + ", BPM=" + bpm + ", Temperature=" + temperature + ", symptomChecklist="
				+ Arrays.toString(symptomChecklist) + "]";
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id; 
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Float> getECG() {
		return ecg;
	}

	public void setECG(List<Float> ecg) {
		this.ecg = ecg;
	}

	public Float getSpO2() {
		return spO2;
	}

	public void setSpO2(Float spO2) {
		this.spO2 = spO2;
	}

	public Integer getBPM() {
		return bpm;
	}

	public void setBPM(Integer bpm) {
		this.bpm = bpm;
	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public Symptom[] getSymptomChecklist() {
		return symptomChecklist;
	}

	public void setSymptomChecklist(Symptom[] symptomChecklist) {
		this.symptomChecklist = symptomChecklist;
	}
}
