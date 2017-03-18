package com.guoyi.circle.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.guoyi.circle.request.RetrofitProvider;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Credit on 2017/3/1.
 */

public class StringUtils {


    /**
     * 得到网页中图片的地址
     */
    public static List<String> getImgSrcList(String htmlStr) {
        List<String> pics = new ArrayList<String>();


        String[] regEX = {"<img.*?src=\"http://(.*?).jpg\"", "<img.*?src=\"http://(.*?).png\"", "<link.*?href=\"http://(.*?).ico\""};
        String[] Extension = {".jpg", ".png", ".ico"};

        for (int i = 0; i < regEX.length; i++) {
            Pattern p_image = Pattern.compile(regEX[i], Pattern.CASE_INSENSITIVE);
            Matcher m_image = p_image.matcher(htmlStr);
            while (m_image.find()) {
                String src = m_image.group(1);
                if (src.length() < 128) {
                    pics.add("http://" + src + Extension[i]);
                }
            }
            if (pics.size() > 0) {
                break;
            }
        }

        return pics;
/*

        String regEx_img = "<img.*?src=\"http://(.*?).jpg\""; // jpg图片链接地址
        String regEx_png = "<img.*?src=\"http://(.*?).png\""; // png图片链接地址
        String regEx_icon = "<link.*?href=\"http://(.*?).ico\""; // icon链接地址

        Pattern p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        Matcher m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            String src = m_image.group(1);
            if (src.length() < 100) {
                pics.add("http://" + src + ".jpg");
            }
        }

        if (pics.size() == 0) {
            p_image = Pattern.compile(regEx_png, Pattern.CASE_INSENSITIVE);
            m_image = p_image.matcher(htmlStr);
            while (m_image.find()) {
                String src = m_image.group(1);
                if (src.length() < 100) {
                    pics.add("http://" + src + ".png");
                }
            }
        }
        if (pics.size() == 0) {
            p_image = Pattern.compile(regEx_icon, Pattern.CASE_INSENSITIVE);
            m_image = p_image.matcher(htmlStr);
            while (m_image.find()) {
                String src = m_image.group(1);
                if (src.length() < 100) {
                    pics.add("http://" + src + ".ico");
                }
            }
        }

        return pics;
*/

    }

    /**
     * 获取网页
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();//通过输入流获取html数据
        byte[] data = readInputStream(inStream);//得到html的二进制数据
        String html = new String(data, "utf-8");
        conn.disconnect();
        return html;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 获取字符串中的url
     *
     * @param share
     * @return
     */
    public static String getStringUrl(String share) {
        Pattern pattern = Pattern.compile("http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(share);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group());
            buffer.append("\r\n");
        }

        return buffer.toString();
    }

    public static boolean isNumber(String paramString) {
        return paramString.matches("^[0-9]*$");
    }

    public static boolean isPhoneNumber(String paramString) {
        boolean bool1 = paramString.equals("");
        boolean bool2 = false;
        if (!bool1) {
            int i = paramString.length();
            bool2 = false;
            if (i == 11) {
                boolean bool3 = isNumber(paramString);
                bool2 = false;
                if (bool3) {
                    //
                    if ((!paramString.startsWith("13")) && (!paramString.startsWith("18")) && (!paramString.startsWith("15") && (!paramString.startsWith("14"))) && (!paramString.startsWith("17"))) {
                        bool2 = false;
                    } else {
                        bool2 = true;
                    }
                }
            }
        }
        return bool2;
    }


    public static int stringToInt(String string) {
        String str = string.substring(0, string.indexOf("."));
        int intgeo = Integer.parseInt(str);
        return intgeo;
    }

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copyText(String content, Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     * add by wangqianzhou
     *
     * @param context
     * @return
     */
    public static String pasteText(Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    public static String stringFormat(Context context, int sR, Object... o) {
        return String.format(context.getString(sR), o);
    }

    /**
     * String 大于maxLength时，替换为省略形式
     *
     * @param str
     * @param maxLength
     * @return
     */
    public static String textSub(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "..";
        } else {
            return str;
        }
    }

    /**
     * 格式化手机号码为 138 **** 123
     *
     * @param data 手机号码
     * @return
     */
    public static String separateFourPhone(String data) {
        String str3 = "";
        if (data == null && data.length() == 0)
            return str3;
        String str1 = new StringBuilder(data).reverse().toString();
        String str2 = data.substring(0, 3);
        str3 = new StringBuilder(str1.substring(0, 3)).reverse().toString();
        return str2 + "*****" + str3;
    }

    /**
     * 格式化字符串 例： 12345678 》》1234 5678
     *
     * @param data
     * @param sub
     * @return
     */
    public static String separateFourNum(String data, int sub) {
        if (data == null && data.length() == 0)
            return "";
        StringBuilder str2 = new StringBuilder();

        for (int i = 0; i < data.length(); i++) {
            if (i > 0 && i % sub == 0) {
                str2.append(' ');
            }
            str2.append(data.charAt(i));
        }
        return str2.toString();
    }


    private static String[] ImgSizes = {"s", "m", "l"};

    /**
     * 获取图片大小URL
     * <p>
     * <br>
     * l
     * 大=3
     * <br>
     * m
     * 中=2
     * <br>
     * s
     * 小=1
     *
     * @return
     */
    public static synchronized String getImage_Size_URL(String filename, String serverImgSize, String size) {


        if (TextUtils.isEmpty(serverImgSize)) {
            return filename;
        }
        if (serverImgSize.contains("s") || serverImgSize.contains("m") || serverImgSize.contains("l")) {
            String[] str = getFileNameAndExtensionDot(filename, true);
            String s = "";
            if (serverImgSize.contains(size)) {
                s = str[1] + "_" + size;
            } else {
                for (int i = 0; i < ImgSizes.length; i++) {
                    if (serverImgSize.contains(ImgSizes[i])) {
                        s = str[1] + "_" + ImgSizes[i];
                        break;
                    }
                }
                if (TextUtils.isEmpty(s)) {
                    return filename;
                }
            }

            return str[0] + s + str[1];
        }

        return filename;


    }

    /**
     * 获取图片大小URL,追加服务器IP
     *
     * @return
     */
    public static synchronized String getImage_SIZE(String filename, String serImgSize, String size) {
        String image_size_url = getImage_Size_URL(filename, serImgSize, size);
        return RetrofitProvider.ServerIp + "/" + image_size_url;
    }

    public static synchronized String getImageDefaultURL(String url) {
        return RetrofitProvider.ServerIp + "/" + url;
    }

    /**
     * 使用正则表达式获取文件名，不匹配则不是图片文件
     *
     * @param filename
     * @return
     */
    public static boolean regexIsImageFile(String filename) {
        // 定义正则表达式\
        //图片格式是计算机存储图片的格式，常见的存储的格式有bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF等。
        String re = "^.*?\\.(jpg|jpeg|bmp|gif|png|ico|svg|ai|raw|tiff|tga|exif|psd|eps|pcx|fpx|cdr|pcd|dxf|ufo|hdri)$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(re);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filename);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }

    /**
     * 获取文件名与文件后缀，String[0]文件名,String[1]带点的后缀.jpg .png
     *
     * @param file
     * @return
     */
    public static String[] getFileNameAndExtensionDot(String file, boolean loadimage) {
        String extensionName = getExtensionName(file, true);
        String fileNameNoEx = getFileNameNoEx(file);
        String[] s = null;
        if (loadimage) {
            if (regexIsImageFile(file)) {
                s = new String[]{fileNameNoEx, extensionName};
            }
        } else {
            s = new String[]{fileNameNoEx, extensionName};
        }
        return s;
    }

    /**
     * 获取文件名与文件后缀，String[0]文件名,String[1]不带点的后缀 jpg png
     *
     * @param file
     * @return
     */
    public static String[] getFileNameAndExtensionNoDot(String file, boolean loadimage) {
        String extensionName = getExtensionName(file, false);
        String fileNameNoEx = getFileNameNoEx(file);
        String[] s = null;
        if (loadimage) {
            if (regexIsImageFile(file)) {
                s = new String[]{fileNameNoEx, extensionName};
            }
        } else {
            s = new String[]{fileNameNoEx, extensionName};
        }
        return s;


    }

    /*
         * Java文件操作 获取文件扩展名,带点.jpg .png
         *
         */
    public static String getExtensionName(String filename, boolean dots) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                if (dots) {
                    return filename.substring(dot);
                }
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取URL文件获取文件名与文件后缀
     */
    public static String getURLFileName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }


    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 提取出城市或者县
     *
     * @param city
     * @param district
     * @return
     */
    public static String extractLocation(final String city, final String district) {
        if (district.contains("县") && district.length() > 2) {
            return district.substring(0, district.length() - 1);
        } else if (city.contains("市") && city.length() > 2) {
            return city.substring(0, city.length() - 1);
        }
        return city;
    }

    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
