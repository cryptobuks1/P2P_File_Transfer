package Cliente;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Comuns.FileBlockRequestMessage;

public class DownloadProcessor implements Runnable {

	private final int N_WORKING_THREADS = 5;
	private Utilizador parentPeer = null;

	public DownloadProcessor(Utilizador parentPeer) {
		this.parentPeer = parentPeer;
	}

	@Override
	public void run() {
		ExecutorService executor = null;

		executor = Executors.newFixedThreadPool(N_WORKING_THREADS);

		while (true) {

			if (!parentPeer.getFileBlockRequestQueue().isEmpty()) {
				System.out.println("A obter block request da queue...");

				//remover FileBlock da queue
				FileBlockRequestMessage fileBlockRequest = parentPeer.getFileBlockRequestQueue().remove();

				System.out.println("Nome do Ficheiro " + fileBlockRequest.getFileName() + " Block Offset "
						+ fileBlockRequest.getFileBlockOffset() + " Block Size " + fileBlockRequest.getFileBlockSize());
				System.out.println("Do utilizador " + fileBlockRequest.getPeer().getPeerId() + " No endereço "
						+ fileBlockRequest.getPeer().getPeerAddress() + " Com porta "
						+ fileBlockRequest.getPeer().getPeerPort());

				//Criar um downloadWorker para o fileBlockRequest
				Runnable downloadWorker = new DownloadWorker(fileBlockRequest, parentPeer);
				executor.execute(downloadWorker);

				//Reportar o progresso à gui
				parentPeer.reportDownloadProgress();

				System.out.println("A fazer do bloco com offset: " + fileBlockRequest.getFileBlockOffset());

			} else {

				// Queue de file blocks request está vazia

			}

		}

	}

}
