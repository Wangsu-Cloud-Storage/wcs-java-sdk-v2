package com.wos.services.internal.io;

import java.io.IOException;
import java.io.InputStream;

public class InterruptableInputStream extends InputStream implements InputStreamWrapper {
    private InputStream inputStream = null;

    private boolean interrupted = false;

    public InterruptableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void doInterrupted() throws IOException {
        if (interrupted) {
            try {
                close();
            } catch (IOException ioe) {
            }
            throw new UnrecoverableIOException("Reading from input stream deliberately interrupted");
        }
    }

    @Override
    public int read() throws IOException {
        doInterrupted();
        return inputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        doInterrupted();
        return inputStream.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        doInterrupted();
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public InputStream getWrappedInputStream() {
        return inputStream;
    }

    public void interrupt() {
        interrupted = true;
    }

}
