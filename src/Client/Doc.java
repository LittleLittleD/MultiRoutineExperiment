package Client;

// 实验三：实现此类
public class Doc implements MyInfo {
	private String ID;
	private String creator;
	private String timestamp;
	private String description;
	private String filename;

	public Doc(String iD, String creator, String timestamp, String description, String filename) {
		super();
		ID = iD;
		this.creator = creator;
		this.timestamp = timestamp;
		this.description = description;
		this.filename = filename;
	}

	public String getFileType() {
		String fileType = filename.substring(filename.lastIndexOf('.') + 1);
		return fileType;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String toString() {
		return ID + separator + creator + separator + timestamp + separator + description + separator + filename;
	}
}