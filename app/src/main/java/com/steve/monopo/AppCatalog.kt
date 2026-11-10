package com.steve.monopo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Article(val nom: String, val prix: Int)

object AppCatalog {
    private const val PREFS = "monopo_catalog"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun estSynchronisee(ctx: Context, pkg: String): Boolean = prefs(ctx).contains(pkg)

    fun synchroniser(ctx: Context, pkg: String) {
        if (!estSynchronisee(ctx, pkg)) enregistrer(ctx, pkg, emptyList())
    }

    fun articles(ctx: Context, pkg: String): List<Article> {
        val raw = prefs(ctx).getString(pkg, null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Article(o.getString("nom"), o.getInt("prix"))
        }
    }

    fun ajouter(ctx: Context, pkg: String, article: Article) {
        enregistrer(ctx, pkg, articles(ctx, pkg) + article)
    }

    private fun enregistrer(ctx: Context, pkg: String, liste: List<Article>) {
        val arr = JSONArray()
        liste.forEach { arr.put(JSONObject().put("nom", it.nom).put("prix", it.prix)) }
        prefs(ctx).edit().putString(pkg, arr.toString()).apply()
    }
}
