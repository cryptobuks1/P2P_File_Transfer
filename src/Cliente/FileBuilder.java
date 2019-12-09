package Cliente;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Comuns.FileBlockReplyMessage;
import Comuns.FileBlockRequestMessage;
import Comuns.UtilizadorInfo;

public class FileBuilder implements Runnable {

	private Utilizador parentPeer = null;
	private FilesToBuild filesToBuild = null;
	private final int N_WORKING_THREADS = 5;


	public FileBuilder(Utilizador parentPeer) {
		this.parentPeer = parentPeer;
		filesToBuild = new FilesToBuild();
	}

	//Getters & Setters
	public Utilizador get_parentPeer() {
		return parentPeer;
	}

	public void set_parentPeer(Utilizador parentPeer) {
		this.parentPeer = parentPeer;
	}

	public FilesToBuild getFilesToBuild() {
		return filesToBuild;
	}

	public void setFilesToBuild(FilesToBuild filesToBuild) {
		this.filesToBuild = filesToBuild;
	}

	// Classe interna
	class FileBlockProvider {
		private UtilizadorInfo peerInfoProvider;
		private int nrOfBlocksProvided;

		public FileBlockProvider(UtilizadorInfo peerInfoProvider) {
			this.peerInfoProvider = peerInfoProvider;
		}

		//Getters & Setters
		public UtilizadorInfo getPeerInfoProvider() {
			return peerInfoProvider;
		}

		public void setPeerInfoProvider(UtilizadorInfo peerInfoProvider) {
			this.peerInfoProvider = peerInfoProvider;
		}

		public int getNrOfBlocksProvided() {
			return nrOfBlocksProvided;
		}

		public void setNrOfBlocksProvided(int nrOfBlocksProvided) {
			this.nrOfBlocksProvided = nrOfBlocksProvided;
		}

	}

	// Classe interna
	class FilesToBuild {
		private ArrayList<FileToBuild> filesToBuild = new ArrayList<FileToBuild>();

		//Getters & Setters
		public synchronized ArrayList<FileToBuild> getFilesToBuild() {
			return filesToBuild;
		}

		public synchronized void setFilesToBuild(ArrayList<FileToBuild> filesToBuild) {
			this.filesToBuild = filesToBuild;
		}

		//Verificar se existe ficheiro para construir
		public synchronized boolean existsFileToBuild(String downloadId) {

			for (FileToBuild fileToBuild : this.getFilesToBuild()) {

				if (fileToBuild.getDownloadId().equals(downloadId)) {
					return true;
				}
			}

			return false;
		}

		//Registar ficheiro para construir
		public synchronized void registerFileToBuild(String downloadId, FileBlockReplyMessage fileBlockReplyMessage) {

			//Definir variaveis
			String fileName = fileBlockReplyMessage.getFileName();
			int nrOfBlocks = fileBlockReplyMessage.getNumberOfBlocks();
			int blockNumber = fileBlockReplyMessage.getBlockNumber();
			byte[] fileBlockContents = fileBlockReplyMessage.getFileBlock();
			UtilizadorInfo respondingPeer = fileBlockReplyMessage.getRespondingPeer();

			//Criar um novo file block
			FileBlock fileBlock = new FileBlock(blockNumber, fileBlockContents, respondingPeer);

			//Criar file block
			FileToBuild newFileToBuild = new FileToBuild(downloadId, fileName, nrOfBlocks);
			newFileToBuild.getFileBlocks().add(fileBlock);

			//Definir tamanho
			int fileSize = fileBlockContents.length;
			newFileToBuild.setFileSize(fileSize);

			//Adicionar à lista
			this.getFilesToBuild().add(newFileToBuild);

		}

		//Adicionar bloco a FileToBuild
		public synchronized void addBlockToFileToBuild(String downloadId, FileBlockReplyMessage fileBlockReplyMessage) {

			for (FileToBuild fileToBuild : this.getFilesToBuild()) {

				if (fileToBuild.getDownloadId().equals(downloadId)) {

					int blockNumber = fileBlockReplyMessage.getBlockNumber();
					byte[] fileBlockContents = fileBlockReplyMessage.getFileBlock();
					UtilizadorInfo respondingPeer = fileBlockReplyMessage.getRespondingPeer();

					FileBlock fileBlock = new FileBlock(blockNumber, fileBlockContents, respondingPeer);

					fileToBuild.getFileBlocks().add(fileBlock);

					//Atualizar tamanho
					int blockSize = fileBlockContents.length;
					fileToBuild.setFileSize(fileToBuild.getFileSize() + blockSize);

				}
			}
		}

		//Preparar o ficheiro para ser construido
		public synchronized FileToBuild getFileReadyToBeBuilt() {

			FileToBuild fileReadyToBeBuilt = null;

			for (FileToBuild fileToBuild : this.getFilesToBuild()) {

				if (fileToBuild.getNrOfBlocks() == fileToBuild.getFileBlocks().size()) {
					System.out.println("O ficheiro " + fileToBuild.getDownloadId() + " Com tamanho: "
							+ fileToBuild.getFileSize() + " Está pronto para ser construido!!");

					fileReadyToBeBuilt = fileToBuild;
					break;

				}
			}

			return fileReadyToBeBuilt;

		}

		//Remover FileToBuild
		public synchronized void removeFileToBuild(String downloadId) {

			FileToBuild fileToRemove = null;

			for (FileToBuild fileToBuild : this.getFilesToBuild()) {

				if (fileToBuild.getDownloadId().equals(downloadId)) {

					System.out.println("ATENÇÃO: O ficheiro " + fileToBuild.getDownloadId() + " vai ser removido!");
					fileToRemove = fileToBuild;

				}
			}

			this.getFilesToBuild().remove(fileToRemove);
		}

	}

	//Classe interna
	class FileToBuild {
		String downloadId;
		String fileName;
		int fileSize;
		int nrOfBlocks;
		ArrayList<FileBlock> fileBlocks = new ArrayList<FileBlock>();

		public FileToBuild() {

		}

		public FileToBuild(String downloadId, String fileName, int nrOfBlocks) {
			this.downloadId = downloadId;
			this.fileName = fileName;
			this.nrOfBlocks = nrOfBlocks;
		}

		//Getters & Setters
		public String getDownloadId() {
			return downloadId;
		}

		public void setDownloadId(String downloadId) {
			this.downloadId = downloadId;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public int getFileSize() {
			return fileSize;
		}

		public void setFileSize(int fileSize) {
			this.fileSize = fileSize;
		}

		public int getNrOfBlocks() {
			return nrOfBlocks;
		}

		public void setNrOfBlocks(int nrOfBlocks) {
			this.nrOfBlocks = nrOfBlocks;
		}

		public ArrayList<FileBlock> getFileBlocks() {
			return fileBlocks;
		}

		public void setFileBlocks(ArrayList<FileBlock> fileBlocks) {
			this.fileBlocks = fileBlocks;
		}

	}

	//Classe Interna
	class FileBlock implements Comparator<FileBuilder.FileBlock> {
		int blockNumber;
		UtilizadorInfo respondingPeer;
		byte[] fileBlockContents;

		public FileBlock(int blockNumber, byte[] fileBlockContents, UtilizadorInfo respondingPeer) {
			this.blockNumber = blockNumber;
			this.respondingPeer = respondingPeer;
			this.fileBlockContents = fileBlockContents;

		}

		//Getters & Setters
		public int getBlockNumber() {
			return blockNumber;
		}

		public void setBlockNumber(int blockNumber) {
			this.blockNumber = blockNumber;
		}

		public UtilizadorInfo getRespondingPeer() {
			return respondingPeer;
		}

		public void setRespondingPeer(UtilizadorInfo respondingPeer) {
			this.respondingPeer = respondingPeer;
		}

		public byte[] getFileBlockContents() {
			return fileBlockContents;
		}

		public void setFileBlockContents(byte[] fileBlockContents) {
			this.fileBlockContents = fileBlockContents;
		}

		@Override
		public int compare(FileBlock o1, FileBlock o2) {
			return 0;
		}

	}
	
	@Override
	public void run() {

		//Criar uma thread pool de threads que vao buscar os blocos
		ExecutorService executor = null;
		executor = Executors.newFixedThreadPool(N_WORKING_THREADS);

		for (int i = 0; i < N_WORKING_THREADS; i++) {
			Runnable fileBlockGatherer = new FileBlockGatherer(get_parentPeer(), this);
			executor.execute(fileBlockGatherer);
		}

		while (true) {


			//Verifica se existe algum ficheiro pronto para ser processado
			FileToBuild fileReadyToBeBuilt = filesToBuild.getFileReadyToBeBuilt();

			if (fileReadyToBeBuilt != null) {

				String downloadId = fileReadyToBeBuilt.getDownloadId();

				System.out.println("******************************************************************");
				System.out.println("CONSTRUIR O FICHEIRO: " + downloadId);
				System.out.println("******************************************************************");

				//Escrever o ficheiro para o disco
				try {
					buildFile(fileReadyToBeBuilt);
					
					//Criar lista de utilizadores envolvidos no download
					ArrayList<UtilizadorInfo> listaUsersDownload = new ArrayList<UtilizadorInfo>();
			
					String strProviders = "";
		
					//Adicionar à lista os users envolvidos
					for (FileBlockRequestMessage fileBlockRequest : parentPeer.fileBlocksRequestListGlobal) {

						UtilizadorInfo peerInfo = parentPeer.getPeerInfoByPeerId(fileBlockRequest.getPeer().getPeerId());
						
						if(!listaUsersDownload.contains(peerInfo)) {
							listaUsersDownload.add(peerInfo);
						}

					}
					
					//Popular a string com informacao de download dos users
					for(UtilizadorInfo user : listaUsersDownload) {
						strProviders += "Fornecedor: ID(" + user.getPeerId() + ") - [Endere�o=/" + user.getPeerAddress() + ", porto="
						+ user.getPeerPort() + "]:" + parentPeer.getDownloadedPartsPerUser(user.getPeerPort()) + "\n";
					}
					
					//Calcular o tempo decorrido
					String tempoDecorrido = "Tempo decorrido: " + (System.currentTimeMillis() - parentPeer.getStart()) + " ms";
					
					//Criar Popup
					parentPeer.gui.showMessageDialog("Descarga completa.\n" + strProviders + tempoDecorrido);

				} catch (IOException e) {
					e.printStackTrace();

				}

				//Remover ficheiro para construir
				filesToBuild.removeFileToBuild(downloadId);

				System.out.println("O ficheiro " + downloadId + " foi removido");

				try {
					Thread.sleep(500);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

			}
		}

	}

	//Obter os fornecedores dos File blocks
	//Esta funcao foi substituida por outra muito mais bonita//
	@SuppressWarnings("unused")
	private ArrayList<FileBlockProvider> getFileBlockProviders(FileToBuild fileToBuild) {

		ArrayList<FileBlockProvider> fileBlockProviders = new ArrayList<FileBlockProvider>();

		//Organizar o array por ID de utilizador
		Collections.sort(fileToBuild.getFileBlocks(), new Comparator<FileBuilder.FileBlock>() {
			@Override
			public int compare(FileBlock filBlock1, FileBlock filBlock2) {

				Integer respondingPeerId = filBlock1.getRespondingPeer().getPeerId();

				return respondingPeerId.compareTo(filBlock2.getRespondingPeer().getPeerId());
			}

		});

		for (FileBlock fileBlock : fileToBuild.getFileBlocks()) {
			System.out.println("*********** -> " + fileBlock.getRespondingPeer().getPeerId());
		}

		UtilizadorInfo currentPeerInfo = fileToBuild.getFileBlocks().get(0).getRespondingPeer();
		FileBlockProvider fileBlockProvider = new FileBlockProvider(currentPeerInfo);

		for (FileBlock fileBlock : fileToBuild.getFileBlocks()) {

			if (fileBlock.getRespondingPeer().getPeerId() == currentPeerInfo.getPeerId()) {
				fileBlockProvider.setNrOfBlocksProvided(fileBlockProvider.getNrOfBlocksProvided() + 1);

			} else {
				fileBlockProviders.add(fileBlockProvider);
				currentPeerInfo = fileBlock.getRespondingPeer();

			}

		}

		return fileBlockProviders;
	}

	//Construir o ficheiro
	private void buildFile(FileToBuild fileToBuild) throws IOException {
		String fileDirectory = parentPeer.get_fileDirectory();

		byte[] fileContents = new byte[fileToBuild.getFileSize()];

		System.out.println(
				"A escrever o ficheiro " + fileToBuild.getFileName() + " para o disco! - Tamanho: " + fileContents.length);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		//Organizar o array antes de escrever em disco
		Collections.sort(fileToBuild.getFileBlocks(), new Comparator<FileBuilder.FileBlock>() {
			@Override
			public int compare(FileBlock filBlock1, FileBlock filBlock2) {

				Integer fBlock1 = filBlock1.getBlockNumber();

				return fBlock1.compareTo(filBlock2.getBlockNumber());
			}

		});

		for (FileBlock fileBlock : fileToBuild.getFileBlocks()) {
			byte[] fileBlockBytes = fileBlock.getFileBlockContents();

			System.out.println("**** A ESCREVER OS BYTES DO BLOCO: " + fileBlock.getBlockNumber());

			outputStream.write(fileBlockBytes);
		}

		fileContents = outputStream.toByteArray();

		System.out.println("BEFORE - FILE CONTENTS SIZE: " + fileContents.length);

		// Transformar de byte[] para ficheiro
		try (FileOutputStream fos = new FileOutputStream(fileDirectory + "/" + fileToBuild.getFileName())) {
			fos.write(fileContents);
		}

	}

}