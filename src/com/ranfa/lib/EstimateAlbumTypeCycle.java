package com.ranfa.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class EstimateAlbumTypeCycle {

	private final static String CYCLEPATH = "generated/albumCycle.json";
	private final static String DATEFORMAT = "YYYY/MM/dd";
	private final static SimpleDateFormat FORMAT = new SimpleDateFormat(DATEFORMAT);
	private static Logger logger = LoggerFactory.getLogger(EstimateAlbumTypeCycle.class);

	public final static String ALBUM_A = "ALBUM A";
	public final static String ALBUM_B = "ALBUM B";
	public final static String ALBUM_C = "ALBUM C";

	public static void Initialization() {
		if(Files.exists(Paths.get(CYCLEPATH))) {
			return;
		}
		logger.info("Cycle definition file does not exist.Trying to ask you...");
		AlbumCycleDefinitionProperty property = new AlbumCycleDefinitionProperty();
		String inputType = JOptionPane.showInputDialog("現在のMASTER＋のALBUMを入力してください。（A,B,C）");
		if(!(inputType.equals("A") || inputType.equals("B") || inputType.equals("C"))) {
			logger.error("inputType has invaild.Canceling initiate...");
			return;
		}
		String inputDaysLeft = JOptionPane.showInputDialog("MASTER＋のALBUM切り替えまであと何日ですか？\n(残り時間が表示されている場合は0を入力してください)");
		String dateDefinited = FORMAT.format(new Date());
		property.setDateDefinited(dateDefinited);
		property.setDaysLeft(Integer.parseInt(inputDaysLeft));
		property.setType(inputType.equals("A") ? ALBUM_A : inputType.equals("B") ? ALBUM_B : ALBUM_C);
		write(property);
		return;
	}

	private static void write(AlbumCycleDefinitionProperty property) {
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(Paths.get(CYCLEPATH).toFile(), property);
		} catch (IOException e) {
			logger.warn("Couldn't write album type information.", e);
		}
	}

	public static String getCurrentCycle() {
		if(Files.notExists(Paths.get(CYCLEPATH))) {
			throw new IllegalStateException("Program seems to have avoided first initiating. how could it have done?");
		}
		AlbumCycleDefinitionProperty property = new AlbumCycleDefinitionProperty();
		try {
			property = new ObjectMapper().readValue(Paths.get(CYCLEPATH).toFile(), AlbumCycleDefinitionProperty.class);
		} catch (IOException e) {
			logger.error("Error while reading local definition file.", e);
		}
		Date presentDate = new Date();
		Calendar presentCalendar = Calendar.getInstance();
		presentCalendar.setTime(presentDate);
		presentCalendar.set(Calendar.HOUR_OF_DAY, 0);
		presentCalendar.set(Calendar.MINUTE, 0);
		presentCalendar.set(Calendar.SECOND, 0);
		presentCalendar.set(Calendar.MILLISECOND, 0);
		presentDate = presentCalendar.getTime();
		String dateDefinited = property.getDateDefinited();
		String dates[] = dateDefinited.split("/");
		presentCalendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));
		Date definiteDate = presentCalendar.getTime();
		switch(presentDate.compareTo(definiteDate)) {
		case 0:
			return property.getType();
		case 1:
			LocalDate presentLocalDate = presentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate definitedLocalDate = definiteDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long delta = ChronoUnit.DAYS.between(definitedLocalDate, presentLocalDate);
			if(delta < property.getDaysLeft()) {
				return property.getType();
			}
			delta = delta - property.getDaysLeft();
			if(delta > Integer.MAX_VALUE) {
				JOptionPane.showMessageDialog(null, "ALBUM周期の推定に失敗しました。暫定的な措置として前回起動時のALBUM種類を表示します。\n(内部変数エラー：delta has the value that is more than Integer.MAX_VALUE.）");
				logger.error("Valuable was overflowed.");
			}
			String res = cycling(property.getType(), (int)delta);
			return res;
		default:
			throw new IllegalStateException("Date delta has illegal value. the system clock might be incorrect?");
		}
	}

	private static String cycling(String currentType, int times) {
		int cyclingDelta = (times / 14) % 3;
		String[] typeArray = {
				ALBUM_A,
				ALBUM_B,
				ALBUM_C
		};
		int currentIndex = Arrays.asList(typeArray).indexOf(currentType);
		int nextIndex = currentIndex + cyclingDelta;
		int nextIndexDelta = nextIndex % 3;
		return typeArray[nextIndexDelta];
	}

}
