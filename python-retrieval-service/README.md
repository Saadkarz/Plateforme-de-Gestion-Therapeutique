# Python Retrieval Microservice

Simple Flask-based retrieval service for testing the RAG pipeline.

## Installation

1. Create a virtual environment (optional but recommended):
```bash
python -m venv venv
```

2. Activate the virtual environment:

**Windows PowerShell:**
```powershell
.\venv\Scripts\Activate.ps1
```

**Windows CMD:**
```cmd
venv\Scripts\activate.bat
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

## Run

```bash
python app.py
```

The service will start on `http://localhost:8000`

## Test

### Health Check
```bash
curl http://localhost:8000/health
```

### Retrieve Documents
```bash
curl -X POST http://localhost:8000/retrieve \
  -H "Content-Type: application/json" \
  -d '{"query":"anxiété","top_k":3}'
```

## Mock Documents

The service provides 5 simulated therapeutic document chunks about:
- Deep breathing techniques
- Grounding techniques
- Physical exercise benefits
- Gratitude journaling
- Mindfulness meditation

These are returned for any query to simulate a real RAG retrieval system.
