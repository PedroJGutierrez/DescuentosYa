def scrape_modo(driver):
    driver.get("https://www.modo.com.ar/promos")

    try:
        WebDriverWait(driver, 20).until(
            EC.presence_of_all_elements_located((By.CSS_SELECTOR, "div[class^='promoCard_card']"))
        )
        print("🟢 Cards de beneficios detectadas.")
    except:
        print("❌ No se encontraron cards de beneficios.")
        return []

    cards = driver.find_elements(By.CSS_SELECTOR, "div[class^='promoCard_card']")

    beneficios = []
    for card in cards:
        try:
            title = card.find_element(By.TAG_NAME, "h3").text.strip()
            desc = card.find_element(By.TAG_NAME, "p").text.strip()

            # Extra: leemos las etiquetas del pie de la card
            etiquetas = card.find_elements(By.CSS_SELECTOR, "div[class*=promoCard_chip]")
            etiquetas_texto = [e.text.strip() for e in etiquetas if e.text.strip()]

            tope = ""
            banco = ""
            for texto in etiquetas_texto:
                if "tope" in texto.lower():
                    tope = texto
                else:
                    banco = texto  # asumimos que el otro es el banco

            # Extra: imagen si existe
            try:
                img_tag = card.find_element(By.TAG_NAME, "img")
                image_url = img_tag.get_attribute("src")
            except:
                image_url = ""

            beneficios.append({
                "id": str(uuid.uuid4()),
                "title": title,
                "description": desc,
                "conditions": desc,
                "category": categorizar(title + desc),
                "days": [],
                "image": image_url,
                "banco": banco,
                "tope": tope
            })
        except Exception as e:
            print(f"⚠️ Error en una card: {e}")
            continue

    return beneficios