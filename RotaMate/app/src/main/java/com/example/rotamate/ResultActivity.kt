package com.example.rotamate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // =======================
        // ANA ÜLKE
        // =======================
        val countryName = intent.getStringExtra("countryName") ?: "Bilinmiyor"
        val apiName = intent.getStringExtra("apiName") ?: ""
        val score = intent.getIntExtra("score", 0)
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        val tvCountryName = findViewById<TextView>(R.id.tvCountryName)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val imgCountry = findViewById<ImageView>(R.id.imgCountry)
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)

        tvCountryName.text = countryName
        tvScore.text = "Skor: $score"
        tvDescription.text = description

        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).into(imgCountry)
        }

        // Ana ülke → API detay
        imgCountry.setOnClickListener {
            openApiDetail(apiName)
        }

        // =======================
        // TOP 3 ÜLKELER
        // =======================
        setupTopCountry(
            name = intent.getStringExtra("top1Name"),
            apiName = intent.getStringExtra("top1ApiName"),
            image = intent.getStringExtra("top1Image"),
            containerId = R.id.top1Container,
            textId = R.id.tvTop1,
            imageId = R.id.imgTop1
        )

        setupTopCountry(
            name = intent.getStringExtra("top2Name"),
            apiName = intent.getStringExtra("top2ApiName"),
            image = intent.getStringExtra("top2Image"),
            containerId = R.id.top2Container,
            textId = R.id.tvTop2,
            imageId = R.id.imgTop2
        )

        setupTopCountry(
            name = intent.getStringExtra("top3Name"),
            apiName = intent.getStringExtra("top3ApiName"),
            image = intent.getStringExtra("top3Image"),
            containerId = R.id.top3Container,
            textId = R.id.tvTop3,
            imageId = R.id.imgTop3
        )

        // Ana sayfaya dön
        btnGoHome.setOnClickListener {
            startActivity(
                Intent(this, UserHomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
    }

    // =======================
    // YARDIMCI FONKSİYONLAR
    // =======================

    private fun setupTopCountry(
        name: String?,
        apiName: String?,
        image: String?,
        containerId: Int,
        textId: Int,
        imageId: Int
    ) {
        val container = findViewById<View>(containerId)
        val tv = findViewById<TextView>(textId)
        val img = findViewById<ImageView>(imageId)

        if (name.isNullOrEmpty()) {
            container.visibility = View.GONE
            return
        }

        tv.text = name
        Glide.with(this).load(image).into(img)

        container.setOnClickListener {
            openApiDetail(apiName ?: name)
        }
    }

    private fun openApiDetail(apiName: String?) {
        if (apiName.isNullOrEmpty()) return

        val intent = Intent(this, CountryDetailActivity::class.java)
        intent.putExtra("countryName", apiName)
        startActivity(intent)
    }
}
