package Comuns;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class PeerCommand implements Serializable {
	public enum CMD {
		UNKOWN_CMD, REGISTER_PEER, GET_PEERS_LIST, UNREGISTER_PEER, SEARCH_FILE, FILE_BLOCK_REQUEST, FILE_BLOCK_REPLY
	}

	private CMD cmd = null;
	private String payload = null;;
	private UtilizadorInfo peerInfo = null;
	private ArrayList<UtilizadorInfo> listOfPeers = null;
	private ArrayList<FileDetails> listOfFileDetails = null;
	private FileBlockRequestMessage fileBlockRequestMessage = null;
	private FileBlockReplyMessage fileBlockReplyMessage = null;

	public PeerCommand(CMD cmd) {
		this.cmd = cmd;
	}

	public PeerCommand(CMD cmd, String payload) {
		this.cmd = cmd;
		this.payload = payload;
	}

	public PeerCommand(CMD cmd, UtilizadorInfo peerInfo) {
		this.cmd = cmd;
		this.peerInfo = peerInfo;
	}

	public PeerCommand(CMD cmd, ArrayList<UtilizadorInfo> listOfPeers) {
		this.cmd = cmd;
		this.listOfPeers = listOfPeers;
	}

	public PeerCommand(CMD cmd, FileBlockRequestMessage fileBlockRequestMessage) {
		this.cmd = cmd;
		this.fileBlockRequestMessage = fileBlockRequestMessage;
	}

	public PeerCommand(CMD cmd, FileBlockReplyMessage fileBlockReplyMessage) {
		this.cmd = cmd;
		this.fileBlockReplyMessage = fileBlockReplyMessage;
	}

	// Getters & Setters
	
	public CMD get_cmd() {
		return cmd;
	}

	public void set_cmd(CMD cmd) {
		this.cmd = cmd;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public ArrayList<UtilizadorInfo> get_listOfPeers() {
		return listOfPeers;
	}

	public void set_listOfPeers(ArrayList<UtilizadorInfo> listOfPeers) {
		this.listOfPeers = listOfPeers;
	}

	public UtilizadorInfo get_peerInfo() {
		return peerInfo;
	}

	public void set_peerInfo(UtilizadorInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public ArrayList<FileDetails> get_listOfFileDetails() {
		return listOfFileDetails;
	}

	public void set_listOfFileDetails(ArrayList<FileDetails> listOfFileDetails) {
		this.listOfFileDetails = listOfFileDetails;
	}

	public FileBlockRequestMessage get_fileBlockRequestMessage() {
		return fileBlockRequestMessage;
	}

	public void set_fileBlockRequestMessage(FileBlockRequestMessage fileBlockRequestMessage) {
		this.fileBlockRequestMessage = fileBlockRequestMessage;
	}

	public FileBlockReplyMessage get_fileBlockReplyMessage() {
		return fileBlockReplyMessage;
	}

	public void set_fileBlockReplyMessage(FileBlockReplyMessage fileBlockReplyMessage) {
		this.fileBlockReplyMessage = fileBlockReplyMessage;
	}

}
