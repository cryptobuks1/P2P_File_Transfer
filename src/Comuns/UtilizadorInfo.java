package Comuns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UtilizadorInfo implements Serializable {

	private int peerId;
	private String peerAddress;
	private int peerPort;

	public UtilizadorInfo(int peerId) {
		this.peerId = peerId;
	}

	public UtilizadorInfo(int peerId, String peerAddress, int peerPort) {
		this.peerId = peerId;
		this.peerAddress = peerAddress;
		this.peerPort = peerPort;
	}

	// Getters & Setters
	public int getPeerId() {
		return peerId;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public int getPeerPort() {
		return peerPort;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public void setPeerPort(int peerPort) {
		this.peerPort = peerPort;
	}

}
