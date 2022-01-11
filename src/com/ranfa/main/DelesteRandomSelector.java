package com.ranfa.main;

import java.awt.BorderLayout;
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

@Version(major = 3, minor = 0, patch = 1)
public class DelesteRandomSelector extends JFrame {

	private static ArrayList<Song> selectedSongsList = new ArrayList<>();

	private JPanel contentPane;
	private JPanel panelNorth;
	private JPanel panelWest;
	private JLabel labelVersion;
	private JLabel labelTitle;
	private JLabel labelDifficulty;
	private JLabel labelLevel;
	private JSpinner spinnerLevel;
	private JCheckBox checkMoreLv;
	private JCheckBox checkLessLv;
	private JPanel panelEast;
	private JPanel panelCentre;
	private JButton btnImport;
	private JButton btnStart;
	private JButton btnExit;
	private JComboBox comboDifficultySelect;
	private JLabel labelLvCaution;
	private JComboBox comboAttribute;
	private SettingJSONProperty property = new SettingJSONProperty();
	private JButton btnTwitterIntegration;
	private String[] integratorArray;
	private boolean integratorBool = false;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private CompletableFuture<Void> softwareUpdateFuture = null;
	private CompletableFuture<Void> albumTypeEstimateFuture = null;
	private String albumType = Messages.MSGAlbumTypeBeingCalculated.toString();
	private Logger logger = LoggerFactory.getLogger(DelesteRandomSelector.class);
	private ManualUpdateThreadImpl impl;
	private Thread manualUpdateThread;
	private JButton btnManualUpdate;
	private Easter easter;
	private JButton btnConfig;

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
		this.setBounds(100, 100, 640, 360);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));

		this.panelNorth = new JPanel();
		this.contentPane.add(this.panelNorth, BorderLayout.NORTH);
		this.panelNorth.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(302dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("40px"),},
				new RowSpec[] {
						RowSpec.decode("20px"),}));

		this.labelTitle = new JLabel(Messages.MSGTitle.toString());
		this.labelTitle.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 16));
		this.panelNorth.add(this.labelTitle, "1, 1, center, top");

		this.labelVersion = new JLabel(CheckVersion.getVersion());
		this.labelVersion.setFont(new Font("SansSerif", Font.BOLD, 12));
		this.panelNorth.add(this.labelVersion, "3, 1, right, top");

		this.panelWest = new JPanel();
		this.contentPane.add(this.panelWest, BorderLayout.WEST);
		this.panelWest.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("112px:grow"),},
				new RowSpec[] {
						FormSpecs.LINE_GAP_ROWSPEC,
						RowSpec.decode("19px"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(12dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(12dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("12dlu"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(12dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(12dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(12dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(52dlu;default)"),}));

		this.labelDifficulty = new JLabel(Messages.MSGSelectDifficulty.toString());
		this.labelDifficulty.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelWest.add(this.labelDifficulty, "2, 2, center, default");

		this.comboDifficultySelect = new JComboBox();
		this.comboDifficultySelect.setFont(new Font("Dialog", Font.BOLD, 12));
		this.comboDifficultySelect.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "DEBUT", "REGULAR", "PRO", "MASTER", "MASTER+", "ⓁMASTER+", "LIGHT", "TRICK", "PIANO", "FORTE", "WITCH"}));
		this.panelWest.add(this.comboDifficultySelect, "2, 4, fill, default");

		this.comboAttribute = new JComboBox();
		this.comboAttribute.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.comboAttribute.setModel(new DefaultComboBoxModel(new String[] {Messages.MSGNonSelected.toString(), "全タイプ", "キュート", "クール", "パッション"}));
		this.panelWest.add(this.comboAttribute, "2, 6, fill, default");

		this.labelLevel = new JLabel(Messages.MSGSongLevel.toString());
		this.labelLevel.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelWest.add(this.labelLevel, "2, 8, center, default");

		this.spinnerLevel = new JSpinner();
		this.panelWest.add(this.spinnerLevel, "2, 10");

		this.checkLessLv = new JCheckBox(Messages.MSGBelowSpecificLevel.toString());
		this.checkLessLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelWest.add(this.checkLessLv, "2, 12");

		this.checkMoreLv = new JCheckBox(Messages.MSGOverSpecificLevel.toString());
		this.checkMoreLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelWest.add(this.checkMoreLv, "2, 14");

		this.labelLvCaution = new JLabel(Messages.MSGLevelCheckboxInfo.toString());
		this.labelLvCaution.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelWest.add(this.labelLvCaution, "2, 16, fill, fill");

		this.panelEast = new JPanel();
		this.contentPane.add(this.panelEast, BorderLayout.EAST);
		this.panelEast.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("98px"),},
				new RowSpec[] {
						RowSpec.decode("26px"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(36dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(30dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(15dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("max(11dlu;default)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,}));

		this.btnImport = new JButton(Messages.MSGUpdatingDatabase.toString());
		this.btnImport.setEnabled(false);
		this.btnImport.addActionListener(e -> {
			if(this.impl != null) {
				if(!this.impl.getFlag()) {
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
		this.btnImport.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelEast.add(this.btnImport, "1, 3, fill, fill");

		this.btnStart = new JButton(Messages.MSGCalcStart.toString());
		this.btnStart.addActionListener(e -> {
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

		this.btnConfig = new JButton(Messages.MSGConfigurations.toString());
		this.btnConfig.addActionListener(e -> {
			ProcessBuilder builder = new ProcessBuilder("java", "-jar", "Configurations.jar");
			try {
				builder.start();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		});
		this.panelEast.add(this.btnConfig, "1, 5");
		this.btnStart.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelEast.add(this.btnStart, "1, 7, fill, fill");

		this.btnTwitterIntegration = new JButton(Messages.MSGTwitterIntegration.toString());
		this.btnTwitterIntegration.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 11));
		this.btnTwitterIntegration.addActionListener(e -> {
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

		this.btnManualUpdate = new JButton(Messages.MSGManualUpdate.toString());
		this.btnManualUpdate.addActionListener(e -> {
			this.impl = new ManualUpdateThreadImpl();
			es.submit(this.impl);
		});
		this.panelEast.add(this.btnManualUpdate, "1, 9");
		this.panelEast.add(this.btnTwitterIntegration, "1, 11");

		this.btnExit = new JButton(Messages.MSGTerminate.toString());
		this.btnExit.addActionListener(e -> {
			if(DelesteRandomSelector.this.softwareUpdateFuture.isDone() || DelesteRandomSelector.this.albumTypeEstimateFuture.isDone() || !this.impl.getFlag()) {
				DelesteRandomSelector.this.logger.info("Requested Exit by Button.");
				this.logger.info("Shut down thread pool.");
				es.shutdown();
				System.exit(0);
			} else {
				JOptionPane.showMessageDialog(null, Messages.MSGInternalYpdateNotDoneYet.toString());
			}
		});
		this.btnExit.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		this.panelEast.add(this.btnExit, "1, 13");

		this.panelCentre = new JPanel();
		this.contentPane.add(this.panelCentre, BorderLayout.CENTER);
		this.panelCentre.setLayout(new BorderLayout(0, 0));

		this.textArea = new JTextArea();
		this.textArea.setText(Messages.MSGNarrowDownProcedure.toString() + this.property.getSongLimit() + Messages.MSGCurrentAlbumType.toString() + this.albumType);
		this.textArea.setEditable(false);

		this.scrollPane = new JScrollPane(this.textArea);
		this.panelCentre.add(this.scrollPane, BorderLayout.CENTER);
		if(isFirst || !this.property.isCheckLibraryUpdates()) {
			setEnabled.run();
		}
	}

}
