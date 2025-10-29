package controllers;

import models.Oeuvre;
import java.util.List;

/**
 * Contrôleur Oeuvre
 * ----------------------
 * Rôle :
 * 1. Faire le lien entre la vue et le modèle Oeuvre.
 * 2. Ne contient pas de logique métier complexe.
 * 3. Appelle les méthodes du modèle et retourne les résultats à la vue.
 *
 * BTS SIO : Séparer la logique de présentation (Vue) et la logique métier (Modèle).
 */
public class OeuvreController {

    /**
     * Récupérer toutes les œuvres
     * return Liste d'objets Oeuvre
     */
    public List<Oeuvre> fetchAllOeuvres() {
        return Oeuvre.getAllOeuvres();
    }

    /**
     * Ajouter une œuvre
     * param nom nom de l'œuvre
     * param idAuteur identifiant de l'auteur
     * return true si l'ajout a réussi, false si doublon ou erreur
     */
    public boolean createOeuvre(String nom, int idAuteur) {
        return Oeuvre.addOeuvre(nom, idAuteur);
    }

    /**
     * Modifier une œuvre existante
     * param id identifiant de l'œuvre
     * param nom nouveau nom de l'œuvre
     * param idAuteur nouvel auteur
     * return true si la modification a réussi, false si doublon ou erreur
     */
   public boolean modifyOeuvre(int id, String nom, int idAuteur) {
    return Oeuvre.updateOeuvre(id, idAuteur, nom);
}

    /**
     * Supprimer une œuvre
     * param id identifiant de l'œuvre
     * return true si la suppression a réussi, false si erreur
     */
    public boolean removeOeuvre(int id) {
        return Oeuvre.deleteOeuvre(id);
    }
}

/**
 * Rappel pédagogique sur la correspondance des méthodes entre Modèle et Contrôleur
 * ---------------------------------------------------------------------------------
 * Dans un projet MVC, il est courant et recommandé que le contrôleur propose
 * des méthodes dont le nom correspond à celles du modèle pour les opérations CRUD
 * simples (ex: getAllOeuvres, addOeuvre, updateOeuvre, deleteOeuvre). 
 *
 * Avantages pédagogiques et pratiques :
 * 1. Clarté et lisibilité : la vue sait exactement quelle méthode appeler.
 * 2. Cohérence : on garde la même logique d'appellation tout au long du projet.
 * 3. Séparation des responsabilités : le contrôleur ne fait que relayer
 *    l'appel vers le modèle, éventuellement en gérant les exceptions ou les retours.
 *
 * À noter :
 * - Si la méthode implique une logique métier plus complexe, le nom dans le
 *   contrôleur peut différer pour refléter cette intention.
 * - Cette pratique est particulièrement utile dans un contexte pédagogique
 *   comme BTS SIO pour illustrer clairement le pattern MVC.
 */
