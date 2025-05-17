package es.grupo18.jobmatcher.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.owasp.html.HtmlPolicyBuilder;

public class InputSanitizer {

    private static final PolicyFactory RICH_TEXT_POLICY =
        Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.STYLES);

    private static final PolicyFactory PLAIN_TEXT_POLICY =
        new HtmlPolicyBuilder().toFactory();

    public static String sanitizeRich(String input) {
        return input == null
            ? null
            : RICH_TEXT_POLICY.sanitize(input);
    }

    public static String sanitizePlain(String input) {
        return input == null
            ? null
            : PLAIN_TEXT_POLICY.sanitize(input);
    }

    public static String normalizeEmail(String email) {
        return email == null
            ? null
            : email.trim().toLowerCase();
    }
}
