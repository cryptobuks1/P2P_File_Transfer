package Cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Comuns.UtilizadorInfo;

public class GUI implements KeyListener {

	Utilizador peer;

	private JFrame frame;
	private JButton btnSearch;
	private String pesquisaText;
	private JPanel painelN = null;
	private JTextField pesquisa = null;
	private JList<String> resultsList;
	private JPanel painelE = null;
	JProgressBar barProgress = null;

	private int progressBarIncrement = 0;

	DefaultListModel<String> listModel = new DefaultListModel<String>();

	public GUI(Utilizador peer) {
		this.peer = peer;

		frame = new JFrame("Peer - " + peer.get_peerInfo().getPeerId());

		frame.setLayout(new BorderLayout());

		painelN = new JPanel();
		painelN.add(new JLabel("Texto a procurar: "));

		// text field 'pesquisa'
		pesquisa = new JTextField(10);
		pesquisa.addKeyListener(this);
		painelN.add(pesquisa);

		// Button 'Procurar'
		btnSearch = new JButton("Procurar");
		painelN.add(btnSearch);

		// *** LISTENER
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pesquisaText = pesquisa.getText();

				if (pesquisaText.equals("CLT")) {
					// Obter lista utilizadores
					peer.requestPeerList();

				} else {
					// Pesquisar ficheiro
					peer.searchFile(pesquisaText);

				}
			}
		});

		painelE = new JPanel(new BorderLayout());

		// Button 'Download'
		JButton btnDownload = new JButton("Download");
		btnDownload.setPreferredSize(new Dimension(10, 70));
		painelE.add(btnDownload, BorderLayout.NORTH);

		// *** LISTENER
		btnDownload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						barProgress.setValue(0);
						barProgress.repaint();
					}
				});

				// Obter a linha clicada

				String downloadRequest = resultsList.getSelectedValue();
				String[] strDownloadRequestTokens = downloadRequest.split(" ");
				String fileToDownload = strDownloadRequestTokens[3];
				int fileSize = Integer.parseInt(strDownloadRequestTokens[5]);

				ArrayList<UtilizadorInfo> peersToRequestFile = new ArrayList<UtilizadorInfo>();

				for (int i = 0; i < resultsList.getModel().getSize(); i++) {

					String listEntry = resultsList.getModel().getElementAt(i);
					String[] strListEntryTokens = listEntry.split(" ");

					String peerId = strListEntryTokens[1];
					String fileName = strListEntryTokens[3];

					if (fileName.equals(fileToDownload)) {
						peersToRequestFile.add(new UtilizadorInfo(Integer.parseInt(peerId)));
					}

				}

				peer.downloadFile(fileToDownload, fileSize, peersToRequestFile);

			}
		});

		barProgress = new JProgressBar(0, 1000);
		painelE.add(barProgress, BorderLayout.CENTER);

		frame.add(painelN, BorderLayout.NORTH);
		frame.add(painelE, BorderLayout.EAST);

		resultsList = new JList<>(listModel);

		JScrollPane scroll = new JScrollPane(resultsList);
		frame.add(scroll, BorderLayout.CENTER);
		frame.setSize(600, 200);
		frame.setLocation(200, 100);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// *** LISTENER
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (peer.isRegisteredAtDirectory())
					peer.unregisterFromDirectory();
			}
		});

		frame.setVisible(true);
	}

	// Getters & Setters

	public int getProgressBarIncrement() {
		return progressBarIncrement;
	}

	public void setProgressBarIncrement(int progressBarIncrement) {
		this.progressBarIncrement = progressBarIncrement;
	}

	public void incrementProgressBar() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (barProgress.getValue() + getProgressBarIncrement() <= 100) {
					barProgress.setValue(barProgress.getValue() + getProgressBarIncrement());
					barProgress.repaint();

				}

			}
		});
	}

	// Popular a ListModel
	public void populateListModel(ArrayList<UtilizadorInfo> peerInfoList) {

		listModel.removeAllElements();

		for (UtilizadorInfo peerInfo : peerInfoList) {

			String listEntry = "Id: " + peerInfo.getPeerId() + " Address: " + peerInfo.getPeerAddress() + " Port: "
					+ peerInfo.getPeerPort();

			listModel.addElement(listEntry);

		}

	}

	// Limpar ListModel
	public void clearListModel() {

		listModel.removeAllElements();

	}

	// Adicionar entrada à ListModel
	public void addEntryToListModel(String listEntry) {

		listModel.addElement(listEntry);

	}

	//Popup de download
	public void showMessageDialog(String messageToShow) {
		JOptionPane.showMessageDialog(null, messageToShow, "Descarga Efectuada", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// Se clicar no Enter em vez do botão também funca
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			pesquisaText = pesquisa.getText();

			if (pesquisaText.equals("CLT")) {
				peer.requestPeerList();

			} else {
				peer.searchFile(pesquisaText);

			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
