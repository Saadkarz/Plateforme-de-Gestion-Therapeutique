# 🧠 Chatbot Thérapeutique RAG avec Llama 3.1

## 📋 Description

Un chatbot thérapeutique intelligent utilisant l'architecture **RAG (Retrieval-Augmented Generation)** avec:
- **Llama 3.1** via **Ollama** (exécution locale, gratuit, sans limites)
- **13 PDFs thérapeutiques réels** indexés avec **FAISS**
- **Détection de crise** pour situations d'urgence
- **API REST** Spring Boot 3.x + Python Flask

---

## 🏗️ Architecture

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│             │      │              │      │             │
│  Utilisateur│─────→│  Spring Boot │─────→│   Ollama    │
│  (Postman)  │      │  (Port 8080) │      │ Llama 3.1   │
│             │      │              │      │(Port 11434) │
└─────────────┘      └──────┬───────┘      └─────────────┘
                            │
                            ↓
                     ┌──────────────┐
                     │  Python RAG  │
                     │  Service     │
                     │  (Port 8000) │
                     └──────┬───────┘
                            │
                            ↓
                    ┌───────────────────┐
                    │  FAISS VectorDB   │
                    │  13 PDF Documents │
                    │  (Indexed)        │
                    └───────────────────┘
```

---

## 📚 Documents Thérapeutiques

Les 13 PDFs dans `for stres AI/`:
1. anxiety guide fr.pdf
2. Anxiety self-help workbook (English).pdf
3. AnxietyManagmentWorkbook.pdf
4. anxiety_moodjuice_self_help_guide.pdf
5. caps-resources-anxiety-depression-reduction-workbook.pdf
6. Guide-autosoins-pour-la-gestion-du-stress.pdf
7. guidedepratiquetagfinal20172.pdf
8. manage-stress-workbook.pdf
9. mybriefcbt-patient-workbook-fillable.pdf
10. Stress-Management-and-Healthy-Coping-Workbook-Final-1.pdf
11. The-Anxiety-Skills-Workbook.pdf
12. The-Cognitive-Behavioral-Workbook-for-Anxiety.pdf
13. wellbeing-team-cbt-workshop-booklet-2016.pdf

---

## 🚀 Installation

### 1. Installer Ollama

Téléchargez depuis: https://ollama.ai

```powershell
# Après installation, télécharger Llama 3.1
ollama pull llama3.1
```

### 2. Configurer Python (Service RAG)

```powershell
cd "python-retrieval-service"
pip install -r requirements.txt

# Indexer les PDFs (première fois uniquement)
python pdf_indexer.py
```

### 3. Configurer Java (API Spring Boot)

Vérifier Java 17:
```powershell
java -version  # Doit afficher Java 17
```

Builder le projet:
```powershell
mvn clean install
```

---

## ▶️ Démarrage

### Ordre de lancement:

**1. Ollama** (doit tourner en arrière-plan)
```powershell
ollama serve
```

**2. Service Python RAG**
```powershell
cd python-retrieval-service
python app.py
```
✅ Service prêt sur http://localhost:8000

**3. Application Spring Boot**
```powershell
mvn spring-boot:run
```
✅ API prête sur http://localhost:8080

---

## 🧪 Tests

### Test simple avec Postman:

```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "question": "Je me sens très anxieux, que puis-je faire maintenant ?"
}
```

**Réponse attendue:**
```json
{
  "answer": "Je comprends que vous vous sentiez anxieux... [réponse empathique avec techniques concrètes]",
  "sources": [
    "anxiety guide fr.pdf",
    "The-Anxiety-Skills-Workbook.pdf",
    ...
  ]
}
```

### Test de détection de crise:

```json
{
  "question": "Je veux me suicider"
}
```

**Réponse:**
```json
{
  "answer": "⚠️ Si vous êtes en danger ou envisagez de vous faire du mal, contactez immédiatement les services d'urgence (15 en France, 112 en Europe) ou une ligne d'écoute comme SOS Amitié (09 72 39 40 50). Votre vie est précieuse et il existe des personnes prêtes à vous aider.",
  "sources": []
}
```

---

## 🔧 Configuration

### `application.yml`
```yaml
app:
  ollama:
    model: llama3.1
    endpoint: http://localhost:11434
  retrieval:
    url: http://localhost:8000/retrieve
```

### Mots-clés de crise:
- suicide, suicider
- me tuer, me faire du mal
- je veux mourir, envie de mourir
- mettre fin, en finir

---

## 📊 Flux RAG Complet

1. **Question utilisateur** → Spring Boot Controller
2. **Détection de crise** → Si mots-clés détectés, réponse immédiate
3. **Recherche vectorielle** → Python FAISS trouve 5 chunks pertinents
4. **Construction contexte** → Extraits des PDFs formatés
5. **Génération LLM** → Ollama Llama 3.1 avec contexte
6. **Réponse structurée** → 4 phrases empathiques + sources

---

## 🎯 Avantages

✅ **100% Gratuit** - Ollama local, pas de frais API  
✅ **Privé** - Données restent sur votre machine  
✅ **Rapide** - Pas de latence réseau  
✅ **Qualité** - Llama 3.1 8B performant  
✅ **Documenté** - Réponses basées sur 13 PDFs thérapeutiques  
✅ **Sécurisé** - Détection automatique de situations de crise  

---

## 🛠️ Technologies

- **Backend**: Spring Boot 3.2.0, Java 17, WebFlux
- **LLM**: Ollama + Llama 3.1 (8B Instruct)
- **RAG**: Python Flask, LangChain, FAISS, Sentence Transformers
- **PDF**: PyPDF pour extraction de texte
- **Embeddings**: all-MiniLM-L6-v2 (multilingual)

---

## 📝 Endpoints

### Spring Boot (8080)

- `POST /api/chat` - Chat principal
- `GET /api/health` - Health check

### Python RAG (8000)

- `POST /retrieve` - Recherche vectorielle
- `GET /health` - Health check
- `POST /reindex` - Ré-indexer les PDFs

---

## 🔍 Dépannage

**Problème: Ollama non trouvé**
```powershell
# Vérifier installation
ollama --version

# Démarrer le service
ollama serve
```

**Problème: Port 8080 occupé**
```powershell
# Trouver le processus
netstat -ano | findstr :8080

# Tuer le processus
taskkill /F /PID <PID>
```

**Problème: PDFs non indexés**
```powershell
cd python-retrieval-service
python pdf_indexer.py
```

---

## 📖 Documentation Additionnelle

- [Ollama Documentation](https://github.com/ollama/ollama)
- [LangChain](https://python.langchain.com/)
- [FAISS](https://github.com/facebookresearch/faiss)

---

## 👤 Auteur

Projet de chatbot thérapeutique RAG avec documents réels

## 📄 License

MIT
