package Cliente;

import Comuns.FileBlockReplyMessage;

public class FileBlockGatherer implements Runnable {

	private Utilizador parentPeer = null;
	private FileBuilder fileBuilder = null;

	public FileBlockGatherer(Utilizador parentPeer, FileBuilder fileBuilder) {
		this.parentPeer = parentPeer;
		this.fileBuilder = fileBuilder;

	}

	@Override
	public void run() {

		// Monitoriza a queue e escreve em filesToBuild
		while (true) {

			// Processa a queue de file blocks recebidos
			if (!parentPeer.getReceivedFileBlockReplyQueue().isEmpty()) {

				System.out.println("A obter File Block");

				// Remover da queue
				FileBlockReplyMessage fileBlockReplyMessage = parentPeer.getReceivedFileBlockReplyQueue().remove();

				String downloadId = fileBlockReplyMessage.getDownloadId();

				// Verifica se o ficheiro para construir já está registado
				if (fileBuilder.getFilesToBuild().existsFileToBuild(downloadId)) {
					// Se já estiver registado -> addBlock
					fileBuilder.getFilesToBuild().addBlockToFileToBuild(downloadId, fileBlockReplyMessage);

				} else {
					// Ficheiro para construir ainda não existe -> regista o ficheiro
					fileBuilder.getFilesToBuild().registerFileToBuild(downloadId, fileBlockReplyMessage);

				}

			} else {

			}

		}

	}

}