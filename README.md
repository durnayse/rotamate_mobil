# 🌍 RotaMate – Akıllı Tatil Öneri Uygulaması

RotaMate, kullanıcıların bütçe, iklim, aktivite tercihi ve uçuş süresi gibi kriterlerine göre **en uygun tatil ülkelerini** öneren Android tabanlı bir mobil uygulamadır.  
Uygulama Firebase altyapısı kullanır ve **admin / kullanıcı** rolleri ile çalışır.

## 🚀 Özellikler

### 👤 Kullanıcı Tarafı
- Firebase Authentication ile giriş / kayıt
- Anket sistemi ile kişisel tercihler:
  - Bütçe seviyesi
  - İklim tercihi
  - Aktivite türü
  - Maksimum uçuş süresi
- En uygun ülke + ilk 3 alternatif öneri
- Ülke detay ekranı
- Favorilere ekleme / çıkarma
- Favorilerde arama ve swipe ile silme
- Haritada ülkeyi görüntüleme

### 👑 Admin Paneli
- Admin rolü ile özel giriş
- Firestore’daki **ülkeleri listeleme**
- Ülke ekleme / silme
- Kullanıcıları görüntüleme
- Kart tabanlı modern arayüz (Material Design)

## 🛠️ Kullanılan Teknolojiler

- **Kotlin**
- **Android SDK**
- **Firebase Authentication**
- **Cloud Firestore**
- **RecyclerView**
- **Material Design**
- **Retrofit (Country API)**
- **Glide (Resim yükleme)**

## 🧩 Mimari Yapı

- **Activities**
  - LoginActivity
  - RegisterActivity
  - SurveyActivity
  - ResultActivity
  - CountryDetailActivity
  - FavoritesActivity
  - UserHomeActivity
  - AdminHomeActivity

- **Model Sınıfları**
  - User
  - Country
  - FavoriteCountry

- **Adapterlar**
  - FavoritesAdapter
  - AdminCountryAdapter
  - AdminUsersAdapter

## 🗂️ Firestore Yapısı

```text
users
 └── {userId}
      ├── email
      ├── role (user / admin)
      ├── status (active / passive)
      └── favorites
           └── {countryName}

countries
 └── {countryId}
      ├── name
      ├── apiName
      ├── budgetLevel
      ├── climate
      ├── activityType
      ├── flightTime
      ├── imageUrl
      └── description

