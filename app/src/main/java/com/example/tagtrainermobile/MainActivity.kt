package com.example.tagtrainermobile

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.ListProductsAdapter
import com.example.tagtrainermobile.models.ListingProduct
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var listingProducts = ListingProduct.SingleList.singleListInstance

    fun onClickedProducts(v: ListView, p: Int) {
        val intent = Intent(applicationContext, ProductActivity::class.java)

        val params = Bundle()
        params.putInt("id", p)
        intent.putExtras(params)

        val product = filteredProductsList()[p]
        val eventBundle = Bundle().apply {
            putString("product_id", product.listProdId.toString())
            putString("product_name", product.listProdName)
            putString("product_category", product.listProdCat)
        }
        firebaseAnalytics.logEvent("product_click", eventBundle)

        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = Firebase.analytics

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        displayListingPage()

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = findViewById<SearchView>(R.id.searchViewId)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Registrar o evento de busca no Firebase Analytics
                    val searchBundle = Bundle().apply {
                        putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
                    }
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, searchBundle)

                    // Filtrar os produtos com base no termo de busca
                    val filteredProducts = listingProducts.filter { product ->
                        product.listProdName.contains(query, ignoreCase = true) ||
                                product.listProdCat.contains(query, ignoreCase = true)
                    }

                    val listView = findViewById<ListView>(R.id.tableID)
                    val adapter = ListProductsAdapter(this@MainActivity, ArrayList(filteredProducts))
                    listView.adapter = adapter
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        })
    }

    fun filteredProductsList() : ArrayList<ListingProduct> {
        val listCategory = intent.getStringExtra("listType")

        val categoryList = ArrayList<ListingProduct>()
        for(i in listingProducts) {
            if(i.listProdCat == listCategory) {
                categoryList.add(i)
            }
        }

        if (categoryList.isNotEmpty()) {
            val eventBundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, listCategory)
                putInt(FirebaseAnalytics.Param.ITEMS, categoryList.size)
                val itemIds = categoryList.map { it.listProdId.toString() }
                putStringArrayList(FirebaseAnalytics.Param.ITEM_LIST_ID, ArrayList(itemIds))
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, eventBundle)
        }

        if (categoryList.size <= 0) return listingProducts
        return categoryList
    }

    fun displayListingPage() {
        val table: ListView = findViewById(R.id.tableID)
        val adapter = ListProductsAdapter(this, filteredProductsList())
        table.adapter = adapter
        table.setOnItemClickListener { parent, view, position, id ->
            onClickedProducts(table, position)
        }
    }
}
