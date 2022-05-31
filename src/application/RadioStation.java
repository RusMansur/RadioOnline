package application;

public class RadioStation {
	private String name;
	private String logoPath;
	private String url;

	public RadioStation(String name, String imagePath, String url) {
		this.name = name;
		this.logoPath = imagePath;
		this.url = url;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
