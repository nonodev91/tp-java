package controllers;

import models.Auteur;
import java.util.List;

/**
 * Contrôleur Auteur
 * ----------------------
 * Rôle :
 * 1. Faire le lien entre la vue et le modèle Auteur.
 * 2. Ne contient pas de logique métier complexe.
 * 3. Appelle les méthodes du modèle et retourne les résultats à la vue.
 *
 * BTS SIO : Séparer la logique de présentation (Vue) et la logique métier (Modèle).
 * Ici les méthodes sont nommées en logique métier (dans le model c'est la logique CRUD)
 */
public class AuteurController {

    /**
     * Récupérer tous les auteurs
     * return liste des auteurs
     */
    public List<Auteur> fetchAllAuteurs() {
        return Auteur.getAllAuteurs();
    }

    /**
     * Ajouter un auteur
     * param prenom prénom de l'auteur
     * param nom nom de l'auteur
     * return true si ajout réussi, false sinon
     */
    public boolean createAuteur(String prenom, String nom) {
        return Auteur.addAuteur(prenom, nom);
    }

    /**
     * Modifier un auteur
     * param id identifiant de l'auteur
     * param prenom nouveau prénom
     * param nom nouveau nom
     * return true si modification réussie, false sinon
     */
    public boolean modifyAuteur(int id, String prenom, String nom) {
        return Auteur.updateAuteur(id, prenom, nom);
    }

    /**
     * Supprimer un auteur
     * param id identifiant de l'auteur
     * return true si suppression réussie, false sinon
     */
    public boolean removeAuteur(int id) {
        return Auteur.deleteAuteur(id);
    }

    /**
     * Récupérer un auteur par son identifiant
     * param id identifiant de l'auteur
     * return Auteur correspondant ou null si inexistant
     */
    public Auteur findAuteurById(int id) {
        return Auteur.getAuteurById(id);
    }
}

/**
 * Rappel pédagogique sur la correspondance des méthodes entre Modèle et Contrôleur
 * ---------------------------------------------------------------------------------
 * Dans un projet MVC, il est courant et recommandé que le contrôleur propose
 * des méthodes dont le nom correspond à celles du modèle pour les opérations CRUD
 * simples (ex: getAllAuteurs, addAuteur, updateAuteur, deleteAuteur). 
 *
 * Avantages pédagogiques et pratiques :
 * 1. Clarté et lisibilité : la vue sait exactement quelle méthode appeler.
 * 2. Cohérence : on garde la même logique d'appellation tout au long du projet.
 * 3. Séparation des responsabilités : le contrôleur ne fait que relayer
 *    l'appel vers le modèle, éventuellement en gérant les exceptions ou les retours.
 *
 * 
 */
