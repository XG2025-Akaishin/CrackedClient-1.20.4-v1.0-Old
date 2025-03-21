package me.alpha432.oyvey.features.modules.render.worldtweaks.colorutil;

public class ColorUtils {
    private static int color;//Color :(

    //Color Setting GL Color
    public static int getRed() {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue() {
        return (color) & 0xFF;
    }

    public static float getGlRed() {
        return getRed() / 255f;
    }

    public static float getGlBlue() {
        return getBlue() / 255f;
    }

    public static float getGlGreen() {

        return getGreen() / 255f;
    }

    public static float getGlAlpha() {
        return getAlpha() / 255f;
    }

    public static int getAlpha() {
        return (color >> 24) & 0xff;
    }
}
