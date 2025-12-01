"""
Python Retrieval Microservice with Real PDF RAG
Uses ChromaDB and sentence transformers for semantic search
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import logging
from pdf_indexer import TherapeuticPDFIndexer

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialiser l'indexeur de PDFs
PDF_DIRECTORY = "../for stres AI"
indexer = None

def initialize_indexer():
    """Initialise l'indexeur au démarrage"""
    global indexer
    try:
        logger.info("🚀 Initialisation du système RAG...")
        indexer = TherapeuticPDFIndexer(PDF_DIRECTORY)
        indexer.load_existing_index()
        logger.info("✅ Système RAG prêt!")
    except Exception as e:
        logger.error(f"❌ Erreur d'initialisation: {e}")
        raise

@app.route('/retrieve', methods=['POST'])
def retrieve():
    """
    Endpoint de récupération de chunks pertinents depuis les PDFs
    Body: {"query": "comment gérer l'anxiété", "top_k": 5}
    """
    try:
        data = request.get_json()
        query = data.get('query', '')
        top_k = data.get('top_k', 5)
        
        logger.info(f"📥 Recherche: '{query}' (top_k={top_k})")
        
        if not query:
            return jsonify({"error": "Query is required"}), 400
        
        if indexer is None:
            return jsonify({"error": "Indexer not initialized"}), 500
        
        # Recherche vectorielle dans les PDFs
        results = indexer.search(query, k=top_k)
        
        # Formater comme avant pour compatibilité Java
        formatted_results = []
        for result in results:
            formatted_results.append({
                "text": result['text'],
                "source": result['source_file'],
                "chunk_id": hash(result['text']) % 10000  # ID unique
            })
        
        logger.info(f"📤 Retour de {len(formatted_results)} chunks pertinents")
        return jsonify({"chunks": formatted_results})
    
    except Exception as e:
        logger.error(f"❌ Erreur: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "healthy", 
        "service": "rag-retrieval-service",
        "indexed": indexer is not None
    })

@app.route('/reindex', methods=['POST'])
def reindex():
    """Force la ré-indexation des PDFs"""
    try:
        global indexer
        logger.info("🔄 Ré-indexation des PDFs...")
        indexer = TherapeuticPDFIndexer(PDF_DIRECTORY)
        indexer.index_documents()
        logger.info("✅ Ré-indexation terminée!")
        return jsonify({"status": "reindexed"})
    except Exception as e:
        logger.error(f"❌ Erreur de ré-indexation: {e}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print("=" * 60)
    print("🚀 RAG Retrieval Service with Real PDFs")
    print("=" * 60)
    print("📍 Running on: http://localhost:8000")
    print("📋 Endpoints:")
    print("   - POST /retrieve  (Search in therapeutic PDFs)")
    print("   - GET  /health    (Health check)")
    print("   - POST /reindex   (Reindex all PDFs)")
    print("=" * 60)
    
    # Initialiser l'indexeur au démarrage
    initialize_indexer()
    
    app.run(host='0.0.0.0', port=8000, debug=True)
