package org.example.core.evaluation;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class TokenIterator implements Iterator<String> {

    private final List<String> tokens;
    private int index = 0;


    @Override
    public boolean hasNext() {
        return index < tokens.size();
    }

    @Override
    public String next() {
        return tokens.get(index++);
    }

}