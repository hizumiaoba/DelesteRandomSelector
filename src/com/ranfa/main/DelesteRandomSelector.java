package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.ranfa.lib.CheckVersion;
import com.ranfa.lib.Easter;
import com.ranfa.lib.EstimateAlbumTypeCycle;
import com.ranfa.lib.ManualUpdateThreadImpl;
import com.ranfa.lib.Scraping;
import com.ranfa.lib.SettingJSONProperty;
import com.ranfa.lib.Settings;
import com.ranfa.lib.Song;
import com.ranfa.lib.TwitterIntegration;
import com.ranfa.lib.Version;

@Version(major = 3, minor = 1, patch = 0)
public class DelesteRandomSelector extends JFrame {

    private static ArrayList<Song> selectedSongsList = new ArrayList<>();

    private JPanel contentPane;
    private SettingJSONProperty property = new SettingJSONProperty();
    private String[] integratorArray;
    private boolean integratorBool = false;
    private CompletableFuture<Void> softwareUpdateFuture = null;
    private CompletableFuture<Void> albumTypeEstimateFuture = null;
    private String albumType = Messages.MSGAlbumTypeBeingCalculated.toString();
    private Logger logger = LoggerFactory.getLogger(DelesteRandomSelector.class);
    private ManualUpdateThreadImpl impl;
    private Easter easter;
    private JPanel panelMain;
    private JPanel panelNorthMain;
    private JLabel labelTitle;
    private JLabel labelVersion;
    private JPanel panelWestMain;
    private JLabel labelDifficulty;
    private JComboBox comboDifficultySelect;
    private JComboBox comboAttribute;
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
    private JPanel panelTool;
    private JPanel panelNorthTool;
    private JLabel labelSubToolTitle;
    private JLabel labelVersionTool;
    private JPanel panelCenterTool;
    private JLabel lblNewLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
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
     * log file prefix:
     *  this.getClass() + ":[LEVEL]: " +
     */

    /**
     * Create the frame.
     */
    public DelesteRandomSelector() {
	this.contentPane = new JPanel();
	boolean isFirst = !Scraping.databaseExists();
	if(isFirst) {
	    JOptionPane.showMessageDialog(this, Messages.MSGDatabaseNotExist.toString());
	    if(!Scraping.writeToJson(Scraping.getWholeData())) {
		JOptionPane.showMessageDialog(this, "Exception:NullPointerException\nCannot Keep up! Please re-download this Application!");
		throw new NullPointerException("FATAL: cannot continue!");
	    }
	}
	ExecutorService es = Executors.newWorkStealingPool();
	CompletableFuture<ArrayList<Song>> getFromJsonFuture = CompletableFuture.supplyAsync(() -> Scraping.getFromJson(), es);
	CompletableFuture<ArrayList<Song>> getWholeDataFuture = CompletableFuture.supplyAsync(() -> Scraping.getWholeData(), es);
	if(!Settings.fileExists() && !Settings.writeDownJSON()) {
	    JOptionPane.showMessageDialog(this, "Exception:NullPointerException\nCannot Keep up! Please re-download this Application!");
	    throw new NullPointerException("FATAL: cannot continue!");
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
	EstimateAlbumTypeCycle.Initialization();
	if(Files.exists(Paths.get("generated/albumCycle.json"))) {
	    this.albumType = EstimateAlbumTypeCycle.getCurrentCycle();
	}
	if(this.property.isCheckVersion()) {
	    this.softwareUpdateFuture = CompletableFuture.runAsync(() -> CheckVersion.needToBeUpdated(), es);
	}
	BiConsumer<ArrayList<Song>, ArrayList<Song>> updateConsumer = (list1, list2) -> {
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
	Runnable setEnabled = () -> {
	    try {
		Thread.sleep(3 * 1000L);
	    } catch (InterruptedException e1) {
		this.logger.error("Thread has been interrupted during waiting cooldown.", e1);
	    }
	    this.btnImport.setEnabled(true);
	    this.btnImport.setText(Messages.MSGNarrowingDownSongs.toString());
	};
	getWholeDataFuture.thenAcceptAsync(list -> this.logger.info("Scraping data size:" + list.size()), es);
	getFromJsonFuture.thenAcceptAsync(list -> this.logger.info("Currently database size:" + list.size()), es);
	if(this.property.isCheckLibraryUpdates()) {
	    CompletableFuture<Void> updatedFuture = getWholeDataFuture.thenAcceptBothAsync(getFromJsonFuture, updateConsumer, es);
	    updatedFuture.thenRunAsync(setEnabled, es);
	}
	this.easter = new Easter();
	this.setTitle(this.easter.getTodaysBirth());
	this.logger.debug("Version: {}", CheckVersion.getVersion());
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// this.setBounds(100, 100, this.property.getWindowWidth(), this.property.getWindowHeight());
	this.setBounds(100, 100, 960, 540);
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
	labelTitle.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 16));
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
	labelDifficulty.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelWestMain.add(labelDifficulty, "2, 2, center, center");
	
	comboDifficultySelect = new JComboBox();
	comboDifficultySelect.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "DEBUT", "REGULAR", "PRO", "MASTER", "MASTER+", "ⓁMASTER+", "LIGHT", "TRICK", "PIANO", "FORTE", "WITCH"}));
	comboDifficultySelect.setFont(new Font("Dialog", Font.BOLD, 12));
	panelWestMain.add(comboDifficultySelect, "2, 4, fill, fill");
	
	comboAttribute = new JComboBox();
	comboAttribute.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "全タイプ", "キュート", "クール", "パッション"}));
	comboAttribute.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelWestMain.add(comboAttribute, "2, 6, fill, top");
	
	labelLevel = new JLabel(Messages.MSGSongLevel.toString());
	labelLevel.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelWestMain.add(labelLevel, "2, 8, center, center");
	
	spinnerLevel = new JSpinner();
	panelWestMain.add(spinnerLevel, "2, 10, fill, center");
	
	checkLessLv = new JCheckBox(Messages.MSGBelowSpecificLevel.toString());
	checkLessLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelWestMain.add(checkLessLv, "2, 12, left, top");
	
	checkMoreLv = new JCheckBox(Messages.MSGOverSpecificLevel.toString());
	checkMoreLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelWestMain.add(checkMoreLv, "2, 14, left, top");
	
	labelLvCaution = new JLabel(Messages.MSGLevelCheckboxInfo.toString());
	labelLvCaution.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
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
		if(impl != null) {
			if(!impl.getFlag()) {
				JOptionPane.showMessageDialog(null, Messages.MSGManualUpdateNotCompleteYet.toString());
			}
		}
		ArrayList<Song> fromJson = Scraping.getFromJson();
		ArrayList<Song> specificlevelList = Scraping.getSpecificLevelSongs(fromJson, (Integer)DelesteRandomSelector.this.spinnerLevel.getValue(), DelesteRandomSelector.this.checkLessLv.isSelected(), DelesteRandomSelector.this.checkMoreLv.isSelected());
		ArrayList<Song> specificDifficultyList = Scraping.getSpecificDifficultySongs(specificlevelList, DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().toString());
		ArrayList<Song> specificAttributeList = Scraping.getSpecificAttributeSongs(specificDifficultyList, DelesteRandomSelector.this.comboAttribute.getSelectedItem().toString());
		ArrayList<Song> specificTypeList = Scraping.getSpecificAlbumTypeSongs(specificAttributeList, EstimateAlbumTypeCycle.getCurrentCycle());
		if(!selectedSongsList.isEmpty()) {
			selectedSongsList.clear();
		}
		selectedSongsList.addAll((DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.MASTERPLUS) || DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.LEGACYMASTERPLUS)) ? specificTypeList : specificAttributeList);
		DelesteRandomSelector.this.logger.info("Songs are selected.We are Ready to go.");
		JOptionPane.showMessageDialog(null, Messages.MSGCompleteNarrowDown.toString());
	});
	btnImport.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	btnImport.setEnabled(false);
	panelEastMain.add(btnImport, "2, 2, fill, fill");
	
	btnConfig = new JButton(Messages.MSGConfigurations.toString());
	btnConfig.addActionListener(e -> {
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", "Configurations.jar");
		try {
			builder.start();
		} catch (IOException e1) {
			logger.error("Cannot start external jar file. maybe are you running this with mac or linux?", e);
		}
	});
	panelEastMain.add(btnConfig, "2, 6, fill, fill");
	
	btnStart = new JButton(Messages.MSGCalcStart.toString());
	btnStart.addActionListener(e -> {
		Random random = new Random(System.currentTimeMillis());
		String paneString = "";
		DelesteRandomSelector.this.integratorArray = new String[DelesteRandomSelector.this.property.getSongLimit()];
		for(int i = 0; i < DelesteRandomSelector.this.property.getSongLimit(); i++) {
			int randomInt = random.nextInt(selectedSongsList.size());
			String typeString = DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.MASTERPLUS) || DelesteRandomSelector.this.comboDifficultySelect.getSelectedItem().equals(Scraping.LEGACYMASTERPLUS) ? EstimateAlbumTypeCycle.getCurrentCycle() : "";
			paneString = paneString + (i + 1) + Messages.MSGNumberOfSongs.toString() + " " + selectedSongsList.get(randomInt).getAttribute() + " [" + selectedSongsList.get(randomInt).getDifficulty() + "]「" + selectedSongsList.get(randomInt).getName() + "」！(Lv:" + selectedSongsList.get(randomInt).getLevel() + " " + typeString + ")\n\n";
			DelesteRandomSelector.this.integratorArray[i] = selectedSongsList.get(randomInt).getName() + "(Lv" + selectedSongsList.get(randomInt).getLevel() + ")\n";
		}
		paneString = paneString + Messages.MSGThisPhrase.toString() + DelesteRandomSelector.this.property.getSongLimit() + Messages.MSGPlayPhrase.toString();
		DelesteRandomSelector.this.textArea.setText(paneString);
		DelesteRandomSelector.this.integratorBool = true;
		DelesteRandomSelector.this.logger.info("show up completed.");
	});
	btnStart.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
	panelEastMain.add(btnStart, "2, 4, fill, fill");
	
	btnManualUpdate = new JButton(Messages.MSGManualUpdate.toString());
	btnManualUpdate.addActionListener(e -> {
		impl = new ManualUpdateThreadImpl();
		es.submit(impl);
	});
	panelEastMain.add(btnManualUpdate, "2, 8, fill, fill");
	
	btnTwitterIntegration = new JButton(Messages.MSGTwitterIntegration.toString());
	btnTwitterIntegration.addActionListener(e -> {
		boolean authorizationStatus = TwitterIntegration.authorization();
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
			updatedStatus = updatedStatus + Messages.MSGTwitterPlayOtherwisePhrase.toString() + "\n#DelesteRandomSelector #デレステ ";
		} else {
			updatedStatus = updatedStatus + Messages.MSGTwitterPlayOnlyPhrase.toString() + "\n#DelesteRandomSelector #デレステ ";
		}
		DelesteRandomSelector.this.logger.info("status message constructed.");
		lengthLimit = updatedStatus.length();
		if(authorizationStatus) {
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
		} else {
			DelesteRandomSelector.this.logger.info("seems to reject the permission.it should need try again.");
		}
	});
	btnTwitterIntegration.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 11));
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
	btnExit.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
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
	tabbedPane.addTab("Main", null, panelMain, null);
	contentPane.add(tabbedPane, "name_307238585319500");
	
	panelTool = new JPanel();
	tabbedPane.addTab("SubTools", null, panelTool, null);
	panelTool.setLayout(new BorderLayout(0, 0));
	
	panelNorthTool = new JPanel();
	panelTool.add(panelNorthTool, BorderLayout.NORTH);
	panelNorthTool.setLayout(new FormLayout(new ColumnSpec[] {
			ColumnSpec.decode("center:max(524dlu;default)"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(30dlu;default)"),},
		new RowSpec[] {
			RowSpec.decode("max(16dlu;default)"),}));
	
	labelSubToolTitle = new JLabel("補助ツール");
	labelSubToolTitle.setFont(new Font("UD デジタル 教科書体 NP-B", Font.PLAIN, 16));
	panelNorthTool.add(labelSubToolTitle, "1, 1");
	
	labelVersionTool = new JLabel(CheckVersion.getVersion());
	labelVersionTool.setFont(new Font("SansSerif", Font.BOLD, 12));
	panelNorthTool.add(labelVersionTool, "3, 1");
	
	panelCenterTool = new JPanel();
	panelTool.add(panelCenterTool, BorderLayout.CENTER);
	panelCenterTool.setLayout(new FormLayout(new ColumnSpec[] {
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("max(166dlu;default)"),},
		new RowSpec[] {
			FormSpecs.RELATED_GAP_ROWSPEC,
			FormSpecs.DEFAULT_ROWSPEC,}));
	
	lblNewLabel = new JLabel("New label");
	panelCenterTool.add(lblNewLabel, "2, 2, center, default");
	if(isFirst || !this.property.isCheckLibraryUpdates()) {
	    setEnabled.run();
	}
    }

}
