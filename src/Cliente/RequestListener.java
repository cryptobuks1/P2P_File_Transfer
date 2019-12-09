package Cliente;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestListener implements Runnable {

	private final int N_SERVING_THREADS = 5;
	private Utilizador parentPeer = null;

	public RequestListener(Utilizador parentPeer) {
		this.parentPeer = parentPeer;
	}

	@Override
	public void run() {
		ExecutorService executor = null;
		int peerPort = parentPeer.get_peerInfo().getPeerPort();

		try (ServerSocket serverSocket = new ServerSocket(peerPort)) {

			System.out.println("REQUEST LISTENER NA PORTA: " + peerPort);
			executor = Executors.newFixedThreadPool(N_SERVING_THREADS);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("NOVO REQUEST - CRIAR REQUEST HANDLER");

				Runnable requestHandler = new RequestHandler(socket, parentPeer);
				executor.execute(requestHandler);
			}

		} catch (IOException ex) {
			System.out.println("Excepção no diretório: " + ex.getMessage());
			ex.printStackTrace();
			executor.shutdown();
		}

		executor.shutdown();
	}

}
