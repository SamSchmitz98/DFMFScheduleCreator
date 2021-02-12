package scheduler;

public class Bowl {
	
	private boolean championship;
	private String name;
	private Conference conference;
	
	public Bowl(Conference conference) {
		this.conference = conference;
		championship = true;
	}
	
	public Bowl(String name) {
		this.name = name;
		championship = false;
	}
	
	public Bowl(Conference conference, String name) {
		this.conference = conference;
		this.name = name;
		championship = true;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isChampionship() {
		return championship;
	}
	
	public String getName() {
		return name;
	}
	
	public Conference getConference() {
		return conference;
	}
	
	public String toString() {
		return name;
	}
}
