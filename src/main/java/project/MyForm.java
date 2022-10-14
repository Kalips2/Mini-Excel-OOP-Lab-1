package project;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import project.Model.HeaderModel;
import project.Model.MainModel;
import project.Model.MyCellModel;
import project.ParserToSave.GeneratorFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyForm extends JFrame {
    int rowSize = 10;
    int columnSize = 10;
    JPanel panel1;
    JButton button1;
    JPanel panel2;
    JButton button2;
    JButton button3;
    JButton button4;
    JTextField textField;
    JTable mainTable;
    JButton button5;
    JScrollPane scrollPane;
    JLabel formulaLabel;
    JTable headerTable;
    MainModel mainModel;
    HeaderModel headerModel;
    JMenuBar menuBar;
    GeneratorFile generatorFile;


    static final int WIDTH = 1000;
    static final int HEIGHT = 400;


    private void createUIComponents() {
        setUpComponents();
    }

    private void setUpComponents() {
        panelsSetUp();
        b1SetUp();
        b2SetUp();
        b3SetUp();
        b4SetUp();
        b5SetUp();
        labelSetUp();
        textFieldSetUp();
        menuSetUp();
        tableSetUp();
        scrollSetUp();
        frameSetUp();
    }

    private void frameSetUp() {
        setTitle("Mini-Excel");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitFromApp();
            }
        });
    }

    private void menuSetUp() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("New", KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newItemSetUp();
            }
        });
        fileMenu.add(newItem);

        JMenuItem saveAgainItem = new JMenuItem("Save", KeyEvent.VK_R);
        saveAgainItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        saveAgainItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(mainModel.getPath());
                saveFile(mainModel.getPath().length() < 1);

            }
        });
        fileMenu.add(saveAgainItem);

        JMenuItem saveItem = new JMenuItem("Save as", KeyEvent.VK_E);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(true);
            }
        });
        fileMenu.add(saveItem);

        JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_S);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        openItem.addActionListener(new ActionListener() {

            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openFile();
                } catch (Exception ex) {
                    showOptionPane(ex.getMessage());
                }
            }

        });
        fileMenu.add(openItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_W);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitFromApp();
            }
        });
        fileMenu.add(exitMenuItem);

        setJMenuBar(menuBar);
        setPreferredSize(new Dimension(1000, 600));
    }

    private void openFile() throws IOException {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Ви хочете зберігти цю таблицю?",
                "Mini-Excel",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (n == 0) saveFile(false);
        if (n == 1) {
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getPath().endsWith(".txt") | f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return ".txt";
                }
            });
            int returnVal = fc.showDialog(this, "Open");
            if (returnVal == 0) {
                String path = fc.getSelectedFile().getPath();
                try {
                    MainModel newModel = GeneratorFile.createTableFromText(path);
                    int x = newModel.getROW_SIZE() - rowSize;
                    rowSize = newModel.getROW_SIZE();
                    columnSize = newModel.getCOLUMN_SIZE();
                    mainModel.setROW_SIZE(rowSize);
                    mainModel.setCOLUMN_SIZE(columnSize);
                    mainModel.setData(newModel.getData());
                    mainModel.setPath(newModel.getPath());
                    //Add/Remove header Rows:
                    if (x > 0) {
                        int k = 0;
                        while (k < x) {
                            headerModel.addRow();
                            k++;
                        }
                    } else {
                        int k = 0;
                        while (k < Math.abs(x)) {
                            headerModel.removeRow();
                            k++;
                        }
                    }

                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(
                            this, "Error in opening file!");
                }
                mainModel.fireTableStructureChanged();
            }
        }
    }

    private void newItemSetUp() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Ви хочете зберігти цю таблицю?",
                "Mini-Excel",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (n == 0) {
            saveFile(false);
            this.dispose();
            MyForm form = new MyForm();
        } else if (n == 1) {
            this.dispose();
            MyForm form = new MyForm();
        }
    }

    private void exitFromApp() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Ви хочете зберігти цю таблицю?",
                "Mini-Excel",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (n == 0) saveFile(false);
        if (n == 0 || n == 1) this.dispose();
    }

    private boolean saveFile(boolean saveAgain) {
        String path;
        System.out.println(mainModel.getPath().length());
        if (mainModel.getPath().length() <= 1 || saveAgain) {
            final JFileChooser fc = new JFileChooser();
            int saved;
            fc.setAcceptAllFileFilterUsed(false);

            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getPath().endsWith(".txt") | f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return ".txt";
                }
            });
            saved = fc.showSaveDialog(this);
            if (saved != 0) {
                return false;
            }
            path = fc.getSelectedFile().toString();
            mainModel.setPath(path);
            System.out.println(path + "NEW PATH");
        } else {
            path = mainModel.getPath();
        }

        if (!path.endsWith(".txt")) {
            path += ".txt";
        }
        System.out.println("Path to save file:" + path);
        generatorFile = new GeneratorFile(mainModel, path);
        try {
            generatorFile.createTableHowText(mainModel, path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this, "Error in saving file!");
        }
        mainModel.setSaved(true);
        return true;
    }

    private void panelsSetUp() {
        panel1 = new JPanel();
        panel2 = new JPanel();
    }

    private void textFieldSetUp() {
        textField = new JTextField();
        textField.setText("Виберіть комірку: ");
        textField.setEditable(false);
        textField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int row, column;
                row = mainTable.getSelectedRow();
                column = mainTable.getSelectedColumn();
                String s = textField.getText();
                MyCell currentCell = mainModel.getData()[row][column];
                String expBefore = currentCell.getExpression();
                String valueBefore = currentCell.getValue();
                List<MyCell> DependedFromMeBefore = new ArrayList<>(currentCell.getDependedCells());
                List<MyCell> IDependedFromBefore = new ArrayList<>(currentCell.getMyDependens());

                try {
                    if (checkTextIsPrimary(s)) {
                        mainModel.setExpressionAt(s.trim(), row, column);
                        mainModel.fireTableDataChanged();
                        setVisibleCell(row, column);
                    } else {
                        try {
                            for (var i : IDependedFromBefore) {
                                i.getDependedCells().remove(currentCell);
                            }
                            currentCell.setMyDependens(new ArrayList<>());
                            currentCell.setExpression(s);
                            mainModel.setExpressionAt(s, row, column);
                            setVisibleCell(row, column);
                        } catch (Exception ex) {
                            currentCell.setExpression(expBefore);
                            currentCell.setValue(valueBefore);
                            currentCell.setDependedCells(DependedFromMeBefore);
                            currentCell.setMyDependens(IDependedFromBefore);
                            showOptionPane(ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    currentCell.setExpression(expBefore);
                    currentCell.setValue(valueBefore);
                    currentCell.DependedCells = DependedFromMeBefore;
                    currentCell.setMyDependens(IDependedFromBefore);
                    showOptionPane(ex.getMessage());
                }
            }

        });
    }

    private boolean checkTextIsPrimary(String s) {
        if (s.length() == 0) return true;
        int i = 0;
        if (s.charAt(0) != '=') {
            if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '-') {
                i++;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    i++;
                }
                if (i < s.length() - 1 && s.charAt(i) == '.') {
                    i++;

                    if (!Character.isDigit(s.charAt(i))) {
                        return false;
                    }
                    while (i < s.length() && Character.isDigit(s.charAt(i))) {
                        i++;
                    }
                }
            }
            if (i != s.length()) throw new RuntimeException("Без '=' можна вводити тільки числа! Перевірте написане");
        }
        return i == s.length();
    }

    private void setVisibleCell(int row, int column) {
        if (column <= columnSize && column != -1) {
            mainTable.setRowSelectionInterval(row, row);
            mainTable.setColumnSelectionInterval(column, column);
        }
    }

    private void showOptionPane(String message) {
        JOptionPane.showMessageDialog(MyForm.this,
                message,
                "Mini-Excel",
                JOptionPane.ERROR_MESSAGE
        );
    }


    private void labelSetUp() {
        formulaLabel = new JLabel();
    }

    private void b5SetUp() {
        button5 = new JButton("Довідка");
        button5.setContentAreaFilled(false);
        button5.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo();
            }
        });
    }

    private void b4SetUp() {
        button4 = new JButton("Delete column");
        button4.setContentAreaFilled(false);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteColumnAtTable();
                } catch (Exception ex) {
                    showOptionPane(ex.getMessage());
                }
            }
        });
    }

    private void b3SetUp() {
        button3 = new JButton("Delete row");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteRowAtTable();
                } catch (Exception ex) {
                    showOptionPane(ex.getMessage());
                }
            }
        });
        button3.setContentAreaFilled(false);
    }

    private void b2SetUp() {
        button2 = new JButton("Add column");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addColumnAtTable();
            }
        });
        button2.setContentAreaFilled(false);
    }

    private void b1SetUp() {
        button1 = new JButton("Add row");
        button1.setContentAreaFilled(false);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRowAtTable();
            }
        });
    }

    private void deleteRowAtTable() {
        //For main and header table:
        if (!(rowSize <= 1)) {
            int row, column;
            row = mainTable.getSelectedRow();
            column = mainTable.getSelectedColumn();
            if (checkCanWeDeleteRow()) {
                rowSize--;
                mainModel.removeRow();
                headerModel.removeRow();
                if (row == rowSize) {
                    mainTable.setRowSelectionInterval(row - 1, row - 1);
                    mainTable.setColumnSelectionInterval(column, column);
                }
            } else
                throw new RuntimeException("Від клітинок цієї строки залежать інші! Видаліть залежності, щоб прибрати строку!");
        }
    }

    private boolean checkCanWeDeleteColumn() {
        for (int i = 0; i < mainModel.getData().length; i++) {
            //System.out.print(model.getData()[i][COLUMN_SIZE - 1].getDependedCells().size() + " ");
            if (mainModel.getData()[i][columnSize - 1].getDependedCells().size() != 0) return false;
        }
        return true;
    }

    private boolean checkCanWeDeleteRow() {
        for (int i = 0; i < mainModel.getData()[0].length; i++) {
            //System.out.print(model.getData()[ROW_SIZE - 1][i].getDependedCells().size() + " ");
            if (mainModel.getData()[rowSize - 1][i].getDependedCells().size() != 0) return false;
        }
        return true;
    }

    private void scrollSetUp() {
        scrollPane = new JScrollPane(mainTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        createHeaderTable();
        scrollPane.setRowHeaderView(headerTable);
    }

    private void tableSetUp() {
        mainModel = new MainModel(rowSize, columnSize);
        mainTable = new JTable(mainModel);
        mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        mainTable.getTableHeader().setReorderingAllowed(false);
        mainTable.setRowSelectionAllowed(false);
        mainTable.setColumnSelectionAllowed(false);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setDefaultRenderer(Object.class, new MyCellModel(textField));
        ListSelectionListener selectionChanged = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                textField.setEditable(true);
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int row, column;
                row = mainTable.getSelectedRow();
                column = mainTable.getSelectedColumn();
                String add = (column / 26 < 1 ? "" : Character.valueOf((char) (column / 26 + 64)).toString()) + Character.valueOf((char) (column % 26 + 65)).toString() + (row + 1);
                if (add.equals("@0")) add = "";
                formulaLabel.setText("Формула в: " + add + " ");

                if (!(row == -1) && !(column == -1)) textField.setText(mainModel.getExpressionAt(row, column));
                else {
                    textField.setText("Виберіть комірку: ");
                    textField.setEditable(false);
                }
            }
        };
        mainTable.getColumnModel().getSelectionModel().addListSelectionListener(selectionChanged);
        mainTable.getSelectionModel().addListSelectionListener(selectionChanged);
    }

    private void deleteColumnAtTable() {
        //For main table:
        if (!(columnSize <= 1)) {
            int row, column;
            row = mainTable.getSelectedRow();
            column = mainTable.getSelectedColumn();
            if (checkCanWeDeleteColumn()) {
                columnSize--;
                mainModel.removeColumn();
                if (column == columnSize) {
                    mainTable.setRowSelectionInterval(row, row);
                    mainTable.setColumnSelectionInterval(column - 1, column - 1);
                } else if (!(row == -1)) {
                    mainTable.setRowSelectionInterval(row, row);
                    mainTable.setColumnSelectionInterval(column, column);
                }
            } else {
                throw new RuntimeException("Від клітинок цього стовпця залежать інші! Видаліть залежності, щоб прибрати стовпець!");
            }
        }
    }

    private void addColumnAtTable() {
        //For main table:
        columnSize++;
        int row, column;
        row = mainTable.getSelectedRow();
        column = mainTable.getSelectedColumn();
        mainModel.addColumn("NEW");
        if (!(row == -1)) {
            mainTable.setRowSelectionInterval(row, row);
            mainTable.setColumnSelectionInterval(column, column);
        }


    }

    private void addRowAtTable() {
        //For main table:

        rowSize++;
        MyCell[] newRow1 = new MyCell[columnSize];
        for (int i = 0; i < columnSize; i++) {
            String name = (i / 26 < 1 ? "" : Character.valueOf((char) (i / 26 + 64)).toString()) + Character.valueOf((char) (i % 26 + 65)).toString() + (rowSize);
            newRow1[i] = new MyCell(rowSize - 1, i, "", "", name);
        }
        mainModel.addRow(newRow1);

        //For Header Table:
        int[] newRow2 = new int[1];
        newRow2[0] = rowSize;
        headerModel.addRow();


    }

    private void createHeaderTable() {
        headerModel = new HeaderModel(rowSize, 0);
        headerTable = new JTable(headerModel);
        headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        headerTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        headerTable.setPreferredScrollableViewportSize(new Dimension(30, 0));
        headerTable.setBackground(mainTable.getTableHeader().getBackground());
        headerTable.setFocusable(false);
        headerTable.setRowSelectionAllowed(false);
        headerTable.setColumnSelectionAllowed(false);
        mainModel.fireTableRowsUpdated(mainModel.getRowCount() - 1, mainModel.getRowCount() - 1);
    }

    @SneakyThrows
    private void showInfo() throws FileNotFoundException {
        File file = new File("src/main/resources/About.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String text = "";
        String line;
        while ((line = br.readLine()) != null) {
            text += line + '\n';
        }
        br.close();
        fr.close();
        JOptionPane.showMessageDialog(
                this, text, "Mini-Excel", JOptionPane.PLAIN_MESSAGE);
    }

    @SneakyThrows
    MyForm() {
        setVisible(true);
        add(panel1);
        setSize(new Dimension(WIDTH, HEIGHT));
        setIconImage(ImageIO.read(new File("src/main/resources/Excel.png")));

    }

    public static void main(String[] args) {
        MyForm form = new MyForm();
    }


}
