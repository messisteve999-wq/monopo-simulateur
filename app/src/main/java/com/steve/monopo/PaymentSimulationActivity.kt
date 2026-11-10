package com.steve.monopo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

class PaymentSimulationActivity : Activity() {

    companion object {
        const val EXTRA_PACKAGE = "package"
    }

    private lateinit var listeEcran: ScrollView
    private lateinit var paiementEcran: LinearLayout
    private lateinit var titrePaiement: TextView
    private lateinit var payButton: Button
    private lateinit var retourButton: Button
    private lateinit var progress: ProgressBar
    private lateinit var statut: TextView
    private var articleChoisi: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: ""
        val nomApp = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString()
        } catch (e: Exception) {
            "Achat"
        }
        val articles = AppCatalog.articles(this, pkg)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(24), px(24), px(24), px(24))
            setBackgroundColor(Color.WHITE)
        }

        val liste = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        liste.addView(TextView(this).apply {
            text = "$nomApp (SIMULATION)"
            textSize = 20f
            setTextColor(Color.BLACK)
        })
        if (articles.isEmpty()) {
            liste.addView(TextView(this).apply {
                text = "Aucun article. Ouvre Monopo et synchronise cette application."
                textSize = 15f
                setTextColor(Color.GRAY)
            })
        }
        articles.forEach { a ->
            liste.addView(Button(this).apply {
                text = "${a.nom}\n${formatPrix(a.prix)} FCFA"
                setOnClickListener { afficherPaiement(a) }
            })
        }
        listeEcran = ScrollView(this).apply { addView(liste) }

        paiementEcran = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }
        titrePaiement = TextView(this).apply {
            textSize = 20f
            setTextColor(Color.BLACK)
        }
        paiementEcran.addView(titrePaiement)
        paiementEcran.addView(TextView(this).apply {
            text = "Aucun argent réel n'est débité."
            textSize = 13f
            setTextColor(Color.GRAY)
        })
        paiementEcran.addView(EditText(this).apply { setText("4242 4242 4242 4242"); hint = "Numéro de carte" })
        paiementEcran.addView(EditText(this).apply { setText("12/30"); hint = "Expiration" })
        paiementEcran.addView(EditText(this).apply { setText("123"); hint = "CVC" })

        payButton = Button(this).apply { setOnClickListener { payer() } }
        retourButton = Button(this).apply {
            text = "Retour aux articles"
            setOnClickListener { afficherListe() }
        }
        progress = ProgressBar(this).apply { visibility = View.GONE }
        statut = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
        }
        paiementEcran.addView(payButton)
        paiementEcran.addView(retourButton)
        paiementEcran.addView(progress)
        paiementEcran.addView(statut)

        root.addView(listeEcran)
        root.addView(paiementEcran)
        setContentView(root)
    }

    private fun afficherPaiement(a: Article) {
        articleChoisi = a
        titrePaiement.text = "${a.nom} : ${formatPrix(a.prix)} FCFA"
        payButton.text = "Payer ${formatPrix(a.prix)} FCFA"
        payButton.isEnabled = true
        payButton.setOnClickListener { payer() }
        retourButton.visibility = View.VISIBLE
        retourButton.isEnabled = true
        statut.text = ""
        statut.setTextColor(Color.BLACK)
        listeEcran.visibility = View.GONE
        paiementEcran.visibility = View.VISIBLE
    }

    private fun afficherListe() {
        paiementEcran.visibility = View.GONE
        listeEcran.visibility = View.VISIBLE
    }

    private fun payer() {
        val a = articleChoisi ?: return
        payButton.isEnabled = false
        retourButton.isEnabled = false
        progress.visibility = View.VISIBLE
        statut.text = "Traitement en cours..."
        Handler(Looper.getMainLooper()).postDelayed({
            progress.visibility = View.GONE
            statut.setTextColor(Color.parseColor("#2E7D32"))
            statut.text = "Paiement approuvé (simulé)\n${a.nom} : ${formatPrix(a.prix)} FCFA\nRéf : SIM-${System.currentTimeMillis() % 100000}"
            retourButton.visibility = View.GONE
            payButton.text = "Fermer"
            payButton.isEnabled = true
            payButton.setOnClickListener { finish() }
        }, 2000)
    }

    private fun formatPrix(n: Int): String =
        n.toString().reversed().chunked(3).joinToString(" ").reversed()
}
