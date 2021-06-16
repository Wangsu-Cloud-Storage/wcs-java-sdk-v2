package com.wos.services.internal.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class HttpMethodReleaseInputStream extends InputStream implements InputStreamWrapper {
    private InputStream inputStream = null;
    private Response httpResponse = null;
    private boolean flag = false;
    private boolean comsumed = false;

    public HttpMethodReleaseInputStream(Response response) {
        this.httpResponse = response;
        try {
            this.inputStream = new InterruptableInputStream(response.body().byteStream());
        } catch (Exception e) {
            try {
                response.close();
            } catch (Exception ee) {
                // ignore
            }
            this.inputStream = new ByteArrayInputStream(new byte[] {});
        }
    }

    public Response getHttpResponse() {
        return httpResponse;
    }

    protected void closeConnection() throws IOException {
        if (!flag) {
            if (!comsumed && httpResponse != null) {
                httpResponse.close();
            }
            flag = true;
        }
    }

    @Override
    public int read() throws IOException {
        try {
            int read = inputStream.read();
            if (read == -1) {
                comsumed = true;
                if (!flag) {
                    closeConnection();
                }
            }
            return read;
        } catch (IOException e) {
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            int read = inputStream.read(b, off, len);
            if (read == -1) {
                comsumed = true;
                if (!flag) {
                    closeConnection();
                }
            }
            return read;
        } catch (IOException e) {
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return inputStream.available();
        } catch (IOException e) {
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (!flag) {
            closeConnection();
        }
        inputStream.close();
    }

    public InputStream getWrappedInputStream() {
        return inputStream;
    }

}
