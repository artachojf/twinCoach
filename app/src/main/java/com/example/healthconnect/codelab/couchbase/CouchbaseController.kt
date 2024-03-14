package com.example.healthconnect.codelab.couchbase

import android.content.Context
import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult

class CouchbaseController {

    private var collection: Collection

    constructor(context: Context, dbName: String, collectionName: String) {
        CouchbaseLite.init(context)
        collection = Database(dbName.lowercase()).createCollection(collectionName)
    }

    fun saveDoc(document: MutableDocument): String {
        collection.save(document)
        return document.id
    }

    fun retrieveDoc(documentId: String): MutableDocument? {
        return collection.getDocument(documentId)?.toMutable()
    }

    fun findAllDocs() {
        val query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))
        val size = query.execute().allResults().size
        Log.i("Query size", "Query's result size: $size")
    }

    fun findFirstDoc(): MutableDocument? {
        val query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.collection(collection))
        val id = query.execute().allResults()[0].getString("id")
        return id?.let { collection.getDocument(it)?.toMutable() }
    }
}