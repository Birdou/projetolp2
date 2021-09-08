
package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import editor.TabManager.Tab;

class Editor extends JFrame {

	private static final long serialVersionUID = 1L;

	public String programName = "Javax Text Editor";

	public FileTree fileTree;
	public TabManager tabManager;
	public StatusBar statusBar;

	public JSplitPane mainPanel;

	public JMenuBar menuBar = new JMenuBar();

	public JFileChooser dirChooser = new JFileChooser();

	public int tabSize = 4;

	public static void main(String[] args) {
		Editor editor = new Editor();
		editor.start();
	}

	Editor() {
		this.setSize(600, 400);
		this.setTitle(programName);
		this.setJMenuBar(menuBar);

		statusBar = new StatusBar();
		fileTree = new FileTree(this);
		tabManager = new TabManager(this);
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, tabManager.tabbedPane);

		this.dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});

		mainPanel.setDividerSize(0);

		statusBar.setTabLenght(tabSize);

		configureMenu();

		this.add(statusBar.panel, java.awt.BorderLayout.SOUTH);
		this.add(mainPanel);
	}

	public void start() {
		this.setVisible(true);
	}

	public void stop() {
		if (!tabManager.verifyChanges())
			return;

		this.setVisible(false);
		this.dispose();
	}

	private void configureMenu() {
		JMenu fileMenu = new JMenu("Arquivo");
		menuBar.add(fileMenu);
		JMenuItem newAction = new JMenuItem("Novo");
		newAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabManager.newTab();
			}
		});
		JMenuItem openFileAction = new JMenuItem("Abrir arquivo");
		openFileAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabManager.newTab();
				Tab tab = tabManager.getSelected();
				int result = tab.fileChooser.showDialog(null, "Abrir arquivo");
				if (result == JFileChooser.CANCEL_OPTION) {
					tab.close();
					return;
				}
				tab.open();
			}
		});
		JMenuItem openDirAction = new JMenuItem("Abrir pasta");
		openDirAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFolder();
			}
		});
		JMenuItem saveAction = new JMenuItem("Salvar");
		saveAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabManager.getSelected().save();
			}
		});
		JMenuItem saveAsAction = new JMenuItem("Salvar como");
		saveAsAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tab tab = tabManager.getSelected();
				int result = tab.fileChooser.showDialog(null, "Salvar como");
				if (result == JFileChooser.CANCEL_OPTION)
					return;

				tab.save();
			}
		});
		JMenuItem closeAction = new JMenuItem("Fechar");
		closeAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabManager.getSelected().close();
			}
		});
		JMenuItem exitAction = new JMenuItem("Sair");
		exitAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		fileMenu.add(newAction);
		fileMenu.addSeparator();
		fileMenu.add(openFileAction);
		fileMenu.add(openDirAction);
		fileMenu.addSeparator();
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();
		fileMenu.add(closeAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);

		JMenu editMenu = new JMenu("Editar");
		menuBar.add(editMenu);
		JMenuItem cutAction = new JMenuItem("Cortar");
		cutAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		JMenuItem copyAction = new JMenuItem("Copiar");
		copyAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		JMenuItem pasteAction = new JMenuItem("Colar");
		pasteAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);

		JMenu viewMenu = new JMenu("Ver");
		menuBar.add(viewMenu);
		JMenuItem tabLenght = new JMenuItem("Tamanho da tabulação");
		tabLenght.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(Editor.this, "Insira o tamanho da tabulação");
				if (input == null)
					return;

				tabSize = Integer.valueOf(input);
				statusBar.setTabLenght(tabSize);
				for (int i = 0; i < tabManager.tabbedPane.getTabCount(); ++i) {
					Component component = tabManager.tabbedPane.getTabComponentAt(i);
					if (component instanceof Tab) {
						Tab tab = (Tab) component;
						tab.textArea.setTabSize(tabSize);
					}
				}
			}
		});
		viewMenu.add(tabLenght);
	}

	protected void openFolder() {
		int result = dirChooser.showDialog(null, "Abrir a pasta");
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		statusBar.setStatus("Lendo árvore de diretórios...");
		fileTree.setRoot(dirChooser.getSelectedFile());
		statusBar.setStatus(StatusBar.READY);
		mainPanel.setLeftComponent(fileTree.treeScrollPane);
		mainPanel.setDividerSize(3);
	}
}
