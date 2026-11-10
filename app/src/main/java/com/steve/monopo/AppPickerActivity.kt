package com.steve.monopo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class AppPickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        val pm = packageManager
        val lancement = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(lancement, 0)
            .map { Pair(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .filter { it.second != packageName }
            .distinctBy { it.second }
            .sortedBy { it.first.lowercase() }

        val liste = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((16 * dp).toInt(), (16 * dp).toInt(), (16 * dp).toInt(), (16 * dp).toInt())
        }
        liste.addView(TextView(this).apply {
            text = "Choisis une application à synchroniser"
            textSize = 18f
        })
        apps.forEach { (nom, pkg) ->
            val coche = if (AppCatalog.estSynchronisee(this, pkg)) "  ✓" else ""
            liste.addView(Button(this).apply {
                text = nom + coche
                setOnClickListener { ouvrirEdition(nom, pkg) }
            })
        }
        setContentView(ScrollView(this).apply { addView(liste) })
    }

    private fun ouvrirEdition(nom: String, pkg: String) {
        AppCatalog.synchroniser(this, pkg)
        val nomInput = EditText(this).apply { hint = "Nom de l'article" }
        val prixInput = EditText(this).apply {
            hint = "Prix (FCFA)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val zone = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(nomInput)
            addView(prixInput)
        }
        AlertDialog.Builder(this)
            .setTitle("Articles de $nom")
            .setView(zone)
            .setPositiveButton("Ajouter") { _, _ ->
                val titre = nomInput.text.toString().trim()
                val prix = prixInput.text.toString().toIntOrNull()
                if (titre.isNotEmpty() && prix != null) {
                    AppCatalog.ajouter(this, pkg, Article(titre, prix))
                    Toast.makeText(this, "Article ajouté", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Nom et prix requis", Toast.LENGTH_SHORT).show()
                }
                ouvrirEdition(nom, pkg)
            }
            .setNegativeButton("Terminé") { _, _ -> recreate() }
            .show()
    }
}
