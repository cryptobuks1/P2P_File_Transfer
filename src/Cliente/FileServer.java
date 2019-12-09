package Cliente;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Comuns.FileBlockRequestMessage;

public class FileServer implements Runnable {

	private final int N_WORKING_THREADS = 5;
	private Utilizador parentPeer = null;

	public FileServer(Utilizador parentPeer) {
		this.parentPeer = parentPeer;
	}

	@Override
	public void run() {
		ExecutorService executor = null;

		//Criar threadPool
		executor = Executors.newFixedThreadPool(N_WORKING_THREADS);

		while (true) {

			if (!parentPeer.getReceivedFileBlockRequestQueue().isEmpty()) {

				//Remover BlockRequest da queue
				FileBlockRequestMessage receivedFileBlockRequest = parentPeer.getReceivedFileBlockRequestQueue().remove();

				//Criar novo filBlockServer e adicionalo a threadpool
				Runnable filBlockServer = new FileBlockServer(receivedFileBlockRequest, parentPeer);
				executor.execute(filBlockServer);

				System.out.println("A expedir bloco " + receivedFileBlockRequest.getBlockNumber() + "/"
						+ receivedFileBlockRequest.getNumberOfBlocks());

			} else {

				//Queue de file block requests está vazia

			}

		}

	}

}
