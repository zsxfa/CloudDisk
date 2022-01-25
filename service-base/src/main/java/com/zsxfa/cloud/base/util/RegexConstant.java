package com.zsxfa.cloud.base.util;

/**
 * @author zsxfa
 */
public class RegexConstant {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    public static final String EMAIL_OR_PHONE_REGEX = "^([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)|(1[3456789]\\d{9})$";
    public static final String PHONE_REGEX = "^1[3456789]\\d{9}$";
    public static final String FILE_NAME_REGEX = "(?!((^(con)$)|^(con)/..*|(^(prn)$)|^(prn)/..*|(^(aux)$)|^(aux)/..*|(^(nul)$)|^(nul)/..*|(^(com)[1-9]$)|^(com)[1-9]/..*|(^(lpt)[1-9]$)|^(lpt)[1-9]/..*)|^/s+|.*/s$)(^[^/////:/*/?/\"/</>/|]{1,255}$)";
    public static final String PARENT_PATH_REGEX = "^(/.+?/)|(/)$";
    public static final String MD5_REGEX = "^[0-9a-z]{32}$";
    public static final String PASSWORD_REGEX = "^[^\\s\\u4e00-\\u9fa5]{6,20}$";
}
