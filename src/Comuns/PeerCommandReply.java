package Comuns;

import java.io.Serializable;
import java.util.ArrayList;

import Comuns.PeerCommand.CMD;

@SuppressWarnings("serial")
public class PeerCommandReply implements Serializable {

	private CMD cmd = null;
	private UtilizadorInfo peerInfo = null;
	private ArrayList<UtilizadorInfo> listOfPeers = null;
	private ArrayList<FileDetails> listOfFileDetails = null;
	private boolean success = false;

	public PeerCommandReply(CMD cmd) {
		this.cmd = cmd;
	}

	public PeerCommandReply(CMD cmd, boolean success) {
		this.cmd = cmd;
		this.success = success;
	}

	public PeerCommandReply(CMD cmd, UtilizadorInfo peerInfo) {
		this.cmd = cmd;
		this.peerInfo = peerInfo;
	}

	@SuppressWarnings("unchecked")
	public PeerCommandReply(CMD cmd, ArrayList<?> arrayList, boolean success) {
		if (cmd == CMD.GET_PEERS_LIST) {
			this.cmd = cmd;
			listOfPeers = (ArrayList<UtilizadorInfo>) arrayList;
			this.success = success;

		} else if (cmd == CMD.SEARCH_FILE) {
			this.cmd = cmd;
			set_listOfFileDetails((ArrayList<FileDetails>) arrayList);
			this.success = success;

		}

	}

	// Getters & Setters

	public CMD get_cmd() {
		return cmd;
	}

	public void set_cmd(CMD cmd) {
		this.cmd = cmd;
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

	public boolean is_success() {
		return success;
	}

	public void set_success(boolean success) {
		this.success = success;
	}

	public ArrayList<FileDetails> get_listOfFileDetails() {
		return listOfFileDetails;
	}

	public void set_listOfFileDetails(ArrayList<FileDetails> listOfFileDetails) {
		this.listOfFileDetails = listOfFileDetails;
	}

}
