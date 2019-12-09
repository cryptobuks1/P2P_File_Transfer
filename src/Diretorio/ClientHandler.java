package Diretorio;

import java.io.*;
import java.net.*;

import Comuns.PeerCommand;
import Comuns.PeerCommandReply;

public class ClientHandler implements Runnable {

	private Socket socket;
	private Diretorio diretorio = null;

	public ClientHandler(Socket socket, Diretorio diretorio) {
		this.socket = socket;
		this.diretorio = diretorio;
	}

	@Override
	public void run() {

		try {

			// Obter o request do utilizador
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommand peerCommand = (PeerCommand) inStream.readObject();

			// Processar
			PeerCommandReply peerCommandReply = diretorio.processarComando(peerCommand);

			// Só para prevenir erros de "lag"
			Thread.sleep(10);

			// Responder ao utilizador
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(peerCommandReply);
			outStream.flush();

		} catch (IOException ex) {
			ex.printStackTrace();

		} catch (InterruptedException ex) {
			ex.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

	}

}
