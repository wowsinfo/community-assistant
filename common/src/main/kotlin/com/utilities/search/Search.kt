package com.utilities.search

import android.os.AsyncTask
import com.utilities.interfaces.IParser
import com.utilities.interfaces.IResponse

class Search<Q : Query?, R : Result<Q>?>(
    private val mParser: IParser<Q, R>,
    private val mCallback: IResponse<Q, R>?
) : AsyncTask<Q, Void?, R>() {
    override fun doInBackground(vararg params: Q): R {
        return mParser.parse(*params)
    }

    override fun onPostExecute(r: R) {
        mCallback?.onReceived(r)
        super.onPostExecute(r)
    }
}
