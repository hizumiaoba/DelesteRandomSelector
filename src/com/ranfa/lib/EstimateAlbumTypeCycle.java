package com.ranfa.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class EstimateAlbumTypeCycle {

	private final static String CYCLEPATH = "generated/albumCycle.json";
	private final static String DATEFORMAT = "YYYY:MM:DD";
	private final static SimpleDateFormat FORMAT = new SimpleDateFormat(DATEFORMAT);

	public final static String ALBUM_A = "ALBUM A";
	public final static String ALBUM_B = "ALBUM B";
	public final static String ALBUM_C = "ALBUM C";

	public static void firstInitiate() {
		if(Files.exists(Paths.get(CYCLEPATH)))
			return;
		LimitedLog.println(EstimateAlbumTypeCycle.class + ":[INFO]: " + "Cycle definition file does not exist.Trying to ask you...");
		AlbumCycleDefinitionProperty property = new AlbumCycleDefinitionProperty();
		String inputType = JOptionPane.showInputDialog("現在のMASTER＋のALBUMを入力してください。（A,B,C）");
		if(!inputType.equals("A") || !inputType.equals("B") || !inputType.equals("C")) {

			LimitedLog.println(EstimateAlbumTypeCycle.class + ":[FATAL]; " + "inputType has invaild.Canceling initiate...");
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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static String getCurrentCycle() {
		if(Files.notExists(Paths.get(CYCLEPATH)))
			throw new IllegalStateException("Program seems to have avoided first initiating. how could it have done?");
		AlbumCycleDefinitionProperty property = new AlbumCycleDefinitionProperty();
		try {
			property = new ObjectMapper().readValue(Paths.get(CYCLEPATH).toFile(), AlbumCycleDefinitionProperty.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int dayPresent = Integer.parseInt(new SimpleDateFormat("DD").format(new Date()));
		int dayDefinited = Integer.parseInt(property.getDateDefinited().substring(8));
		int daysLeft = property.getDaysLeft();
		if(dayPresent - dayDefinited <= 1 && daysLeft == 0) {
			int delta = dayPresent - dayDefinited;
			int weeksPassed = delta / 14;
			if(weeksPassed > 0) {
				int weeksPassedDelta = weeksPassed / 3;
				
			}
		}
		return property.getType();
	}

}
