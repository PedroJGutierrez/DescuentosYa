from bs4 import BeautifulSoup
import uuid
import firebase_admin
from firebase_admin import credentials, firestore
import re

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
    """Configura Firebase - opcional si no tienes las credenciales"""
    try:
        cred = credentials.Certificate(path)
        firebase_admin.initialize_app(cred)
        return firestore.client()
    except Exception as e:
        print(f"⚠️ No se pudo conectar a Firebase: {e}")
        return None

def parse_html(file_path="modo_final_rendered.html"):
    """
    Parse del HTML de MODO usando múltiples estrategias para encontrar las cards
    """
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

    # Estrategia 1: Buscar por clases que contengan "card" (case insensitive)
    possible_selectors = [
        # Selectores específicos de MODO
        "div[class*='Card']",
        "div[class*='card']",
        "[class*='promo']",
        "[class*='benefit']",
        "[class*='offer']",
        # Selectores genéricos
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
        # Estrategia de respaldo: buscar elementos que contengan texto común de promociones
        all_divs = soup.find_all(['div', 'article', 'section'])
        cards = []
        for div in all_divs:
            text = div.get_text(strip=True).lower()
            if any(keyword in text for keyword in ['descuento', '%', 'promo', 'oferta', 'beneficio']):
                cards.append(div)
        print(f"🔍 Encontradas {len(cards)} cards por contenido de texto")

    beneficios = []

    for i, card in enumerate(cards[:50]):  # Limitamos a 50 para evitar spam
        try:
            # Extraer texto de la card
            card_text = card.get_text(separator=' ', strip=True)

            if len(card_text) < 10:  # Filtrar cards muy pequeñas
                continue

            # Estrategias para extraer título y descripción
            title = ""
            description = ""

            # Buscar elementos de título comunes
            title_selectors = ['h1', 'h2', 'h3', 'h4', 'h5', '[class*="title"]', '[class*="Title"]', 'strong']
            for selector in title_selectors:
                title_elem = card.select_one(selector)
                if title_elem:
                    title = title_elem.get_text(strip=True)
                    if len(title) > 5:  # Título debe tener al menos 5 caracteres
                        break

            # Buscar elementos de descripción
            desc_selectors = ['p', '[class*="description"]', '[class*="Description"]', 'span']
            for selector in desc_selectors:
                desc_elems = card.select(selector)
                for desc_elem in desc_elems:
                    desc_text = desc_elem.get_text(strip=True)
                    if len(desc_text) > len(description) and desc_text != title:
                        description = desc_text

            # Si no encontramos título específico, usar las primeras palabras
            if not title and len(card_text) > 0:
                words = card_text.split()
                title = ' '.join(words[:8])  # Primeras 8 palabras como título
                description = ' '.join(words[8:]) if len(words) > 8 else card_text

            # Si no hay descripción, usar todo el texto
            if not description:
                description = card_text

            # Buscar información adicional
            banco = ""
            tope = ""

            # Buscar información de banco
            banco_keywords = ['banco', 'santander', 'bbva', 'galicia', 'nación', 'provincia', 'macro', 'icbc']
            for keyword in banco_keywords:
                if keyword in card_text.lower():
                    banco = keyword.title()
                    break

            # Buscar topes o límites
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

            # Solo agregar si tenemos contenido mínimo
            if title and len(title.strip()) > 3:
                beneficio = {
                    "id": str(uuid.uuid4()),
                    "title": title[:200],  # Limitar longitud
                    "description": description[:500],  # Limitar longitud
                    "conditions": description[:300],
                    "category": categorizar(title + " " + description),
                    "days": [],
                    "image": "",
                    "banco": banco,
                    "tope": tope,
                    "raw_text": card_text[:1000]  # Para debug
                }
                beneficios.append(beneficio)

                # Debug: mostrar los primeros 3 beneficios encontrados
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

def save_to_firestore(data):
    """Guarda los datos en Firestore - opcional"""
    db = setup_firebase()
    if not db:
        print("⚠️ No se pudo conectar a Firestore, saltando guardado...")
        return

    try:
        for b in data:
            # Remover campo raw_text antes de guardar
            if 'raw_text' in b:
                del b['raw_text']
            db.collection("benefits_modo").document(b["id"]).set(b)
        print(f"✅ Subidos {len(data)} beneficios a Firestore → benefits_modo")
    except Exception as e:
        print(f"❌ Error guardando en Firestore: {e}")

def save_to_json(data, filename="beneficios_modo.json"):
    """Guarda los datos en un archivo JSON local como respaldo"""
    import json
    try:
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print(f"💾 Beneficios guardados en {filename}")
    except Exception as e:
        print(f"❌ Error guardando JSON: {e}")

def main():
    print("🚀 Iniciando extracción de beneficios MODO...")

    # Intentar con el nombre de archivo que mencionaste
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

    print(f"\n📊 Resumen por categorías:")
    from collections import Counter
    categories = Counter([b['category'] for b in beneficios])
    for cat, count in categories.items():
        print(f"   {cat}: {count}")

    # Guardar en JSON local
    save_to_json(beneficios)

    # Intentar guardar en Firestore (opcional)
    save_to_firestore(beneficios)

    print(f"\n🎉 Proceso completado! Se procesaron {len(beneficios)} beneficios.")

if __name__ == "__main__":
    main()