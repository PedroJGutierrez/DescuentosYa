import requests
from bs4 import BeautifulSoup
from google.cloud import firestore
import os

# Configurar Firestore
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "serviceAccount.json"
db = firestore.Client()
collection_name = "bbva_benefits"

# Webscraping de beneficios individuales
def scrape_benefits():
    url = "https://www.bbva.com.ar/beneficios/beneficios"
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    }

    response = requests.get(url, headers=headers)

    if response.status_code != 200:
        raise Exception(f"Error al obtener la página: {response.status_code}")

    soup = BeautifulSoup(response.text, "html.parser")
    benefits = soup.select("div.benefits__item")

    if not benefits:
        raise Exception("No se encontraron beneficios en la página.")

    extracted = []

    for benefit in benefits:
        title = benefit.select_one(".benefits__title")
        category = benefit.select_one(".benefits__category")

        benefit_data = {
            "title": title.text.strip() if title else "Sin título",
            "category": category.text.strip() if category else "Sin categoría"
        }

        extracted.append(benefit_data)

    return extracted

# Guardar en Firestore cada beneficio por separado
def save_benefits_to_firestore(benefits):
    for benefit in benefits:
        doc_ref = db.collection(collection_name).document()
        doc_ref.set(benefit)
        print(f"Beneficio guardado: {benefit['title']} (ID: {doc_ref.id})")

if __name__ == "__main__":
    try:
        benefits = scrape_benefits()
        save_benefits_to_firestore(benefits)
    except Exception as e:
        print(f"Error: {e}")

