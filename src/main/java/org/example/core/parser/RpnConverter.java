package org.example.core.parser;

import java.util.List;

public interface RpnConverter {
    List<String> toRpn(List<String> tokens);
}