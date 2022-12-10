package pl.edu.pw.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final int KLIK_CODE_LENGTH = 6;
    public static final int KLIK_DURATION_SECONDS = 120;
    public static final int KLIK_CONFIRM_PAYMENT_DURATION_SECONDS = 60;
    public static final String NOTIFICATION_EVENT_NAME = "transfer_notification";
    public final static String NO_ACTIVE_KLIK_PAYMENT = "There is no active klik payment to finalize for this client";

}
