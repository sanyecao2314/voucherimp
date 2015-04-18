package com.citsamex.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 类名:StringUtil
 * </p>
 * <p>
 * 类说明:字符串帮助类。
 * </p>
 *
 * @version 1.0
 * @since J2SDK1.4.2
 * @author KINGDEE 黎亮
 */

public class StringUtil {

	public static boolean stringIsEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}


}
