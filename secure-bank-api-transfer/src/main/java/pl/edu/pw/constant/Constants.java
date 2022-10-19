package pl.edu.pw.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final int KLIK_CODE_LENGTH = 6;
    public static final int KLIK_DURATION_SECONDS = 120;
    public static final String NOTIFICATION_EVENT_NAME = "transfer_notification";
}
