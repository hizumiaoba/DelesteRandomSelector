/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.ranfa.lib.CheckVersion;
import com.ranfa.lib.Easter;
import com.ranfa.lib.ManualUpdateThreadImpl;
import com.ranfa.lib.SettingJSONProperty;
import com.ranfa.lib.Settings;
import com.ranfa.lib.Suffix;
import com.ranfa.lib.TwitterIntegration;
import com.ranfa.lib.Version;
import com.ranfa.lib.calc.FanCalc;
import com.ranfa.lib.calc.PRPCalc;
import com.ranfa.lib.concurrent.CountedThreadFactory;
import com.ranfa.lib.database.EstimateAlbumTypeCycle;
import com.ranfa.lib.database.Scraping;
import com.ranfa.lib.database.Song;
import com.ranfa.lib.handler.CrashHandler;
import com.ranfa.lib.io.FileIO;
import com.ranfa.lib.io.OutputDataStructure;
import com.ranfa.lib.songinfo.FetchFromAPI;

/**
 * ???????????????????????????
 * <p>
 * ?????????????????????????????????????????????????????????
 * 
 * @author hizum
 *
 * @since 0.0.1
 */
@Version(major = 4, minor = 0, patch = 3, suffix = Suffix.BETA)
public class DelesteRandomSelector extends JFrame {

	/**
	 * ????????????????????????????????????????????????????????????List???
	 * <p>
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 */
    private static ArrayList<Song> selectedSongsList = new ArrayList<>();
    
    private static boolean isDebugMode = false;

    private JPanel contentPane;
    private SettingJSONProperty property = new SettingJSONProperty();
    private String[] integratorArray;
    private boolean integratorBool = false;
    private CompletableFuture<Void> softwareUpdateFuture = null;
    private CompletableFuture<Void> albumTypeEstimateFuture = null;
    private String albumType = Messages.MSGAlbumTypeBeingCalculated.toString();
    private Logger logger = LoggerFactory.getLogger(DelesteRandomSelector.class);
    private ManualUpdateThreadImpl impl;
    private List<Song> toolIntegrateList;
    private FetchFromAPI fetchData;
    private List<Map<String, String>> listToolMapData;
    private CompletableFuture<List<Map<String, String>>> listToolMapDataFuture;
    private Easter easter;
    private JPanel panelMain;
    private JPanel panelNorthMain;
    private JLabel labelTitle;
    private JLabel labelVersion;
    private JPanel panelWestMain;
    private JLabel labelDifficulty;
    private JComboBox<String[]> comboDifficultySelect;
    private JComboBox<String[]> comboAttribute;
    private JLabel labelLevel;
    private JSpinner spinnerLevel;
    private JCheckBox checkLessLv;
    private JCheckBox checkMoreLv;
    private JLabel labelLvCaution;
    private JPanel panelEastMain;
    private JButton btnImport;
    private JButton btnConfig;
    private JButton btnStart;
    private JButton btnManualUpdate;
    private JButton btnTwitterIntegration;
    private JButton btnExit;
    private JPanel panelCenterMain;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JTabbedPane tabbedPane;
    private JPanel panelInfo;
    private JPanel panelNorthTool;
    private JLabel labelSubToolTitle;
    private JLabel labelVersionTool;
    private JPanel panelCenterTool;
    private JLabel labelInfoPlaySongs;
    private JLabel labelSongNameToolTitle;
    private JLabel labelSongNameToolTip;
    private JLabel labelAttributeToolTitle;
    private JLabel labelAttributeToolTip;
    private JLabel labelDifficultyToolTitle;
    private JLabel labelDifficultyToolTip;
    private JLabel labelLevelToolTitle;
    private JLabel labelLevelToolTip;
    private JLabel labelNotesToolTitle;
    private JLabel labelNotesToolTip;
    private JButton btnPrevSongTool;
    private JButton btnNextSongTool;
    private JLabel labelSlashTool;
    private JLabel labelCurrentSongOrderTool;
    private JLabel labelSongLimitTool;
    private JLabel labelLyricToolTitle;
    private JLabel labelLyricToolTip;
    private JLabel labelComposerToolTitle;
    private JLabel labelArrangeToolTitle;
    private JLabel labelComposerToolTip;
    private JLabel labelArrangeToolTip;
    private JLabel labelMemberToolTitle;
    private JLabel labelMemberToolTip;
    private JButton btnMoreInfoTool;
    
    /**
     * ??????????????????????????????{@link BiConsumer}???
     * <p>
     * ?????????{@link CompletableFuture}??????????????????????????????
     */
    BiConsumer<List<Song>, List<Song>> updateConsumer = (list1, list2) -> {
	    this.logger.info("Checking database updates...");
	    if(list1.size() > list2.size()) {
		long time = System.currentTimeMillis();
		this.logger.info("{} Update detected.", (list1.size() - list2.size()));
		Scraping.writeToJson(list1);
		this.logger.info("Update completed in {} ms", (System.currentTimeMillis() - time));
		this.logger.info("Updated database size: {}", list1.size());
	    } else {
		this.logger.info("database is up-to-date.");
	    }
	};
	
	/**
	 * ??????????????????????????????????????????{@link JButton}???????????????????????????{@link Runnable}???
	 * <p>
	 * <ul>
	 * <li>{@link JButton}????????????</li>
	 * <li>?????????????????????????????????</li>
	 * </ul>
	 * ?????????
	 */
	Runnable setEnabled = () -> {
	    try {
		Thread.sleep(3 * 1000L);
	    } catch (InterruptedException e1) {
		this.logger.error("Thread has been interrupted during waiting cooldown.", e1);
	    }
	    this.btnImport.setEnabled(true);
	    this.btnImport.setText(Messages.MSGNarrowingDownSongs.toString());
	};
	private JLabel labelToolProgress;
	public static JProgressBar progressTool;
	public static JLabel labelInfoProgressSongName;
	private JPanel panelScore;
	private JPanel panelScoreNorth;
	private JPanel panelScoreCenter;
	private JLabel labelScoreTitle;
	private JLabel labelScoreVersion;
	private JLabel labelScoreUserPlayed;
	private JTextField fieldScoreUserPlayed;
	private JLabel labelScoreEarnedFan;
	private JTextField fieldScoreEarnedFan;
	private JLabel lblSongname;
	private JLabel labelScoreAttribute;
	private JLabel labelScoreDifficulty;
	private JLabel labelScoreLevel;
	private JLabel labelScoreNotes;
	private JLabel labelScoreSongnameDynamic;
	private JLabel labelScoreAttributeDynamic;
	private JLabel labelScoreDifficultyDynamic;
	private JLabel labelScoreLevelDynamic;
	private JLabel labelScoreNotesDynamic;
	private JButton btnScorePrev;
	private JButton btnScoreNext;
	private JLabel labelScoreCurrentSongOrder;
	private JLabel labelScoreSlash;
	private JLabel labelScoreOrderMax;
	private JLabel labelPlayerScore;
	private JLabel labelPlayerFan;
	private JLabel labelPlayerPRP;
	private JLabel labelPlayerScoreDynamic;
	private JLabel labelPlayerFanDynamic;
	private JLabel labelPlayerPRPDynamic;
	private JTextField fieldScoreRoom;
	private JTextField fieldScoreCenter;
	private JTextField fieldScoreProduce;
	private JTextField fieldScorePremium;
	private JLabel label;
	private JLabel lblRoom;
	private JLabel lblCenter;
	private JLabel lblProduce;
	private JLabel lblPremium;
	private JButton button;
	private JButton btnScoreFileOut;
	private JButton btnScoreReadFile;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
    	if(args.length == 1) {
    		isDebugMode = Boolean.valueOf(args[0]);
    	}
	EventQueue.invokeLater(() -> {
	    try {
		DelesteRandomSelector frame = new DelesteRandomSelector();
		frame.setVisible(true);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	});
    }

    /**
     * Create the frame.
     */
    public DelesteRandomSelector() {
    ExecutorService es = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "AsyncEventInquerier", false));
	this.contentPane = new JPanel();
	// output system info phase
	CompletableFuture.runAsync(() -> {
		CrashHandler handle = new CrashHandler();
		handle.outSystemInfo();
	}, es).whenCompleteAsync((ret, ex) -> {
		if(ex != null) {
			logger.error("Exception was thrown during concurrent process", ex);
			CrashHandler handle = new CrashHandler(ex);
			if(ex instanceof NullPointerException) {
				handle.execute();
			}
			handle = new CrashHandler(new IllegalStateException(ex));
			handle.execute();
		}
	}, es);
	boolean isFirst = !Scraping.databaseExists();
	// database check phase
	CompletableFuture.runAsync(() -> {
		if(isFirst) {
		    JOptionPane.showMessageDialog(this, Messages.MSGDatabaseNotExist.toString());
		    if(!Scraping.writeToJson(Scraping.getWholeData())) {
			JOptionPane.showMessageDialog(this, "Exception:NullPointerException\nCannot Keep up! Please re-download this Application!");
			throw new NullPointerException("FATAL: cannot continue!");
		    }
		}
	}, es).whenCompleteAsync((ret, ex) -> {
		if(ex != null) {
			logger.error("Exception was thrown during concurrent process", ex);
			CrashHandler handle = new CrashHandler(ex.getMessage(), ex);
			if(ex instanceof NullPointerException) {
				handle.execute();
			}
			handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
			handle.execute();
		}
	}, es);
	CompletableFuture<List<Song>> getFromJsonFuture = CompletableFuture.supplyAsync(() -> Scraping.getFromJson(), es);
	CompletableFuture<List<Song>> getWholeDataFuture = CompletableFuture.supplyAsync(() -> Scraping.getWholeData(), es);
	// setting check phase
	CompletableFuture.runAsync(() -> {
		if(!Settings.fileExists() && !Settings.writeDownJSON()) {
		    JOptionPane.showMessageDialog(this, "Exception:NullPointerException\nPlease see crash report for more detail.");
		    CrashHandler handle = new CrashHandler("Failed to generate setting file.", new NullPointerException("FATAL: cannot continue!"));
		    handle.execute();
		}
	}, es).whenCompleteAsync((ret, ex) -> {
		if(ex != null) {
			logger.error("Exception was thrown during concurrent process", ex);
			CrashHandler handle = new CrashHandler(ex.getMessage(), ex);
			if(ex instanceof NullPointerException) {
				handle.execute();
			}
			handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
			handle.execute();
		}
		if(isDebugMode) {
			logger.debug("WARNING: This is the debug mode. you cannot use outside API services.");
		}
		this.logger.debug("Loading settings...");
		this.property.setCheckLibraryUpdates(Settings.needToCheckLibraryUpdates());
		this.property.setCheckVersion(Settings.needToCheckVersion());
		this.property.setWindowWidth(Settings.getWindowWidth());
		this.property.setWindowHeight(Settings.getWindowHeight());
		this.property.setSongLimit(Settings.getSongsLimit());
		this.property.setSaveScoreLog(Settings.saveScoreLog());
		this.logger.debug("Load settings done.");
		this.logger.debug("Version check: {}", this.property.isCheckVersion());
		this.logger.debug("Library update check: {}", this.property.isCheckLibraryUpdates());
		this.logger.debug("Window Width: {}", this.property.getWindowWidth());
		this.logger.debug("Window Height: {}", this.property.getWindowHeight());
		this.logger.debug("Song Limit: {}", this.property.getSongLimit());
		this.logger.debug("SaveScoreLog: {}", this.property.isSaveScoreLog());
		this.setBounds(100, 100, this.property.getWindowWidth(), this.property.getWindowHeight());
		if(this.property.isCheckVersion()) {
		    this.softwareUpdateFuture = CompletableFuture.runAsync(() -> CheckVersion.needToBeUpdated(), es);
		}
		if(this.property.isCheckLibraryUpdates()) {
		    CompletableFuture<Void> updatedFuture = getWholeDataFuture.thenAcceptBothAsync(getFromJsonFuture, updateConsumer, es);
		    updatedFuture.thenRunAsync(setEnabled, es);
		}
	}, es);
	CompletableFuture.runAsync(() -> {
		EstimateAlbumTypeCycle.Initialization();
		if(Files.exists(Paths.get("generated/albumCycle.json"))) {
		    this.albumType = EstimateAlbumTypeCycle.getCurrentCycle();
		}
	}, es).whenCompleteAsync((ret, ex) -> {
		if(ex != null) {
			logger.error("Exception was thrown during concurrent process", ex);
			CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
			handle.execute();
		}
	}, es);
	getWholeDataFuture.thenAcceptAsync(list -> this.logger.info("Scraping data size:" + list.size()), es);
	getFromJsonFuture.thenAcceptAsync(list -> this.logger.info("Currently database size:" + list.size()), es);
	// easter phase
	CompletableFuture.runAsync(() -> {
		this.easter = new Easter();
		this.setTitle(this.easter.getTodaysBirth());
	}, es);
	this.logger.debug("Version: {}", CheckVersion.getVersion());
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setBounds(100, 100, 960, 643);
	this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	this.setContentPane(this.contentPane);
	contentPane.setLayout(new CardLayout(0, 0));
	
	panelMain = new JPanel();
	panelMain.setLayout(new BorderLayout(0, 0));
	
	panelNorthMain = new JPanel();
	panelMain.add(panelNorthMain, BorderLayout.NORTH);
	panelNorthMain.setLayout(new FormLayout(new ColumnSpec[] {
			ColumnSpec.decode("829px"),
			FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
			ColumnSpec.decode("center:94px"),},
		new RowSpec[] {
			RowSpec.decode("20px"),}));
	
	labelTitle = new JLabel(Messages.MSGTitle.toString());
	labelTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 16));
	panelNorthMain.add(labelTitle, "1, 1, center, top");
	
	labelVersion = new JLabel(CheckVersion.getVersion());
	labelVersion.setFont(new Font("SansSerif", Font.BOLD, 12));
	panelNorthMain.add(labelVersion, "3, 1, center, center");
	
	panelWestMain = new JPanel();
	panelMain.add(panelWestMain, BorderLayout.WEST);
	panelWestMain.setLayout(new FormLayout(new ColumnSpec[] {
			FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
			ColumnSpec.decode("120px"),},
		new RowSpec[] {
			FormSpecs.LINE_GAP_ROWSPEC,
			RowSpec.decode("25px"),
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			RowSpec.decode("max(41dlu;default)"),}));
	
	labelDifficulty = new JLabel(Messages.MSGSelectDifficulty.toString());
	labelDifficulty.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(labelDifficulty, "2, 2, center, center");
	
	comboDifficultySelect = new JComboBox<>();
	comboDifficultySelect.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "DEBUT", "REGULAR", "PRO", "MASTER", "MASTER+", "???MASTER+", "LIGHT", "TRICK", "PIANO", "FORTE", "WITCH"}));
	comboDifficultySelect.setFont(new Font("Dialog", Font.BOLD, 12));
	panelWestMain.add(comboDifficultySelect, "2, 4, fill, fill");
	
	comboAttribute = new JComboBox<>();
	comboAttribute.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "????????????", "????????????", "?????????", "???????????????"}));
	comboAttribute.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(comboAttribute, "2, 6, fill, top");
	
	labelLevel = new JLabel(Messages.MSGSongLevel.toString());
	labelLevel.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(labelLevel, "2, 8, center, center");
	
	spinnerLevel = new JSpinner();
	panelWestMain.add(spinnerLevel, "2, 10, fill, center");
	
	checkLessLv = new JCheckBox(Messages.MSGBelowSpecificLevel.toString());
	checkLessLv.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(checkLessLv, "2, 12, left, top");
	
	checkMoreLv = new JCheckBox(Messages.MSGOverSpecificLevel.toString());
	checkMoreLv.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(checkMoreLv, "2, 14, left, top");
	
	labelLvCaution = new JLabel(Messages.MSGLevelCheckboxInfo.toString());
	labelLvCaution.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelWestMain.add(labelLvCaution, "2, 16, left, center");
	
	panelEastMain = new JPanel();
	panelMain.add(panelEastMain, BorderLayout.EAST);
	panelEastMain.setLayout(new FormLayout(new ColumnSpec[] {
			FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
			ColumnSpec.decode("100px"),},
		new RowSpec[] {
			FormSpecs.LINE_GAP_ROWSPEC,
			RowSpec.decode("77px"),
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			RowSpec.decode("max(36dlu;default)"),
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,}));
	
	btnImport = new JButton(Messages.MSGUpdatingDatabase.toString());
	btnImport.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			if(impl != null) {
				if(!impl.getFlag()) {
					JOptionPane.showMessageDialog(null, Messages.MSGManualUpdateNotCompleteYet.toString());
				}
			}
			List<Song> fromJson = Scraping.getFromJson();
			List<Song> specificlevelList = Scraping.getSpecificLevelSongs(fromJson, (Integer)DelesteRandomSelector.this.spinnerLevel.getValue(), DelesteRandomSelector.this.checkLessLv.isSelected(), DelesteRandomSelector.this.checkMoreLv.isSelected());
			List<Song> specificDifficultyList = Scraping.getSpecificDifficultySongs(specificlevelList, DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().toString());
			List<Song> specificAttributeList = Scraping.getSpecificAttributeSongs(specificDifficultyList, DelesteRandomSelector.this.comboAttribute.getSelectedItem().toString());
			List<Song> specificTypeList = Scraping.getSpecificAlbumTypeSongs(specificAttributeList, EstimateAlbumTypeCycle.getCurrentCycle());
			if(!selectedSongsList.isEmpty()) {
				selectedSongsList.clear();
			}
			selectedSongsList.addAll((DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.MASTERPLUS) || DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.LEGACYMASTERPLUS)) ? specificTypeList : specificAttributeList);
			DelesteRandomSelector.this.logger.info("Songs are selected.We are Ready to go.");
			JOptionPane.showMessageDialog(null, Messages.MSGCompleteNarrowDown.toString());
		}, es).whenCompleteAsync((ret, ex) -> {
			if(ex != null) {
				logger.error("Exception was thrown during concurrent process", ex);
				if(ex instanceof IllegalArgumentException)
					return; // ignore
				CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
				handle.execute();
			}
		}, es);
	});
	btnImport.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	btnImport.setEnabled(false);
	panelEastMain.add(btnImport, "2, 2, fill, fill");
	
	btnConfig = new JButton(Messages.MSGConfigurations.toString());
	btnConfig.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			ProcessBuilder builder = new ProcessBuilder("java", "-jar", "Configurations.jar");
			try {
				builder.start();
			} catch (IOException e1) {
				logger.error("Exception was thrown during concurrent process", e1);
				CrashHandler handle = new CrashHandler(new IllegalStateException(e1));
				handle.execute();
			}
		}, es).whenCompleteAsync((ret, ex) -> {
			if(ex != null) {
				logger.error("Exception was thrown during concurrent process", ex);
				CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
				handle.execute();
			}
		}, es);
	});
	panelEastMain.add(btnConfig, "2, 6, fill, fill");
	
	btnStart = new JButton(Messages.MSGCalcStart.toString());
	btnStart.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			if(selectedSongsList.isEmpty()) {
				logger.warn("User has not started playing yet.");
				return;
			}
			Random random = new Random(System.currentTimeMillis());
			toolIntegrateList = new ArrayList<>();
			StringBuilder paneBuilder = new StringBuilder();
			DelesteRandomSelector.this.integratorArray = new String[DelesteRandomSelector.this.property.getSongLimit()];
			for(int i = 0; i < DelesteRandomSelector.this.property.getSongLimit(); i++) {
				int randomInt = random.nextInt(selectedSongsList.size());
				String typeString = DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.MASTERPLUS) || DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.LEGACYMASTERPLUS) ? EstimateAlbumTypeCycle.getCurrentCycle() : "";
				paneBuilder.append(i + 1)
					.append(Messages.MSGNumberOfSongs.toString())
					.append(" ")
					.append(selectedSongsList.get(randomInt).getAttribute())
					.append("[")
					.append(selectedSongsList.get(randomInt).getDifficulty())
					.append("]???")
					.append(selectedSongsList.get(randomInt).getName())
					.append("??????(Lv:")
					.append(selectedSongsList.get(randomInt).getLevel())
					.append(" ")
					.append(typeString)
					.append(")\n\n");
				this.integratorArray[i] = selectedSongsList.get(randomInt).getName() + "(Lv" + selectedSongsList.get(randomInt).getLevel() + ")\n";
				toolIntegrateList.add(selectedSongsList.get(randomInt));
			}
				paneBuilder.append(Messages.MSGThisPhrase.toString())
					.append(this.property.getSongLimit())
					.append(Messages.MSGPlayPhrase.toString());
			DelesteRandomSelector.this.textArea.setText(paneBuilder.toString());
			DelesteRandomSelector.this.integratorBool = true;
			DelesteRandomSelector.this.logger.info("show up completed.");
			if(isDebugMode) {
				logger.warn("API publish will NOT be executed due to debug mode.");
				return;
			}
			labelCurrentSongOrderTool.setText("null");
			progressTool.setValue(0);
			listToolMapDataFuture = CompletableFuture.supplyAsync(() -> {
				List<String> data = toolIntegrateList.stream()
						.map(s -> s.getName())
						.collect(Collectors.toList());
				fetchData = new FetchFromAPI(data.toArray(new String[0]));
				return fetchData.getInformation();
			}, es);
			logger.debug("api fetch inquery published");
		}, es).whenCompleteAsync((ret, ex) -> {
			if(ex != null) {
				logger.error("Exception was thrown during concurrent process", ex);
				CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
				handle.execute();
			}
		}, es);
	});
	btnStart.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelEastMain.add(btnStart, "2, 4, fill, fill");
	
	btnManualUpdate = new JButton(Messages.MSGManualUpdate.toString());
	btnManualUpdate.addActionListener(e -> {
		impl = new ManualUpdateThreadImpl();
		CompletableFuture.runAsync(impl, es).whenCompleteAsync((t, u) -> {
			if(u != null) {
				logger.warn("Exception while processing update manually.", u);
				CrashHandler handle = new CrashHandler(u.getMessage(), new IllegalStateException(u));
				handle.execute();
				JOptionPane.showMessageDialog(null, "There was a problem during processing library update manually.\nIf this appears repeatedly, please contact developer with your app log.");
			}
		}, es);
	});
	panelEastMain.add(btnManualUpdate, "2, 8, fill, fill");
	
	btnTwitterIntegration = new JButton(Messages.MSGTwitterIntegration.toString());
	btnTwitterIntegration.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			String updatedStatus = Messages.MSGUsingThisAppPhrase.toString();
			int lengthLimit = updatedStatus.length();
			boolean isBroken = false;
			if(!DelesteRandomSelector.this.integratorBool) {
				JOptionPane.showMessageDialog(null, Messages.MSGNotPlayYet.toString());
				return;
			}
			for (String element : DelesteRandomSelector.this.integratorArray) {
				updatedStatus = updatedStatus + element;
				lengthLimit += element.length();
				if(lengthLimit > 69) {
					isBroken = true;
					break;
				}
			}
			if(isBroken) {
				updatedStatus = updatedStatus + Messages.MSGTwitterPlayOtherwisePhrase.toString();
			} else {
				updatedStatus = updatedStatus + Messages.MSGTwitterPlayOnlyPhrase.toString();
			}
			DelesteRandomSelector.this.logger.info("status message constructed.");
			lengthLimit = updatedStatus.length();
				int option = JOptionPane.showConfirmDialog(null, Messages.MSGTwitterIntegrationConfirm.toString() + updatedStatus + Messages.MSGStringLength.toString() + lengthLimit);
				DelesteRandomSelector.this.logger.info("user seletced: " + option);
				switch(option) {
				case JOptionPane.OK_OPTION:
					TwitterIntegration.PostTwitter(updatedStatus);
					DelesteRandomSelector.this.logger.info("Success to update the status.");
					JOptionPane.showMessageDialog(null, Messages.MSGCompletePost.toString());
					break;
				case JOptionPane.NO_OPTION:
					DelesteRandomSelector.this.logger.info("There is no will to post.");
					break;
				case JOptionPane.CANCEL_OPTION:
					DelesteRandomSelector.this.logger.info("The Operation was canceled by user.");
					break;
				default:
					break;
				}
		}, es).whenCompleteAsync((ret, ex) -> {
			if(ex != null) {
				logger.error("Exception was thrown during concurrent process", ex);
				CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
				handle.execute();
			}
		}, es);
	});
	btnTwitterIntegration.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 11));
	panelEastMain.add(btnTwitterIntegration, "2, 10, fill, fill");
	
	btnExit = new JButton(Messages.MSGTerminate.toString());
	btnExit.addActionListener(e -> {
		if(DelesteRandomSelector.this.softwareUpdateFuture.isDone() || DelesteRandomSelector.this.albumTypeEstimateFuture.isDone() || !this.impl.getFlag()) {
			DelesteRandomSelector.this.logger.info("Requested Exit by Button.");
			logger.info("Shut down thread pool.");
			es.shutdown();
			System.exit(0);
		} else {
			JOptionPane.showMessageDialog(null, Messages.MSGInternalYpdateNotDoneYet.toString());
		}
	});
	btnExit.setFont(new Font("UD ???????????? ???????????? NP-B", Font.BOLD, 13));
	panelEastMain.add(btnExit, "2, 12, fill, fill");
	
	panelCenterMain = new JPanel();
	panelMain.add(panelCenterMain, BorderLayout.CENTER);
	panelCenterMain.setLayout(new BorderLayout(0, 0));
	
	scrollPane = new JScrollPane();
	panelCenterMain.add(scrollPane);
	
	textArea = new JTextArea();
	textArea.setText(Messages.MSGNarrowDownProcedure.toString() + property.getSongLimit() + Messages.MSGCurrentAlbumType.toString() + albumType);
	textArea.setEditable(false);
	scrollPane.setViewportView(textArea);
	
	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane.addChangeListener(e -> {
		CompletableFuture.runAsync(() -> {
			labelToolProgress.setText(Messages.MSGAPIWaitAPIFetch.toString());
			String currentTabName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
			if(currentTabName.equals("SongInfo") && labelCurrentSongOrderTool.getText().equals("null")) {
				logger.info("Detected switching tool tab");
				if(listToolMapDataFuture == null) {
					logger.warn("Async task has not initialized yet. Aborting...");
					return;
				}
				if(toolIntegrateList == null) {
					return;
				}
				listToolMapData = listToolMapDataFuture.join();
				Song firstSong = toolIntegrateList.get(0);
				Map<String, String> fetchMap = new HashMap<>();
				for(Map<String, String> tmpMap : listToolMapData) {
					String normalizeApiName = Normalizer.normalize(tmpMap.get("songname").toString(), Normalizer.Form.NFKD);
					String normalizeLocalName = Normalizer.normalize(firstSong.getName(), Normalizer.Form.NFKD);
					if(normalizeApiName.equals(normalizeLocalName)) {
						fetchMap = tmpMap;
						break;
					}
				}
				labelSongNameToolTip.setText(firstSong.getName());
				labelAttributeToolTip.setText(firstSong.getAttribute());
				labelDifficultyToolTip.setText(firstSong.getDifficulty());
				labelLevelToolTip.setText(String.valueOf(firstSong.getLevel()));
				labelNotesToolTip.setText(String.valueOf(firstSong.getNotes()));
				labelCurrentSongOrderTool.setText("1");
				labelLyricToolTip.setText(fetchMap.get("lyric"));
				labelComposerToolTip.setText(fetchMap.get("composer"));
				labelArrangeToolTip.setText(fetchMap.get("arrange"));
				labelMemberToolTip.setText("<html><body>" + fetchMap.get("member") + "</html></body>");
			}
			if(currentTabName.equals("Scores") && labelScoreCurrentSongOrder.getText().equals("null")) {
				logger.info("Detected switching score tab");
				if(toolIntegrateList == null)
					return;
				Song firstSong = toolIntegrateList.get(0);
				labelScoreCurrentSongOrder.setText("1");
				labelScoreSongnameDynamic.setText("<html><body>" + firstSong.getName() + "</body></html>");
				labelScoreAttributeDynamic.setText(firstSong.getAttribute());
				labelScoreDifficultyDynamic.setText(firstSong.getDifficulty());
				labelScoreLevelDynamic.setText(String.valueOf(firstSong.getLevel()));
				labelScoreNotesDynamic.setText(String.valueOf(firstSong.getNotes()));
			}
		}, es).whenCompleteAsync((ret, ex) -> {
			labelToolProgress.setText(Messages.MSGInfoCompletedSongInformationParsing.toString());
			if(ex != null) {
				logger.error("Exception was thrown during concurrent process", ex);
				CrashHandler handle = new CrashHandler(ex.getMessage(), new IllegalStateException(ex));
				handle.execute();
			}
		}, es);
	});
	tabbedPane.addTab("Main", null, panelMain, null);
	contentPane.add(tabbedPane, "name_307238585319500");
	
	panelInfo = new JPanel();
	tabbedPane.addTab("SongInfo", null, panelInfo, null);
	panelInfo.setLayout(new BorderLayout(0, 0));
	
	panelNorthTool = new JPanel();
	panelInfo.add(panelNorthTool, BorderLayout.NORTH);
	panelNorthTool.setLayout(new FormLayout(new ColumnSpec[] {
			ColumnSpec.decode("center:max(524dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(30dlu;default)"),},
		new RowSpec[] {
			RowSpec.decode("max(16dlu;default)"),}));
	
	labelSubToolTitle = new JLabel(Messages.MSGInfoSongInformations.toString());
	labelSubToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 16));
	panelNorthTool.add(labelSubToolTitle, "1, 1");
	
	labelVersionTool = new JLabel(CheckVersion.getVersion());
	labelVersionTool.setFont(new Font("SansSerif", Font.BOLD, 12));
	panelNorthTool.add(labelVersionTool, "3, 1");
	
	panelCenterTool = new JPanel();
	panelInfo.add(panelCenterTool, BorderLayout.CENTER);
	panelCenterTool.setLayout(new FormLayout(new ColumnSpec[] {
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(40dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("10dlu"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("10dlu"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(12dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(90dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(14dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(14dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(90dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(90dlu;default)"),},
		new RowSpec[] {
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,}));
	
	labelInfoPlaySongs = new JLabel(Messages.MSGInfoPlayedSongs.toString());
	labelInfoPlaySongs.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelInfoPlaySongs, "2, 2, center, default");
	
	labelSongNameToolTitle = new JLabel(Messages.MSGInfoSongName.toString());
	labelSongNameToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelSongNameToolTitle, "2, 6, center, default");
	
	labelSongNameToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelSongNameToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelSongNameToolTip, "10, 6, center, default");
	
	labelLyricToolTitle = new JLabel(Messages.MSGInfoLyricsBy.toString());
	labelLyricToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelLyricToolTitle, "17, 6, center, default");
	
	labelLyricToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelLyricToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelLyricToolTip, "25, 6, center, default");
	
	labelAttributeToolTitle = new JLabel(Messages.MSGInfoSongAttribute.toString());
	labelAttributeToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelAttributeToolTitle, "2, 10, center, default");
	
	labelAttributeToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelAttributeToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelAttributeToolTip, "10, 10, center, default");
	
	labelComposerToolTitle = new JLabel(Messages.MSGInfoComposedBy.toString());
	labelComposerToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelComposerToolTitle, "17, 10, center, default");
	
	labelComposerToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelComposerToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelComposerToolTip, "25, 10, center, default");
	
	labelDifficultyToolTitle = new JLabel(Messages.MSGInfoSongDifficulty.toString());
	labelDifficultyToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelDifficultyToolTitle, "2, 14, center, default");
	
	labelDifficultyToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelDifficultyToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelDifficultyToolTip, "10, 14, center, default");
	
	labelArrangeToolTitle = new JLabel(Messages.MSGInfoArrangedBy.toString());
	labelArrangeToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelArrangeToolTitle, "17, 14, center, default");
	
	labelArrangeToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelArrangeToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelArrangeToolTip, "25, 14, center, default");
	
	labelLevelToolTitle = new JLabel(Messages.MSGInfoSongLevel.toString());
	labelLevelToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelLevelToolTitle, "2, 18, center, default");
	
	labelLevelToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelLevelToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelLevelToolTip, "10, 18, center, default");
	
	labelMemberToolTitle = new JLabel(Messages.MSGInfoMember.toString());
	labelMemberToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelMemberToolTitle, "17, 18, center, default");
	
	labelMemberToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelMemberToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelMemberToolTip, "25, 18, center, default");
	
	labelNotesToolTitle = new JLabel(Messages.MSGInfoSongNotes.toString());
	labelNotesToolTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelNotesToolTitle, "2, 22, center, default");
	
	labelNotesToolTip = new JLabel(Messages.MSGInfoWait.toString());
	labelNotesToolTip.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelNotesToolTip, "10, 22, center, default");
	
	btnNextSongTool = new JButton(Messages.MSGNext.toString());
	btnNextSongTool.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			int currentIndex = Integer.parseInt(labelCurrentSongOrderTool.getText()) - 1;
			if(currentIndex != property.getSongLimit() - 1) {
				Song nextSong = toolIntegrateList.get(currentIndex + 1);
				logger.info("currently : {} Next: {}", currentIndex + 1, currentIndex + 2);
				logger.info("nextSong: {}", nextSong);
				Map<String, String> fetchMap = new HashMap<>();
				for(Map<String, String> tmpMap : listToolMapData) {
					String normalizeApiName = Normalizer.normalize(tmpMap.get("songname").toString(), Normalizer.Form.NFKD);
					String normalizeLocalName = Normalizer.normalize(nextSong.getName(), Normalizer.Form.NFKD);
					if(normalizeApiName.equals(normalizeLocalName)) {
						fetchMap = tmpMap;
						break;
					}
				}
				labelSongNameToolTip.setText(nextSong.getName());
				labelAttributeToolTip.setText(nextSong.getAttribute());
				labelDifficultyToolTip.setText(nextSong.getDifficulty());
				labelLevelToolTip.setText(String.valueOf(nextSong.getLevel()));
				labelNotesToolTip.setText(String.valueOf(nextSong.getNotes()));
				labelCurrentSongOrderTool.setText(String.valueOf(currentIndex + 2));
				labelLyricToolTip.setText(fetchMap.get("lyric"));
				labelComposerToolTip.setText(fetchMap.get("composer"));
				labelArrangeToolTip.setText(fetchMap.get("arrange"));
				labelMemberToolTip.setText("<html><body>" + fetchMap.get("member") + "</html></body>");
			}
		}, es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	
	btnPrevSongTool = new JButton(Messages.MSGPrev.toString());
	btnPrevSongTool.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			int currentIndex = Integer.parseInt(labelCurrentSongOrderTool.getText()) - 1;
			if(currentIndex != 0) {
				Song prevSong = toolIntegrateList.get(currentIndex - 1);
				logger.info("currently : {} Next: {}", currentIndex + 1, currentIndex);
				logger.info("prevSong: {}", prevSong);
				Map<String, String> fetchMap = new HashMap<>();
				for(Map<String, String> tmpMap : listToolMapData) {
					String normalizeApiName = Normalizer.normalize(tmpMap.get("songname").toString(), Normalizer.Form.NFKD);
					String normalizeLocalName = Normalizer.normalize(prevSong.getName(), Normalizer.Form.NFKD);
					if(normalizeApiName.equals(normalizeLocalName)) {
						fetchMap = tmpMap;
						break;
					}
				}
				labelSongNameToolTip.setText(prevSong.getName());
				labelAttributeToolTip.setText(prevSong.getAttribute());
				labelDifficultyToolTip.setText(prevSong.getDifficulty());
				labelLevelToolTip.setText(String.valueOf(prevSong.getLevel()));
				labelNotesToolTip.setText(String.valueOf(prevSong.getNotes()));
				labelCurrentSongOrderTool.setText(String.valueOf(currentIndex));
				labelLyricToolTip.setText(fetchMap.get("lyric"));
				labelComposerToolTip.setText(fetchMap.get("composer"));
				labelArrangeToolTip.setText(fetchMap.get("arrange"));
				labelMemberToolTip.setText("<html><body>" + fetchMap.get("member") + "</html></body>");
			}
		}, es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	btnPrevSongTool.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(btnPrevSongTool, "10, 28");
	
	labelCurrentSongOrderTool = new JLabel("null");
	panelCenterTool.add(labelCurrentSongOrderTool, "12, 28");
	
	labelSlashTool = new JLabel("/");
	panelCenterTool.add(labelSlashTool, "14, 28");
	
	labelSongLimitTool = new JLabel(String.valueOf(this.property.getSongLimit()));
	panelCenterTool.add(labelSongLimitTool, "15, 28");
	btnNextSongTool.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(btnNextSongTool, "17, 28");
	
	btnMoreInfoTool = new JButton(Messages.MSGInfoOpenBrowser.toString());
	btnMoreInfoTool.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			int currentIndex = Integer.parseInt(labelCurrentSongOrderTool.getText()) - 1;
			Song currentSong = toolIntegrateList.get(currentIndex);
			Map<String, String> fetchMap = new HashMap<>();
			for(Map<String, String> tmpMap : listToolMapData) {
				String normalizeApiName = Normalizer.normalize(tmpMap.get("songname").toString(), Normalizer.Form.NFKD);
				String normalizeLocalName = Normalizer.normalize(currentSong.getName(), Normalizer.Form.NFKD);
				if(normalizeApiName.equals(normalizeLocalName)) {
					fetchMap = tmpMap;
					break;
				}
			}
			Desktop desk = Desktop.getDesktop();
			String api = fetchMap.get("link");
			URI uri;
			try {
				uri = new URI(api);
				logger.info("Opening default browser with : {}", uri);
				desk.browse(uri);
			} catch (URISyntaxException | IOException e1) {
				JOptionPane.showMessageDialog(null, "????????????????????????????????????Exception : " + e1.getClass().getSimpleName());
				logger.error("Exception while opening default browser.", e1);
			}
		}, es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	btnMoreInfoTool.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(btnMoreInfoTool, "25, 28");
	
	labelToolProgress = new JLabel(Messages.MSGAPIWaitAPIFetch.toString());
	labelToolProgress.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelToolProgress, "10, 32, center, default");
	
	progressTool = new JProgressBar();
	progressTool.setStringPainted(true);
	progressTool.setValue(0);
	progressTool.setMaximum(property.getSongLimit());
	panelCenterTool.add(progressTool, "17, 32");
	
	labelInfoProgressSongName = new JLabel("Processing:");
	labelInfoProgressSongName.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelCenterTool.add(labelInfoProgressSongName, "10, 34");
	
	panelScore = new JPanel();
	tabbedPane.addTab("Scores", null, panelScore, null);
	panelScore.setLayout(new BorderLayout(0, 0));
	
	panelScoreNorth = new JPanel();
	panelScore.add(panelScoreNorth, BorderLayout.NORTH);
	panelScoreNorth.setLayout(new FormLayout(new ColumnSpec[] {
			ColumnSpec.decode("828px"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(53dlu;default)"),},
		new RowSpec[] {
			RowSpec.decode("20px"),}));
	
	labelScoreTitle = new JLabel("???????????????????????????");
	labelScoreTitle.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 16));
	panelScoreNorth.add(labelScoreTitle, "1, 1, center, center");
	
	labelScoreVersion = new JLabel(CheckVersion.getVersion());
	labelScoreVersion.setFont(new Font("SansSerif", Font.BOLD, 12));
	panelScoreNorth.add(labelScoreVersion, "3, 1, center, default");
	
	panelScoreCenter = new JPanel();
	panelScore.add(panelScoreCenter, BorderLayout.CENTER);
	panelScoreCenter.setLayout(new FormLayout(new ColumnSpec[] {
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(60dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(60dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(60dlu;default):grow"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(68dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(59dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("center:max(97dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(25dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(9dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(25dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("40dlu:grow"),},
		new RowSpec[] {
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			RowSpec.decode("max(13dlu;default)"),
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,
			FormSpecs.RELATED_GAP_ROWSPEC,
			RowSpec.decode("default:grow"),}));
	
	labelScoreUserPlayed = new JLabel(Messages.MSGActual.toString() + " " + Messages.MSGToolScore.toString());
	labelScoreUserPlayed.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreUserPlayed, "4, 10, center, default");
	
	fieldScoreUserPlayed = new JTextField();
	panelScoreCenter.add(fieldScoreUserPlayed, "6, 10, fill, default");
	fieldScoreUserPlayed.setColumns(10);
	
	lblSongname = new JLabel(Messages.MSGInfoSongName.toString());
	lblSongname.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(lblSongname, "10, 10, center, default");
	
	labelScoreSongnameDynamic = new JLabel("<dynamic>");
	labelScoreSongnameDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreSongnameDynamic, "12, 10, center, default");
	
	labelScoreEarnedFan = new JLabel(Messages.MSGEstimated.toString() + " " + Messages.MSGToolFan.toString());
	labelScoreEarnedFan.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreEarnedFan, "4, 14, center, default");
	
	fieldScoreEarnedFan = new JTextField();
	panelScoreCenter.add(fieldScoreEarnedFan, "6, 14, fill, default");
	fieldScoreEarnedFan.setColumns(10);
	
	labelScoreAttribute = new JLabel(Messages.MSGInfoSongAttribute.toString());
	labelScoreAttribute.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreAttribute, "10, 14, center, default");
	
	labelScoreAttributeDynamic = new JLabel("<dynamic>");
	labelScoreAttributeDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreAttributeDynamic, "12, 14");
	
	lblRoom = new JLabel(Messages.MSGRoom.toString());
	lblRoom.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(lblRoom, "4, 18, center, default");
	
	fieldScoreRoom = new JTextField();
	panelScoreCenter.add(fieldScoreRoom, "6, 18, fill, default");
	fieldScoreRoom.setColumns(10);
	
	labelScoreDifficulty = new JLabel(Messages.MSGInfoSongDifficulty.toString());
	labelScoreDifficulty.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreDifficulty, "10, 18, center, default");
	
	labelScoreDifficultyDynamic = new JLabel("<dynamic>");
	labelScoreDifficultyDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreDifficultyDynamic, "12, 18");
	
	lblCenter = new JLabel(Messages.MSGCenter.toString());
	lblCenter.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(lblCenter, "4, 22, center, default");
	
	fieldScoreCenter = new JTextField();
	panelScoreCenter.add(fieldScoreCenter, "6, 22, fill, default");
	fieldScoreCenter.setColumns(10);
	
	labelScoreLevel = new JLabel(Messages.MSGInfoSongLevel.toString());
	labelScoreLevel.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreLevel, "10, 22, center, default");
	
	labelScoreLevelDynamic = new JLabel("<dynamic>");
	labelScoreLevelDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreLevelDynamic, "12, 22");
	
	lblProduce = new JLabel(Messages.MSGProducer.toString());
	lblProduce.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(lblProduce, "4, 26, center, default");
	
	fieldScoreProduce = new JTextField();
	panelScoreCenter.add(fieldScoreProduce, "6, 26, fill, default");
	fieldScoreProduce.setColumns(10);
	
	labelScoreNotes = new JLabel(Messages.MSGInfoSongNotes.toString());
	labelScoreNotes.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreNotes, "10, 26, center, default");
	
	labelScoreNotesDynamic = new JLabel("<dynamic>");
	labelScoreNotesDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreNotesDynamic, "12, 26");
	
	labelScoreCurrentSongOrder = new JLabel("null");
	labelScoreCurrentSongOrder.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreCurrentSongOrder, "14, 28, center, default");
	
	labelScoreSlash = new JLabel("/");
	labelScoreSlash.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreSlash, "16, 28, center, default");
	
	labelScoreOrderMax = new JLabel(String.valueOf(property.getSongLimit()));
	labelScoreOrderMax.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelScoreOrderMax, "18, 28, center, default");
	
	lblPremium = new JLabel(Messages.MSGPremium.toString());
	lblPremium.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(lblPremium, "4, 30, center, default");
	
	fieldScorePremium = new JTextField();
	panelScoreCenter.add(fieldScorePremium, "6, 30, fill, default");
	fieldScorePremium.setColumns(10);
	
	labelPlayerScore = new JLabel(Messages.MSGEstimated.toString() + " " + Messages.MSGToolScore.toString());
	labelPlayerScore.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerScore, "10, 30, center, default");
	
	labelPlayerScoreDynamic = new JLabel("<dynamic>");
	labelPlayerScoreDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerScoreDynamic, "12, 30");
	
	btnScoreNext = new JButton(Messages.MSGNext.toString());
	btnScoreNext.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			CompletableFuture.runAsync(() -> {
				int currentIndex = Integer.parseInt(labelScoreCurrentSongOrder.getText()) - 1;
				if(currentIndex != property.getSongLimit() - 1) {
					Song nextSong = toolIntegrateList.get(currentIndex + 1);
					logger.info("currently : {} Next: {}", currentIndex + 1, currentIndex + 2);
					logger.info("nextSong: {}", nextSong);
					labelScoreSongnameDynamic.setText("<html><body>" + nextSong.getName() + "</body></html>");
					labelScoreAttributeDynamic.setText(nextSong.getAttribute());
					labelScoreDifficultyDynamic.setText(nextSong.getDifficulty());
					labelScoreLevelDynamic.setText(String.valueOf(nextSong.getLevel()));
					labelScoreNotesDynamic.setText(String.valueOf(nextSong.getNotes()));
					labelScoreCurrentSongOrder.setText(String.valueOf(currentIndex + 2));
				}
			}, es);
		}
	});
	
	btnScorePrev = new JButton(Messages.MSGPrev.toString());
	btnScorePrev.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			CompletableFuture.runAsync(() -> {
				int currentIndex = Integer.parseInt(labelScoreCurrentSongOrder.getText()) - 1;
				if(currentIndex != 0) {
					Song prevSong = toolIntegrateList.get(currentIndex - 1);
					logger.info("currently : {} Next: {}", currentIndex + 1, currentIndex);
					logger.info("prevSong: {}", prevSong);
					labelScoreSongnameDynamic.setText("<html><body>" + prevSong.getName() + "</body></html>");
					labelScoreAttributeDynamic.setText(prevSong.getAttribute());
					labelScoreDifficultyDynamic.setText(prevSong.getDifficulty());
					labelScoreLevelDynamic.setText(String.valueOf(prevSong.getLevel()));
					labelScoreNotesDynamic.setText(String.valueOf(prevSong.getNotes()));
					labelScoreCurrentSongOrder.setText(String.valueOf(currentIndex));
				}
			}, es);
		}
	});
	panelScoreCenter.add(btnScorePrev, "14, 30");
	panelScoreCenter.add(btnScoreNext, "18, 30");
	
	labelPlayerFan = new JLabel(Messages.MSGEstimated.toString() + " " + Messages.MSGToolFan.toString());
	labelPlayerFan.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerFan, "10, 34, center, default");
	
	labelPlayerFanDynamic = new JLabel("<dynamic>");
	labelPlayerFanDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerFanDynamic, "12, 34");
	
	btnScoreFileOut = new JButton("?????????????????????");
	btnScoreFileOut.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			int currIndex = Integer.parseInt(labelScoreCurrentSongOrder.getText()) - 1;
			Song curr = toolIntegrateList.get(currIndex);
			String songname = curr.getName();
			int level = curr.getLevel();
			String difficulty = curr.getDifficulty();
			String attribute = curr.getAttribute();
			int score = Integer.parseInt(fieldScoreUserPlayed.getText());
			FileIO ioOut = new FileIO(new OutputDataStructure(songname, level, difficulty, attribute, score));
			try {
				ioOut.write();
				JOptionPane.showMessageDialog(null, "?????????????????????????????????????????????");
			} catch (IOException e1) {
				logger.error("There was a problem during writing object file.", e1);
				JOptionPane.showMessageDialog(null, "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
			}
		}, es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	
	button = new JButton("????????????");
	button.addActionListener(e -> {
			CompletableFuture.runAsync(() -> scoreCalcDetail(), es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	panelScoreCenter.add(button, "18, 34");
	panelScoreCenter.add(btnScoreFileOut, "18, 36");
	
	labelPlayerPRP = new JLabel(Messages.MSGEstimated.toString() + " " + Messages.MSGPRP.toString());
	labelPlayerPRP.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerPRP, "10, 38, center, default");
	
	labelPlayerPRPDynamic = new JLabel("<dynamic>");
	labelPlayerPRPDynamic.setFont(new Font("UD ???????????? ???????????? NP-B", Font.PLAIN, 12));
	panelScoreCenter.add(labelPlayerPRPDynamic, "12, 38");
	
	btnScoreReadFile = new JButton("?????????????????????");
	btnScoreReadFile.addActionListener(e -> {
		CompletableFuture.runAsync(() -> {
			JFileChooser chooser = new JFileChooser(Paths.get("").toFile());
			int option = chooser.showOpenDialog(this);
			logger.info("user selected : {}", option);
			switch (option) {
			case JFileChooser.APPROVE_OPTION:
				File file = chooser.getSelectedFile();
				OutputDataStructure s = FileIO.read(file);
				labelScoreSongnameDynamic.setText(s.getSongname());
				labelScoreLevelDynamic.setText(String.valueOf(s.getLevel()));
				labelScoreDifficultyDynamic.setText(s.getDifficulty());
				labelScoreAttributeDynamic.setText(s.getAttribute());
				fieldScoreUserPlayed.setText(String.valueOf(s.getScore()));
				logger.info("Selected file: {}", file);
				logger.info("infomations: {}", s);
				CompletableFuture.runAsync(() -> scoreCalcDetail(), es);
				break;
			default:
				logger.warn("There is no options we can do");
			}
		}, es).whenCompleteAsync(this::whenCompleteProcess, es);
	});
	panelScoreCenter.add(btnScoreReadFile, "18, 38");
	
	label = new JLabel("<html><body>????????????????????????????????????????????????????????????????????????????????????</body></html>");
	panelScoreCenter.add(label, "6, 40");
	if(isFirst || !this.property.isCheckLibraryUpdates()) {
	    setEnabled.run();
	}
    }
    
    /**
     * ???????????????????????????????????????????????????????????????
     * <p>
     * ??????????????????????????????????????????
     * @param ret ?????????????????????????????????
     * @param ex ???????????????????????????????????????????????????
     */
    private void whenCompleteProcess(Void ret, Throwable ex) {
    	if(ex != null) {
			logger.error("Exception was thrown during concurrent process.", ex);
			JOptionPane.showMessageDialog(null, "??????????????????????????????????????????????????????" + ex.getLocalizedMessage(), "?????????????????????", ERROR);
		}
    }

	/**
	 * score?????????????????????????????????????????????????????????????????????
	 * <p>
	 * ?????????{@link ActionEvent}???????????????????????????
	 */
	private void scoreCalcDetail() {
		String scoreStr = fieldScoreUserPlayed.getText();
		String fanStr = fieldScoreEarnedFan.getText();
		if(scoreStr.isEmpty() && fanStr.isEmpty()) {
			logger.warn("there is no data to calculate.");
			JOptionPane.showMessageDialog(null, "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
			return;
		}
		labelPlayerScoreDynamic.setText("Calculating...");
		labelPlayerFanDynamic.setText("Calculating...");
		String roomStr = fieldScoreRoom.getText();
		String centerStr = fieldScoreCenter.getText();
		String produceStr = fieldScoreProduce.getText();
		String premiumStr = fieldScorePremium.getText();
		if(!scoreStr.isEmpty()) {
			int score = Integer.parseInt(scoreStr);
			int room = roomStr.isEmpty() ? 100 : Integer.parseInt(roomStr);
			int center = centerStr.isEmpty() ? 100 : Integer.parseInt(centerStr);
			int produce = produceStr.isEmpty() ? 100 : Integer.parseInt(produceStr);
			int premium = premiumStr.isEmpty() ? 100 : Integer.parseInt(premiumStr);
			int res = FanCalc.fanAsync(score, room, center, produce, premium).join();
			labelPlayerScoreDynamic.setText(scoreStr);
			labelPlayerFanDynamic.setText(String.valueOf(res));
		} else {
			int fan = Integer.parseInt(fanStr);
			int room = roomStr.isEmpty() ? 100 : Integer.parseInt(fanStr);
			int center = centerStr.isEmpty() ? 100 : Integer.parseInt(centerStr);
			int produce = produceStr.isEmpty() ? 100 : Integer.parseInt(produceStr);
			int premium = premiumStr.isEmpty() ? 100 : Integer.parseInt(premiumStr);
			int res = FanCalc.scoreAsync(fan, 1, room, center, produce, premium).join();
			labelPlayerFanDynamic.setText(fanStr);
			labelPlayerScoreDynamic.setText(String.valueOf(res));
		}
		CompletableFuture.runAsync(() -> {
			int labelScore = Integer.parseInt(labelPlayerScoreDynamic.getText());
			int estimatedPRP = PRPCalc.calcPRPFromScore(labelScore);
			labelPlayerPRPDynamic.setText(String.valueOf(estimatedPRP));
		});
	}
}
