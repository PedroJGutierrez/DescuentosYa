from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from google.cloud import firestore
import os
import time

# FIRESTORE
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "serviceAccount.json"
db = firestore.Client()

# Mapeo de íconos (podés ajustarlo)
ICON_MAP = {
    "Cine": "Movie",
    "Indumentaria": "Checkroom",
    "Deportes": "SportsSoccer",
    "Tecnología": "Devices",
    "Comida": "Fastfood",
    "Supermercado": "ShoppingCart",
    "Sin categoría": "Info"
}

def scrape_benefits():
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')

    from selenium.webdriver.chrome.service import Service

    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    driver.get("https://www.bbva.com.ar/beneficios/beneficios")
    time.sleep(5)  # Esperar que cargue JS (ajustá si es necesario)

    items = driver.find_elements(By.CLASS_NAME, "card-promo")  # detectado por inspección en la web

    benefits = []
    for item in items:
        try:
            descripcion = item.find_element(By.CLASS_NAME, "card-title").text
            detalle = item.find_element(By.CLASS_NAME, "card-subtitle").text
            categoria = "Comida" if "restaurante" in descripcion.lower() else "Sin categoría"
            icon = ICON_MAP.get(categoria, "Info")

            benefits.append({
                "descripcion": f"{descripcion} - {detalle}",
                "disponible": True,
                "icon": icon
            })
        except:
            continue

    driver.quit()
    return benefits

def update_firestore_bbva(benefits):
    doc = db.collection("billeteras").document("BBVA")
    doc.set({"beneficios": benefits})
    print(f"✅ {len(benefits)} beneficios actualizados en billeteras/BBVA.")

if __name__ == "__main__":
    try:
        beneficios = scrape_benefits()
        update_firestore_bbva(beneficios)
    except Exception as e:
        print(f"❌ Error: {e}")
