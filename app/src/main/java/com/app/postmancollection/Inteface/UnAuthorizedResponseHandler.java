package com.app.postmancollection.Inteface;

import retrofit2.Response;

public interface UnAuthorizedResponseHandler {
    void handleUnAuthorisedResponse(Response<?> response);
}
