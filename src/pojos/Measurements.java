package pojos;

import java.sql.Date;
import java.util.Arrays;

public class Measurements {

	private Integer id;
	private Integer patientId;
	private Date date;
	private String ECG;
	private Float SpO2;
	private Integer BPM;
	private Float Temperature;
	private Symptoms symptomChecklist[];
	
	public enum Symptoms {FEVER, DRY_COUGH, TIREDNESS, ANOSMIA, AUGEUSIA, DIFF_BREATH, CHEST_PAIN};
	
	public Measurements() {
		super();
	}

	public Measurements(Integer id, Integer pacientId, Date date, String eCG, Float spO2, Integer bPM,
			Float temperature, Symptoms[] symptomChecklist) {
		super();
		this.id = id;
		this.patientId = pacientId;
		this.date = date;
		ECG = eCG;
		SpO2 = spO2;
		BPM = bPM;
		Temperature = temperature;
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
		Measurements other = (Measurements) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "Measurements [id=" + id + ", pacientId=" + patientId + ", date=" + date + ", ECG=" + ECG + ", SpO2="
				+ SpO2 + ", BPM=" + BPM + ", Temperature=" + Temperature + ", symptomChecklist="
				+ Arrays.toString(symptomChecklist) + "]";
	}

	public Integer getid() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id; 
	}

	public Integer getPacientId() {
		return patientId;
	}

	public void setPacientId(Integer pacientId) {
		this.patientId = pacientId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getECG() {
		return ECG;
	}

	public void setECG(String eCG) {
		ECG = eCG;
	}

	public Float getSpO2() {
		return SpO2;
	}

	public void setSpO2(Float spO2) {
		SpO2 = spO2;
	}

	public Integer getBPM() {
		return BPM;
	}

	public void setBPM(Integer bPM) {
		BPM = bPM;
	}

	public Float getTemperature() {
		return Temperature;
	}

	public void setTemperature(Float temperature) {
		Temperature = temperature;
	}

	public Symptoms[] getSymptomChecklist() {
		return symptomChecklist;
	}

	public void setSymptomChecklist(Symptoms[] symptomChecklist) {
		this.symptomChecklist = symptomChecklist;
	}
}
