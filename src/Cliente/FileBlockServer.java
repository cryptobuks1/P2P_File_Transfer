package Cliente;

import java.io.*;
import java.util.Arrays;

import Comuns.FileBlockReplyMessage;
import Comuns.FileBlockRequestMessage;
import Comuns.PeerCommand;
import Comuns.PeerCommandReply;
import Comuns.UtilizadorInfo;
import Comuns.PeerCommand.CMD;

import java.net.Socket;
import java.nio.file.Files;

public class FileBlockServer implements Runnable {

	private FileBlockRequestMessage receivedFileBlockRequestMessage;
	private Utilizador parentPeer = null;
	private Socket socket = null;

	public FileBlockServer(FileBlockRequestMessage receivedFileBlockRequestMessage, Utilizador parentPeer) {
		this.receivedFileBlockRequestMessage = receivedFileBlockRequestMessage;
		this.parentPeer = parentPeer;
	}

	// Getters & Setters
	public Utilizador get_parentPeer() {
		return parentPeer;
	}

	public void set_parentPeer(Utilizador _parentPeer) {
		this.parentPeer = _parentPeer;
	}

	public FileBlockRequestMessage get_receivedFileBlockRequestMessage() {
		return receivedFileBlockRequestMessage;
	}

	public void set_receivedFileBlockRequestMessage(FileBlockRequestMessage _receivedFileBlockRequestMessage) {
		this.receivedFileBlockRequestMessage = _receivedFileBlockRequestMessage;
	}

	@Override
	public void run() {

		System.out.println("File Block Server foi iniciado!");

		String downloadId = this.get_receivedFileBlockRequestMessage().getDownloadId();
		String requestingPeerAddress = get_receivedFileBlockRequestMessage().getRequestingPeer().getPeerAddress();
		int requestingPeerPort = this.get_receivedFileBlockRequestMessage().getRequestingPeer().getPeerPort();
		String fileName = this.get_receivedFileBlockRequestMessage().getFileName();
		int fileBlockOffset = this.get_receivedFileBlockRequestMessage().getFileBlockOffset();
		int fileBlockSize = this.get_receivedFileBlockRequestMessage().getFileBlockSize();
		int numberOfBlocks = this.get_receivedFileBlockRequestMessage().getNumberOfBlocks();
		int blockNumber = this.get_receivedFileBlockRequestMessage().getBlockNumber();

		// Para quem estamos a servir o bloco
		UtilizadorInfo requestingPeer = this.get_receivedFileBlockRequestMessage().getRequestingPeer();

		// Informação deste utilizador
		UtilizadorInfo respondingPeer = this.get_parentPeer().get_peerInfo();

		File imagem = new File(this.get_parentPeer().get_fileDirectory() + "/" + fileName);

		// Calcular file block
		byte[] fileBlock = null;

		try {
			fileBlock = getFileBlock(imagem, this.get_receivedFileBlockRequestMessage());

		} catch (IOException e1) {
			e1.printStackTrace();

		}

		//Criar uma reply message
		FileBlockReplyMessage fileBlockReplyMessage = new FileBlockReplyMessage(downloadId, fileName, numberOfBlocks,
				blockNumber, fileBlockOffset, fileBlockSize, requestingPeer, respondingPeer, fileBlock);

		try {
			socket = new Socket(requestingPeerAddress, requestingPeerPort);

			PeerCommand fileBlockReply = new PeerCommand(CMD.FILE_BLOCK_REPLY, fileBlockReplyMessage);

			// Fazer pedido
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(fileBlockReply);
			outStream.flush();

			// Processar resposta
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

			if (!peerCommandReply.is_success()) {
				// Tem de substituir na queue...
				parentPeer.getReceivedFileBlockRequestQueue().add(this.get_receivedFileBlockRequestMessage());
			}

			System.out.println("*** Utilizador handling: (utilizador request) <--- " + peerCommandReply.toString());
			socket.close();

		} catch (IOException ex) {
			ex.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

	}

	//Obter um bloco de bytes de um ficheiro
	public byte[] getFileBlock(File imagem, FileBlockRequestMessage bloco) throws IOException {
		int offset = bloco.getFileBlockOffset();
		int tamanhoBloco = bloco.getFileBlockSize();
		byte[] fileContents = Files.readAllBytes(imagem.toPath());
		byte[] partePedida = Arrays.copyOfRange(fileContents, offset, tamanhoBloco + offset);

		return partePedida;
	}
}
