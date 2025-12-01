"""
PDF Indexer pour documents thérapeutiques
Charge et indexe tous les PDFs du dossier avec FAISS
"""
import os
from pathlib import Path
from typing import List, Dict
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import PyPDFLoader
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS
import logging
import pickle

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class TherapeuticPDFIndexer:
    def __init__(self, pdf_directory: str, persist_directory: str = "./faiss_index"):
        self.pdf_directory = Path(pdf_directory)
        self.persist_directory = Path(persist_directory)
        self.embeddings = HuggingFaceEmbeddings(
            model_name="sentence-transformers/all-MiniLM-L6-v2"
        )
        self.vector_store = None
        
    def load_pdfs(self) -> List:
        """Charge tous les PDFs du répertoire"""
        documents = []
        pdf_files = list(self.pdf_directory.glob("*.pdf"))
        
        logger.info(f"Trouvé {len(pdf_files)} fichiers PDF")
        
        for pdf_path in pdf_files:
            try:
                logger.info(f"Chargement: {pdf_path.name}")
                loader = PyPDFLoader(str(pdf_path))
                docs = loader.load()
                
                # Ajouter métadonnées
                for doc in docs:
                    doc.metadata['source_file'] = pdf_path.name
                    doc.metadata['doc_type'] = 'therapeutic_guide'
                
                documents.extend(docs)
                logger.info(f"✓ {pdf_path.name}: {len(docs)} pages")
            except Exception as e:
                logger.error(f"✗ Erreur avec {pdf_path.name}: {e}")
        
        logger.info(f"Total: {len(documents)} pages chargées")
        return documents
    
    def split_documents(self, documents: List) -> List:
        """Découpe les documents en chunks"""
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=200,
            length_function=len,
            separators=["\n\n", "\n", " ", ""]
        )
        
        chunks = text_splitter.split_documents(documents)
        logger.info(f"Documents découpés en {len(chunks)} chunks")
        return chunks
    
    def index_documents(self):
        """Indexe tous les PDFs dans ChromaDB"""
        logger.info("🔄 Début de l'indexation...")
        
        # Charger et découper les PDFs
        documents = self.load_pdfs()
        if not documents:
            raise ValueError("Aucun document chargé!")
        
        chunks = self.split_documents(documents)
        
        # Créer le vector store avec FAISS
        logger.info("📊 Création de l'index vectoriel...")
        self.vector_store = FAISS.from_documents(
            documents=chunks,
            embedding=self.embeddings
        )
        
        # Sauvegarder l'index
        self.persist_directory.mkdir(parents=True, exist_ok=True)
        self.vector_store.save_local(str(self.persist_directory))
        
        logger.info("✅ Indexation terminée!")
        return self.vector_store
    
    def load_existing_index(self):
        """Charge un index existant"""
        if not self.persist_directory.exists():
            logger.info("Aucun index existant, création d'un nouveau...")
            return self.index_documents()
        
        logger.info("📂 Chargement de l'index existant...")
        self.vector_store = FAISS.load_local(
            str(self.persist_directory),
            self.embeddings
        )
        logger.info("✅ Index chargé!")
        return self.vector_store
    
    def search(self, query: str, k: int = 5) -> List[Dict]:
        """Recherche dans l'index"""
        if self.vector_store is None:
            raise ValueError("Vector store non initialisé!")
        
        results = self.vector_store.similarity_search_with_score(query, k=k)
        
        formatted_results = []
        for doc, score in results:
            formatted_results.append({
                "text": doc.page_content,
                "source_file": doc.metadata.get('source_file', 'unknown'),
                "page": doc.metadata.get('page', 'N/A'),
                "score": float(score)
            })
        
        return formatted_results


if __name__ == "__main__":
    # Test de l'indexeur
    pdf_dir = "../for stres AI"
    indexer = TherapeuticPDFIndexer(pdf_dir)
    indexer.load_existing_index()
    
    # Test de recherche
    results = indexer.search("techniques de respiration pour l'anxiété", k=3)
    print("\n🔍 Résultats de test:")
    for i, result in enumerate(results, 1):
        print(f"\n{i}. Source: {result['source_file']} (Page {result['page']})")
        print(f"   Score: {result['score']:.4f}")
        print(f"   Texte: {result['text'][:200]}...")
