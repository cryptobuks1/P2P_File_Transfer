package Comuns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileBlockReplyMessage implements Serializable {
	private String downloadId;
	private String fileName;
	private int numberOfBlocks;
	private int blockNumber;
	private int fileBlockOffset;
	private int fileBlockSize;
	private UtilizadorInfo requestingPeer;
	private UtilizadorInfo respondingPeer;
	private byte[] fileBlock;

	public FileBlockReplyMessage(String downloadId, String fileName, int numberOfBlocks, int blockNumber,
			int fileBlockOffset, int fileBlockSize, UtilizadorInfo requestingPeer, UtilizadorInfo respondingPeer,
			byte[] fileBlock) {
		this.downloadId = downloadId;
		this.fileName = fileName;
		this.numberOfBlocks = numberOfBlocks;
		this.blockNumber = blockNumber;
		this.fileBlockOffset = fileBlockOffset;
		this.fileBlockSize = fileBlockSize;
		this.requestingPeer = requestingPeer;
		this.respondingPeer = respondingPeer;
		this.fileBlock = fileBlock;
	}

	// Getters & Setters

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public void setNumberOfBlocks(int numberOfBlocks) {
		this.numberOfBlocks = numberOfBlocks;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
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

	public UtilizadorInfo getRequestingPeer() {
		return requestingPeer;
	}

	public void setRequestingPeer(UtilizadorInfo requestingPeer) {
		this.requestingPeer = requestingPeer;
	}

	public UtilizadorInfo getRespondingPeer() {
		return respondingPeer;
	}

	public void setRespondingPeer(UtilizadorInfo respondingPeer) {
		this.respondingPeer = respondingPeer;
	}

	public byte[] getFileBlock() {
		return fileBlock;
	}

	public void setFileBlock(byte[] fileBlock) {
		this.fileBlock = fileBlock;
	}

}
