package com.example.rotamate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rotamate.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // View Binding sınıf adı, XML dosyanızın adı activity_register.xml ise ActivityRegisterBinding olmalıdır.
    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kayıt Ol butonuna tıklandığında hesap oluşturma fonksiyonunu çağır
        binding.btnRegister.setOnClickListener {
            createAccount()
        }

        // Giriş Yap metnine tıklandığında LoginActivity'ye geç
        binding.tvGoLogin.setOnClickListener {
            // Intent ile LoginActivity'yi başlat
            startActivity(Intent(this, LoginActivity::class.java))
            // Mevcut Activity'yi kapatma (geri tuşuyla tekrar Register'a gelme olasılığı için kapatılabilir)
        }
    }

    private fun createAccount() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Alan Doğrulama Kontrolleri
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Tüm alanları doldurmanız gerekmektedir!", Toast.LENGTH_LONG).show()
            return
        }

        // 2. Şifre Uzunluk Kontrolü (Firebase min 6 karakter ister)
        if (password.length < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır.", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Firebase Authentication Kayıt İşlemi
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user!!.uid // Yeni kullanıcının UID'si alındı

                // Kullanıcının adını Firestore'a kaydetmek için hazırlanan veri
                val userData = hashMapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email,
                    "role" to "user",
                    "status" to "active"
                )

                // 4. Firestore'a Kullanıcı Bilgilerini Kaydetme
                db.collection("users")
                    .document(userId) // UID ile benzersiz bir belge oluştur
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Kayıt Başarılı! Giriş ekranına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show()

                        // Başarılı kayıt sonrası Login ekranına git
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish() // Register ekranını Activity yığınından kaldır
                    }
                    .addOnFailureListener { firestoreError ->
                        Toast.makeText(this, "Veritabanı (Firestore) hatası: ${firestoreError.message}", Toast.LENGTH_LONG).show()
                        // Firestore kaydı başarısız olursa, Auth kaydını geri alma mekanizması eklenebilir.
                    }
            }
            .addOnFailureListener { authError ->
                // Authentication hatası (örneğin: e-posta daha önce kullanılmışsa, geçersiz e-posta formatı)
                Toast.makeText(this, "Kayıt başarısız: ${authError.message}", Toast.LENGTH_LONG).show()
            }
    }
}