package kr.starly.libs.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Pair<A, B> {

    private A first;
    private B second;
}