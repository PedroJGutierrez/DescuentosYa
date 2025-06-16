import requests
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import firebase_admin
from firebase_admin import credentials, firestore
import json
import time
import uuid
from typing import Dict, List

def setup_selenium() -> webdriver.Chrome:
    """Set up Selenium WebDriver with Chrome."""
    chrome_options = Options()
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)
    return driver

def setup_firebase(cred_path: str) -> firestore.client:
    """Initialize Firebase Firestore client."""
    try:
        cred = credentials.Certificate(cred_path)
        firebase_admin.initialize_app(cred)
        return firestore.client()
    except Exception as e:
        print(f"❌ Error initializing Firebase: {e}")
        raise

def scrape_banco_ciudad(page_number: int, driver: webdriver.Chrome = None, max_retries: int = 3, use_manual_html: bool = False) -> tuple[List[Dict], bool]:
    """Scrape benefits from Banco Ciudad website or local HTML for a given page."""
    benefits = []
    close_driver = False
    url = f"https://www.bancociudad.com.ar/beneficios/promo?pagina={page_number}"
    html_file = f'manual_page_{page_number}.html'
    
    if use_manual_html:
        try:
            with open(html_file, 'r', encoding='utf-8') as f:
                soup = BeautifulSoup(f, 'html.parser')
            print(f"📄 Parsing manual HTML: {html_file}")
        except FileNotFoundError:
            print(f"❌ File {html_file} not found")
            return [], page_number < 30
    else:
        if driver is None:
            driver = setup_selenium()
            close_driver = True
        
        for attempt in range(1, max_retries + 1):
            try:
                print(f"🔍 Navigating to {url} (Attempt {attempt}/{max_retries})")
                driver.get(url)
                time.sleep(10)  
                
                
                page_text = driver.page_source.lower()
                if "captcha" in page_text or "bot" in page_text:
                    print(f"❌ CAPTCHA detected on page {page_number}.")
                    print("Please solve the CAPTCHA in the browser window, then press Enter in this console to continue.")
                    input()  
                    time.sleep(5)  

                soup = BeautifulSoup(driver.page_source, 'html.parser')

                with open(f'page_source_page_{page_number}_attempt_{attempt}.html', 'w', encoding='utf-8') as f:
                    f.write(soup.prettify())
                print(f"Page source saved to page_source_page_{page_number}_attempt_{attempt}.html")

                selectors = [
                    'div.col-12.col-md-6.col-lg-4.mb-4',
                    'div.card',
                    'div.promo',
                    'app-card-beneficio'
                ]
                promo_sections = []
                for selector in selectors:
                    sections = soup.select(selector)
                    if sections:
                        print(f"✅ Found {len(sections)} sections with selector: {selector}")
                        promo_sections = sections
                        break
                
                if not promo_sections:
                    print(f"⚠️ No benefits found on page {page_number}. Tried selectors: {selectors}")
                    sample_html = soup.select('body')[0].prettify()[:1000] if soup.select('body') else "No body found"
                    with open(f'page_source_page_{page_number}_sample.html', 'w', encoding='utf-8') as f:
                        f.write(sample_html)
                    print(f"Sample HTML saved to page_source_page_{page_number}_sample.html")
                    if attempt < max_retries:
                        print(f"Retrying page {page_number}...")
                        time.sleep(5)
                        continue
                    return [], page_number < 30
                
                for promo in promo_sections:
                    benefit = {}
                    benefit['id'] = str(uuid.uuid4())  

                    title_elem = promo.select_one('h5, h4, h3, .card-title, .promo-title')
                    benefit['title'] = title_elem.get_text(strip=True) if title_elem else 'No title'

                    text_elems = promo.select('div.text span, p, .card-text, .description')
                    description_parts = [elem.get_text(strip=True) for elem in text_elems if elem.get_text(strip=True)]
                    benefit['description'] = ' | '.join(description_parts) if description_parts else ''

                    conditions_elem = promo.select_one('.terms, .conditions, small')
                    benefit['conditions'] = conditions_elem.get_text(strip=True) if conditions_elem else benefit['description']

                    day_elems = promo.select('div.days span.day')
                    benefit['days'] = [day.get_text(strip=True) for day in day_elems] if day_elems else []

                    image_elem = promo.select_one('img')
                    benefit['image'] = image_elem['src'] if image_elem and image_elem.has_attr('src') else ''

                    category = 'Other'
                    title_lower = benefit['title'].lower()
                    if 'viaje' in title_lower or 'turismo' in title_lower:
                        category = 'Travel'
                    elif 'reintegro' in title_lower or 'cuotas' in title_lower or 'coto' in title_lower or 'shopgallery' in title_lower:
                        category = 'Shopping'
                    elif 'comida' in title_lower or 'restaurante' in title_lower or 'gastronomía' in title_lower:
                        category = 'Dining'
                    elif 'cine' in title_lower:
                        category = 'Entertainment'
                    benefit['category'] = category

                    print(f"Extracted benefit: {benefit}")

                    if benefit['title'] != 'No title':
                        benefits.append(benefit)
                
                return benefits, page_number < 30  
            
            except Exception as e:
                print(f"❌ Error on page {page_number}, attempt {attempt}: {e}")
                if attempt < max_retries:
                    print(f"Retrying page {page_number}...")
                    time.sleep(5)
                else:
                    return [], page_number < 30
            finally:
                if close_driver:
                    driver.quit()

    for selector in [
        'div.col-12.col-md-6.col-lg-4.mb-4',
        'div.card',
        'div.promo',
        'app-card-beneficio'
    ]:
        promo_sections = soup.select(selector)
        if promo_sections:
            print(f"✅ Found {len(promo_sections)} sections with selector: {selector}")
            break
    
    if not promo_sections:
        print(f"⚠️ No benefits found on page {page_number}. Tried selectors: {selectors}")
        return [], page_number < 30
    
    for promo in promo_sections:
        benefit = {}
        benefit['id'] = str(uuid.uuid4())
        title_elem = promo.select_one('h5, h4, h3, .card-title, .promo-title')
        benefit['title'] = title_elem.get_text(strip=True) if title_elem else 'No title'
        text_elems = promo.select('div.text span, p, .card-text, .description')
        description_parts = [elem.get_text(strip=True) for elem in text_elems if elem.get_text(strip=True)]
        benefit['description'] = ' | '.join(description_parts) if description_parts else ''
        conditions_elem = promo.select_one('.terms, .conditions, small')
        benefit['conditions'] = conditions_elem.get_text(strip=True) if conditions_elem else benefit['description']
        day_elems = promo.select('div.days span.day')
        benefit['days'] = [day.get_text(strip=True) for day in day_elems] if day_elems else []
        image_elem = promo.select_one('img')
        benefit['image'] = image_elem['src'] if image_elem and image_elem.has_attr('src') else ''
        category = 'Other'
        title_lower = benefit['title'].lower()
        if 'viaje' in title_lower or 'turismo' in title_lower:
            category = 'Travel'
        elif 'reintegro' in title_lower or 'cuotas' in title_lower or 'coto' in title_lower or 'shopgallery' in title_lower:
            category = 'Shopping'
        elif 'comida' in title_lower or 'restaurante' in title_lower or 'gastronomía' in title_lower:
            category = 'Dining'
        elif 'cine' in title_lower:
            category = 'Entertainment'
        benefit['category'] = category
        print(f"Extracted benefit: {benefit}")
        if benefit['title'] != 'No title':
            benefits.append(benefit)
    
    return benefits, page_number < 30

def save_to_firebase(db: firestore.client, benefits: List[Dict]) -> int:
    """Save unique benefits to Firestore and return count of saved items."""
    try:
        unique_benefits = []
        seen = set()
        for benefit in benefits:
            unique_key = (benefit['title'], benefit['description'])
            if unique_key not in seen:
                seen.add(unique_key)
                unique_benefits.append(benefit)
        
        for benefit in unique_benefits:
            db.collection('benefits').document(benefit['id']).set(benefit)
        print(f"✅ Saved {len(unique_benefits)} unique benefits to Firestore")
        return len(unique_benefits)
    except Exception as e:
        print(f"❌ Error saving to Firebase: {e}")
        return 0

def export_to_json(benefits: List[Dict], filename: str) -> None:
    """Export benefits to a JSON file for backup."""
    try:
        unique_benefits = []
        seen = set()
        for benefit in benefits:
            identifier = (benefit['title'], benefit['description'])
            if identifier not in seen:
                seen.add(identifier)
                unique_benefits.append(benefit)
        
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(unique_benefits, f, ensure_ascii=False, indent=2)
        print(f"✅ Benefits exported to {filename} ({len(unique_benefits)} unique benefits)")
    except Exception as e:
        print(f"❌ Error exporting to JSON: {e}")

def main():
    """Main function to run the scraper."""
    all_benefits = []
    MAX_PAGES = 30  
    driver = setup_selenium()
    db = None
    USE_MANUAL_HTML = False  
    
    try:
        db = setup_firebase('serviceAccount.json')
        
        for page in range(1, MAX_PAGES + 1):
            print(f"🔍 Scraping page {page}...")
            benefits, continue_scraping = scrape_banco_ciudad(page, driver, use_manual_html=USE_MANUAL_HTML)
            all_benefits.extend(benefits)
            if not continue_scraping:
                break
        
        if all_benefits:
            saved_count = save_to_firebase(db, all_benefits)
            export_to_json(all_benefits, 'banco_ciudad_benefits.json')
            if saved_count == 0:
                print("❌ Failed to save benefits to Firebase. Check Firebase configuration.")
        else:
            print("❌ No benefits found across all pages. Check selectors, CAPTCHA resolution, or page content.")
            print("Inspect page_source_page_X_attempt_Y.html files for HTML structure.")
            
    except Exception as e:
        print(f"❌ Main error: {e}")
    finally:
        if db is not None:
            try:
                firebase_admin.delete_app(firebase_admin.get_app())
            except:
                pass
        if driver:
            driver.quit()

if __name__ == "__main__":
    main()
