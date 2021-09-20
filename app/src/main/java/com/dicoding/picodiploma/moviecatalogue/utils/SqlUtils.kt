package com.dicoding.picodiploma.moviecatalogue.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object SqlUtils {
    const val NEWEST = "ASC"
    const val OLDEST = "DESC"

    fun getMoviesQuery(
        keyword: String = "",
        bookmarked: Boolean = false,
        filter: String = OLDEST,
    ): SimpleSQLiteQuery {
        val query =
            StringBuilder().append("SELECT * FROM moviesentities WHERE (title LIKE '%$keyword%' OR overview LIKE '%$keyword%') ")
        if (bookmarked) query.append("AND bookmarked = 1 ")
        when (filter) {
            NEWEST -> query.append("ORDER BY id DESC")
            OLDEST -> query.append("ORDER BY id ASC")
        }

        return SimpleSQLiteQuery(query.toString())
    }

    fun getShowsQuery(
        keyword: String = "",
        bookmarked: Boolean = false,
        filter: String = OLDEST,
    ): SimpleSQLiteQuery {
        val query =
            StringBuilder().append("SELECT * FROM showsentities WHERE (name LIKE '%$keyword%' OR overview LIKE '%$keyword%') ")
        if (bookmarked) query.append("AND bookmarked = 1 ")
        when (filter) {
            NEWEST -> query.append("ORDER BY id DESC")
            OLDEST -> query.append("ORDER BY id ASC")
        }

        return SimpleSQLiteQuery(query.toString())
    }
}