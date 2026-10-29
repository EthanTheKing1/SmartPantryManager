package com.example.smartpantrymanager;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

public final class ButtonStyler {

    private ButtonStyler() {
        // Prevent creating unnecessary instances.
    }

    public static void primary(Button button) {
        style(button, Color.rgb(212, 184, 121),
                Color.rgb(16, 37, 29), 14);
    }

    public static void secondary(Button button) {
        style(button, Color.rgb(37, 68, 54),
                Color.rgb(244, 240, 228), 14);
    }

    public static void danger(Button button) {
        style(button, Color.rgb(102, 55, 45),
                Color.rgb(244, 240, 228), 12);
    }

    private static void style(
            Button button, int backgroundColor,
            int textColor, int radiusDp) {

        GradientDrawable background = new GradientDrawable();
        background.setColor(backgroundColor);
        background.setCornerRadius(
                radiusDp * button.getResources()
                        .getDisplayMetrics().density);

        button.setBackgroundTintList(null);
        button.setBackground(background);
        button.setTextColor(textColor);
    }
}
