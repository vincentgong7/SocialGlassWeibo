package mt.weibo.model;

public class Face {
	private int age = 0;
	private int range = 0;
	private String gender = "Male";
	private double genderConfidence = 0d;
	private String race = "";
	private double raceConfidence = 0d;
	private String glass;
	private double glassConfidence = 0d;
	private double smiling = 0d;

	public Face(int age, int range, String gender, double genderConfidant,
			String race, double raceConfidant, String glass, double glassConfident ,double smiling) {
		this.age = age;
		this.range = range;
		this.gender = gender;
		this.genderConfidence = genderConfidant;
		this.race = race;
		this.raceConfidence = raceConfidant;
		this.glass = glass;
		this.glassConfidence = glassConfident;
		this.smiling = smiling;
	};

	public String toString() {
		String line = "age=" + age + ", range=" + range + ", gender=" + gender
				+ ", gender Confidant=" + genderConfidence + ", race=" + race
				+ ", raceConfidant=" + raceConfidence + ", glass=" + glass
				+ ", glassConfident=" + glassConfidence + ", smiling=" + smiling;
		return line;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public double getGenderConfidence() {
		return genderConfidence;
	}

	public void setGenderConfidence(double genderConfidence) {
		this.genderConfidence = genderConfidence;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public double getRaceConfidence() {
		return raceConfidence;
	}

	public void setRaceConfidence(double raceConfidence) {
		this.raceConfidence = raceConfidence;
	}

	public String getGlass() {
		return glass;
	}

	public void setGlass(String glass) {
		this.glass = glass;
	}

	public double getGlassConfidence() {
		return glassConfidence;
	}

	public void setGlassConfidence(double glassConfidence) {
		this.glassConfidence = glassConfidence;
	}

	public double getSmiling() {
		return smiling;
	}

	public void setSmiling(double smiling) {
		this.smiling = smiling;
	}

}
