package Diretorio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

import Comuns.*;
import Comuns.PeerCommand.CMD;

public class Diretorio {

	private final int N_THREADS = 2;

	ArrayList<UtilizadorInfo> listaUtilizadores = new ArrayList<UtilizadorInfo>();
	public static int porta;

	public Diretorio() {

	}

	public static void main(String[] args) throws IOException {
		porta = Integer.parseInt(args[0]);

		Diretorio diretorio = new Diretorio();
		diretorio.run();
	}

	private void run() {
		ExecutorService executor = null;

		try (ServerSocket serverSocket = new ServerSocket(porta)) {

			System.out.println("Diretório a correr na porta: " + porta);
			executor = Executors.newFixedThreadPool(N_THREADS);

			while (true) {
				//Esperar por conexões
				Socket socket = serverSocket.accept();
				System.out.println("Novo utilizador conectado -> Criar clientHandler");

				//Criar um client handler para o utilizador conectado
				Runnable clientHandler = new ClientHandler(socket, this);
				executor.execute(clientHandler);

			}
		} catch (IOException ex) {
			System.out.println("Excepção apanhada: " + ex.getMessage());
			ex.printStackTrace();
			executor.shutdown();
		}

		executor.shutdown();
	}

	public PeerCommandReply processarComando(PeerCommand comando) {
		CMD cmd = comando.get_cmd();

		if (cmd == CMD.REGISTER_PEER) {
			UtilizadorInfo infoUser = comando.get_peerInfo();

			if (registarUtilizador(infoUser)) {
				return new PeerCommandReply(CMD.REGISTER_PEER, true);

			} else {
				return new PeerCommandReply(CMD.REGISTER_PEER, false);

			}

		} else if (cmd == CMD.GET_PEERS_LIST) {
			ArrayList<UtilizadorInfo> list = getPeersList();
			return new PeerCommandReply(CMD.GET_PEERS_LIST, list, true);

		} else if (cmd == CMD.UNREGISTER_PEER) {

			if (unregisterPeer(comando.get_peerInfo())) {
				return new PeerCommandReply(CMD.UNREGISTER_PEER, true);

			} else {
				return new PeerCommandReply(CMD.UNREGISTER_PEER, false);

			}

		}

		return new PeerCommandReply(CMD.UNKOWN_CMD);

	}

	// Registar utilizador no diretório
	public synchronized boolean registarUtilizador(UtilizadorInfo infoUser) {

		try {
			listaUtilizadores.add(infoUser);
			System.out.println("Novo utilizador registado com ID: " + infoUser.getPeerId());
			return true;

		} catch (Exception e) {
			return false;

		}

	}

	// Obter lista de utilizadores
	public ArrayList<UtilizadorInfo> getPeersList() {
		return listaUtilizadores;

	}

	// Remover utilizador do diretório
	private synchronized boolean unregisterPeer(UtilizadorInfo infoUser) {

		try {
			// Usar iterator em vez de "for loop" para codigo mais limpo
			Iterator<UtilizadorInfo> itr = listaUtilizadores.iterator();
			while (itr.hasNext()) {
				UtilizadorInfo pf = itr.next();
				if (pf.getPeerId() == infoUser.getPeerId()) {
					itr.remove();
					return true;
				}
			}
		} catch (Exception ex) {
			System.out.println("Excepção: " + ex.getMessage());
			return false;
		}

		return false;
	}

}
