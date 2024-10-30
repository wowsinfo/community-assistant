package com.utilities.interfaces

import com.utilities.search.Query
import com.utilities.search.Result

interface IParser<Q : Query?, R : Result<Q>?> {
    fun parse(vararg results: Q): R
}
