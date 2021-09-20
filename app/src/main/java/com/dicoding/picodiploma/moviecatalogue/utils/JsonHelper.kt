package com.dicoding.picodiploma.moviecatalogue.utils

import android.content.Context
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class JsonHelper(private val context: Context) {

    private fun parsingFileToString(fileName: String): String? {
        return try {
            val `is` = context.assets.open(fileName)
            val buffer = ByteArray(`is`.available())
            `is`.read(buffer)
            `is`.close()
            String(buffer)

        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun loadMovies(): PopularMoviesResponse? {
        var popularMoviesResponse: PopularMoviesResponse? = null
        try {
            val responseObject = JSONObject(parsingFileToString("popular-movies.json").toString())
            val page = responseObject.getInt("page")
            val totalPage = responseObject.getInt("total_pages")
            val totalResults = responseObject.getInt("total_results")
            val moviesResults = responseObject.getJSONArray("results")
            val results = ArrayList<MoviesResultItem>()
            for (i in 0 until moviesResults.length()) {
                val movie = moviesResults.getJSONObject(i)

                val overview = movie.getString("overview")
                val originalLanguage = movie.getString("original_language")
                val originalTitle = movie.getString("original_title")
                val video = movie.getBoolean("video")
                val title = movie.getString("title")
                val genreIds = ArrayList<Int>()
                val posterPath = movie.getString("poster_path")
                val backdropPath = movie.getString("backdrop_path")
                val releaseDate = movie.getString("release_date")
                val popularity = movie.getDouble("popularity")
                val voteAverage = movie.getDouble("vote_average")
                val id = movie.getInt("id")
                val adult = movie.getBoolean("adult")
                val voteCount = movie.getInt("vote_count")

                val genres = movie.getJSONArray("genre_ids")
                for (j in 0 until genres.length()) {
                    val genreId = genres.getInt(j)
                    genreIds.add(genreId)
                }

                val moviesResultItem = MoviesResultItem(
                    overview,
                    originalLanguage,
                    originalTitle,
                    video,
                    title,
                    genreIds,
                    posterPath,
                    backdropPath,
                    releaseDate,
                    popularity,
                    voteAverage,
                    id,
                    adult,
                    voteCount,
                )
                results.add(moviesResultItem)
            }

            popularMoviesResponse = PopularMoviesResponse(page, totalPage, results, totalResults)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return popularMoviesResponse
    }

    fun loadShows(): PopularTvShowsResponse? {
        var popularTvShowsResponse: PopularTvShowsResponse? = null
        try {
            val responseObject = JSONObject(parsingFileToString("popular-shows.json").toString())
            val page = responseObject.getInt("page")
            val totalPage = responseObject.getInt("total_pages")
            val totalResults = responseObject.getInt("total_results")
            val moviesResults = responseObject.getJSONArray("results")
            val results = ArrayList<TvShowsResultItem>()
            for (i in 0 until moviesResults.length()) {
                val show = moviesResults.getJSONObject(i)

                val firstAirDate = show.getString("first_air_date")
                val overview = show.getString("overview")
                val originalLanguage = show.getString("original_language")
                val genreIds = ArrayList<Int>()
                val posterPath = show.getString("poster_path")
                val originCountry = ArrayList<String>()
                val backdropPath = show.getString("backdrop_path")
                val originalName = show.getString("original_name")
                val popularity = show.getDouble("popularity")
                val voteAverage = show.getDouble("vote_average")
                val name = show.getString("name")
                val id = show.getInt("id")
                val voteCount = show.getInt("vote_count")

                val genres = show.getJSONArray("genre_ids")
                for (j in 0 until genres.length()) {
                    val genreId = genres.getInt(j)
                    genreIds.add(genreId)
                }

                val originCountries = show.getJSONArray("origin_country")
                for (j in 0 until originCountries.length()) {
                    val country = genres.getString(j)
                    originCountry.add(country)
                }

                val moviesResultItem = TvShowsResultItem(
                    firstAirDate,
                    overview,
                    originalLanguage,
                    genreIds,
                    posterPath,
                    originCountry,
                    backdropPath,
                    originalName,
                    popularity,
                    voteAverage,
                    name,
                    id,
                    voteCount
                )
                results.add(moviesResultItem)
            }

            popularTvShowsResponse = PopularTvShowsResponse(page, totalPage, results, totalResults)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return popularTvShowsResponse
    }

    fun loadShow(showId: Int): TvShowDetailResponse? {
        val fileName = String.format("show-%s.json", showId.toString())
        var tvShowDetailResponse: TvShowDetailResponse? = null
        try {
            val result = parsingFileToString(fileName)
            if (result != null) {
                val responseObject = JSONObject(result)

                val originalLanguage = responseObject.getString("original_language")
                val numberOfEpisodes = responseObject.getInt("number_of_episodes")
                val networks = ArrayList<NetworksItem>()
                val networkList = responseObject.getJSONArray("networks")
                for (i in 0 until networkList.length()) {
                    val network = networkList.getJSONObject(i)
                    val logoPath = network.getString("logo_path")
                    val name = network.getString("name")
                    val id = network.getInt("id")
                    val originCountry = network.getString("origin_country")

                    val networksItem = NetworksItem(logoPath, name, id, originCountry)
                    networks.add(networksItem)
                }
                val type = responseObject.getString("type")
                val backdropPath = responseObject.getString("backdrop_path")
                val genres = ArrayList<GenresItem>()
                val genresList = responseObject.getJSONArray("genres")
                for (i in 0 until genresList.length()) {
                    val genre = genresList.getJSONObject(i)
                    val name = genre.getString("name")
                    val id = genre.getInt("id")

                    val genresItem = GenresItem(name, id)
                    genres.add(genresItem)
                }
                val popularity = responseObject.getDouble("popularity")
                val productionCountries = ArrayList<ProductionCountriesItem>()
                val productionCountryList = responseObject.getJSONArray("production_countries")
                for (i in 0 until productionCountryList.length()) {
                    val productionCountry = productionCountryList.getJSONObject(i)
                    val iso = productionCountry.getString("iso_3166_1")
                    val name = productionCountry.getString("name")

                    val productionCompaniesItem = ProductionCountriesItem(iso, name)
                    productionCountries.add(productionCompaniesItem)
                }
                val id = responseObject.getInt("id")
                val numberOfSeasons = responseObject.getInt("number_of_seasons")
                val voteCount = responseObject.getInt("vote_count")
                val firstAirDate = responseObject.getString("first_air_date")
                val overview = responseObject.getString("overview")
                val seasons = ArrayList<SeasonsItem>()
                val seasonList = responseObject.getJSONArray("seasons")
                for (i in 0 until seasonList.length()) {
                    val season = seasonList.getJSONObject(i)
                    val airDate = season.getString("air_date")
                    val seasonOverview = season.getString("overview")
                    val episodeCount = season.getInt("episode_count")
                    val name = season.getString("name")
                    val seasonNumber = season.getInt("season_number")
                    val seasonId = season.getInt("id")
                    val posterPath = season.getString("poster_path")

                    val seasonsItem = SeasonsItem(
                        airDate,
                        seasonOverview,
                        episodeCount,
                        name,
                        seasonNumber,
                        seasonId,
                        posterPath
                    )
                    seasons.add(seasonsItem)
                }
                val languages = ArrayList<String>()
                val languageList = responseObject.getJSONArray("languages")
                for (i in 0 until languageList.length()) {
                    val language = languageList.getString(i)
                    languages.add(language)
                }
                val createdBy = ArrayList<CreatedByItem>()
                val creatorList = responseObject.getJSONArray("created_by")
                for (i in 0 until creatorList.length()) {
                    val creator = creatorList.getJSONObject(i)
                    val gender = creator.getInt("gender")
                    val creditId = creator.getString("credit_id")
                    val name = creator.getString("name")
                    val profilePath = creator.get("profile_path")
                    val creatorId = creator.getInt("id")

                    val createdByItem =
                        CreatedByItem(gender, creditId, name, profilePath, creatorId)
                    createdBy.add(createdByItem)
                }
                val lastEpisodeAired = responseObject.getJSONObject("last_episode_to_air")
                val productionCode = lastEpisodeAired.getString("production_code")
                val airDate = lastEpisodeAired.getString("air_date")
                val lastEpisodeOverview = lastEpisodeAired.getString("overview")
                val episodeNumber = lastEpisodeAired.getInt("episode_number")
                val lastEpisodeVoteAverage = lastEpisodeAired.getDouble("vote_average")
                val lastEpisodeName = lastEpisodeAired.getString("name")
                val seasonNumber = lastEpisodeAired.getInt("season_number")
                val lastEpisodeId = lastEpisodeAired.getInt("id")
                val stillPath = lastEpisodeAired.getString("still_path")
                val lastEpisodeVoteCount = lastEpisodeAired.getInt("vote_count")
                val lastEpisodeToAir = LastEpisodeToAir(
                    productionCode,
                    airDate,
                    lastEpisodeOverview,
                    episodeNumber,
                    lastEpisodeVoteAverage,
                    lastEpisodeName,
                    seasonNumber,
                    lastEpisodeId,
                    stillPath,
                    lastEpisodeVoteCount
                )
                val posterPath = responseObject.getString("poster_path")
                val originCountry = ArrayList<String>()
                val originCountryList = responseObject.getJSONArray("origin_country")
                for (i in 0 until originCountryList.length()) {
                    val origin = originCountryList.getString(i)
                    originCountry.add(origin)
                }
                val spokenLanguages = ArrayList<SpokenLanguagesItem>()
                val spokenLanguageList = responseObject.getJSONArray("spoken_languages")
                for (i in 0 until spokenLanguageList.length()) {
                    val spokenLanguage = spokenLanguageList.getJSONObject(i)
                    val name = spokenLanguage.getString("name")
                    val iso6391 = spokenLanguage.getString("iso_639_1")
                    val englishName = spokenLanguage.getString("english_name")

                    val spokenLanguagesItem = SpokenLanguagesItem(name, iso6391, englishName)
                    spokenLanguages.add(spokenLanguagesItem)
                }
                val productionCompanies = ArrayList<ProductionCompaniesItem>()
                val companyList = responseObject.getJSONArray("production_companies")
                for (i in 0 until companyList.length()) {
                    val productionCompany = companyList.getJSONObject(i)
                    val logoPath = productionCompany.getString("logo_path")
                    val name = productionCompany.getString("name")
                    val companyId = productionCompany.getInt("id")
                    val companyOriginCountry = productionCompany.getString("origin_country")

                    val productionCompaniesItem =
                        ProductionCompaniesItem(logoPath, name, companyId, companyOriginCountry)
                    productionCompanies.add(productionCompaniesItem)
                }
                val originalName = responseObject.getString("original_name")
                val voteAverage = responseObject.getDouble("vote_average")
                val name = responseObject.getString("name")
                val tagline = responseObject.getString("tagline")
                val episodeRunTime = ArrayList<Int>()
                val episodeList = responseObject.getJSONArray("episode_run_time")
                for (i in 0 until episodeList.length()) {
                    val episode = episodeList.getInt(i)
                    episodeRunTime.add(episode)
                }
                val nextEpisodeToAir = responseObject.get("next_episode_to_air")
                val inProduction = responseObject.getBoolean("in_production")
                val lastAirDate = responseObject.getString("last_air_date")
                val homepage = responseObject.getString("homepage")
                val status = responseObject.getString("status")

                tvShowDetailResponse = TvShowDetailResponse(
                    originalLanguage,
                    numberOfEpisodes,
                    networks,
                    type,
                    backdropPath,
                    genres,
                    popularity,
                    productionCountries,
                    id,
                    numberOfSeasons,
                    voteCount,
                    firstAirDate,
                    overview,
                    seasons,
                    languages,
                    createdBy,
                    lastEpisodeToAir,
                    posterPath,
                    originCountry,
                    spokenLanguages,
                    productionCompanies,
                    originalName,
                    voteAverage,
                    name,
                    tagline,
                    episodeRunTime,
                    nextEpisodeToAir,
                    inProduction,
                    lastAirDate,
                    homepage,
                    status
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return tvShowDetailResponse
    }

    fun loadMovie(movieId: Int): MovieDetailResponse? {
        val fileName = String.format("movie-%s.json", movieId.toString())
        var movieDetailResponse: MovieDetailResponse? = null
        try {
            val result = parsingFileToString(fileName)
            if (result != null) {
                val responseObject = JSONObject(result)

                val originalLanguage = responseObject.getString("original_language")
                val imdbId = responseObject.getString("imdb_id")
                val video = responseObject.getBoolean("video")
                val title = responseObject.getString("title")
                val backdropPath = responseObject.getString("backdrop_path")
                val revenue = responseObject.getInt("revenue")
                val genres = ArrayList<GenresItem>()
                val genresList = responseObject.getJSONArray("genres")
                for (i in 0 until genresList.length()) {
                    val genre = genresList.getJSONObject(i)
                    val name = genre.getString("name")
                    val id = genre.getInt("id")

                    val genresItem = GenresItem(name, id)
                    genres.add(genresItem)
                }
                val popularity = responseObject.getDouble("popularity")
                val productionCountries = ArrayList<ProductionCountriesItem>()
                val productionCountryList = responseObject.getJSONArray("production_countries")
                for (i in 0 until productionCountryList.length()) {
                    val productionCountry = productionCountryList.getJSONObject(i)
                    val iso = productionCountry.getString("iso_3166_1")
                    val name = productionCountry.getString("name")

                    val productionCompaniesItem = ProductionCountriesItem(iso, name)
                    productionCountries.add(productionCompaniesItem)
                }
                val id = responseObject.getInt("id")
                val voteCount = responseObject.getInt("vote_count")
                val budget = responseObject.getInt("budget")
                val overview = responseObject.getString("overview")
                val originalTitle = responseObject.getString("original_title")
                val runtime = responseObject.getInt("runtime")
                val posterPath = responseObject.getString("poster_path")
                val spokenLanguages = ArrayList<SpokenLanguagesItem>()
                val spokenLanguageList = responseObject.getJSONArray("spoken_languages")
                for (i in 0 until spokenLanguageList.length()) {
                    val spokenLanguage = spokenLanguageList.getJSONObject(i)
                    val name = spokenLanguage.getString("name")
                    val iso6391 = spokenLanguage.getString("iso_639_1")
                    val englishName = spokenLanguage.getString("english_name")

                    val spokenLanguagesItem = SpokenLanguagesItem(name, iso6391, englishName)
                    spokenLanguages.add(spokenLanguagesItem)
                }
                val productionCompanies = ArrayList<ProductionCompaniesItem>()
                val companyList = responseObject.getJSONArray("production_companies")
                for (i in 0 until companyList.length()) {
                    val productionCompany = companyList.getJSONObject(i)
                    val logoPath = productionCompany.getString("logo_path")
                    val name = productionCompany.getString("name")
                    val companyId = productionCompany.getInt("id")
                    val companyOriginCountry = productionCompany.getString("origin_country")

                    val productionCompaniesItem =
                        ProductionCompaniesItem(logoPath, name, companyId, companyOriginCountry)
                    productionCompanies.add(productionCompaniesItem)
                }
                val releaseDate = responseObject.getString("release_date")
                val voteAverage = responseObject.getDouble("vote_average")

                val isCollectionNull = responseObject.isNull("belongs_to_collection")
                var belongsToCollection: BelongsToCollection? = null
                if (!isCollectionNull) {
                    val collectionList = responseObject.getJSONObject("belongs_to_collection")
                    val collectionBackdropPath = collectionList.getString("backdrop_path")
                    val name = collectionList.getString("name")
                    val collectionId = collectionList.getInt("id")
                    val collectionPosterPath = collectionList.getString("poster_path")
                    belongsToCollection = BelongsToCollection(
                        collectionBackdropPath, name, collectionId, collectionPosterPath
                    )
                }

                val tagline = responseObject.getString("tagline")
                val adult = responseObject.getBoolean("adult")
                val homepage = responseObject.getString("homepage")
                val status = responseObject.getString("status")

                movieDetailResponse = MovieDetailResponse(
                    originalLanguage,
                    imdbId,
                    video,
                    title,
                    backdropPath,
                    revenue,
                    genres,
                    popularity,
                    productionCountries,
                    id,
                    voteCount,
                    budget,
                    overview,
                    originalTitle,
                    runtime,
                    posterPath,
                    spokenLanguages,
                    productionCompanies,
                    releaseDate,
                    voteAverage,
                    belongsToCollection,
                    tagline,
                    adult,
                    homepage,
                    status
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return movieDetailResponse
    }
}