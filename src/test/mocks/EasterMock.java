package test.mocks;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ranfa.lib.Easter;

public class EasterMock {
	
	public static String getSpecifiedDateBirth(Date date) throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		
		Class<?> clazz = Class.forName("com.ranfa.lib.Easter");
		Easter easter = new Easter();
		Field dateFormatField = clazz.getDeclaredField("dATE_FORMAT_STRING");
		dateFormatField.setAccessible(true);
		
		Map<String, String> birthData = easter.readBirthData();
		SimpleDateFormat format = new SimpleDateFormat(dateFormatField.get(easter).toString());
		String todayDateString = format.format(date);
		List<String> res =  birthData.entrySet().stream()
				.filter((e) -> e.getValue().equals(todayDateString))
				.map(e -> e.getKey())
				.collect(Collectors.toList());
		StringBuilder builder = new StringBuilder("Happy Birth Day! : ");
		for(String element : res) {
			builder.append(element);
		}
		if(builder.indexOf("氏家むつみ") != -1) {
			builder.append(" 「お誕生日って、なんだかわくわくしますよねっ！」");
		} else if(builder.indexOf("鷺沢文香") != -1) {
			builder.append(" 「素敵な物語の1頁を、一緒に刻んでいきましょう…プロデューサーさん」");
		}
		return res.isEmpty() ? "" : builder.toString();
	}

}
