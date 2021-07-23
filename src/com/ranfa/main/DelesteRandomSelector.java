package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.ranfa.lib.LimitedLog;
import com.ranfa.lib.Scraping;
import com.ranfa.lib.SettingJSONProperty;
import com.ranfa.lib.Settings;
import com.ranfa.lib.Song;
import com.ranfa.lib.Version;

@Version(major = 0, minor = 2, patch = 0)
public class DelesteRandomSelector extends JFrame {

	private static ArrayList<Song> selectedSongsList = new ArrayList<Song>();

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
	private JTextPane textPane;
	private JComboBox comboDifficultySelect;
	private JLabel labelLvCaution;
	private JComboBox comboAttribute;

	private SettingJSONProperty property = new SettingJSONProperty();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DelesteRandomSelector frame = new DelesteRandomSelector();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * log file prefix:
	 *  "[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[LEVEL]: " +
	 */

	/**
	 * Create the frame.
	 */
	public DelesteRandomSelector() {
		if(!Settings.fileExists() && !Settings.writeDownJSON()) {
			JOptionPane.showMessageDialog(this, "Exception:NullPointerException\nCannot Keep up! Please re-download this Application!");
			throw new NullPointerException("FATAL: cannot continue!");
		}
		LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[DEBUG]: " + "Loading Settings...");
		property.setCheckLibraryUpdates(Settings.needToCheckLibraryUpdates());
		property.setCheckVersion(Settings.needToCheckVersion());
		property.setWindowWidth(Settings.getWindowWidth());
		property.setWindowHeight(Settings.getWindowHeight());
		property.setSongLimit(Settings.getSongsLimit());
		property.setSaveScoreLog(Settings.saveScoreLog());
		property.setOutputDebugSentences(Settings.outputDebugSentences());
		LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[DEBUG]: " + "Loading Settings done."
				+ "\nVersion Check: " + property.isCheckVersion()
				+ "\nLibrary Update Check: " + property.isCheckLibraryUpdates()
				+ "\nWindow Width: " + property.getWindowWidth()
				+ "\nWindow Height: " + property.getWindowHeight()
				+ "\nSong Limit: " + property.getSongLimit()
				+ "\nSaveScoreLog: " + property.isSaveScoreLog()
				+ "\nOutputDebugSentences: " + property.isOutputDebugSentences());
		if(!Scraping.databaseExists()) {
			JOptionPane.showMessageDialog(this, "楽曲データベースが見つかりませんでした。自動的に作成されます…\n注意：初回起動ではなく、かつ、Jarファイルと同じ階層に\"database.json\"というファイルが存在するにも関わらず\nこのポップアップが出た場合、開発者までご一報ください。\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues");
			if(!Scraping.writeToJson(Scraping.getWholeData())) {
				JOptionPane.showMessageDialog(this, "Exception:NullPointerException\\nCannot Keep up! Please re-download this Application!");
				throw new NullPointerException("FATAL: cannot continue!");
			}
		} else if(Scraping.getFromJson().size() < Scraping.getWholeData().size()) {
			LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: " + "Update detected.Initiate update process...");
			Path path = Paths.get(Scraping.getDBPath());
			try {
				Files.delete(path);
			} catch (IOException e1) {
				LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[FATAL]: " + "Exception while updating library.\n" + e1.getLocalizedMessage());
				JOptionPane.showMessageDialog(null, "データベースファイルをアップデートできませんでした。ファイルの削除権限があるかどうか確認してください。\n" + e1.getLocalizedMessage());
			}
			Scraping.writeToJson(Scraping.getWholeData());
			LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: " + "Library update completed.");
		}
		ExecutorService es = Executors.newWorkStealingPool();
		CompletableFuture<ArrayList<Song>> getFromJsonFuture = CompletableFuture.supplyAsync(() -> Scraping.getFromJson(), es);
		CompletableFuture<ArrayList<Song>> getWholeDataFuture = CompletableFuture.supplyAsync(() -> Scraping.getWholeData(), es);
		getWholeDataFuture.thenAcceptAsync(list -> LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: Scraping data size:" + list.size()), es);
		getFromJsonFuture.thenAcceptAsync(list -> LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO] Currently database size:" + list.size()), es);
		LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[DEBUG]: " + "Version:" + getVersion());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panelNorth = new JPanel();
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(302dlu;default)"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("40px"),},
			new RowSpec[] {
				RowSpec.decode("20px"),}));

		labelTitle = new JLabel("デレステ課題曲セレクター");
		labelTitle.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 16));
		panelNorth.add(labelTitle, "1, 1, center, top");

		labelVersion = new JLabel(getVersion());
		labelVersion.setFont(new Font("SansSerif", Font.BOLD, 12));
		panelNorth.add(labelVersion, "3, 1, right, top");

		panelWest = new JPanel();
		contentPane.add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new FormLayout(new ColumnSpec[] {
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

		labelDifficulty = new JLabel("難易度選択");
		labelDifficulty.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelWest.add(labelDifficulty, "2, 2, center, default");

		comboDifficultySelect = new JComboBox();
		comboDifficultySelect.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		comboDifficultySelect.setModel(new DefaultComboBoxModel(new String[] {"指定なし", "DEBUT", "REGULAR", "PRO", "MASTER", "MASTER+", "ⓁMASTER+", "LIGHT", "TRICK", "PIANO", "FORTE", "WITCH"}));
		panelWest.add(comboDifficultySelect, "2, 4, fill, default");

				comboAttribute = new JComboBox();
				comboAttribute.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				comboAttribute.setModel(new DefaultComboBoxModel(new String[] {"指定なし", "全タイプ", "キュート", "クール", "パッション"}));
				panelWest.add(comboAttribute, "2, 6, fill, default");

				labelLevel = new JLabel("楽曲Lv");
				labelLevel.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				panelWest.add(labelLevel, "2, 8, center, default");

				spinnerLevel = new JSpinner();
				panelWest.add(spinnerLevel, "2, 10");

				checkLessLv = new JCheckBox("指定Lv以下");
				checkLessLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				panelWest.add(checkLessLv, "2, 12");

				checkMoreLv = new JCheckBox("指定Lv以上");
				checkMoreLv.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				panelWest.add(checkMoreLv, "2, 14");

				labelLvCaution = new JLabel("<html><body>※以下以上両方にチェックをつけることで指定レベルのみ絞り込むことができます</body></html>");
				labelLvCaution.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				panelWest.add(labelLvCaution, "2, 16, fill, fill");

		panelEast = new JPanel();
		contentPane.add(panelEast, BorderLayout.EAST);
		panelEast.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("98px"),},
			new RowSpec[] {
				RowSpec.decode("26px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(50dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(50dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(11dlu;default)"),}));

		btnImport = new JButton("<html><body>楽曲<br>絞り込み</body></html>");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Song> fromJson = Scraping.getFromJson();
					ArrayList<Song> specificlevelList = Scraping.getSpecificLevelSongs(fromJson, (Integer)spinnerLevel.getValue(), checkLessLv.isSelected(), checkMoreLv.isSelected());
					ArrayList<Song> specificDifficultyList = Scraping.getSpecificDifficultySongs(specificlevelList, comboDifficultySelect.getSelectedItem().toString());
					ArrayList<Song> specificAttributeList = Scraping.getSpecificAttributeSongs(specificDifficultyList, comboAttribute.getSelectedItem().toString());
					if(!selectedSongsList.isEmpty())
					selectedSongsList.clear();
				selectedSongsList.addAll(specificAttributeList);
				LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: " +"Songs are selected.We are Ready to go.");
			}
		});
		btnImport.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnImport, "1, 3, fill, fill");

		btnStart = new JButton("開始！");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random random = new Random(System.currentTimeMillis());
				String[] tmp = new String[property.getSongLimit()];
				for(int i = 0; i < property.getSongLimit(); i++) {
					int randomInt = random.nextInt(selectedSongsList.size());
					tmp[i] = (i + 1) + "曲目： " + selectedSongsList.get(randomInt).getAttribute() + " [" + selectedSongsList.get(randomInt).getDifficulty() + "]「" + selectedSongsList.get(randomInt).getName() + "」！(Lv:" + selectedSongsList.get(randomInt).getLevel() + ")\n\n";
				}
				String paneString = "";
				for (int i = 0; i < tmp.length; i++) {
					paneString = paneString + tmp[i];
				}
				paneString = paneString + "この" + tmp.length + "曲をプレイしましょう！！！";
				textPane.setText(paneString);
				LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: " + "show up completed.");
			}
		});
		btnStart.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnStart, "1, 7, fill, fill");

		btnExit = new JButton("終了");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LimitedLog.println("[" + Thread.currentThread().toString() + "]:" + this.getClass() + ":[INFO]: " +"Requested Exit by Button");
				if(getWholeDataFuture.isDone()) {
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(null, "非同期処理が完了していません。少し時間が経ってからやり直してください。");
				}
			}
		});
		btnExit.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnExit, "1, 11");

		panelCentre = new JPanel();
		contentPane.add(panelCentre, BorderLayout.CENTER);
		panelCentre.setLayout(new BorderLayout(0, 0));

		textPane = new JTextPane();
		textPane.setText("楽曲選択の手順\r\n１．難易度、属性、レベルを選択する\r\n２．「楽曲取り込み」ボタンを押す！\r\n３．「開始」ボタンを押す！\r\n４．選択された楽曲がここに表示されます！\r\n現在設定されている楽曲選択の最大数：" + property.getSongLimit());
		textPane.setEditable(false);
		panelCentre.add(textPane);
	}


	/**
	 * アノテーションで記載されているバージョンを取得します
	 * @since v1.0.0
	 * @return アノテーションで定義されているバージョン
	 */
	public static String getVersion() {
		String value = "v"
				+ getMajorVersion() + "."
				+ getMinorVersion() + "."
				+ getPatchVersion();
		return value;
	}

	public static int getMajorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.major();
	}

	public static int getMinorVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.minor();
	}

	public static int getPatchVersion() {
		Version version = (Version) DelesteRandomSelector.class.getAnnotation(Version.class);
		return version.patch();
	}
}
