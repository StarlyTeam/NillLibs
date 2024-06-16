package kr.starly.libs.util;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MathUtils {

    public static final Random RANDOM = new SecureRandom();
}