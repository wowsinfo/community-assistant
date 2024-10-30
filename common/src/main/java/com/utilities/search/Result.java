package com.utilities.search;

import com.utilities.enums.ResponseType;

/**
 * Super class to results. User this to send back results from servers to the front end.
 *
 * @param <Q>
 * @author Obsidian47
 */
public class Result<Q extends Query> {

    private Q query;
    private Error error;
    private ResponseType type;

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }


    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
