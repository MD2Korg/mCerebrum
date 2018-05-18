/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.datakit.cerebralcortex;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * This interceptor compresses the HTTP request body.
 */
class GzipRequestInterceptor implements Interceptor {
    /**
     * Intercepts HTTP requests and compresses them.
     *
     * @param chain The interceptor chain.
     * @return The compressed HTTP response.
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest);
        }

        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(originalRequest.method(),
                        forceContentLength(gzip(originalRequest.body())))
                .build();
        return chain.proceed(compressedRequest);
    }

    /**
     * Returns a new <code>RequestBody</code> with a <code>contentLength</code> the same size as the
     * buffer the parametrized <code>RequestBody</code> is written to.
     *
     * <p>
     *     https://github.com/square/okhttp/issues/350
     * </p>
     *
     * @param requestBody <code>RequestBody</code>.
     */
    private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }

    /**
     *
     *
     * @param body <code>RequestBody</code>.
     * @return A new <code>RequestBody</code>.
     */
    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            /**
             * Returns the content type of the body.
             *
             * @return The content type of the body.
             */
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            /**
             * Returns the compressed content length.
             *
             * @return Always returns -1 because we don't know the compressed length in advance.
             */
            @Override
            public long contentLength() {
                return -1;
            }

            /**
             * Writes the <code>RequestBody</code> to a <code>BufferedSink</code>.
             *
             * @param sink <code>BufferedSink</code>
             * @throws IOException
             */
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}