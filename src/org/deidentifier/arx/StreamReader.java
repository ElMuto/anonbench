package org.deidentifier.arx;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for reading the output and error streams of child processes
 * @author Fabian Prasser
 */
public class StreamReader implements Runnable {

    /** A buffer for output*/
    private StringBuffer buffer = new StringBuffer();
    /** The wrapped stream*/
    private InputStream in = null;

    /**
     * Creates a new stream reader
     * @param in
     */
    public StreamReader(final InputStream in) {
        this.in = in;
    }

    /**
     * Returns the buffered content
     * @return
     */
    public String getString() {
        return buffer.toString();
    }
    
    @Override
    public void run() {
        try {
            int i = this.in.read();
            while (i != -1) {
                buffer.append((char) i);
                i = this.in.read();
            }
        } catch (final IOException e) {
            // Die silently
        }
    }
}