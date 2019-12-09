package Cliente;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Comuns.BlockingQueue;
import Comuns.InfoDiretorio;
import Comuns.FileBlockReplyMessage;
import Comuns.FileBlockRequestMessage;
import Comuns.FileDetails;
import Comuns.PeerCommand;
import Comuns.PeerCommandReply;
import Comuns.UtilizadorInfo;
import Comuns.PeerCommand.CMD;

public class Utilizador {

	// Definir um timeout para a socket
	private final int SOCKET_TIMEOUT = 20000;
	//

	private final int FILE_BLOCK_SIZE = 1024;

	Utilizador thisPeer = null;
	public UtilizadorInfo peerInfo = null;
	InfoDiretorio directoryInfo = null;
	private boolean registeredAtDirectory = false;

	GUI gui = null;
	String fileDirectory = "";
	ArrayList<FileDetails> filesInDirectory = new ArrayList<FileDetails>();

	private long start;

	private Socket socket = null;
	private ArrayList<UtilizadorInfo> activePeerList = new ArrayList<UtilizadorInfo>();
	ArrayList<FileBlockRequestMessage> fileBlocksRequestListGlobal = new ArrayList<FileBlockRequestMessage>();

	// Este user a pedir blocos a outros utilizadores
	private BlockingQueue<FileBlockRequestMessage> fileBlockRequestQueue = new BlockingQueue<FileBlockRequestMessage>(
			500);

	// Blocos pedidos a este utilizador
	private BlockingQueue<FileBlockRequestMessage> receivedFileBlockRequestQueue = new BlockingQueue<FileBlockRequestMessage>(
			500);

	// Blocos recebidos de outros utilizadores
	private BlockingQueue<FileBlockReplyMessage> receivedFileBlockReplyQueue = new BlockingQueue<FileBlockReplyMessage>(
			500);

	public Utilizador(int peerId, String peerAddress, int peerPort, String directoryAddress, int directoryPort) {
		// Criar info do Utilizador
		this.peerInfo = new UtilizadorInfo(peerId, peerAddress, peerPort);
		this.directoryInfo = new InfoDiretorio(directoryAddress, directoryPort);
		this.fileDirectory = "FileDirectory" + peerId;

		// Criar Gui
		this.gui = new GUI(this);

		this.thisPeer = this;

	}

	// Getters & Setters
	public UtilizadorInfo get_peerInfo() {
		return peerInfo;
	}

	public void set_peerInfo(UtilizadorInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public String get_fileDirectory() {
		return fileDirectory;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void set_fileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public ArrayList<UtilizadorInfo> get_activePeerList() {
		return activePeerList;
	}

	public void set_activePeerList(ArrayList<UtilizadorInfo> activePeerList) {
		this.activePeerList = activePeerList;
	}

	public synchronized BlockingQueue<FileBlockRequestMessage> getFileBlockRequestQueue() {
		return fileBlockRequestQueue;
	}

	public synchronized void setFileBlockRequestQueue(BlockingQueue<FileBlockRequestMessage> fileBlockRequestQueue) {
		this.fileBlockRequestQueue = fileBlockRequestQueue;
	}

	public synchronized BlockingQueue<FileBlockRequestMessage> getReceivedFileBlockRequestQueue() {
		return receivedFileBlockRequestQueue;
	}

	public synchronized void setReceivedFileBlockRequestQueue(
			BlockingQueue<FileBlockRequestMessage> receivedFileBlockRequestQueue) {
		this.receivedFileBlockRequestQueue = receivedFileBlockRequestQueue;
	}

	public synchronized BlockingQueue<FileBlockReplyMessage> getReceivedFileBlockReplyQueue() {
		return receivedFileBlockReplyQueue;
	}

	public synchronized void setReceivedFileBlockReplyQueue(
			BlockingQueue<FileBlockReplyMessage> receivedFileBlockReplyQueue) {
		this.receivedFileBlockReplyQueue = receivedFileBlockReplyQueue;
	}

	public boolean isRegisteredAtDirectory() {
		return registeredAtDirectory;
	}

	public void setRegisteredAtDirectory(boolean registeredAtDirectory) {
		this.registeredAtDirectory = registeredAtDirectory;
	}

	// Argumentos: idUser, enderecoUser, portaUser, enderecoDiretorio,
	// portaDiretorio
	public static void main(String[] args) {

		//Processar argumentos em variaveis
		int peerId = Integer.parseInt(args[0]);
		String peerAddress = args[1];
		int peerPort = Integer.parseInt(args[2]);

		String directoryAddress = args[3];
		int directoryPort = Integer.parseInt(args[4]);

		//Criar um novo utilizador
		Utilizador thisPeer = new Utilizador(peerId, peerAddress, peerPort, directoryAddress, directoryPort);

		// Registar no diretorio
		if (thisPeer.registerAtDirectory()) {
			System.out.println("* Registado no diretorio com sucesso! *");

			//Lancar threads necessárias
			
			thisPeer.launchRequestListener();

			thisPeer.launchDownloadProcessor();

			thisPeer.launchFileServer();

			thisPeer.launchFileBuilder();

		} else {
			System.out.println("* Não conseguiu registar no diretório!!! *");

		}

	}

	// Registar no diretorio
	private synchronized boolean registerAtDirectory() {

		try {
			socket = new Socket(directoryInfo.getEnderecoDiretorio(), directoryInfo.getPortaDiretorio());
			socket.setSoTimeout(SOCKET_TIMEOUT);

			PeerCommand registerCommand = new PeerCommand(CMD.REGISTER_PEER, peerInfo);

			// Fazer o pedido
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(registerCommand);
			outStream.flush();

			// Processar resposta
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

			if (peerCommandReply.is_success()) {
				this.setRegisteredAtDirectory(true);

				return true;

			} else {
				return false;

			}

		} catch (UnknownHostException ex) {
			System.out.println("Utilizador - Servidor não encontrado: " + ex.getMessage());

			return false;

		} catch (IOException ex) {
			System.out.println("Utilizador - Erro I/O: " + ex.getMessage());

			try {
				if (socket != null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();

			}

			return false;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

		return false;

	}

	// Remover utilizador do Diretorio
	public synchronized boolean unregisterFromDirectory() {

		try {
			socket = new Socket(directoryInfo.getEnderecoDiretorio(), directoryInfo.getPortaDiretorio());
			socket.setSoTimeout(SOCKET_TIMEOUT);

			PeerCommand registerCommand = new PeerCommand(CMD.UNREGISTER_PEER, peerInfo);

			// Fazer pedido
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(registerCommand);
			outStream.flush();

			// Processar resposta
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

			if (peerCommandReply.is_success()) {
				return true;

			} else {
				return false;

			}

		} catch (UnknownHostException ex) {
			System.out.println("Utilizador - Servidor não encontrado: " + ex.getMessage());

		} catch (IOException ex) {
			System.out.println("Utilizador - Erro I/O: " + ex.getMessage());

			try {
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();

			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	// Iniciar requestListener
	private void launchRequestListener() {

		Thread requestListenerThread = new Thread(new RequestListener(this));
		requestListenerThread.start();

	}

	// Iniciar processador de Downloads
	private void launchDownloadProcessor() {

		Thread downloadProcessorThread = new Thread(new DownloadProcessor(this));
		downloadProcessorThread.start();

	}

	// Iniciar servidor de FileBlocks
	private void launchFileServer() {

		Thread fileServerThread = new Thread(new FileServer(this));
		fileServerThread.start();

	}

	// Iniciar fileBuilder
	private void launchFileBuilder() {

		Thread fileBuilderThread = new Thread(new FileBuilder(this));
		fileBuilderThread.start();

	}

	// Obter lista de utilizadores online
	public synchronized void requestPeerList() {

		try {
			socket = new Socket(directoryInfo.getEnderecoDiretorio(), directoryInfo.getPortaDiretorio());
			socket.setSoTimeout(SOCKET_TIMEOUT);

			PeerCommand registerCommand = new PeerCommand(CMD.GET_PEERS_LIST);

			// Fazer pedido
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			outStream.writeObject(registerCommand);
			outStream.flush();

			// Processar resposta
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

			if (peerCommandReply.is_success()) {

				set_activePeerList(peerCommandReply.get_listOfPeers());

				gui.populateListModel(peerCommandReply.get_listOfPeers());

			}

		} catch (UnknownHostException ex) {
			System.out.println("Utilizador - Srervidor não encontrado: " + ex.getMessage());

		} catch (IOException ex) {
			System.out.println("Utilizador - Erro I/O: " + ex.getMessage());

			try {
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();

			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		}

	}

	// Pesquisar ficheiro
	public synchronized boolean searchFile(String keyword) {

		// Limpar lista
		gui.clearListModel();

		ArrayList<UtilizadorInfo> currentActivePeersList = this.get_activePeerList();

		// Enviar pedido a cada um dos utilizadores online
		for (UtilizadorInfo activePeer : currentActivePeersList) {

			if (activePeer.getPeerId() != this.get_peerInfo().getPeerId()) {

				try {
					socket = new Socket(activePeer.getPeerAddress(), activePeer.getPeerPort());
					socket.setSoTimeout(SOCKET_TIMEOUT);

					PeerCommand searchFileCommand = new PeerCommand(CMD.SEARCH_FILE, keyword);

					// Faazer pedido
					ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
					outStream.writeObject(searchFileCommand);
					outStream.flush();

					// Processar resposta
					ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
					PeerCommandReply peerCommandReply = (PeerCommandReply) inStream.readObject();

					if (peerCommandReply.is_success()) {
						System.out.println(activePeer.getPeerId() + " TEM O FICHEIRO");

						for (FileDetails fileDetails : peerCommandReply.get_listOfFileDetails()) {
							String listEntry = "UTILIZADOR: " + activePeer.getPeerId() + " NOME: "
									+ fileDetails.getFileName() + " TAMANHO: " + fileDetails.getFileSize();

							gui.addEntryToListModel(listEntry);
						}

					} else {

					}

				} catch (UnknownHostException ex) {
					System.out.println("Utilizador - Servidor não encontrado: " + ex.getMessage());

					try {
						socket.close();

					} catch (IOException e) {
						e.printStackTrace();

					}

					return false;

				} catch (IOException ex) {
					System.out.println("Utilizador - Erro I/O: " + ex.getMessage());

					try {
						socket.close();

					} catch (IOException e) {
						e.printStackTrace();

					}

					return false;

				} catch (ClassNotFoundException e) {
					e.printStackTrace();

				}

			}

		}

		return true;

	}

	// Realizar o download de um ficheiro
	public synchronized void downloadFile(String fileToDownload, int fileSize,
			ArrayList<UtilizadorInfo> peersToAskFile) {

		setStart(System.currentTimeMillis());

		System.out.println("Vou fazer o download de: " + fileToDownload + " Com tamanho: " + fileSize
				+ " Dos seguintes utilizadores...");

		ArrayList<FileBlockRequestMessage> fileBlocksRequestList = createFileBlocksRequestList(fileSize,
				peersToAskFile);

		fileBlocksRequestListGlobal = fileBlocksRequestList;

		// Não está a funcionar corretamente
		int progressIncrement = (1000 / fileBlocksRequestList.size());

		int blockNumber = 1;
		String downloadId = FileBlockRequestMessage.generateGUID();
		int nrOfBlocks = fileBlocksRequestList.size();

		for (FileBlockRequestMessage fileBlockRequest : fileBlocksRequestList) {

			// Mesmo id para cada bloco...
			fileBlockRequest.setDownloadId(downloadId);
			fileBlockRequest.setFileName(fileToDownload);
			fileBlockRequest.setNumberOfBlocks(nrOfBlocks);
			fileBlockRequest.setBlockNumber(blockNumber);

			UtilizadorInfo peerInfo = getPeerInfoByPeerId(fileBlockRequest.getPeer().getPeerId());
			fileBlockRequest.setPeer(peerInfo);

			// A informação deste user para os outros users saberem para quem responder
			fileBlockRequest.setRequestingPeer(this.get_peerInfo());

			System.out.println("***** DOWNLOAD: " + downloadId);
			System.out.println("NOME " + fileBlockRequest.getFileName() + " BLOCO " + fileBlockRequest.getBlockNumber()
					+ "/" + fileBlockRequest.getNumberOfBlocks());
			System.out.println("PEDIDO DO USER: " + fileBlockRequest.getRequestingPeer().getPeerId() + " EM: "
					+ fileBlockRequest.getRequestingPeer().getPeerAddress() + ":"
					+ fileBlockRequest.getRequestingPeer().getPeerPort() + "   ----------->   AO UTILIZADOR: "
					+ fileBlockRequest.getPeer().getPeerId() + " EM: " + fileBlockRequest.getPeer().getPeerAddress()
					+ ":" + fileBlockRequest.getPeer().getPeerPort());

			// Colocar o pedido do bloco na queue
			getFileBlockRequestQueue().add(fileBlockRequest);

			blockNumber++;

		}

		setDownloadProgressIncrement(progressIncrement);

	}

	// Incrementar a barra de progresso
	public synchronized void setDownloadProgressIncrement(int increment) {

		gui.setProgressBarIncrement(increment);

	}

	// Incrementar a barra de progresso 2
	public synchronized void reportDownloadProgress() {

		gui.incrementProgressBar();

	}

	// Criar lista de File Blocks a pedir
	// Devolve lista fileBlockRequests ais utilizadores que teem com blockSize e
	// offset
	private synchronized ArrayList<FileBlockRequestMessage> createFileBlocksRequestList(int fileSize,
			ArrayList<UtilizadorInfo> peersToAskFile) {

		int numberOfBlocks = fileSize / FILE_BLOCK_SIZE;
		int lastBlock = fileSize % FILE_BLOCK_SIZE;
		int listSize;
		int offset = 0;

		ArrayList<FileBlockRequestMessage> fileBlocksRequestList = new ArrayList<FileBlockRequestMessage>();

		//ALGORITMO PODEROSO QUE DEMOROU 8 HORAS :') !!!
		
		if (lastBlock == 0) {
			listSize = numberOfBlocks;

		} else {
			listSize = numberOfBlocks + 1;

		}

		int fileBlockSize = FILE_BLOCK_SIZE;
		int i = 0;

		while (i < listSize) {

			int aux = i;

			for (UtilizadorInfo peer : peersToAskFile) {

				if (aux >= listSize)
					break;

				if (i == listSize - 1)
					fileBlockSize = lastBlock;

				fileBlocksRequestList.add(new FileBlockRequestMessage(fileBlockSize, peer, offset));

				aux++;

				offset = offset + FILE_BLOCK_SIZE;

			}

			i = i + peersToAskFile.size();

		}

		return fileBlocksRequestList;
	}

	// **********************************************************************//

	// Processar pedido de utilizador
	public synchronized PeerCommandReply processPeerRequest(PeerCommand peerCommand) {

		CMD cmd = peerCommand.get_cmd();

		if (cmd == CMD.SEARCH_FILE) {

			String keyword = peerCommand.getPayload();
			ArrayList<FileDetails> listOfFoundFiles = searchFilesByKeyword(keyword);
			return new PeerCommandReply(cmd, listOfFoundFiles, true);

		} else if (cmd == CMD.FILE_BLOCK_REQUEST) {

			FileBlockRequestMessage receivedFileBlockRequestMessage = peerCommand.get_fileBlockRequestMessage();

			// Colocar file block request na queue
			receivedFileBlockRequestQueue.add(receivedFileBlockRequestMessage);

			return new PeerCommandReply(CMD.FILE_BLOCK_REQUEST, true);

		} else if (cmd == CMD.FILE_BLOCK_REPLY) {

			FileBlockReplyMessage receivedFileBlockReplyMessage = peerCommand.get_fileBlockReplyMessage();

			// Colocar file block reply na queue
			receivedFileBlockReplyQueue.add(receivedFileBlockReplyMessage);

			return new PeerCommandReply(CMD.FILE_BLOCK_REPLY, true);

		} else {
			return new PeerCommandReply(CMD.UNKOWN_CMD, true);

		}

	}

	// Obter lista de ficheiros
	public synchronized ArrayList<FileDetails> getFilesList() {

		File pasta = new File("./" + fileDirectory);
		File[] ficheirosNaPasta = pasta.listFiles();
		ArrayList<FileDetails> ficheiros = new ArrayList<FileDetails>();

		for (int i = 0; i < ficheirosNaPasta.length; i++) {
			if (ficheirosNaPasta[i].isFile()) {
				FileDetails ficheiro = new FileDetails(ficheirosNaPasta[i].getName(),
						(int) ficheirosNaPasta[i].length());
				ficheiros.add(ficheiro);

			}
		}

		return ficheiros;
	}

	// Procurar ficheiros por keyword
	private synchronized ArrayList<FileDetails> searchFilesByKeyword(String keyword) {
		ArrayList<FileDetails> allFiles = getFilesList();
		ArrayList<FileDetails> foundFiles = new ArrayList<FileDetails>();

		// Ver se tem o ficheiro com o nome pesquisado
		for (FileDetails ficheiro : allFiles) {

			// if (ficheiro.getFileName().startsWith(keyword)) {
			if (ficheiro.getFileName().toUpperCase().contains(keyword.toUpperCase())) {
				foundFiles.add(ficheiro);
			}
		}

		return foundFiles;
	}

	// Obter nº de partes que cada utilizador descarregou
	public int getDownloadedPartsPerUser(int port) {
		int n = 0;
		for (FileBlockRequestMessage fileBlockRequest : fileBlocksRequestListGlobal) {
			if (fileBlockRequest.getPeer().getPeerPort() == port)
				n++;
		}
		return n;
	}

	// Obter informação de um utilizador com um certo ID
	synchronized UtilizadorInfo getPeerInfoByPeerId(int peerId) {
		UtilizadorInfo peerInfo = null;
		for (UtilizadorInfo peer : get_activePeerList()) {
			if (peer.getPeerId() == peerId) {
				peerInfo = peer;
				break;
			}
		}
		return peerInfo;
	}

}
