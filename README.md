# projetolp2
Repositório do projeto final da disciplina de Linguagem de Programação II

Arquivo executável: Editor.java

Questão 01 - Concluída.
	BufferedReader: TabManager.Tab.open[307, 17];
	PrintStream: TabManager.Tab.save[353, 17];

Questão 02 - Concluída.
	JTextArea: TabManager.newTab[74, 9]; (Instanciação)
	JTextArea: TabManager.Tab.Tab[147, 13]; (Atribuição e configuração)

Questão 03 - Concluída.
	JScrollPane: TabManager.newTab[75, 23];

Questão 04 - Concluída.
	StatusBar: Editor.Editor[48, 25]; (Instanciação)
	StatusBar: Editor.Editor[68, 18]; (Inserção no painel)
	Caminho do arquivo aberto: StatusBar.update[60, 38];
	Linha e coluna corrente: StatusBar.update[61, 25];

Questão 05 - Concluída.
	TabManager: Editor.Editor[50, 9]; (Instanciação)
	TabManager: Editor.Editor[51, 71]; (Inserção no painel)

Questão 06 - Concluída.
	JMenuBar: Editor.Editor[46, 9]; (Atribuição como barra de menu do frame)
	Ação de abrir arquivo: Editor.configureMenu[95, 9];
	Ação de fechar: Editor.configureMenu[135, 9];
	Ação de salvar: Editor.configureMenu[116, 9];
	Ação de salvar como: Editor.configureMenu[123, 9];
	Cada aba aberta possui seu próprio JFileChooser: TabManager.Tab[131, 16]

Questão 07 - Concluída.
	FileTree: Editor.Editor[49, 9]; (Instanciação)

	Observações:
	1. A árvore possui um filtro de extensões de arquivo e não se limita
	apenas à arquivos .txt: FileTree[24, 23]

Questão 08 - Concluída.
	Ação de abrir pasta: Editor.configureMenu[109, 9];
	
	Observações:
	1. Ao invés de um botão à parte, a função de selecionar um diretório para a árvore
	de diretórios foi adicionada como parte do menu.

