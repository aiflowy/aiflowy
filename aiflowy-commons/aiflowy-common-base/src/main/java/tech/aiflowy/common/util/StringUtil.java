package tech.aiflowy.common.util;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class StringUtil extends StringUtils {

    public static boolean areHasText(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }

        for (String string : strings) {
            if (!hasText(string)) {
                return false;
            }
        }
        return true;
    }

    public static boolean noText(String string) {
        return !hasText(string);
    }

    public static boolean isEmail(String email) {
        return StringUtils.hasText(email)
                && email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    public static boolean isMobileNumber(String str) {
        return hasText(str) && str.length() == 11 && str.startsWith("1") && isNumeric(str);
    }

    public static boolean isNumeric(String str) {
        if (noText(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    public static String getHasTextOne(String... strings) {
        for (String string : strings) {
            if (hasText(string)) {
                return string;
            }
        }
        return null;
    }


    public static Set<String> splitToSet(String src, String regex) {
        if (src == null) {
            return Collections.emptySet();
        }

        String[] strings = src.split(regex);
        Set<String> set = new LinkedHashSet<>();
        for (String s : strings) {
            if (hasText(s)) {
                set.add(s.trim());
            }
        }
        return set;
    }

    public static Set<String> splitToSetByComma(String src) {
        return splitToSet(src, ",");
    }


    /**
     * 删除文件名的后缀。
     *
     * @param fileName 完整的文件名，包括后缀
     * @return 不带后缀的文件名
     */
    public static String removeFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName; // 没有后缀
        }

        return fileName.substring(0, dotIndex);
    }

}
