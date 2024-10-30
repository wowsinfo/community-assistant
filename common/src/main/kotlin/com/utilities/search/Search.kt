package com.utilities.search

import android.os.AsyncTask
import com.utilities.interfaces.IParser
import com.utilities.interfaces.IResponse
import org.greenrobot.eventbus.EventBus

class Search<Q : Query?, R : Result<Q>?>(
    private val mParser: IParser<Q, R>,
    private val mCallback: IResponse<Q, R>?,
    private val mBus: EventBus?
) : AsyncTask<Q, Void?, R>() {
    override fun doInBackground(vararg params: Q): R {
        return mParser.parse(*params)
    }

    override fun onPostExecute(r: R) {
        mCallback?.onReceived(r)
        mBus?.post(r)
        super.onPostExecute(r)
    }
}
