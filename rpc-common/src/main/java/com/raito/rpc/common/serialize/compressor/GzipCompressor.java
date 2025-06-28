package com.raito.rpc.common.serialize.compressor;

import com.raito.rpc.common.exception.CompressException;
import com.raito.rpc.common.exception.DecompressException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author cn
 * @since 2025/6/25 13:40
 * @version 1.0
 */
public class GzipCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new CompressException("Gzip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[256];
            int n;
            while ((n = gzip.read(buf)) > 0) {
                bos.write(buf, 0, n);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new DecompressException("Gzip decompress error", e);
        }
    }
}
