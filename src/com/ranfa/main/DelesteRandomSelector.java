package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
import com.ranfa.lib.Scraping;
import com.ranfa.lib.Song;
import com.ranfa.lib.Version;

@Version(major = 1, minor = 0, patch = 0)
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

	private int wholeDataSize;

	private ArrayList<Song> wholeDataList;

	private ArrayList<Song> fromJsonList;

	private Integer fromJsonSize;

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
	 * Create the frame.
	 */
	public DelesteRandomSelector() {
		ExecutorService es = Executors.newWorkStealingPool();
		CompletableFuture<ArrayList<Song>> getFromJsonFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return Scraping.getFromJson();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			return null;
		}, es);
		CompletableFuture<ArrayList<Song>> getWholeDataFuture = CompletableFuture.supplyAsync(() -> Scraping.getWholeData(), es);
		getWholeDataFuture.thenAcceptAsync(list -> System.out.println(list.size()), es);
		getFromJsonFuture.thenAcceptAsync(list -> System.out.println(list.size()), es);
		getWholeDataFuture.thenAcceptAsync(list -> {
			wholeDataList.addAll(list);
		}, es);
		getFromJsonFuture.thenAcceptAsync(list -> {
			fromJsonList.addAll(list);
			if(getWholeDataFuture.isDone())
				if(wholeDataList.size() != list.size()) {
					fromJsonList.clear();
					fromJsonList.addAll(wholeDataList);
				}
		}, es);
		System.out.println("Version:" + getVersion());
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
		comboDifficultySelect.setModel(new DefaultComboBoxModel(new String[] {"DEBUT", "REGULAR", "PRO", "MASTER", "MASTER+", "ⓁMASTER+", "LIGHT", "TRICK", "PIANO", "FORTE", "WITCH"}));
		panelWest.add(comboDifficultySelect, "2, 4, fill, default");

				comboAttribute = new JComboBox();
				comboAttribute.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
				comboAttribute.setModel(new DefaultComboBoxModel(new String[] {"全タイプ", "キュート", "クール", "パッション"}));
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
				ArrayList<Song> limitedList = new ArrayList<>();
				ArrayList<Song> fromJson = new ArrayList<Song>();
					try {
						fromJson.addAll(getFromJsonFuture.get());
					} catch (InterruptedException e1) {
						JOptionPane.showMessageDialog(null, "例外：InterruptedException\n非同期処理待機中に割り込みが発生しました。\n" + e1.getLocalizedMessage());
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						JOptionPane.showMessageDialog(null, "例外：ExecutionException\n非同期処理中に例外が発生しました。\n" + e1.getLocalizedMessage());
						e1.printStackTrace();
					}
				limitedList.addAll(Scraping.getSpecificAttributeSongs(Scraping.getSpecificDifficultySongs(Scraping.getSpecificLevelSongs(fromJson, (Integer)spinnerLevel.getValue(), checkLessLv.isSelected(), checkMoreLv.isSelected()), comboDifficultySelect.getSelectedItem().toString()), comboAttribute.getSelectedItem().toString()));
				selectedSongsList.addAll(limitedList);
				System.out.println("Songs are selected.We are Ready to go.");
			}
		});
		btnImport.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnImport, "1, 3, fill, fill");

		btnStart = new JButton("開始！");
		btnStart.setFont(new Font("UD デジタル 教科書体 NP-B", Font.BOLD, 13));
		panelEast.add(btnStart, "1, 7, fill, fill");

		btnExit = new JButton("終了");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Requested Exit by Button");
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
