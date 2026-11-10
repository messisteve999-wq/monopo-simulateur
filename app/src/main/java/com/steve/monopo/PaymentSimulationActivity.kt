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

    private data class Produit(val nom: String, val prix: Int)

    private val produits = listOf(
        Produit("Pack de 100 pièces", 500),
        Produit("Pack de 500 pièces", 2000),
        Produit("Abonnement mensuel Premium", 1500),
        Produit("Amélioration niveau 2", 1000),
        Produit("Supprimer les publicités", 3000)
    )

    private lateinit var listeEcran: ScrollView
    private lateinit var paiementEcran: LinearLayout
    private lateinit var titrePaiement: TextView
    private lateinit var payButton: Button
    private lateinit var retourButton: Button
    private lateinit var progress: ProgressBar
    private lateinit var statut: TextView
    private var produitChoisi: Produit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(24), px(24), px(24), px(24))
            setBackgroundColor(Color.WHITE)
        }

        val liste = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        liste.addView(TextView(this).apply {
            text = "Choisis un article (SIMULATION)"
            textSize = 20f
            setTextColor(Color.BLACK)
        })
        produits.forEach { p ->
            liste.addView(Button(this).apply {
                text = "${p.nom}\n${formatPrix(p.prix)} FCFA"
                setOnClickListener { afficherPaiement(p) }
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

    private fun afficherPaiement(p: Produit) {
        produitChoisi = p
        titrePaiement.text = "${p.nom} : ${formatPrix(p.prix)} FCFA"
        payButton.text = "Payer ${formatPrix(p.prix)} FCFA"
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
        val p = produitChoisi ?: return
        payButton.isEnabled = false
        retourButton.isEnabled = false
        progress.visibility = View.VISIBLE
        statut.text = "Traitement en cours..."
        Handler(Looper.getMainLooper()).postDelayed({
            progress.visibility = View.GONE
            statut.setTextColor(Color.parseColor("#2E7D32"))
            statut.text = "Paiement approuvé (simulé)\n${p.nom} : ${formatPrix(p.prix)} FCFA\nRéf : SIM-${System.currentTimeMillis() % 100000}"
            retourButton.visibility = View.GONE
            payButton.text = "Fermer"
            payButton.isEnabled = true
            payButton.setOnClickListener { finish() }
        }, 2000)
    }

    private fun formatPrix(n: Int): String =
        n.toString().reversed().chunked(3).joinToString(" ").reversed()
}
