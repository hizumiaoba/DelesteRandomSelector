package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

	private JPanel contentPane;
	private JPanel panelNorth;
	private JPanel panelWest;
	private JLabel labelVersion;
	private JLabel labelTitle;
	private JCheckBox chkDEBUT;
	private JCheckBox chkREGULAR;
	private JCheckBox chkPRO;
	private JCheckBox chkMASTER;
	private JLabel labelDifficulty;
	private JCheckBox chkMASTERPLUS;
	private JLabel labelLevel;
	private JSpinner spinnerLevel;
	private JCheckBox chckbxNewCheckBox_5;
	private JCheckBox chckbxNewCheckBox_6;
	private JPanel panelEast;
	private JPanel panelCentre;
	private JButton btnImport;
	private JButton btnStart;
	private JButton btnExit;
	private JTextPane textPane;

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
		CompletableFuture<ArrayList<Song>> getWholedataFuture = CompletableFuture.supplyAsync(() -> Scraping.getWholeData(), es);
		try {
			System.out.println("総楽曲数：" + getWholedataFuture.get().size());
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(this, "例外:InterruptedException\n内容:非同期処理中に割り込みが発生しました。詳細を確認する場合は、batファイルからアプリケーションを起動してください。 \n" + e.getStackTrace());
			e.printStackTrace();
			e.getCause();
		} catch (ExecutionException e) {
			JOptionPane.showMessageDialog(this, "例外:ExecutionException\n内容:非同期処理中に例外が発生しました。。詳細を確認する場合は、batファイルからアプリケーションを起動しスタックトレースを確認してください。 \n" + e.getStackTrace());
			e.printStackTrace();
			e.getCause();
		}
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));

		labelDifficulty = new JLabel("難易度選択");
		panelWest.add(labelDifficulty, "2, 2, center, default");

		chkDEBUT = new JCheckBox("DEBUT");
		panelWest.add(chkDEBUT, "2, 4, left, top");

		chkREGULAR = new JCheckBox("REGULAR");
		panelWest.add(chkREGULAR, "2, 6");

		chkPRO = new JCheckBox("PRO");
		panelWest.add(chkPRO, "2, 8");

		chkMASTER = new JCheckBox("MASTER");
		panelWest.add(chkMASTER, "2, 10");

		chkMASTERPLUS = new JCheckBox("MASTER+");
		panelWest.add(chkMASTERPLUS, "2, 12");

		labelLevel = new JLabel("楽曲Lv");
		panelWest.add(labelLevel, "2, 14, center, default");

		spinnerLevel = new JSpinner();
		panelWest.add(spinnerLevel, "2, 16");

		chckbxNewCheckBox_6 = new JCheckBox("指定Lv以下");
		chckbxNewCheckBox_6.setFont(new Font("ＭＳ Ｐゴシック", Font.BOLD, 12));
		panelWest.add(chckbxNewCheckBox_6, "2, 18");

		chckbxNewCheckBox_5 = new JCheckBox("指定Lv以上");
		chckbxNewCheckBox_5.setFont(new Font("ＭＳ Ｐゴシック", Font.BOLD, 12));
		panelWest.add(chckbxNewCheckBox_5, "2, 20");

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

		btnImport = new JButton("<html><body>楽曲<br>取り込み</body></html>");
		panelEast.add(btnImport, "1, 3, fill, fill");

		btnStart = new JButton("開始！");
		panelEast.add(btnStart, "1, 7, fill, fill");

		btnExit = new JButton("終了");
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
