package net.tcpshield.tcpshield.validation.impl;

import net.tcpshield.tcpshield.Constants;
import net.tcpshield.tcpshield.validation.ITimestampValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * inspired by https://github.com/iridium77/htpdate/blob/master/htpdate.c
 */
public class HTPDateTimestampValidatorImpl implements ITimestampValidator {

    private volatile long timestampOffset = 0;

    public HTPDateTimestampValidatorImpl() {
        ForkJoinPool.commonPool().submit(() -> {
            try {
                syncTimestamp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }); // do not execute network requests on the main thread
    }

    @Override
    public boolean validateTimestamp(long checkedTimestamp) {
        long currentTime = getTime();

        return checkedTimestamp >= (currentTime - Constants.MAX_TIME_DIFFERENCE) && checkedTimestamp <= (currentTime + Constants.MAX_TIME_DIFFERENCE);
    }

    private void syncTimestamp() throws IOException {
        try (Socket socket = new Socket("tcpshield.com", 80)) {
            // no-cache forces the server to return time
            String payload = "HEAD http://tcpshield.com/ HTTP/1.1\r\nHost: tcpshield.com\r\nUser-Agent: tcpshield/1.0\r\nPragma: no-cache\r\nCache-Control: no-cache\r\nConnection: close\r\n\r\n";
            socket.getOutputStream().write(payload.getBytes());

            long readTime = System.currentTimeMillis(); // assuming server -> client time is negligible

            List<String> response = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.toList());

            Date serverDate = extractDate(response);

            timestampOffset = Math.round((serverDate.getTime() - readTime) / 1000.0) * 1000; // the HTTP protocol only returns time in seconds; round offset
        }
    }

    private Date extractDate(List<String> response) {
        for (String line : response) {
            if (!line.startsWith("Date: ")) continue;

            String dateString = line.substring("Date: ".length());
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            try {
                return sdf.parse(dateString);
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        throw new IllegalArgumentException("no date line found - response: " + response);
    }

    @Override
    public long getTime() {
        return (System.currentTimeMillis() + timestampOffset) / 1000;
    }
}
