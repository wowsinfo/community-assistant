package com.utilities.interfaces

import com.utilities.search.Query
import com.utilities.search.Result


interface IResponse<Q : Query?, R : Result<Q>?> {
    fun onReceived(result: R)
}
