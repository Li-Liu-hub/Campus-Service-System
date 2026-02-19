package com.jsyl.utils;

public class HtmlSanitizerUtil {

    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }
        return html
                .replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<style[^>]*>.*?</style>", "")
                .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("on\\w+=\"[^\"]*\"", "")
                .replaceAll("on\\w+='[^']*'", "");
    }

}
