# 🧠 Plateforme de Gestion Thérapeutique - RAG-Powered Chatbot

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![Python](https://img.shields.io/badge/Python-3.13-blue?style=for-the-badge&logo=python)
![Ollama](https://img.shields.io/badge/Ollama-Llama%203.1-purple?style=for-the-badge&logo=meta)
![FAISS](https://img.shields.io/badge/FAISS-Vector%20Search-red?style=for-the-badge)

*Un chatbot thérapeutique intelligent alimenté par l'IA et des documents médicaux validés*

[Installation](#-installation) • [Architecture](#-architecture) • [Utilisation](#-utilisation) • [API](#-api-documentation)

</div>

---

## 📋 Table des Matières

- [Vue d'ensemble](#-vue-densemble)
- [Caractéristiques](#-caractéristiques)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [API Documentation](#-api-documentation)
- [Base de Connaissances](#-base-de-connaissances)
- [Sécurité & Confidentialité](#-sécurité--confidentialité)
- [Dépannage](#-dépannage)
- [Contribution](#-contribution)
- [License](#-license)

---

## 🌟 Vue d'ensemble

La **Plateforme de Gestion Thérapeutique** est un système de chatbot intelligent conçu pour fournir des conseils thérapeutiques basés sur des documents médicaux validés. En utilisant une architecture RAG (Retrieval-Augmented Generation) avancée, le système combine la puissance du modèle Llama 3.1 avec une recherche sémantique dans 13 guides thérapeutiques officiels.

### 🎯 Objectifs

- **Réponses Contextualisées** : Chaque réponse est basée sur des documents médicaux officiels
- **Empathie & Bienveillance** : Conçu pour communiquer avec compassion en français
- **Détection de Crise** : Identification automatique des situations d'urgence (risque suicidaire)
- **Confidentialité Totale** : Toutes les données restent locales, aucun appel API externe
- **Open Source** : Architecture transparente et personnalisable

---

## ✨ Caractéristiques

### 🤖 Intelligence Artificielle Avancée

- **Modèle LLM** : Llama 3.1 (4.9 GB) via Ollama pour des réponses contextuelles
- **RAG Pipeline** : Recherche sémantique dans 2683 chunks de documents thérapeutiques
- **Embeddings** : Sentence-Transformers (all-MiniLM-L6-v2) pour la vectorisation
- **Vector Store** : FAISS pour une recherche rapide et efficace

### 🛡️ Sécurité & Conformité

- **Détection de Crise** : Mots-clés en français pour identifier les situations d'urgence
- **Données Locales** : Aucun transfert de données vers des services cloud
- **Sources Validées** : 13 guides officiels d'anxiété, stress et TCC
- **Réponses Transparentes** : Citations des sources utilisées

### 🎨 Interface & Intégration

- **API REST** : Endpoint `/api/chat` avec validation Jakarta
- **Architecture Réactive** : Spring WebFlux pour des performances optimales
- **Documentation** : Swagger/OpenAPI intégré
- **Logging** : SLF4J avec niveaux configurables

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Postman/Frontend)                │
└────────────────────────────┬────────────────────────────────────┘
                             │ POST /api/chat
                             │ {"question": "..."}
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT API (Port 8080)                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ ChatController → ChatService                               │  │
│  │   ├─ Validation (Jakarta)                                 │  │
│  │   ├─ Crisis Detection (French Keywords)                   │  │
│  │   └─ Error Handling                                       │  │
│  └───────────────────────────────────────────────────────────┘  │
│         │                                        │                │
│         ▼                                        ▼                │
│  ┌──────────────┐                    ┌──────────────────────┐   │
│  │ RetrievalClient│                   │   OllamaClient       │   │
│  │ (WebClient)   │                    │   (WebClient)        │   │
│  └──────────────┘                    └──────────────────────┘   │
└─────────┬──────────────────────────────────────┬────────────────┘
          │                                       │
          │ POST /retrieve                        │ POST /api/generate
          │ top_k=5                               │ prompt + context
          ▼                                       ▼
┌─────────────────────────────┐    ┌──────────────────────────────┐
│  PYTHON RAG SERVICE         │    │   OLLAMA SERVER              │
│  (Flask - Port 8000)        │    │   (Port 11434)               │
│  ┌───────────────────────┐  │    │  ┌────────────────────────┐ │
│  │ TherapeuticPDFIndexer │  │    │  │  Llama 3.1 (4.9 GB)    │ │
│  │   ├─ FAISS Index      │  │    │  │  - System Prompt       │ │
│  │   ├─ 2683 Chunks      │  │    │  │  - Context Injection   │ │
│  │   └─ Semantic Search  │  │    │  │  - French Generation   │ │
│  └───────────────────────┘  │    │  └────────────────────────┘ │
│            │                 │    └──────────────────────────────┘
│            ▼                 │
│  ┌───────────────────────┐  │
│  │  13 PDF Documents     │  │
│  │  - 1314 Pages         │  │
│  │  - Anxiety Guides     │  │
│  │  - CBT Workbooks      │  │
│  │  - Stress Management  │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### 🔄 Flux de Traitement

1. **Requête Client** : L'utilisateur envoie une question en français
2. **Validation** : ChatController valide le format et la longueur
3. **Détection de Crise** : Analyse des mots-clés critiques (suicide, etc.)
4. **Recherche RAG** : RetrievalClient récupère 5 chunks pertinents via FAISS
5. **Construction du Context** : Assemblage des chunks en contexte cohérent
6. **Génération LLM** : OllamaClient envoie prompt + contexte à Llama 3.1
7. **Réponse Enrichie** : Retour de la réponse + sources citées

---

## 🛠️ Technologies

### Backend Java (Spring Boot 3.2.0)

| Technologie | Version | Rôle |
|------------|---------|------|
| **Java** | 17 | Langage principal |
| **Spring Boot** | 3.2.0 | Framework REST |
| **Spring WebFlux** | 6.1.1 | Client HTTP réactif |
| **Jakarta Validation** | 3.0.2 | Validation des entrées |
| **Lombok** | 1.18.30 | Réduction du boilerplate |
| **Jackson** | 2.15.3 | Sérialisation JSON |
| **SLF4J** | 2.0.9 | Logging |

### Backend Python (Flask 3.0.0)

| Technologie | Version | Rôle |
|------------|---------|------|
| **Python** | 3.13 | Langage RAG |
| **Flask** | 3.0.0 | API REST |
| **FAISS** | 1.9.0 | Recherche vectorielle |
| **LangChain** | 0.3.13 | Pipeline RAG |
| **Sentence-Transformers** | 3.3.1 | Embeddings |
| **PyPDF** | 5.1.0 | Extraction de texte |

### Infrastructure LLM

| Composant | Détails |
|-----------|---------|
| **Ollama** | Serveur local pour LLMs |
| **Llama 3.1** | Modèle 8B instruct (4.9 GB) |
| **Embeddings** | all-MiniLM-L6-v2 (384 dimensions) |
| **Chunk Size** | 1000 caractères avec 200 de chevauchement |

---

## 📦 Prérequis

### Système d'Exploitation
- ✅ Windows 10/11 (testé)
- ✅ macOS 12+
- ✅ Linux (Ubuntu 20.04+)

### Logiciels Requis

```bash
# Java Development Kit 17
java -version
# Doit afficher: java version "17.0.x"

# Maven 3.8+
mvn -version

# Python 3.13 (ou 3.11+)
python --version

# Git
git --version

# Ollama (pour Llama 3.1)
# Télécharger depuis: https://ollama.ai
```

### Ressources Matérielles Recommandées

| Composant | Minimum | Recommandé |
|-----------|---------|------------|
| **CPU** | 4 cores | 8+ cores |
| **RAM** | 8 GB | 16 GB |
| **Stockage** | 10 GB | 20 GB |
| **GPU** | Aucun | NVIDIA (optionnel) |

---

## 🚀 Installation

### Étape 1 : Cloner le Repository

```bash
git clone https://github.com/Saadkarz/Plateforme-de-Gestion-Therapeutique.git
cd Plateforme-de-Gestion-Therapeutique
```

### Étape 2 : Installer Ollama et Llama 3.1

#### Windows
```powershell
# Télécharger Ollama depuis https://ollama.ai/download
# Après installation:
ollama pull llama3.1

# Vérifier l'installation
ollama list
# Doit afficher: llama3.1:latest    4.9 GB
```

#### macOS/Linux
```bash
# Installer Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Télécharger Llama 3.1
ollama pull llama3.1

# Démarrer le serveur
ollama serve
```

### Étape 3 : Configurer Java 17

#### Windows
```powershell
# Définir JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Vérifier
java -version
```

#### macOS/Linux
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Étape 4 : Installer les Dépendances Python

```bash
cd python-retrieval-service

# Créer un environnement virtuel (optionnel mais recommandé)
python -m venv venv
source venv/bin/activate  # Sur Windows: venv\Scripts\activate

# Installer les dépendances
pip install -r requirements.txt
```

**Contenu de `requirements.txt`:**
```txt
flask==3.0.0
flask-cors==4.0.0
langchain==0.3.13
langchain-community==0.3.13
sentence-transformers==3.3.1
faiss-cpu==1.9.0
pypdf==5.1.0
numpy>=1.26.0
```

### Étape 5 : Indexer les Documents PDF

```bash
# Dans le dossier python-retrieval-service
python pdf_indexer.py

# Sortie attendue:
# 🚀 Indexation des PDFs thérapeutiques...
# 📂 Trouvé 13 fichiers PDF
# 📄 Total: 1314 pages chargées
# ✂️  Documents découpés en 2683 chunks
# 💾 Index vectoriel créé et sauvegardé!
```

### Étape 6 : Compiler le Backend Java

```bash
# Retour à la racine du projet
cd ..

# Compiler avec Maven
mvn clean install

# Sortie attendue:
# [INFO] BUILD SUCCESS
# [INFO] Total time: 4-6 seconds
```

---

## ⚙️ Configuration

### `application.yml` (Spring Boot)

```yaml
server:
  port: 8080

spring:
  application:
    name: RAG Chat API

app:
  ollama:
    model: llama3.1
    endpoint: http://localhost:11434
  retrieval:
    url: http://localhost:8000/retrieve
    timeout: 30000

logging:
  level:
    com.therapist.ragchat: DEBUG
    org.springframework.web: INFO
```

### `app.py` Configuration (Flask)

```python
PDF_DIRECTORY = "../for stres AI"  # Chemin vers les PDFs
CHUNK_SIZE = 1000
CHUNK_OVERLAP = 200
EMBEDDING_MODEL = "sentence-transformers/all-MiniLM-L6-v2"
```

---

## 🎮 Utilisation

### Démarrer Tous les Services

#### Terminal 1: Ollama Server
```bash
ollama serve
# Le serveur démarre sur http://localhost:11434
```

#### Terminal 2: Python RAG Service
```bash
cd python-retrieval-service
python app.py

# Sortie:
# ============================================================
# 🚀 RAG Retrieval Service with Real PDFs
# ============================================================
# 📍 Running on: http://localhost:8000
```

#### Terminal 3: Spring Boot API
```bash
# À la racine du projet
export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"  # Adapter selon votre OS
mvn spring-boot:run

# Sortie:
# Started RagChatApplication in 1.8 seconds
# Tomcat started on port 8080
```

### Tester avec Postman

#### Requête Simple
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "question": "Je me sens très anxieux, que puis-je faire maintenant ?"
}
```

#### Réponse Attendue
```json
{
  "answer": "Je comprends que vous vous sentez anxieux en ce moment. Voici quelques techniques qui peuvent vous aider immédiatement:\n\n1. **Respiration 4-7-8**: Inspirez par le nez pendant 4 secondes, retenez votre souffle pendant 7 secondes, puis expirez lentement par la bouche pendant 8 secondes. Répétez 3-4 fois.\n\n2. **Grounding 5-4-3-2-1**: Identifiez 5 choses que vous voyez, 4 que vous touchez, 3 que vous entendez, 2 que vous sentez, et 1 que vous goûtez. Cette technique vous ramène au moment présent.\n\n3. **Relaxation Musculaire Progressive**: Contractez puis relâchez chaque groupe musculaire de votre corps, en commençant par les pieds et en remontant vers la tête.\n\nCes techniques sont recommandées dans les guides de gestion de l'anxiété et peuvent procurer un soulagement rapide.",
  "sources": [
    "anxiety guide fr.pdf",
    "Guide-autosoins-pour-la-gestion-du-stress.pdf",
    "The-Anxiety-Skills-Workbook.pdf"
  ]
}
```

#### Test de Détection de Crise
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "question": "Je pense au suicide"
}
```

Réponse:
```json
{
  "answer": "🚨 ALERTE: Si vous avez des pensées suicidaires, contactez immédiatement:\n- Urgences: 112 (Europe) ou 911 (Amérique du Nord)\n- Ligne de prévention du suicide: 3114 (France) ou 1-833-456-4566 (Canada)\n- Rendez-vous aux urgences de l'hôpital le plus proche\n\nVous n'êtes pas seul(e), et de l'aide est disponible 24/7.",
  "sources": []
}
```

### Tester avec cURL

```bash
# Requête simple
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"Comment gérer mon stress quotidien ?"}'

# Health check
curl http://localhost:8080/actuator/health
```

---

## 📚 API Documentation

### Endpoint Principal: `/api/chat`

#### Request
```http
POST /api/chat HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "question": "string (2-1000 caractères, obligatoire)"
}
```

#### Response Success (200 OK)
```json
{
  "answer": "string (réponse générée en français)",
  "sources": ["string (nom du fichier PDF)", ...]
}
```

#### Response Error (400 Bad Request)
```json
{
  "timestamp": "2025-12-01T18:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Question must be between 2 and 1000 characters",
  "path": "/api/chat"
}
```

### Endpoints Python Service

#### `POST /retrieve`
Recherche sémantique dans les PDFs

**Request:**
```json
{
  "query": "string",
  "top_k": 5
}
```

**Response:**
```json
{
  "chunks": [
    {
      "text": "string (contenu du chunk)",
      "source": "string (nom du fichier)",
      "chunk_id": 0
    }
  ]
}
```

#### `GET /health`
Vérification de l'état du service

**Response:**
```json
{
  "status": "healthy",
  "indexed_chunks": 2683,
  "pdf_files": 13
}
```

#### `POST /reindex`
Réindexe tous les PDFs (utile après ajout de documents)

**Response:**
```json
{
  "status": "success",
  "message": "Reindexed 2683 chunks from 13 PDFs"
}
```

---

## 📖 Base de Connaissances

Le système utilise **13 documents thérapeutiques officiels** (1314 pages, 2683 chunks):

### Guides en Français 🇫🇷
1. **anxiety guide fr.pdf** - Guide complet sur l'anxiété
2. **Guide-autosoins-pour-la-gestion-du-stress.pdf** - Techniques d'autogestion
3. **guidedepratiquetagfinal20172.pdf** - Guide pratique TAG (Trouble Anxiété Généralisée)

### Guides en Anglais 🇬🇧
4. **Anxiety self-help workbook (English).pdf** - Workbook d'auto-assistance
5. **AnxietyManagmentWorkbook.pdf** - Gestion pratique de l'anxiété
6. **anxiety_moodjuice_self_help_guide.pdf** - Guide Moodjuice
7. **caps-resources-anxiety-depression-reduction-workbook.pdf** - CAPS workbook
8. **manage-stress-workbook.pdf** - Gestion du stress
9. **mybriefcbt-patient-workbook-fillable.pdf** - TCC brève
10. **Stress-Management-and-Healthy-Coping-Workbook-Final-1.pdf** - Coping strategies
11. **The-Anxiety-Skills-Workbook.pdf** - Compétences anti-anxiété
12. **The-Cognitive-Behavioral-Workbook-for-Anxiety.pdf** - TCC pour anxiété
13. **wellbeing-team-cbt-workshop-booklet-2016.pdf** - Atelier TCC

### Ajout de Nouveaux Documents

```bash
# 1. Placer les PDFs dans le dossier
cp nouveau-guide.pdf "for stres AI/"

# 2. Réindexer
curl -X POST http://localhost:8000/reindex

# Ou redémarrer le service Python
```

---

## 🔒 Sécurité & Confidentialité

### Détection de Crise

Le système détecte automatiquement les situations d'urgence via des mots-clés français:

```java
private static final List<String> CRISIS_KEYWORDS = List.of(
    "suicide", "me tuer", "en finir", "mourir", 
    "faire du mal", "me suicider", "plus envie de vivre"
);
```

Réponse immédiate avec numéros d'urgence internationaux.

### Confidentialité des Données

- ✅ **100% Local** : Aucune donnée envoyée à des services externes
- ✅ **Pas de Tracking** : Aucun cookie ou télémétrie
- ✅ **Pas de Stockage** : Les conversations ne sont pas sauvegardées
- ✅ **Open Source** : Code transparent et auditable

### Bonnes Pratiques

- 🔐 Ne pas exposer l'API sur Internet sans authentification
- 🔐 Utiliser HTTPS en production
- 🔐 Implémenter un rate limiting pour éviter les abus
- 🔐 Logger uniquement les métadonnées (pas le contenu des questions)

---

## 🐛 Dépannage

### Problème : Port déjà utilisé

```bash
# Windows - Trouver le processus sur port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill -9
```

### Problème : Ollama ne répond pas

```bash
# Vérifier si Ollama est en cours d'exécution
curl http://localhost:11434/api/tags

# Redémarrer Ollama
# Windows: Relancer depuis le menu Démarrer
# Linux/macOS:
pkill ollama
ollama serve
```

### Problème : FAISS n'installe pas (Windows)

```powershell
# Installer Visual C++ Build Tools
# https://visualstudio.microsoft.com/visual-cpp-build-tools/

# Ou utiliser faiss-cpu (pas besoin de compilateur)
pip install faiss-cpu
```

### Problème : Erreur "Connection refused 127.0.0.1:8000"

```bash
# Vérifier que le service Python est démarré
curl http://localhost:8000/health

# Si non, le redémarrer
cd python-retrieval-service
python app.py
```

### Problème : Réponses lentes de Llama 3.1

```bash
# Réduire la longueur du prompt dans OllamaClient.java
# Ou utiliser un modèle plus petit:
ollama pull llama3.1:8b-instruct-q4_0  # Version quantifiée (plus rapide)
```

### Logs de Débogage

```bash
# Java (application.yml)
logging.level.com.therapist.ragchat: DEBUG

# Python (app.py)
app.logger.setLevel(logging.DEBUG)
```

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Voici comment participer:

### 1. Fork le Repository
```bash
git clone https://github.com/VOTRE-USERNAME/Plateforme-de-Gestion-Therapeutique.git
```

### 2. Créer une Branche
```bash
git checkout -b feature/nouvelle-fonctionnalite
```

### 3. Faire vos Changements
- Suivre les conventions de code existantes
- Ajouter des tests si applicable
- Mettre à jour la documentation

### 4. Commit et Push
```bash
git add .
git commit -m "feat: Ajout de [fonctionnalité]"
git push origin feature/nouvelle-fonctionnalite
```

### 5. Créer une Pull Request
Décrire clairement les changements et leur motivation.

### Idées de Contributions

- 🌍 Support multilingue (anglais, espagnol, etc.)
- 💾 Ajout de l'historique de conversation
- 🎨 Interface web (React/Vue.js)
- 📊 Dashboard d'analytics
- 🔐 Système d'authentification
- 🧪 Tests unitaires et d'intégration
- 📱 Application mobile

---

## 📊 Performances

### Benchmarks (Machine Standard)

| Opération | Temps Moyen | Notes |
|-----------|-------------|-------|
| Recherche FAISS | 50-100ms | 2683 chunks |
| Génération Llama 3.1 | 2-5s | Dépend de la longueur |
| Requête totale | 3-7s | End-to-end |
| Chargement index | 2-3s | Au démarrage |

### Optimisations Possibles

- 🚀 GPU pour Llama 3.1 (gain de 5-10x)
- 🚀 Redis pour cache des embeddings
- 🚀 Quantification du modèle (q4_0, q5_0)
- 🚀 Augmenter `top_k` pour plus de contexte

---

## 📄 License

Ce projet est sous licence **MIT**. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

```
MIT License

Copyright (c) 2025 Saad Karzazi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 🙏 Remerciements

- **Meta AI** - Pour le modèle Llama 3.1
- **Ollama** - Pour le runtime LLM local
- **Facebook Research** - Pour FAISS
- **LangChain** - Pour les outils RAG
- **Spring Team** - Pour Spring Boot 3
- **Communauté Open Source** - Pour tous les contributeurs

---

## 📞 Contact & Support

- **GitHub Issues** : [Signaler un bug](https://github.com/Saadkarz/Plateforme-de-Gestion-Therapeutique/issues)
- **Discussions** : [Forum de discussion](https://github.com/Saadkarz/Plateforme-de-Gestion-Therapeutique/discussions)
- **Email** : saad.karzazi@example.com

---

## 🗺️ Roadmap

### Version 1.1 (Q1 2026)
- [ ] Interface web React
- [ ] Support multilingue (EN, ES, DE)
- [ ] Historique de conversation
- [ ] Export des réponses en PDF

### Version 1.2 (Q2 2026)
- [ ] Authentification JWT
- [ ] Dashboard d'analytics
- [ ] API GraphQL
- [ ] Tests E2E Cypress

### Version 2.0 (Q3 2026)
- [ ] Application mobile (React Native)
- [ ] Support audio (Speech-to-Text)
- [ ] Fine-tuning du modèle
- [ ] Déploiement Docker

---

<div align="center">

**Fait avec ❤️ et 🤖 par [Saad Karzouz](https://github.com/Saadkarz)**

⭐ **Si ce projet vous aide, n'oubliez pas de mettre une étoile !** ⭐

[⬆ Retour en haut](#-plateforme-de-gestion-thérapeutique---rag-powered-chatbot)

</div>
