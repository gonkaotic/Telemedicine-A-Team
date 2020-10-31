package pojos;

import java.sql.Date;

public class Patient {

	private Integer id;
	private String name;
	private Date birthDate;
	private Sex sex;
	private Boolean riskFactor;
	public enum Sex {MALE, FEMALE}
	
	
	
	public Patient() {
		super();
	}

	public Patient(Integer id, String name, Date birthDate, Sex sex, Boolean riskFactor) {
		super();
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.sex = sex;
		this.riskFactor = riskFactor;
		
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
		Patient other = (Patient) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", birthDate=" + birthDate + ", sex=" + sex + ", riskFactor="
				+ riskFactor + "]";
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Boolean getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(Boolean riskFactor) {
		this.riskFactor = riskFactor;
	}
}
