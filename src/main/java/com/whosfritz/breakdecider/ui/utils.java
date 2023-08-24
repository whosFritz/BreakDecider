package com.whosfritz.breakdecider.ui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class utils {
    public static void showNotification(Notification.Position position, String whatToShow, NotificationVariant notificationVariant) {
        Notification notification = Notification.show(whatToShow, 3000, position);
        notification.addThemeVariants(notificationVariant);
    }

    public static String formatDateString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime date = LocalDateTime.parse(dateString, formatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE H:mm 'Uhr', dd. MMMM yyyy", Locale.GERMAN);
        return date.format(outputFormatter);
    }
}
