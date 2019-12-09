package Comuns;

public class InfoDiretorio {

	private String enderecoDiretorio;
	private int portaDiretorio;

	public InfoDiretorio(String enderecoDiretorio, int portaDiretorio) {
		this.enderecoDiretorio = enderecoDiretorio;
		this.portaDiretorio = portaDiretorio;
	}

	// Getters & Setters

	public String getEnderecoDiretorio() {
		return enderecoDiretorio;
	}

	public void setEnderecoDiretorio(String enderecoDiretorio) {
		this.enderecoDiretorio = enderecoDiretorio;
	}

	public int getPortaDiretorio() {
		return portaDiretorio;
	}

	public void setPortaDiretorio(int portaDiretorio) {
		this.portaDiretorio = portaDiretorio;
	}

}
