package idv.ron.imagetojson_login_android;

public class User {
	private String name;
	private String password;
	private byte[] image;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String name, String password, byte[] image) {
		super();
		this.name = name;
		this.password = password;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
