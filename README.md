# TaskFlow

> Application de gestion de tâches collaborative — projet académique Java Maven

[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://openjdk.org/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0.3-6BA539?logo=swagger)](https://swagger.io/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Module](https://img.shields.io/badge/Module-Conception%20Logicielle-purple)]()

---

## Présentation

**TaskFlow** est une application de gestion de tâches collaborative conçue
dans le cadre du module *Conception Logicielle*. Elle permet à des équipes
d'organiser leur travail autour de projets, de gérer des tâches avec un cycle
de vie structuré et de recevoir des notifications automatiques lors de chaque
mise à jour.

Le projet met en pratique les **principes SOLID**, le principe **KISS** et
trois **Design Patterns** classiques : Observer, Factory et Singleton.

---

## Fonctionnalités

- Gestion des utilisateurs avec deux rôles : **Admin** et **Membre**
- Création et organisation de **projets** multi-membres
- Cycle de vie des **tâches** : `TODO → IN_PROGRESS → DONE`
- **Notifications automatiques** (email + push) à chaque changement de statut
- API REST documentée avec **OpenAPI 3.0 / Swagger**
- Filtrage des tâches par statut, projet et assigné

---

## Architecture technique

```
taskflow/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/taskflow/
    │   │   ├── models/              # Entités du domaine
    │   │   │   ├── Task.java
    │   │   │   ├── User.java
    │   │   │   ├── Project.java
    │   │   │   └── TaskStatus.java
    │   │   ├── patterns/            # Design Patterns
    │   │   │   ├── ITaskObserver.java
    │   │   │   ├── ITaskSubject.java
    │   │   │   ├── TaskEventPublisher.java
    │   │   │   ├── EmailNotificationObserver.java
    │   │   │   └── PushNotificationObserver.java
    │   │   ├── services/            # Couche applicative
    │   │   │   ├── TaskService.java
    │   │   │   └── ProjectService.java
    │   │   └── utils/               # Infrastructure
    │   │       └── DatabaseConnection.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/taskflow/
            ├── models/
            └── patterns/
```

### Couches de l'application

| Couche | Package | Rôle |
|--------|---------|------|
| Domaine | `models` | Entités métier, règles de validation, enums |
| Patterns | `patterns` | Design Patterns Observer, Factory, Singleton |
| Application | `services` | Orchestration des cas d'usage |
| Infrastructure | `utils` | Connexion base de données, utilitaires |

---

## Design Patterns implémentés

### Observer — Notifications
Le `TaskEventPublisher` maintient une liste d'`ITaskObserver` et les notifie
à chaque modification de tâche. Ajouter un canal (ex. Slack) se fait
en créant une nouvelle classe sans toucher au publisher.

```
TaskEventPublisher ──notifie──► EmailNotificationObserver
                   ──notifie──► PushNotificationObserver
                   ──notifie──► AuditLogObserver  (extensible)
```

### Factory — Création de tâches
L'interface `ITaskFactory` délègue la création à des factories spécialisées.
Chaque type de tâche (simple, urgente, récurrente) a sa propre factory.

```
ITaskFactory ◄── SimpleTaskFactory
             ◄── UrgentTaskFactory
             ◄── RecurringTaskFactory
```

### Singleton — Connexion base de données
`DatabaseConnection` garantit une seule connexion JDBC partagée dans toute
l'application, avec double-checked locking pour la sécurité thread.

```java
Connection conn = DatabaseConnection.getInstance().getConnection();
```

---

## Principes SOLID appliqués

| Principe | Description | Application dans TaskFlow |
|----------|-------------|--------------------------|
| **S** — Single Responsibility | Une classe = une responsabilité | `TaskService` orchestre, `DatabaseConnection` gère la connexion, chaque Observer gère son canal |
| **O** — Open/Closed | Ouvert à l'extension, fermé à la modification | Nouveau canal de notif = nouvelle classe Observer, zéro modification existante |
| **L** — Liskov Substitution | Les sous-classes substituent le type parent | `AdminUser` et `MemberUser` substituent `User` sans casser le contrat |
| **I** — Interface Segregation | Interfaces petites et ciblées | `ITaskObserver` expose uniquement `onTaskUpdated()` |
| **D** — Dependency Inversion | Dépendre des abstractions, pas des concrets | `TaskService` dépend de `ITaskFactory`, pas de `SimpleTaskFactory` |

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 17 LTS | Langage principal |
| Maven | 3.9+ | Build, dépendances, cycle de vie |
| PostgreSQL | 15+ | Base de données relationnelle |
| JDBC | — | Accès base de données (pattern Singleton) |
| JUnit 5 | 5.10 | Tests unitaires |
| Mockito | 5.x | Mocking pour les tests |
| SLF4J + Logback | 2.x | Logging structuré |
| OpenAPI / Swagger | 3.0.3 | Documentation de l'API REST |
| Swagger UI | — | Interface de test des endpoints |

---

## Prérequis

Avant d'installer le projet, assurez-vous d'avoir :

- **JDK 17+** — [Télécharger OpenJDK](https://openjdk.org/projects/jdk/17/)
- **Maven 3.9+** — [Télécharger Maven](https://maven.apache.org/download.cgi)
- **PostgreSQL 15+** — [Télécharger PostgreSQL](https://www.postgresql.org/download/)
- **Git** — [Télécharger Git](https://git-scm.com/)

Vérification des versions installées :

```bash
java -version    # doit afficher 17.x.x ou supérieur
mvn -version     # doit afficher 3.9.x ou supérieur
psql --version   # doit afficher 15.x ou supérieur
```

---

## Installation et démarrage

### 1. Cloner le dépôt

```bash
git clone https://github.com/votre-username/taskflow.git
cd taskflow
```

### 2. Configurer la base de données

Créer la base de données et l'utilisateur PostgreSQL :

```sql
-- Se connecter en tant que superuser PostgreSQL
psql -U postgres

-- Créer l'utilisateur et la base
CREATE USER taskflow_user WITH PASSWORD 'taskflow_pass';
CREATE DATABASE taskflow_db OWNER taskflow_user;
GRANT ALL PRIVILEGES ON DATABASE taskflow_db TO taskflow_user;

\q
```

Initialiser le schéma :

```bash
psql -U taskflow_user -d taskflow_db -f src/main/resources/schema.sql
```

### 3. Configurer l'application

Copier le fichier de configuration exemple et l'adapter :

```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

Éditer `application.properties` :

```properties
# Base de données
db.url=jdbc:postgresql://localhost:5432/taskflow_db
db.username=taskflow_user
db.password=taskflow_pass

# Serveur
server.port=8080

# Notifications email (SMTP)
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.smtp.username=noreply@taskflow.com
mail.smtp.password=votre_mot_de_passe_smtp

# Logging
logging.level=INFO
```

### 4. Compiler et lancer

```bash
# Compiler et exécuter les tests
mvn clean install

# Lancer l'application
mvn exec:java -Dexec.mainClass="com.taskflow.Main"
```

L'API est accessible à l'adresse : `http://localhost:8080/api/v1`

### 5. Accéder à la documentation Swagger

Ouvrir dans un navigateur : `http://localhost:8080/swagger-ui.html`

Ou importer le fichier `swagger.yaml` dans [Swagger Editor](https://editor.swagger.io/)
pour visualiser et tester les endpoints interactivement.

---

## Lancer les tests

```bash
# Tous les tests
mvn test

# Tests d'un package spécifique
mvn test -Dtest="com.taskflow.patterns.*"

# Tests avec rapport de couverture (JaCoCo)
mvn test jacoco:report
# Rapport disponible dans : target/site/jacoco/index.html
```

---

## Endpoints principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/v1/tasks` | Créer une nouvelle tâche |
| `GET` | `/api/v1/tasks` | Lister les tâches (filtrable par `?status=TODO`) |
| `GET` | `/api/v1/tasks/{id}` | Récupérer une tâche par son ID |
| `PATCH` | `/api/v1/tasks/{id}/status` | Mettre à jour le statut |
| `GET` | `/api/v1/health` | Vérifier l'état de l'API |

Exemple de requête avec `curl` :

```bash
# Créer une tâche
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <votre_token_jwt>" \
  -d '{
    "title": "Implémenter le pattern Observer",
    "dueDate": "2025-12-31",
    "assigneeId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "projectId": "p9q8r7s6-t5u4-v3w2-x1y0-z9876543210a"
  }'

# Filtrer les tâches en cours
curl -X GET "http://localhost:8080/api/v1/tasks?status=IN_PROGRESS&page=0&size=20" \
  -H "Authorization: Bearer <votre_token_jwt>"
```

---

## Structure du projet détaillée

```
src/main/java/com/taskflow/
│
├── models/
│   ├── Task.java             # Entité tâche avec validation des transitions de statut
│   ├── User.java             # Classe abstraite — AdminUser et MemberUser l'étendent
│   ├── AdminUser.java        # Rôle Admin : gestion des projets et membres
│   ├── MemberUser.java       # Rôle Membre : mise à jour des tâches assignées
│   ├── Project.java          # Agrégat contenant des tâches et des membres
│   └── TaskStatus.java       # Enum : TODO | IN_PROGRESS | DONE
│
├── patterns/
│   ├── ITaskObserver.java              # Interface Observer (onTaskUpdated)
│   ├── ITaskSubject.java               # Interface Subject (subscribe/unsubscribe/notify)
│   ├── TaskEventPublisher.java         # Publisher concret — thread-safe
│   ├── EmailNotificationObserver.java  # Canal email
│   ├── PushNotificationObserver.java   # Canal push mobile
│   ├── ITaskFactory.java               # Interface Factory
│   ├── SimpleTaskFactory.java          # Tâche standard
│   └── UrgentTaskFactory.java          # Tâche urgente (délai réduit)
│
├── services/
│   ├── TaskService.java      # Orchestration : Factory + Repository + Observer
│   └── ProjectService.java   # Gestion des projets et de leurs membres
│
└── utils/
    └── DatabaseConnection.java  # Singleton JDBC avec double-checked locking
```

---

## Contribuer

Les contributions sont les bienvenues dans le cadre du projet académique.

```bash
# Créer une branche pour votre fonctionnalité
git checkout -b feature/nom-de-la-fonctionnalite

# Commiter vos modifications
git commit -m "feat: description de la fonctionnalité"

# Pousser et ouvrir une Pull Request
git push origin feature/nom-de-la-fonctionnalite
```

**Conventions de commit :**
- `feat:` nouvelle fonctionnalité
- `fix:` correction de bug
- `docs:` mise à jour de la documentation
- `test:` ajout ou modification de tests
- `refactor:` refactorisation sans changement de comportement

---

## Licence

Ce projet est distribué sous licence **MIT**.
Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## Auteurs

Projet réalisé dans le cadre du module **Conception Logicielle**.

---

*Documentation API complète disponible dans `swagger.yaml` — à importer dans [Swagger Editor](https://editor.swagger.io/) ou [Postman](https://www.postman.com/).*
