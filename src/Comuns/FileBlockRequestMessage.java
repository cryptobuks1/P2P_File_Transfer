package Comuns;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class FileBlockRequestMessage implements Serializable {
	private String downloadId;
	private String fileName;
	private int numberOfBlocks;
	private int blockNumber;
	private int fileBlockOffset;
	private int fileBlockSize;
	private UtilizadorInfo peer;
	private UtilizadorInfo requestingPeer;

	//Criar um identificador unico para o id do download
	public static String generateGUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public FileBlockRequestMessage(int fileBlockSize, UtilizadorInfo peer) {
		this.fileBlockSize = fileBlockSize;
		this.peer = peer;
	}
	
	public FileBlockRequestMessage(int fileBlockSize, UtilizadorInfo peer, int fileBlockOffset) {
		this.fileBlockSize = fileBlockSize;
		this.peer = peer;
		this.fileBlockOffset = fileBlockOffset;
	}

	public FileBlockRequestMessage(String fileName, int fileBlockOffset, int fileBlockSize, UtilizadorInfo peer) {
		this.fileName = fileName;
		this.fileBlockOffset = fileBlockOffset;
		this.fileBlockSize = fileBlockSize;
		this.peer = peer;
	}

	// Getters & Setters

	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public void setNumberOfBlocks(int numberOfBlocks) {
		this.numberOfBlocks = numberOfBlocks;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}

	public UtilizadorInfo getRequestingPeer() {
		return requestingPeer;
	}

	public void setRequestingPeer(UtilizadorInfo requestingPeer) {
		this.requestingPeer = requestingPeer;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileBlockOffset() {
		return fileBlockOffset;
	}

	public void setFileBlockOffset(int fileBlockOffset) {
		this.fileBlockOffset = fileBlockOffset;
	}

	public int getFileBlockSize() {
		return fileBlockSize;
	}

	public void setFileBlockSize(int fileBlockSize) {
		this.fileBlockSize = fileBlockSize;
	}

	public UtilizadorInfo getPeer() {
		return peer;
	}

	public void setPeer(UtilizadorInfo peer) {
		this.peer = peer;
	}

}
