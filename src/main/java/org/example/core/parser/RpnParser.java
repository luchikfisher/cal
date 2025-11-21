package org.example.core.parser;

import java.util.List;

public interface RpnParser {
    List<String> toRpn(List<String> tokens);
}