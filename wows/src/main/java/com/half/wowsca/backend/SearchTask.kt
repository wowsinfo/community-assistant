package com.half.wowsca.backend

import android.os.AsyncTask
import android.text.TextUtils
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.model.Captain
import com.half.wowsca.model.queries.SearchQuery
import com.half.wowsca.model.result.SearchResults
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.wtf
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

/**
 * Created by slai4 on 9/19/2015.
 */
class SearchTask : AsyncTask<SearchQuery?, Void?, SearchResults>() {
    protected override fun doInBackground(vararg params: SearchQuery?): SearchResults? {
        val query = params[0] ?: return null
        val results = SearchResults()
        val url =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/account/list/?application_id=" + query.server.appId + "&search=" + query.search
        wtf("Search URL", url)

        //send url
        val feed = getURLResult(url)

        //parse
        if (!TextUtils.isEmpty(feed)) {
            var feedJSON: JSONObject? = null
            try {
                feedJSON = JSONObject(feed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (feedJSON != null) {
                val users = feedJSON.optJSONArray("data")
                if (users != null) for (i in 0 until users.length()) {
                    val user = users.optJSONObject(i)
                    if (user != null) {
                        val c = Captain()
                        c.id = user.optLong("account_id")
                        c.name = user.optString("nickname")
                        c.server = query.server
                        results.captains.add(c)
                    }
                }
            }
        }
        return results
    }

    fun getURLResult(url: String?): String? {
        var results: String? = null
        try {
            val feed = URL(url)
            results = getInputStreamResponse(feed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }

    override fun onPostExecute(searchResults: SearchResults) {
        super.onPostExecute(searchResults)
        eventBus.post(searchResults)
    }
}
