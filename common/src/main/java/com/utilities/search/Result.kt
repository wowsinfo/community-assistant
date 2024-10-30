package com.utilities.search

import com.utilities.enums.ResponseType

/**
 * Super class to results. User this to send back results from servers to the front end.
 *
 * @param <Q>
 * @author Obsidian47
</Q> */
class Result<Q : Query?> {
    var query: Q? = null
    var error: Error? = null
    var type: ResponseType? = null
}
