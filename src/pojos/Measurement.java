package pojos;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Measurement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149809256669406163L;
	
	
	private Integer id;
	private Integer patientId;
	private Date date;
	private ECG ecg;
	private Float spO2;
	private Integer bpm;
	private Float temperature;
	private List<Symptom> symptomChecklist;
	
	public enum Symptom {FEVER, DRY_COUGH, TIREDNESS, ANOSMIA, AUGEUSIA, DIFF_BREATH, CHEST_PAIN}
	
	public Measurement() {

		super();
		this.bpm = 75;
		this.spO2 = 9.87f;
		this.ecg = new ECG();
		ArrayList<Float> ecg = new ArrayList<Float>();
		ecg.add(36f);
		ecg.add(36.7f);
		this.ecg.setEcg(ecg);
		this.temperature = 37.5f;
		this.patientId = 1;

		this.symptomChecklist = new ArrayList<>();

		this.symptomChecklist.add(Symptom.ANOSMIA);
		this.symptomChecklist.add(Symptom.CHEST_PAIN);
		this.symptomChecklist.add(Symptom.FEVER);

	}

	public Measurement(Integer id, Integer patientId, Date date, ECG ecg, Float spO2, Integer bpm, Float temperature, List<Symptom> symptomChecklist) {
		this.id = id;
		this.patientId = patientId;
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
			return other.id != null;
		} else {
			return id.equals(other.id);
		}
	}


	@Override
	public String toString() {
		return "Measurement{" +
				"id=" + id +
				", patient=" + patientId +
				", date=" + date +
				", ecg=" + ecg +
				", spO2=" + spO2 +
				", bpm=" + bpm +
				", temperature=" + temperature +
				", symptomChecklist=" + symptomChecklist +
				'}';
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id; 
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ECG getECG() {
		return ecg;
	}

	public void setECG(ECG ecg) {
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

	public List<Symptom> getSymptomChecklist() {
		return symptomChecklist;
	}

	public void setSymptomChecklist(List<Symptom> symptomChecklist) {
		this.symptomChecklist = symptomChecklist;
	}
}
