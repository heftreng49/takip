# Instagram Unfollowers 📊

Instagram'da sizi takip etmeyenleri keşfedin. ZIP veya JSON dosyası yükleyin, analiz edin, ve takip etmeyenlerin profillerine tek tıkla gidin.

## Özellikler

- 📦 Instagram ZIP dosyası veya ayrı JSON dosyaları yükleme
- 👁️ Sizi takip etmeyenleri görme
- 🔍 Kullanıcı arama
- 📊 İstatistik kartları (takipçi/takip edilen sayısı)
- 🔗 Tek tıkla Instagram profiline gitme
- 🌙 Karanlık tema

## Kurulum

### Gereksinimler

- Android Studio Ladybug (2024.2+)
- JDK 17+
- Android SDK 35

### Projeyi Çalıştırma

```bash
git clone https://github.com/KULLANICI_ADI/instagram-unfollowers.git
cd instagram-unfollowers
./gradlew assembleDebug
```

APK konumu: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions ile Build

Her `main` veya `develop` push'unda otomatik olarak debug APK derlenir.

**Release oluşturmak için:**

```bash
git tag v1.0.0
git push origin v1.0.0
```

Bu işlem otomatik olarak GitHub Release oluşturur ve APK'yı ekler.

## Instagram Verilerini İndirme

1. Instagram → **Profil** → **☰ Menü** → **Hesabınız**
2. **"Bilgilerinizi İndirin"** seçeneğine tıklayın
3. **"Bağlantılar"** kategorisini seçin
4. Format olarak **JSON** seçin
5. ZIP dosyasını indirin (birkaç dakika sürebilir)
6. ZIP dosyasını uygulamaya yükleyin

## Proje Yapısı

```
app/
└── src/main/
    ├── java/com/instagram/unfollowers/
    │   ├── data/
    │   │   ├── model/        # Veri modelleri
    │   │   └── parser/       # JSON ayrıştırıcı
    │   ├── ui/
    │   │   ├── components/   # Ortak UI bileşenleri
    │   │   ├── screens/      # HomeScreen, ResultsScreen
    │   │   └── theme/        # Renkler, tipografi
    │   ├── viewmodel/        # MainViewModel
    │   └── MainActivity.kt
    └── AndroidManifest.xml
```

## İzinler

- `INTERNET` — Takipçi profillerini Instagram'da açmak için

## Gizlilik

Tüm veriler yalnızca cihazınızda işlenir. Hiçbir veri internete gönderilmez.

## Lisans

MIT License
