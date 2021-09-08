
package editor;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StatusBar {

	public JPanel panel = new JPanel();

	public JLabel left = new JLabel();
	public JLabel right = new JLabel();

	private String status;
	private String file;
	private int x, y;
	private int tabLenght;

	public static String READY = "Pronto";

	public StatusBar() {
		super();

		panel.setLayout(new GridLayout(1, 2));
		panel.add(left, java.awt.BorderLayout.WEST);
		panel.add(right, java.awt.BorderLayout.EAST);

		right.setHorizontalAlignment(SwingConstants.RIGHT);

		status = READY;

		update();
	}

	public void setStatus(String status) {
		this.status = status;
		update();
	}

	public void setFile(String file) {
		this.file = file;
		update();
	}

	public void setCursor(int x, int y) {
		this.x = x;
		this.y = y;
		update();
	}

	public void setTabLenght(int tabLenght) {
		this.tabLenght = tabLenght;
		update();
	}

	private void update() {
		left.setText(" " + status + (file == null ? "" : "  " + file));
		right.setText(" Ln " + y + ", Col " + x + "  Tamanho da tabulação: " + tabLenght + " ");
	}
}