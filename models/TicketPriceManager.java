package models;

import java.awt.Color;

public class TicketPriceManager {
    // Ticket prices
    public static final double VIP_PRICE = 100.00;
    public static final double STANDARD_PRICE = 50.00;
    public static final double DISCOUNTED_PRICE = 35.00;

    // Colors for UI
    public static final Color VIP_COLOR = new Color(147, 112, 219);
    public static final Color STANDARD_COLOR = new Color(100, 149, 237);
    public static final Color DISCOUNTED_COLOR = new Color(60, 179, 113);

    public static double getTicketPrice(String ticketType) {
        if (ticketType == null)
            return STANDARD_PRICE;

        switch (ticketType.toLowerCase()) {
            case "vip":
                return VIP_PRICE;
            case "discounted":
                return DISCOUNTED_PRICE;
            case "standard":
            default:
                return STANDARD_PRICE;
        }
    }

    public static Color getTicketColor(String ticketType) {
        if (ticketType == null)
            return STANDARD_COLOR;

        switch (ticketType.toLowerCase()) {
            case "vip":
                return VIP_COLOR;
            case "discounted":
                return DISCOUNTED_COLOR;
            case "standard":
            default:
                return STANDARD_COLOR;
        }
    }

    public static String getPriceInfo() {
        return String.format(
                "VIP: ₱%.2f | Standard: ₱%.2f | Discounted: ₱%.2f",
                VIP_PRICE, STANDARD_PRICE, DISCOUNTED_PRICE);
    }
}