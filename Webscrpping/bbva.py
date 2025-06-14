import requests
from bs4 import BeautifulSoup
from google.cloud import firestore
import os

# Set up Firestore credentials
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "serviceAccount.json"
db = firestore.Client()
collection_name = "scraped_html"

# Step 1: Web scrape
def scrape_html(url, css_selector):
    response = requests.get(url)
    if response.status_code != 200:
        raise Exception(f"Failed to fetch page. Status code: {response.status_code}")
    
    soup = BeautifulSoup(response.text, "html.parser")
    element = soup.select_one(css_selector)
    
    if not element:
        raise Exception("No element found with the given CSS selector")
    
    return element.prettify()

# Step 2: Save to Firestore
def save_to_firestore(content, doc_id=None):
    doc_ref = db.collection(collection_name).document(doc_id) if doc_id else db.collection(collection_name).document()
    doc_ref.set({"html": content})
    print(f"Document saved with ID: {doc_ref.id}")

# === Example Usage ===
if __name__ == "__main__":
    url = "https://example.com"
    css_selector = "div#main"  # Change to what you want to extract
    
    try:
        html_content = scrape_html(url, css_selector)
        save_to_firestore(html_content)
    except Exception as e:
        print(f"Error: {e}")
