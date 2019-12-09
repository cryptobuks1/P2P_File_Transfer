package Cliente;

import java.io.*;
import java.net.*;

import Comuns.PeerCommand;
import Comuns.PeerCommandReply;

public class RequestHandler implements Runnable {

	private Socket socket;
	private Utilizador parentPeer = null;

	public RequestHandler(Socket socket, Utilizador parentPeer) {
		this.socket = socket;
		this.parentPeer = parentPeer;
	}

	@Override
	public void run() {

		System.out.println("***	Request listener: a começar...");

		try {
			// Obter pedido
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommand peerCommand = (PeerCommand) inStream.readObject();

			System.out.println("***	Handling: (utilizador request) <--- " + peerCommand.toString());

			// Processar pedido
			PeerCommandReply peerCommandReply = parentPeer.processPeerRequest(peerCommand);

			// Responder ao utilizador
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(peerCommandReply);
			outStream.flush();

		} catch (IOException ex) {
			ex.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

	}
}
