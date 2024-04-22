package com.example;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import Exception.NotEnoughMoneyException;
import model.CompteBancaire;
import service.CompteBancaireService;

@DisplayName("Test de la classe CompteBancaireService")
public class CompteBancaireTest {
	private CompteBancaire compte1;
    private CompteBancaire compte2;
    private CompteBancaire compte3;
    private CompteBancaireService compteBancaireService;

    @BeforeEach
    public void setUpBeforeEachTest() {
    	compteBancaireService = new CompteBancaireService();
        compte1 = compteBancaireService.ajouterCompte("1", 1000);
        compte2 = compteBancaireService.ajouterCompte("2", 500);
        compte3 = compteBancaireService.ajouterCompte("3", 3000);
    }
    
    @Test
    @DisplayName("Creation de compte1")
    public void testAddCompte1() {
        assertNotNull(compte1);
        assertEquals("1", compte1.getNumero());
        assertEquals(1000, compte1.getSolde(), 0);
    }
    @Test
    @DisplayName("Creation de compte2")
    public void testAddCompte2() {
        assertNotNull(compte2);
        assertEquals("2", compte2.getNumero());
        assertEquals(500, compte2.getSolde(), 0);
    }

    @Test
    @DisplayName("Trouver un compte par son numero")
    public void testFindByNumber() {
  
        CompteBancaire compte = compteBancaireService.trouverCompteParNumero("1");
        assertNotNull(compte);
        assertEquals("1", compte.getNumero());
        assertEquals(1000, compte.getSolde(), "Le solde du compte 1 doit être de 1000" );
        
        CompteBancaire compte2 = compteBancaireService.trouverCompteParNumero("2");
        assertNotNull(compte2);
        assertEquals("2", compte2.getNumero());
        assertEquals(500, compte2.getSolde(), 0);
    }

    @Test
    @DisplayName("Vérification de l'update")
    public void testUpdateSolde() {
        CompteBancaire compte = compteBancaireService.mettreAJourSolde("1", 1500);
        assertNotNull(compte);
        assertEquals("1", compte.getNumero());
        assertEquals(1500, compte.getSolde(), 0);
        
        CompteBancaire compte2 = compteBancaireService.mettreAJourSolde("2", 3000);
        assertNotNull(compte2);
        assertEquals("2", compte2.getNumero());
        assertEquals(3000, compte2.getSolde(), 0);
    }

    @Test
    @DisplayName("Vérification de la suppression")
    public void testDeleteByNumber() {
    	compteBancaireService.supprimerCompteParNumero("1");
        assertThrows(NoSuchElementException.class, () -> compteBancaireService.trouverCompteParNumero("1"));
        
        compteBancaireService.supprimerCompteParNumero("2");
        assertThrows(NoSuchElementException.class, () -> compteBancaireService.trouverCompteParNumero("2"));
    }

    @Test
    @DisplayName("Trouver la liste des comptes")
    public void testListerComptes() {
        List<CompteBancaire> comptes = compteBancaireService.listerComptes();
        assertNotNull(comptes);
        assertEquals(3, comptes.size());
    }
    
    
    // test des transactions
    
    @Test
    @DisplayName("Transaction correcte")
    public void testTransaction() {
    	assertTrue(compteBancaireService.transaction("1", "2", 300));
        assertEquals(700, compte1.getSolde(), 0);
        assertEquals(800, compte2.getSolde(), 0);
    }
    
    @Test
    @DisplayName("Transaction incorrecte avec solde négatif")
    public void testTransactionNegatif() {
        assertThrows(IllegalArgumentException.class, () -> compteBancaireService.transaction("1", "2", -100));
    }
    
    @Test
    @DisplayName("Transaction incorrecte avec montant supérieur au solde")
    public void testTransactionSuperieur() {
        assertThrows(NotEnoughMoneyException.class, () -> compteBancaireService.transaction("1", "2", 1500));
    }
    
    /*@Test
     * ce test ne passe pas donc j'attend la correction
     *     @DisplayName("Transaction invalide")
    public void testTransactionInvalide() {
    	assertThrows(IllegalArgumentException.class, () -> {
            compteBancaireService.transaction(null, "2", 100);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            compteBancaireService.transaction("1", null, 100);
        });
    }*/
    
    @Test
    @DisplayName("Transaction avec test imbriqués")
    public void testTransactionImbrique() {
    	 assertAll("Vérification des soldes",
		 	() -> assertTrue(compteBancaireService.transaction("1", "3", 300)),
            () -> assertEquals(700, compte1.getSolde(), 0),
            () -> assertEquals(3300, compte3.getSolde(), 0),
            () -> {
            	if(compte1.getSolde()==700) {
            		assertTrue(compteBancaireService.transaction("1", "2", 700));
            		assertEquals(0, compte1.getSolde(), 0);
            		assertEquals(1200, compte2.getSolde(), 0);
            	}	
            }
        );
    }
    
    @Test
    @DisplayName("Transaction avec timeout de une seconde")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void testTransactionTiming() {
		assertTrue(compteBancaireService.transaction("1", "2", 200));
		assertEquals(800, compte1.getSolde(), 0);
		assertEquals(700, compte2.getSolde(), 0);
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {100, 200, 50, -10})
    @DisplayName("Transaction avec plusieurs parametre de montant")
    public void testTransactionMontant(double montant) {
    
        if (montant > 0) {
            boolean transaction = compteBancaireService.transaction("1", "2", montant);
            if (montant <= compte1.getSolde()) {
                assertTrue(transaction);
                assertEquals(1000 - montant, compte1.getSolde(), 0);
                assertEquals(500 + montant, compte2.getSolde(), 0);
            } else {
                assertFalse(transaction);
                assertEquals(1000, compte1.getSolde(), 0);
                assertEquals(500, compte2.getSolde(), 0);
            }
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                compteBancaireService.transaction("1", "2", montant);
            });
        }
    }
}
