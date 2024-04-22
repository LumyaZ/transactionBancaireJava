package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompteBancaire {
	private String numero;
    private double solde;

    public CompteBancaire(String numero, double soldeInitial) {
        this.numero = numero;
        this.solde = soldeInitial;
    }

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public double getSolde() {
		return solde;
	}

	public void setSolde(double solde) {
		this.solde = solde;
	}

}
