package com.ranfa.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.ranfa.lib.Scraping;
import com.ranfa.lib.Song;
import com.ranfa.lib.Version;

@Version(major = 1, minor = 0, patch = 0)
public class DelesteRandomSelector extends JFrame {

	private JPanel contentPane;

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
		System.out.println(getVersion());
		ArrayList<Song> tmp = Scraping.getWholeData();
		for(int i = 0; i < tmp.size(); i++) {
			System.out.println(tmp.get(i).toString());
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
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
