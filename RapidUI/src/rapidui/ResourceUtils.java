package rapidui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtils {
	private static final Pattern patternEndsWidthNumber = Pattern.compile("_?[0-9]+$");
	
	public static String toLowerUnderscored(String s) {
		String postfix = "";
		
		Matcher m = patternEndsWidthNumber.matcher(s);
		if (m.find()) {
			final String str = m.group();
			if (str.charAt(0) == '_') {
				postfix = str;
			} else {
				postfix = '_' + str;
			}
			
			s = s.substring(0, s.length() - str.length());
		}

		final int STATE_NONE = 0;
		final int STATE_UPPER_CASE = 1;
		final int STATE_LOWER_CASE = 2;
		
		final StringBuilder sb = new StringBuilder();

		int upperStartIndex = -1;
		int state = STATE_NONE;
		
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			
			final int newState;
			if (Character.isUpperCase(c)) {
				newState = STATE_UPPER_CASE;
			} else if (Character.isLowerCase(c)) {
				newState = STATE_LOWER_CASE;
			} else {
				if (c == '_') {
					newState = STATE_NONE;
				} else {
					newState = state;
				}
			}
			
			switch (newState) {
			case STATE_UPPER_CASE:
				if (state != STATE_UPPER_CASE) {
					upperStartIndex = i;
					if (state == STATE_LOWER_CASE) {
						sb.append('_');
					}
				}
				break;
				
			case STATE_LOWER_CASE:
				if (state == STATE_UPPER_CASE) {
					if (upperStartIndex < i - 1) {
						for (int j = upperStartIndex; j < i - 1; ++j) {
							sb.append(Character.toLowerCase(s.charAt(j)));
						}
						sb.append('_');
					}
					
					sb.append(Character.toLowerCase(s.charAt(i - 1)))
					  .append(c);
					
					upperStartIndex = -1;
				} else {
					sb.append(c);
				}
				break;
				
			case STATE_NONE:
				sb.append(c);
				break;
			}
			
			state = newState;
		}
		
		if (upperStartIndex >= 0) {
			for (int i = upperStartIndex; i < s.length(); ++i) {
				sb.append(Character.toLowerCase(s.charAt(i)));
			}
		}
		
		sb.append(postfix);
		return sb.toString();
	}

	public static int findResourceId(Context context, String name, String type) {
		final Resources res = context.getResources();
		final String packageName = context.getPackageName();
		
		int id = res.getIdentifier(ResourceUtils.toLowerUnderscored(name), type, packageName);
		if (id == 0) {
			id = res.getIdentifier(name, type, packageName);
		}
		
		return id;
	}
}
