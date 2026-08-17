package com.acme.pmms.patterns;

import com.acme.pmms.model.*;

public class PenFactory {
    public static Pen createPen(String type, String id, String brand, double price) {
        switch (type.toLowerCase()) {
            case "gel":
                return new GelPen(id, brand, price, 1.8);
            case "ballpoint":
                return new BallpointPen(id, brand, price, true);
            default:
                throw new IllegalArgumentException("Unknown pen type: " + type);
        }
    }
}