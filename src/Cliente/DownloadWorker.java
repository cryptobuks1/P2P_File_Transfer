package Cliente;

import java.io.*;
import java.net.Socket;

import Comuns.FileBlockRequestMessage;
import Comuns.PeerCommand;
import Comuns.PeerCommandReply;
import Comuns.PeerCommand.CMD;

public class DownloadWorker implements Runnable {

	private FileBlockRequestMessage fileBlockRequest;
	private Utilizador parentPeer = null;
	private Socket socket = null;

	public DownloadWorker(FileBlockRequestMessage fileBlockRequest, Utilizador parentPeer) {
		this.fileBlockRequest = fileBlockRequest;
		this.parentPeer = parentPeer;
	}

	// Getters & Setters
	public FileBlockRequestMessage get_fileBlockRequest() {
		return fileBlockRequest;
	}

	public void set_fileBlockRequest(FileBlockRequestMessage _fileBlockRequest) {
		this.fileBlockRequest = _fileBlockRequest;
	}

	@Override
	public void run() {

		//Definir variaveis
		int peerId = get_fileBlockRequest().getPeer().getPeerId();
		String peerAddress = get_fileBlockRequest().getPeer().getPeerAddress();
		int peerPort = get_fileBlockRequest().getPeer().getPeerPort();

		String fileName = get_fileBlockRequest().getFileName();
		int fileBlockNumber = get_fileBlockRequest().getBlockNumber();
		int fileNumberOfBlocks = get_fileBlockRequest().getNumberOfBlocks();

		System.out.println("A pedir o bloco: " + fileName + " Bloco: " + fileBlockNumber + "/"
				+ fileNumberOfBlocks + " Do utilizador: " + peerId + " em: " + peerAddress + ":" + peerPort);

		try {
			socket = new Socket(peerAddress, peerPort);

			PeerCommand requestFileBlockCommand = new PeerCommand(CMD.FILE_BLOCK_REQUEST, get_fileBlockRequest());

			//Fazer o request
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(requestFileBlockCommand);
			outStream.flush();

			//Processar resposta
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

			if (!peerCommandReply.is_success()) {
				parentPeer.getReceivedFileBlockRequestQueue().add(get_fileBlockRequest());
			}

			socket.close();

		} catch (IOException ex) {
			System.out.println(": " + ex.getMessage());
			ex.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

	}
}
