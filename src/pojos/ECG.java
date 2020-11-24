package pojos;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ECG implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6948532391999286976L;
	private ArrayList<Float> ecg;
	private ArrayList<Float> times;
	
	
	public ECG() {
		super();
	}
	
	public ECG(ArrayList<Float> ecg, ArrayList<Float> times) {
		this.ecg = ecg;
		this.times = times;
	}

	public List<Float> getEcg() {
		return ecg;
	}

	public void setEcg( ArrayList<Float> ecg) {
		this.ecg = ecg;
	}

	public List<Float> getTimes() {
		return times;
	}

	public void setTimes( ArrayList<Float> times) {
		this.times = times;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ecg == null) ? 0 : ecg.hashCode());
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
		ECG other = (ECG) obj;
		if (ecg == null) {
			if (other.ecg != null)
				return false;
		} else if (!ecg.equals(other.ecg))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ecg.toString() + "\n" + times.toString();
	}

}
