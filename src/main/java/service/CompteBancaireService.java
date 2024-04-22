package service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import Exception.NotEnoughMoneyException;
import model.CompteBancaire;

public class CompteBancaireService {
    private List<CompteBancaire> listeComptes = new ArrayList<>();

    public CompteBancaire ajouterCompte(String numero, double soldeInitial) {
        CompteBancaire nouveauCompte = new CompteBancaire(numero, soldeInitial);
        listeComptes.add(nouveauCompte);
        return nouveauCompte;
    }

    public CompteBancaire trouverCompteParNumero(String numero) {
        return listeComptes.stream()
                .filter(compte -> compte.getNumero().equals(numero))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Compte avec numéro: " + numero + " n'existe pas"));
    }

    public CompteBancaire mettreAJourSolde(String numero, double nouveauSolde) {
        CompteBancaire compte = trouverCompteParNumero(numero);
        compte.setSolde(nouveauSolde);
        return compte;
    }

    public void supprimerCompteParNumero(String numero) {
        listeComptes.removeIf(compte -> compte.getNumero().equals(numero));
    }

    public List<CompteBancaire> listerComptes() {
        return listeComptes;
    }
    
    public boolean transaction(String numeroCompteADebiter, String numeroCompteACrediter, double montant) {
        CompteBancaire compteADebiter = trouverCompteParNumero(numeroCompteADebiter);
        CompteBancaire compteACrediter = trouverCompteParNumero(numeroCompteACrediter);
        
        
        if (compteADebiter == null || compteACrediter == null) {
            throw new IllegalArgumentException("Les comptes spécifiés sont invalides");
        }

        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant de la transaction doit être positif");
        }

        if (compteADebiter.getSolde() < montant) {
            throw new NotEnoughMoneyException("Le compte à débiter ne dispose pas de suffisamment de fonds");
        }

        compteADebiter.setSolde(compteADebiter.getSolde() - montant);
        compteACrediter.setSolde(compteACrediter.getSolde() + montant);
        return true;
    }
    
}
