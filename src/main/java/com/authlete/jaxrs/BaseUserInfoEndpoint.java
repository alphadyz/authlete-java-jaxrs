/*
 * Copyright (C) 2016 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package com.authlete.jaxrs;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.authlete.common.api.AuthleteApi;
import com.authlete.jaxrs.spi.UserInfoRequestHandlerSpi;


/**
 * A base class for userinfo endpoints.
 *
 * @since 1.2
 *
 * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#UserInfo"
 *      >OpenID Connect Core 1.0, 5.3. UserInfo Endpoint</a>
 *
 * @author Takahiko Kawasaki
 */
public class BaseUserInfoEndpoint extends BaseEndpoint
{
    /**
     * Handle a userinfo request.
     *
     * <p>
     * This method internally creates a {@link UserInfoRequestHandler} instance
     * and calls its {@link UserInfoRequestHandler#handle(String)} method.
     * Then, this method uses the value returned from the {@code handle()} method
     * as a response from this method.
     * </p>
     *
     * <p>
     * When {@code UserInfoRequestHandler.handle()} method raises a {@link
     * WebApplicationException}, this method calls {@link #onError(WebApplicationException)
     * onError()} method with the exception. The default implementation of {@code onError()}
     * calls {@code printStackTrace()} of the exception and does nothing else. You
     * can override the method as necessary. After calling {@code onError()} method,
     * this method calls {@code getResponse()} method of the exception and uses the
     * returned value as a response from this method.
     * </p>
     *
     * @param api
     *         An implementation of {@link AuthleteApi}.
     *
     * @param spi
     *         An implementation of {@link UserInfoRequestHandlerSpi}.
     *
     * @param authorization
     *         The value of {@code Authorization} header of the userinfo request.
     *         An access token must be embedded there as a Bearer Token
     *         (<a href="http://tools.ietf.org/html/rfc6750">RFC 6750</a>).
     *
     * @return
     *         A response that should be returned to the client application.
     */
    public Response handle(
            AuthleteApi api, UserInfoRequestHandlerSpi spi, String authorization)
    {
        try
        {
            // Create a handler.
            UserInfoRequestHandler handler = new UserInfoRequestHandler(api, spi);

            // Delegate the task to the handler.
            return handler.handle(authorization);
        }
        catch (WebApplicationException e)
        {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
        }
    }
}