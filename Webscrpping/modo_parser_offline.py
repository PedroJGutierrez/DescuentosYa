from bs4 import BeautifulSoup
import uuid
import firebase_admin
from firebase_admin import credentials, firestore
import re
import json
from datetime import datetime
from collections import Counter

def categorizar(texto: str) -> str:
    texto = texto.lower()
    if any(word in texto for word in ["comida", "restaurante", "gastronomía", "pizza", "burger", "café", "bar"]):
        return "Gastronomía"
    if any(word in texto for word in ["cine", "teatro", "entretenimiento", "show", "música"]):
        return "Entretenimiento"
    if any(word in texto for word in ["carrefour", "dia", "super", "mercado", "hipermercado"]):
        return "Supermercado"
    if any(word in texto for word in ["ropa", "moda", "indumentaria", "zapatillas", "calzado"]):
        return "Indumentaria"
    if any(word in texto for word in ["hogar", "electro", "muebles", "decoración"]):
        return "Hogar"
    if any(word in texto for word in ["farmacia", "medicamento", "salud"]):
        return "Salud"
    if any(word in texto for word in ["combustible", "ypf", "shell", "axion"]):
        return "Combustible"
    return "Otros"

def setup_firebase(path="serviceAccount.json"):
    try:
        cred = credentials.Certificate(path)
        firebase_admin.initialize_app(cred)
        return firestore.client()
    except Exception as e:
        print(f"⚠️ No se pudo conectar a Firebase: {e}")
        return None

def parse_html(file_path="modo_final_rendered.html"):
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()
            soup = BeautifulSoup(content, "html.parser")
    except FileNotFoundError:
        print(f"❌ No se encontró el archivo {file_path}")
        return []
    except Exception as e:
        print(f"❌ Error leyendo el archivo: {e}")
        return []

    print(f"📄 Archivo HTML cargado, tamaño: {len(content)} caracteres")

    possible_selectors = [
        "div[class*='Card']",
        "div[class*='card']",
        "[class*='promo']",
        "[class*='benefit']",
        "[class*='offer']",
        "div[class*='container'] div[class*='item']",
        "article",
        ".card",
        "[data-testid*='card']",
        "[data-testid*='promo']"
    ]

    cards = []
    for selector in possible_selectors:
        found_cards = soup.select(selector)
        if found_cards:
            print(f"🔍 Encontradas {len(found_cards)} cards con selector: {selector}")
            cards = found_cards
            break

    if not cards:
        print("⚠️ No se encontraron cards con selectores estándar, buscando texto...")
        all_divs = soup.find_all(['div', 'article', 'section'])
        cards = []
        for div in all_divs:
            text = div.get_text(strip=True).lower()
            if any(keyword in text for keyword in ['descuento', '%', 'promo', 'oferta', 'beneficio']):
                cards.append(div)
        print(f"🔍 Encontradas {len(cards)} cards por contenido de texto")

    beneficios = []

    for i, card in enumerate(cards[:50]):
        try:
            card_text = card.get_text(separator=' ', strip=True)
            if len(card_text) < 10:
                continue

            # Mejor detección del título
            title_candidates = card.select('h1, h2, h3, h4, h5, strong, span, div')
            title = ""
            for elem in title_candidates:
                text = elem.get_text(strip=True)
                if len(text) > 4 and not re.search(r"(?i)sin tope|tope.*|macro|banco|promoción", text):
                    title = text
                    break

            # Si no se encontró título útil, tomar primeras palabras del texto
            if not title:
                words = card_text.split()
                title = ' '.join(words[:5]) if len(words) >= 5 else card_text

            description = card_text.replace(title, '').strip()
            if not description or description.lower() == title.lower():
                description = card_text

            if title.lower() in description.lower():
                conditions = description
            else:
                conditions = f"{title}. {description}"

            banco = ""
            tope = ""

            banco_keywords = ['banco', 'santander', 'bbva', 'galicia', 'nación', 'provincia', 'macro', 'icbc']
            for keyword in banco_keywords:
                if keyword in card_text.lower():
                    banco = keyword.title()
                    break

            tope_patterns = [
                r'tope.*?\$?\d+',
                r'hasta.*?\$?\d+',
                r'máximo.*?\$?\d+',
                r'límite.*?\$?\d+'
            ]
            for pattern in tope_patterns:
                match = re.search(pattern, card_text.lower())
                if match:
                    tope = match.group(0)
                    break

            beneficio = {
                "id": str(uuid.uuid4()),
                "title": title[:200],
                "description": description[:500],
                "conditions": conditions[:300],
                "category": categorizar(f"{title} {description}"),
                "days": [],
                "image": "",
                "banco": banco,
                "tope": tope,
                "origen": "modo",
                "timestamp": datetime.utcnow().isoformat(),
                "raw_text": card_text[:1000]
            }
            beneficios.append(beneficio)

            if len(beneficios) <= 3:
                print(f"\n🎯 Beneficio {len(beneficios)}:")
                print(f"   Título: {title[:100]}...")
                print(f"   Descripción: {description[:150]}...")
                print(f"   Categoría: {beneficio['category']}")

        except Exception as e:
            print(f"⚠️ Error procesando card {i}: {e}")
            continue

    print(f"\n✅ Total de beneficios extraídos: {len(beneficios)}")
    return beneficios

def agrupar_beneficios(beneficios):
    agrupados = {}

    for b in beneficios:
        key = b['title'].strip().lower()

        if key not in agrupados:
            agrupados[key] = b
        else:
            existente = agrupados[key]

            # Combinar descripciones si son distintas
            if b['description'] not in existente['description']:
                existente['description'] += f" | {b['description']}"

            # Combinar condiciones si son distintas
            if b['conditions'] not in existente['conditions']:
                existente['conditions'] += f" | {b['conditions']}"

            # Combinar topes si son distintos
            if b['tope'] and b['tope'] not in existente['tope']:
                if existente['tope']:
                    existente['tope'] += f", {b['tope']}"
                else:
                    existente['tope'] = b['tope']

    print(f"🔁 Reducidos {len(beneficios)} beneficios a {len(agrupados)} únicos por título.")
    return list(agrupados.values())

def save_to_firestore(data):
    db = setup_firebase()
    if not db:
        print("⚠️ No se pudo conectar a Firestore, saltando guardado...")
        return

    try:
        docs = db.collection("benefits_modo").stream()
        for doc in docs:
            db.collection("benefits_modo").document(doc.id).delete()
        print("🗑️ Colección 'benefits_modo' borrada correctamente.")

        for b in data:
            if 'raw_text' in b:
                del b['raw_text']
            db.collection("benefits_modo").document(b["id"]).set(b)
        print(f"✅ Subidos {len(data)} beneficios nuevos a Firestore → benefits_modo")
    except Exception as e:
        print(f"❌ Error guardando en Firestore: {e}")

def save_to_json(data, filename="beneficios_modo.json"):
    try:
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print(f"💾 Beneficios guardados en {filename}")
    except Exception as e:
        print(f"❌ Error guardando JSON: {e}")

def main():
    print("🚀 Iniciando extracción de beneficios MODO...")
    file_names = ["modo_final_rendered.html", "paste.txt", "modo.html"]

    beneficios = []
    for file_name in file_names:
        try:
            beneficios = parse_html(file_name)
            if beneficios:
                print(f"✅ Extracción exitosa desde {file_name}")
                break
        except:
            continue

    if not beneficios:
        print("❌ No se pudieron extraer beneficios de ningún archivo")
        return

    beneficios_agrupados = agrupar_beneficios(beneficios)

    print(f"\n📊 Resumen por categorías:")
    categories = Counter([b['category'] for b in beneficios_agrupados])
    for cat, count in categories.items():
        print(f"   {cat}: {count}")

    save_to_json(beneficios_agrupados)
    save_to_firestore(beneficios_agrupados)

    print(f"\n🎉 Proceso completado! Se procesaron {len(beneficios_agrupados)} beneficios.")

if __name__ == "__main__":
    main()