package com.raito.rpc.common.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author cn
 * @since 2025/7/2 17:38
 * @version 1.0
 */
@SuppressWarnings("SpellCheckingInspection")
public class MessageUtils {
    public static String decodeOctalEscapes(Object obj) {
        String input = obj.toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < input.length(); ) {
            char c = input.charAt(i);
            if (c == '\\' && i + 3 < input.length()) {
                String octal = input.substring(i + 1, i + 4);
                try {
                    int b = Integer.parseInt(octal, 8);
                    baos.write(b);
                    i += 4; // 跳过 \ + 3位八进制
                    continue;
                } catch (NumberFormatException e) {
                    // 不是合法八进制，按普通字符处理
                }
            }
            baos.write((byte) c);
            i++;
        }
        return baos.toString(StandardCharsets.UTF_8);
    }
}
