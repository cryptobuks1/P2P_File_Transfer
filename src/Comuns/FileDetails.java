package Comuns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileDetails implements Serializable {
	private String fileName;
	private int fileSize;

	
	public FileDetails(String fileName, int fileSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	// Getters & Setters
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

}
