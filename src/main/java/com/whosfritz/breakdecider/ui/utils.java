package com.whosfritz.breakdecider.ui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class utils {
    public static void showNotification(Notification.Position position, String whatToShow, NotificationVariant notificationVariant) {
        Notification notification = Notification.show(whatToShow, 3000, position);
        notification.addThemeVariants(notificationVariant);
    }
}
