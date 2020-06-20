package com.yazao.lib.util;

public class StringUtil {
    /**
     * is null or its length is 0 or it is made by space
     * <p>
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return
     * true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * is null or its length is 0
     * <p>
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return
     * false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 检查用户输入是否合法
     *
     * @param username
     */
    public static boolean checkUserName(String username) {
        if (isBlank(username)) {
            return false;
        }

        boolean result = checkPhone(username);
        if (result) {
            return true;
        } else {
//        //TODO  6-11位数字字母组合，字母不区分大小写，且禁止五连以上数字
//        String regex = "^[a-z0-9]{6,11}$";

            //格式为6-12字母数字组合
//            String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{6,12}$";//仅支持： 数字字母组合
            String regex = "^(?![0-9]+$)[a-zA-Z0-9]{6,12}$";//仅支持： 非纯数字，纯字母，数字字母组合

//            return username.matches(regex);//// 31/07/2017  为了验证规则的随时改变，客户端暂不处理验证问题，都交由服务端来处理验证。
            return true;

        }


    }

    /**
     * 检查密码的合法性（格式为6-12字母、数字任意）
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (isBlank(password)) {
            return false;
        }

//        //TODO 格式为6-12字母、数字任意
//        String regex = "^[a-zA-Z0-9]{6,12}$";

        //TODO 格式为6-12位密码（至少含数字、字母、字符2中组合）
        String regex = "^[a-zA-Z0-9]{6,12}$";
//        return password.matches(regex); // 31/07/2017  为了验证规则的随时改变，客户端暂不处理验证问题，都交由服务端来处理验证。
        return true;
    }

    /**
     * 检查手机号合法性
     *
     * @param contact
     * @return
     */
    public static boolean checkPhone(String contact) {
        if (isBlank(contact)) {
            return false;
        }

        // TODO: 13/04/2017 手机号或者邮箱合法行
        String regex;
//        if (contact.contains("@")) {
//            //email //  \\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*
//            regex = "^\\w+((-\\w+)|(\\.\\w+))*@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
//        } else {
//            //phone
//            regex = "^1(3|4|5|7|8)\\d{9}$";
//
//        }

        //phone
        regex = "^1(3|4|5|7|8)\\d{9}$";
        return contact.matches(regex);
    }

    /**
     * 检查邮箱合法性
     *
     * @param contact
     * @return
     */
    public static boolean checkEmail(String contact) {
        if (isBlank(contact)) {
            return false;
        }

        // TODO: 13/04/2017 手机号或者邮箱合法行
        String regex;
//        if (contact.contains("@")) {
        //email //  \\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*
        regex = "^\\w+((-\\w+)|(\\.\\w+))*@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
//        } else {
//            //phone
//            regex = "^1(3|4|5|7|8)\\d{9}$";
//
//        }

        return contact.matches(regex);
    }

    /**
     * 检查验证码合法性（验证码为6位纯数字）
     *
     * @param code
     * @return
     */
    public static boolean checkVerifyCode(String code) {
        if (isBlank(code)) {
            return false;
        }

        // TODO: 13/04/2017   判断纯数字
        String regex = "^\\d{6}$";
        return code.matches(regex);
    }

    /**
     * 方法描述：隐藏手机号码中间四位，用*代替
     *
     * @return
     * @author zhaishaoping
     * @time 08/07/2017 7:01 PM
     */
    public static String hidePhone(String phone) {
        if (isBlank(phone)) {
            return "";
        }
        //$1，$2分别匹配第一个括号和第二个括号中的内容
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 方法描述：隐藏邮箱，只显示@前面的首位和末位
     *
     * @return
     * @author zhaishaoping
     * @time 08/07/2017 7:03 PM
     */
    public static String hideEmail(String email) {
        if (isBlank(email)) {
            return "";
        }
        //$1，$3,$4分别匹配第一个括号、第三个括号 和 第四个括号中的内容
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }
}
