## 🧪 Local Development

### Prerequisites

- [Android Studio](https://developer.android.com/studio)
- [Docker](https://www.docker.com/)

## 📱 Run Android App

1. Open `ProductList/app` in **Android Studio**
2. Sync Gradle and run the app on emulator/device
3. Test product listing, create/edit/delete features, and image preview

> ✅ **Emulator Note:**  
> Android emulator does not recognize `localhost` as your machine’s loopback.  
> You must use `http://10.0.2.2:8080` in your app for backend calls.

---

## 📶 Running on Real Device

To test your app on a real device (on the same Wi-Fi as your computer):

1. Find your computer's **local IP address** (e.g., `192.168.0.126`)
2. Update your backend `BASE_URL` in the app:
   ```kotlin
   const val BASE_URL = "http://192.168.0.126:8080"
3.	In res/xml/network_security_config.xml, make sure this IP is allowed:
   <domain includeSubdomains="false">192.168.0.126</domain>

---

## ▶️ Run Backend
•	Ktor API: http://localhost:8080
•	PostgreSQL DB: port 5432

```bash
git clone https://github.com/tanzhwei/productlist.git
cd productlist
docker-compose up --build