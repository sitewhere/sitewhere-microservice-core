package com.sitewhere.microservice.cache;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.ToByteBufEncoder;
import io.netty.buffer.ByteBuf;

/**
 * Implementation of {@link RedisCodec} that uses a String key and byte[] value.
 */
public class StringByteArrayCodec implements RedisCodec<String, byte[]>, ToByteBufEncoder<String, byte[]> {

    public static final StringByteArrayCodec INSTANCE = new StringByteArrayCodec();

    private static final byte[] EMPTY = new byte[0];

    /*
     * @see io.lettuce.core.codec.ToByteBufEncoder#encodeKey(java.lang.Object,
     * io.netty.buffer.ByteBuf)
     */
    @Override
    public void encodeKey(String key, ByteBuf target) {
	if (key != null) {
	    target.writeBytes(key.getBytes(StandardCharsets.UTF_8));
	}
    }

    /*
     * @see io.lettuce.core.codec.ToByteBufEncoder#encodeValue(java.lang.Object,
     * io.netty.buffer.ByteBuf)
     */
    @Override
    public void encodeValue(byte[] value, ByteBuf target) {
	if (value != null) {
	    target.writeBytes(value);
	}
    }

    /*
     * @see io.lettuce.core.codec.ToByteBufEncoder#estimateSize(java.lang.Object)
     */
    @Override
    public int estimateSize(Object keyOrValue) {
	if (keyOrValue == null) {
	    return 0;
	}
	if (keyOrValue instanceof byte[]) {
	    return ((byte[]) keyOrValue).length;
	} else {
	    return ((String) keyOrValue).getBytes(StandardCharsets.UTF_8).length;
	}
    }

    /*
     * @see io.lettuce.core.codec.RedisCodec#decodeKey(java.nio.ByteBuffer)
     */
    @Override
    public String decodeKey(ByteBuffer bytes) {
	return new String(getBytes(bytes), StandardCharsets.UTF_8);
    }

    /*
     * @see io.lettuce.core.codec.RedisCodec#decodeValue(java.nio.ByteBuffer)
     */
    @Override
    public byte[] decodeValue(ByteBuffer bytes) {
	return getBytes(bytes);
    }

    /*
     * @see io.lettuce.core.codec.RedisCodec#encodeKey(java.lang.Object)
     */
    @Override
    public ByteBuffer encodeKey(String key) {
	if (key == null) {
	    return ByteBuffer.wrap(EMPTY);
	}
	return ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * @see io.lettuce.core.codec.RedisCodec#encodeValue(java.lang.Object)
     */
    @Override
    public ByteBuffer encodeValue(byte[] value) {
	if (value == null) {
	    return ByteBuffer.wrap(EMPTY);
	}
	return ByteBuffer.wrap(value);
    }

    private static byte[] getBytes(ByteBuffer buffer) {
	int remaining = buffer.remaining();
	if (remaining == 0) {
	    return EMPTY;
	}
	byte[] b = new byte[remaining];
	buffer.get(b);
	return b;
    }
}
