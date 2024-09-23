
# Bati-Cuisine

## Table des Matières
- [Description](#description)
- [Fonctionnalités](#fonctionnalités)
- [Structure du Projet](#structure-du-projet)
  - [Modèle (Model)](#modèle-model)
  - [DAO (Data Access Object)](#dao-data-access-object)
  - [Services](#services)
  - [Interface Utilisateur (UI)](#interface-utilisateur-ui)
- [Technologies Utilisées](#technologies-utilisées)
- [Architecture du Système](#architecture-du-système)
- [Installation et Déploiement](#installation-et-déploiement)
- [Utilisation](#utilisation)
- [Scénarios d'Utilisation](#scénarios-dutilisation)
- [Contributions](#contributions)
---

## Description

**Bati-Cuisine** est une application Java conçue pour faciliter la gestion de projets de construction et de rénovation. Elle s'adresse aux professionnels du bâtiment et leur permet de créer des projets, de gérer les composants (matériaux et main-d'œuvre), d'associer des clients, et de générer des devis détaillés pour estimer les coûts des travaux. L'application offre une interface simple et intuitive pour suivre les projets du début à la fin.

## Fonctionnalités

### 1. Gestion des Projets
- **Créer, modifier et supprimer des projets** avec des informations détaillées sur le client et les composants.
- **Associer des devis aux projets** pour estimer les coûts avant les travaux.
- **Gérer l'état du projet** (en cours, terminé, annulé).

### 2. Gestion des Composants
- **Matériaux :** Ajouter, modifier et supprimer des matériaux avec des informations telles que le coût unitaire, la quantité, le coût de transport, etc.
- **Main-d'œuvre :** Gérer les informations sur la main-d'œuvre, y compris le taux horaire, le nombre d'heures travaillées et la productivité.

### 3. Gestion des Clients
- **Ajouter de nouveaux clients** avec des informations de contact complètes.
- **Différencier les clients professionnels et particuliers** pour appliquer des remises spécifiques et gérer la TVA.

### 4. Création de Devis
- **Générer des devis détaillés** pour chaque projet, incluant une estimation des coûts des matériaux, de la main-d'œuvre et des taxes.
- **Suivre l'acceptation des devis** par les clients et gérer la date de validité des devis.

### 5. Calcul des Coûts
- **Calculer automatiquement les coûts** des matériaux et de la main-d'œuvre.
- **Appliquer des marges bénéficiaires et des taxes** pour obtenir le coût final du projet.

### 6. Affichage des Détails et Résultats
- **Afficher les détails complets du projet** y compris le client, les composants, le coût total et les devis associés.

## Structure du Projet

### Modèle (Model)
Les classes de modèle représentent les entités principales du système, telles que `Client`, `Project`, `Material`, `Labor`, `Estimate`, et `Component`. Chaque classe encapsule les attributs et les comportements spécifiques à l'entité.

### DAO (Data Access Object)
Les classes DAO, telles que `ClientDAO`, `ProjectDAO`, `ComponentDAO`, et `EstimateDAO`, permettent d'interagir avec la base de données PostgreSQL pour effectuer des opérations CRUD (Create, Read, Update, Delete).

### Services
Les services comme `ClientService`, `ProjectService`, `ComponentService`, et `EstimateService` contiennent la logique métier de l'application et fournissent une interface de haut niveau pour les opérations sur les entités.

### Interface Utilisateur (UI)
Les classes de l'interface utilisateur (`MainMenu`, `ClientMenu`, `ProjectMenu`, `ComponentMenu`, `EstimateMenu`) gèrent les interactions utilisateur et permettent de naviguer à travers les fonctionnalités de l'application.

## Technologies Utilisées

- **Java** : Langage de programmation principal.
- **JDBC (Java Database Connectivity)** : Interface pour la connexion à la base de données.
- **PostgreSQL** : Base de données relationnelle.
- **Git & GitHub** : Contrôle de version et collaboration.
- **JIRA** : Outil de gestion de projet et suivi des tâches.

## Architecture du Système

L'application Bati-Cuisine est organisée en différentes couches :
- **Couche Modèle (Model)** : Représente les données de l'application et leur logique métier.
- **Couche DAO** : Gère les opérations de base de données en utilisant JDBC.
- **Couche Service** : Contient la logique métier et gère les transactions entre les couches DAO et UI.
- **Couche UI** : Fournit une interface utilisateur simple et intuitive via la console.

## Installation et Déploiement

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/kobecode24/bati-cuisine.git
   cd bati-cuisine
   ```

2. **Configurer la base de données PostgreSQL :**
   - Créez une base de données nommée `baticuisine`.
   - Exécutez les scripts SQL disponibles dans le dossier `sql` :
   ```sql
   \i sql/create_tables.sql
   \i sql/insert_initial_data.sql
   ```

3. **Configurer les informations de connexion**

4. **Compiler et exécuter le projet**

## Utilisation

Lancez l'application depuis la console et utilisez les options du menu principal pour interagir avec le système.

### Scénarios d'Utilisation

1. **Créer un nouveau projet :**
   - Choisissez "Créer un nouveau projet" dans le menu principal.
   - Entrez les informations du projet, du client et des composants.
   - Générez un devis et enregistrez-le.

2. **Afficher les projets existants :**
   - Choisissez "Afficher les projets existants" pour voir tous les projets enregistrés.

3. **Calculer le coût d'un projet :**
   - Choisissez "Calculer le coût d'un projet" pour obtenir une estimation détaillée.

## Contributions

Les contributions sont les bienvenues ! Si vous souhaitez contribuer, veuillez soumettre une *pull request* ou ouvrir une *issue* sur GitHub.

---
