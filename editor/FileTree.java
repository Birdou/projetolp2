
package editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class FileTree {
	private Editor editor;

	public String folderPath;
	public DefaultMutableTreeNode root;
	public JScrollPane treeScrollPane;
	public JTree tree;

	public static String[] extensions = { ".txt", ".md", ".java", ".cpp", ".hpp", ".c", ".h", ".html", ".css", ".json",
			".php", ".xml", ".csv", ".bat", ".py", ".conf", ".ini" };
	public FileFilter filter;

	FileTree(Editor editor) {
		this.editor = editor;
		filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				} else {
					for (String ext : extensions) {
						if (pathname.getName().endsWith(ext)) {
							return true;
						}
					}
				}
				return false;
			}
		};
	}

	private int walk(File file, DefaultMutableTreeNode root) {
		File[] list = file.listFiles(filter);

		if (list == null) {
			return 0;
		}

		int fileCount = 0;
		for (File f : list) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getName());
			if (f.isDirectory() && !f.isHidden()) {
				int nodes = walk(f, node);
				if (nodes > 0) {
					root.add(node);
					fileCount += nodes;
				}
			} else {
				String fileName = f.getName();
				for (String ext : extensions) {
					if (fileName.endsWith(ext)) {
						root.add(node);
						fileCount++;
						break;
					}
				}
			}
		}
		return fileCount;
	}

	public void setRoot(File file) {
		this.folderPath = file.getAbsolutePath();
		root = new DefaultMutableTreeNode(file.getName());

		walk(file, root);

		tree = new JTree(root);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				// if (me.getClickCount() == 2) {
				TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
				if (tp != null) {
					StringBuilder sb = new StringBuilder();
					Object[] nodes = tp.getPath();
					// O laço tem que iniciar em 1 para ignorar a raiz da árvore.
					for (int i = 1; i < nodes.length; ++i) {
						sb.append(File.separatorChar).append(nodes[i].toString());
					}
					editor.tabManager.open(new File(folderPath + sb.toString()));
				}
				// }
			}
		});
		editor.mainPanel.setDividerLocation(150);

		treeScrollPane = new JScrollPane(tree);
	}
}
