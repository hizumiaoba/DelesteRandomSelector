package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.ranfa.lib.CheckVersion;
import com.ranfa.lib.LimitedLog;
import com.ranfa.lib.Scraping;
import com.ranfa.lib.SettingJSONProperty;
import com.ranfa.lib.Settings;
import com.ranfa.lib.Song;
import com.ranfa.lib.TwitterIntegration;
import com.ranfa.lib.Version;

@Version(major = 1, minor = 3, patch = 6)
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
	 *  this.getClass() + ":[LEVEL]: " +
	 */

	/**
	 * Create the frame.
	 */
	public DelesteRandomSelector() {
		boolean isFirst = !Scraping.databaseExists();
		if(isFirst) {
			JOptionPane.showMessageDialog(this, "楽曲データベースが見つかりませんでした。自動的に作成されます…\n注意：初回起動ではなく、かつ、Jarファイルと同じ階層に\"database.json\"というファイルが存在するにも関わらず\nこのポップアップが出た場合、開発者までご一報ください。\nGithub URL: https://github.com/hizumiaoba/DelesteRandomSelector/issues");
			if(!Scraping.writeToJson(Scraping.getWholeData())) {
				JOptionPane.showMessageDialog(this, "Exception:NullPointerException\\nCannot Keep up! Please re-download this Application!");
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
		LimitedLog.println(this.getClass() + ":[DEBUG]: " + "Loading Settings...");
		property.setCheckLibraryUpdates(Settings.needToCheckLibraryUpdates());
		property.setCheckVersion(Settings.needToCheckVersion());
		property.setWindowWidth(Settings.getWindowWidth());
		property.setWindowHeight(Settings.getWindowHeight());
		property.setSongLimit(Settings.getSongsLimit());
		property.setSaveScoreLog(Settings.saveScoreLog());
		property.setOutputDebugSentences(Settings.outputDebugSentences());
		LimitedLog.println(this.getClass() + ":[DEBUG]: " + "Loading Settings done."
				+ "\nVersion Check: " + property.isCheckVersion()
				+ "\nLibrary Update Check: " + property.isCheckLibraryUpdates()
				+ "\nWindow Width: " + property.getWindowWidth()
				+ "\nWindow Height: " + property.getWindowHeight()
				+ "\nSong Limit: " + property.getSongLimit()
				+ "\nSaveScoreLog: " + property.isSaveScoreLog()
				+ "\nOutputDebugSentences: " + property.isOutputDebugSentences());
		if(property.isCheckVersion()) {
			softwareUpdateFuture = CompletableFuture.runAsync(() -> CheckVersion.needToBeUpdated(), es);
		}
		BiConsumer<ArrayList<Song>, ArrayList<Song>> updateConsumer = (list1, list2) -> {
			LimitedLog.println(this.getClass() + ":[INFO]: " + "Checking database updates...");
			if(list1.size() > list2.size()) {
				long time = System.currentTimeMillis();
				LimitedLog.println(this.getClass() + ":[INFO]: " + (list1.size() - list2.size()) + " Update detected.");
				Scraping.writeToJson(list1);
				LimitedLog.println(this.getClass() + ":[INFO]: " + "Update completed in " + (System.currentTimeMillis() - time) + "ms");
				LimitedLog.println(this.getClass() + ":[INFO]: " + "Updated database size: " + list1.size());
			} else {
				LimitedLog.println(this.getClass() + ":[INFO]: " + "database is up-to-date.");
			}
		};
		Runnable setEnabled = () -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			btnImport.setEnabled(true);
			btnImport.setText("<html><body>楽曲<br>絞り込み</body></html>");
		};
		getWholeDataFuture.thenAcceptAsync(list -> LimitedLog.println(this.getClass() + ":[INFO]: Scraping data size:" + list.size()), es);
		getFromJsonFuture.thenAcceptAsync(list -> LimitedLog.println(this.getClass() + ":[INFO]: Currently database size:" + list.size()), es);
		if(property.isCheckLibraryUpdates()) {
			CompletableFuture<Void> updatedFuture = getWholeDataFuture.thenAcceptBothAsync(getFromJsonFuture, updateConsumer, es);
			updatedFuture.thenRunAsync(setEnabled, es);
		}
		LimitedLog.println(this.getClass() + ":[DEBUG]: " + "Version:" + CheckVersion.getVersion());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, property.getWindowWidth(), property.getWindowHeight());
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

		labelVersion = new JLabel(CheckVersion.getVersion());
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
		comboDifficultySelect.setFont(new Font("Dialog", Font.BOLD, 12));
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

		btnImport = new JButton("<html><body>データベース<br>更新中…</body></html>");
		btnImport.setEnabled(false);
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Song> fromJson = Scraping.getFromJson();
					ArrayList<Song> specificlevelList = Scraping.getSpecificLevelSongs(fromJson, (Integer)spinnerLevel.getValue(), checkLessLv.isSelected(), checkMoreLv.isSelected());
					ArrayList<Song> specificDifficultyList = Scraping.getSpecificDifficultySongs(specificlevelList, comboDifficultySelect.getSelectedItem().toString());
					ArrayList<Song> specificAttributeList = Scraping.getSpecificAttributeSongs(specificDifficultyList, comboAttribute.getSelectedItem().toString());
					if(!selectedSongsList.isEmpty())
					selectedSongsList.clear();
				selectedSongsList.addAll(specificAttributeList);
				LimitedLog.println(this.getClass() + ":[INFO]: " +"Songs are selected.We are Ready to go.");
				JOptionPane.showMessageDialog(null, "絞り込み完了！「開始」をクリックすることで選曲できます！");
			}
		});
		btnImport.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnImport, "1, 3, fill, fill");

		btnStart = new JButton("開始！");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random random = new Random(System.currentTimeMillis());
				String paneString = "";
				integratorArray = new String[property.getSongLimit()];
				for(int i = 0; i < property.getSongLimit(); i++) {
					int randomInt = random.nextInt(selectedSongsList.size());
					paneString = paneString + (i + 1) + "曲目： " + selectedSongsList.get(randomInt).getAttribute() + " [" + selectedSongsList.get(randomInt).getDifficulty() + "]「" + selectedSongsList.get(randomInt).getName() + "」！(Lv:" + selectedSongsList.get(randomInt).getLevel() + ")\n\n";
					integratorArray[i] = selectedSongsList.get(randomInt).getName() + "(Lv" + selectedSongsList.get(randomInt).getLevel() + ")\n";
				}
				paneString = paneString + "この" + property.getSongLimit() + "曲をプレイしましょう！！！";
				textArea.setText(paneString);
				integratorBool = true;
				LimitedLog.println(this.getClass() + ":[INFO]: " + "show up completed.");
			}
		});
		btnStart.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnStart, "1, 7, fill, fill");

				btnTwitterIntegration = new JButton("Twitter連携");
				btnTwitterIntegration.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 11));
				btnTwitterIntegration.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						LimitedLog.println(this.getClass() + ":[INFO]: " + "Twitter Integration requested.Verify permission status.");
						boolean authorizationStatus = TwitterIntegration.authorization();
						LimitedLog.println(this.getClass() + ":[INFO]: " + "Permission Verifying completed.\nStatus: " + authorizationStatus);
						LimitedLog.print(this.getClass() + ":[INFO]: " + "Construction status message...");
						String updatedStatus = "デレステ課題曲セレクターで\n";
						int lengthLimit = updatedStatus.length();
						boolean isBroken = false;
						if(!integratorBool) {
							JOptionPane.showMessageDialog(null, "ちひろ「まだプレイを始めていないみたいですね」");
							LimitedLog.println();
							return;
						}
						for(int i = 0; i < integratorArray.length; i++) {
							updatedStatus = updatedStatus + integratorArray[i];
							lengthLimit += integratorArray[i].length();
							if(lengthLimit > 69) {
								isBroken = true;
								break;
							}
						}
						if(isBroken) {
							updatedStatus = updatedStatus + "…その他数曲\nをプレイしました！\n#DelesteRandomSelector #デレステ ";
						} else {
							updatedStatus = updatedStatus + "をプレイしました！\n#DelesteRandomSelector #デレステ ";
						}
						LimitedLog.println("completed.\n" + updatedStatus);
						lengthLimit = updatedStatus.length();
						if(authorizationStatus) {
							int option = JOptionPane.showConfirmDialog(null, "Twitterへ以下の内容を投稿します。よろしいですか？\n\n" + updatedStatus + "\n\n文字数：" + lengthLimit);
							LimitedLog.println(this.getClass() + ":[INFO]: " + "User selected " + option);
							switch(option) {
								case JOptionPane.OK_OPTION:
									TwitterIntegration.PostTwitter(updatedStatus);
									LimitedLog.println(this.getClass() + ":[INFO]: " + "Success to update the status.");
									JOptionPane.showMessageDialog(null, "投稿が完了しました。");
									break;
								case JOptionPane.NO_OPTION:
									LimitedLog.println(this.getClass() + ":[INFO]: " + "There is no will to post.");
									break;
								case JOptionPane.CANCEL_OPTION:
									LimitedLog.println(this.getClass() + ":[INFO]: " + "The Operation was canceled by user.");
									break;
								default:
									break;
							}
						} else {
							LimitedLog.println(this.getClass() + ":[WARN]: " + "seems to reject the permission.it should need try again.");
						}
					}
				});
				panelEast.add(btnTwitterIntegration, "1, 11");

								btnExit = new JButton("終了");
								btnExit.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(softwareUpdateFuture.isDone()) {
											LimitedLog.println(this.getClass() + ":[INFO]: " +"Requested Exit by Button");
											System.exit(0);
										} else {
											JOptionPane.showMessageDialog(null, "内部更新処理が完了していません。少し待ってからやり直してください。");
										}
									}
								});
								btnExit.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
								panelEast.add(btnExit, "1, 13");

		panelCentre = new JPanel();
		contentPane.add(panelCentre, BorderLayout.CENTER);
		panelCentre.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setText("楽曲選択の手順\r\n１．難易度、属性、レベルを選択する\r\n２．「楽曲取り込み」ボタンを押す！\r\n３．「開始」ボタンを押す！\r\n４．選択された楽曲がここに表示されます！\r\n現在設定されている楽曲選択の最大数：" + property.getSongLimit());
		textArea.setEditable(false);

		scrollPane = new JScrollPane(textArea);
		panelCentre.add(scrollPane, BorderLayout.CENTER);
		if(isFirst || !property.isCheckLibraryUpdates())
			setEnabled.run();
	}

}
