
package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

public class TabManager extends JPanel {
	public Editor editor;

	public JTabbedPane tabbedPane;
	public int tabCount = 0;

	public TabManager(Editor editor) {
		this.editor = editor;

		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

		newTab();

		tabbedPane.add(new JPanel(), "✚", tabCount++);
		tabbedPane.addChangeListener(changeListener);
	}

	public Tab getSelected() {
		int index = tabbedPane.getSelectedIndex();
		if (index == -1)
			return null;

		return (Tab) tabbedPane.getTabComponentAt(index);
	}

	public void newTab() {
		int index = tabCount == 0 ? tabCount : tabCount - 1;
		JPanel panel = new JPanel(new GridLayout(1, 1));

		JTextArea textArea = new JTextArea();
		panel.add(new JScrollPane(textArea));

		tabbedPane.add(panel, "Sem título " + String.valueOf(index), index);
		tabbedPane.setTabComponentAt(index, new Tab(textArea));
		if (tabCount != 0) {
			tabbedPane.removeChangeListener(changeListener);
			tabbedPane.setSelectedIndex(index);
			tabbedPane.addChangeListener(changeListener);
		}
		tabCount++;
	}

	ChangeListener changeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (tabbedPane.getSelectedIndex() == tabCount - 1) {
				newTab();
			}
		}
	};

	public void open(File file) {
		if (file.isDirectory())
			return;

		int index = tabbedPane.indexOfTab(file.getAbsolutePath());
		if (index == -1) {
			newTab();
			Tab tab = getSelected();

			tab.fileChooser.setSelectedFile(file);

			tab.open();
		} else {
			tabbedPane.setSelectedIndex(index);
		}
	}

	public boolean verifyChanges() {
		for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
			Component component = tabbedPane.getTabComponentAt(i);
			if (component instanceof Tab) {
				Tab tab = (Tab) component;
				if (!tab.confirmExit()) {
					return false;
				}
			}
		}
		return true;
	}

	public class Tab extends JPanel {

		private String content = "";
		private boolean modified = false;

		public JFileChooser fileChooser = new JFileChooser();

		public JLabel label;
		public JButton button;
		public JTextArea textArea;

		public Tab(JTextArea ta) {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					tabbedPane.setSelectedIndex(thisIndex());

					updateBar();
					updateTitle();
				}
			});
			textArea = ta;
			textArea.setTabSize(editor.tabSize);

			String[] ext = { "txt", "md", "cpp", "hpp", "c", "h", "java", "html", "csv", "bat", "xml" };
			FileFilter filter = new FileNameExtensionFilter("Arquivos de texto", ext);
			fileChooser.setFileFilter(filter);

			InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
			ActionMap actionMap = textArea.getActionMap();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
			inputMap.put(keyStroke, "save_shortcut");
			actionMap.put("save_shortcut", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			textArea.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					int linenum = 1;
					int columnnum = 1;
					int caretpos = textArea.getCaretPosition();
					try {
						linenum = textArea.getLineOfOffset(caretpos);
						columnnum = caretpos - textArea.getLineStartOffset(linenum);
					} catch (BadLocationException ex) {
					}
					editor.statusBar.setCursor(columnnum + 1, linenum + 1);
				}
			});
			textArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					updateTitle();
				}
			});

			label = new JLabel() {
				public String getText() {
					int index = thisIndex();
					if (fileChooser.getSelectedFile() != null) {
						return filename();
					} else if (index != -1) {
						return tabbedPane.getTitleAt(index);
					} else {
						return null;
					}
				}
			};
			label.setBorder(new EmptyBorder(0, 0, 0, 10));
			add(label);

			button = new JButton();
			button.setText("✖");
			button.setPreferredSize(new Dimension(18, 18));
			button.setToolTipText("Fechar");
			button.setContentAreaFilled(false);
			button.setBorder(new EtchedBorder());
			button.setBorderPainted(false);
			button.setBackground(new Color(160, 197, 223));
			button.setFocusable(false);
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (confirmExit())
						close();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					button.setText("✖");
					button.setForeground(Color.WHITE);
					button.setOpaque(true);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					updateButton();
					button.setForeground(Color.BLACK);
					button.setOpaque(false);
				}
			});
			add(button);

			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setBorder(new EmptyBorder(5, 2, 2, 2));
			setOpaque(false);
		}

		public int thisIndex() {
			return tabbedPane.indexOfTabComponent(Tab.this);
		}

		public String thisTitle() {
			return tabbedPane.getTitleAt(thisIndex());
		}

		public void updateTitle() {
			String title = filepath() + " - " + editor.programName;
			if (content.equals(textArea.getText())) {
				editor.setTitle(title);
				modified = false;
			} else {
				editor.setTitle("●" + title);
				modified = true;
			}
			updateButton();
		}

		public void updateBar() {
			editor.statusBar.setFile(filepath());
		}

		private void updateButton() {
			if (modified) {
				button.setText("●");
			} else {
				button.setText("✖");
			}
		}

		public String filepath() {
			if (fileChooser.getSelectedFile() == null)
				return thisTitle();

			return fileChooser.getSelectedFile().getAbsolutePath();
		}

		public String filename() {
			if (fileChooser.getSelectedFile() == null)
				return thisTitle();

			return fileChooser.getSelectedFile().getName();
		}

		public boolean confirmExit() {
			if (modified) {
				int save = JOptionPane.showConfirmDialog(editor,
						"Deseja salvar as alterações feitas em " + filename() + "?");
				if (save == JOptionPane.YES_OPTION) {
					File current_file = fileChooser.getSelectedFile();
					if (current_file == null) {
						int result = fileChooser.showDialog(null, "Salvar como");
						if (result == JFileChooser.CANCEL_OPTION)
							return false;
					}
					current_file = fileChooser.getSelectedFile();
					save();
				} else if (save == JOptionPane.CANCEL_OPTION) {
					return false;
				}
			}
			return true;
		}

		public void open() {
			editor.statusBar.setStatus("Lendo arquivo...");
			try {
				FileReader fileReader = new FileReader(filepath());
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				textArea.read(bufferedReader, null);

				this.content = textArea.getText();
				this.modified = false;

				tabbedPane.setTitleAt(thisIndex(), filepath());
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(editor, "Arquivo não encontrado '" + filepath() + "'");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(editor,
						"Ocorreu um erro durante a leitura do arquivo '" + filepath() + "'");
			}
			editor.statusBar.setStatus(StatusBar.READY);
			textArea.setTabSize(editor.tabSize);

			updateBar();
			updateTitle();
		}

		public void close() {
			int index = thisIndex();
			if (index != -1) {
				tabbedPane.remove(index);
				tabCount--;
				if (index > 0) {
					tabbedPane.setSelectedIndex(index - 1);
				} else {
					tabbedPane.setSelectedIndex(index);
				}

				if (tabCount == 1) {
					newTab();
				}
			}
		}

		public void save() {
			if (fileChooser.getSelectedFile() == null) {
				int result = fileChooser.showDialog(null, "Salvar como");
				if (result == JFileChooser.CANCEL_OPTION)
					return;
			}

			File file = fileChooser.getSelectedFile();
			try {
				file.createNewFile();
				PrintStream pout = new PrintStream(file);
				pout.print(textArea.getText());
				pout.close();

				content = textArea.getText();
				modified = false;

				editor.setTitle(editor.programName + " " + file.toString());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(editor, "Ocorreu um erro durante a escrita do arquivo");
			}
			updateBar();
			updateTitle();
			tabbedPane.updateUI();
		}
	}
}
