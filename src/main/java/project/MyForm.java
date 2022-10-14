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
    int ROW_SIZE = 10;
    int COLUMN_SIZE = 10;
    JPanel panel1;
    JButton button1;
    JPanel panel2;
    JButton button2;
    JButton button3;
    JButton button4;
    JTextField textField1;
    JTable table1;
    JButton button5;
    JScrollPane scrollPane1;
    JLabel label;
    JTable headerTable;
    MainModel model;
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
                newMenu();
            }
        });
        fileMenu.add(newItem);

        JMenuItem saveAgainItem = new JMenuItem("Save", KeyEvent.VK_R);
        saveAgainItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        saveAgainItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(model.getPath());
                if (model.getPath().length() >= 1) {
                    saveFile(false);
                } else saveFile(true);

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
                    int x = newModel.getROW_SIZE() - ROW_SIZE;
                    ROW_SIZE = newModel.getROW_SIZE();
                    COLUMN_SIZE = newModel.getCOLUMN_SIZE();
                    model.setROW_SIZE(ROW_SIZE);
                    model.setCOLUMN_SIZE(COLUMN_SIZE);
                    model.setData(newModel.getData());
                    model.setPath(newModel.getPath());
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
                model.fireTableStructureChanged();
            }
        }
    }

    private void newMenu() {
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
        //System.out.println("" + n);

        if (n == 0) saveFile(false);
        if (n == 0 || n == 1) this.dispose();
    }

    private boolean saveFile(boolean saveAgain) {
        String path;
        System.out.println(model.getPath().length());
        if (model.getPath().length() <= 1 || saveAgain) {
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
            model.setPath(path);
            System.out.println(path + "NEW PATH");
        } else {
            path = model.getPath();
        }

        if (!path.endsWith(".txt")) {
            path += ".txt";
        }
        System.out.println("Path to save file:" + path);
        generatorFile = new GeneratorFile(model, path);
        try {
            generatorFile.createTableHowText(model, path);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this, "Error in saving file!");
        }
        model.setSaved(true);
        return true;
    }

    private void panelsSetUp() {
        panel1 = new JPanel();
        panel2 = new JPanel();
    }

    private void textFieldSetUp() {
        textField1 = new JTextField();
        textField1.setText("Виберіть комірку: ");
        textField1.setEditable(false);
        textField1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //System.out.println("=======================================================");
                int row, column;
                row = table1.getSelectedRow();
                column = table1.getSelectedColumn();
                String s = textField1.getText();
                MyCell currentCell = model.getData()[row][column];
                String expBefore = currentCell.getExpression();
                String valueBefore = currentCell.getValue();
                List<MyCell> DependedFromMeBefore = new ArrayList<>(currentCell.getDependedCells());
                List<MyCell> IDependedFromBefore = new ArrayList<>(currentCell.getIDependedFrom());

                if(currentCell.getName().equals("D1")) {

                }

                try {
                    if (checkTextIsPrimary(s)) {
                        model.setExpressionAt(s.trim(), row, column);
                        model.fireTableDataChanged();
                        setVisibleCell(row, column);
                    } else {
                        try {
                            for (var i : IDependedFromBefore) {
                                i.getDependedCells().remove(currentCell);
                            }
                            currentCell.setIDependedFrom(new ArrayList<>());
                            currentCell.setExpression(s);
                            model.setExpressionAt(s, row, column);
                            setVisibleCell(row, column);
                        } catch (Exception ex) {
                            currentCell.setExpression(expBefore);
                            currentCell.setValue(valueBefore);
                            currentCell.setDependedCells(DependedFromMeBefore);
                            currentCell.setIDependedFrom(IDependedFromBefore);
                            showOptionPane(ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    currentCell.setExpression(expBefore);
                    currentCell.setValue(valueBefore);
                    currentCell.DependedCells = DependedFromMeBefore;
                    currentCell.setIDependedFrom(IDependedFromBefore);
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
            if (i != s.length()) throw new RuntimeException("Без \'=\' можна вводити тільки числа! Перевірте написане");
        }
        return i == s.length();
    }

    private void setVisibleCell(int row, int column) {
        if (column <= COLUMN_SIZE && column != -1) {
            table1.setRowSelectionInterval(row, row);
            table1.setColumnSelectionInterval(column, column);
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
        label = new JLabel();
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
        if (!(ROW_SIZE <= 1)) {
            int row, column;
            row = table1.getSelectedRow();
            column = table1.getSelectedColumn();
            if (checkCanWeDeleteRow()) {
                ROW_SIZE--;
                model.removeRow();
                headerModel.removeRow();
                if (row == ROW_SIZE) {
                    table1.setRowSelectionInterval(row - 1, row - 1);
                    table1.setColumnSelectionInterval(column, column);
                }
            } else
                throw new RuntimeException("Від клітинок цієї строки залежать інші! Видаліть залежності, щоб прибрати строку!");
        }
    }

    private boolean checkCanWeDeleteColumn() {
        for (int i = 0; i < model.getData().length; i++) {
            //System.out.print(model.getData()[i][COLUMN_SIZE - 1].getDependedCells().size() + " ");
            if (model.getData()[i][COLUMN_SIZE - 1].getDependedCells().size() != 0) return false;
        }
        return true;
    }

    private boolean checkCanWeDeleteRow() {
        for (int i = 0; i < model.getData()[0].length; i++) {
            //System.out.print(model.getData()[ROW_SIZE - 1][i].getDependedCells().size() + " ");
            if (model.getData()[ROW_SIZE - 1][i].getDependedCells().size() != 0) return false;
        }
        return true;
    }

    private void scrollSetUp() {
        scrollPane1 = new JScrollPane(table1);
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());
        createHeaderTable();
        scrollPane1.setRowHeaderView(headerTable);
    }

    private void tableSetUp() {
        model = new MainModel(ROW_SIZE, COLUMN_SIZE);
        table1 = new JTable(model);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table1.getTableHeader().setReorderingAllowed(false);
        table1.setRowSelectionAllowed(false);
        table1.setColumnSelectionAllowed(false);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setDefaultRenderer(Object.class, new MyCellModel(textField1));
        ListSelectionListener selectionChanged = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                textField1.setEditable(true);
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int row, column;
                row = table1.getSelectedRow();
                column = table1.getSelectedColumn();
                String add = (column / 26 < 1 ? "" : Character.valueOf((char) (column / 26 + 64)).toString()) + Character.valueOf((char) (column % 26 + 65)).toString() + (row + 1);
                if (add.equals("@0")) add = "";
                label.setText("Формула в: " + add + " ");

                if (!(row == -1) && !(column == -1)) textField1.setText(model.getExpressionAt(row, column));
                else {
                    textField1.setText("Виберіть комірку: ");
                    textField1.setEditable(false);
                }
            }
        };
        table1.getColumnModel().getSelectionModel().addListSelectionListener(selectionChanged);
        table1.getSelectionModel().addListSelectionListener(selectionChanged);
    }

    private void deleteColumnAtTable() {
        //For main table:
        if (!(COLUMN_SIZE <= 1)) {
            int row, column;
            row = table1.getSelectedRow();
            column = table1.getSelectedColumn();
            if (checkCanWeDeleteColumn()) {
                COLUMN_SIZE--;
                model.removeColumn();
                if (column == COLUMN_SIZE) {
                    table1.setRowSelectionInterval(row, row);
                    table1.setColumnSelectionInterval(column - 1, column - 1);
                } else if (!(row == -1)) {
                    table1.setRowSelectionInterval(row, row);
                    table1.setColumnSelectionInterval(column, column);
                }
            } else {
                throw new RuntimeException("Від клітинок цього стовпця залежать інші! Видаліть залежності, щоб прибрати стовпець!");
            }
        }
    }

    private void addColumnAtTable() {
        //For main table:
        COLUMN_SIZE++;
        int row, column;
        row = table1.getSelectedRow();
        column = table1.getSelectedColumn();
        model.addColumn("NEW");
        if (!(row == -1)) {
            table1.setRowSelectionInterval(row, row);
            table1.setColumnSelectionInterval(column, column);
        }


    }

    private void addRowAtTable() {
        //For main table:

        ROW_SIZE++;
        MyCell[] newRow1 = new MyCell[COLUMN_SIZE];
        for (int i = 0; i < COLUMN_SIZE; i++) {
            String name = (i / 26 < 1 ? "" : Character.valueOf((char) (i / 26 + 64)).toString()) + Character.valueOf((char) (i % 26 + 65)).toString() + (ROW_SIZE);
            newRow1[i] = new MyCell(ROW_SIZE - 1, i, "", "", name);
        }
        model.addRow(newRow1);

        //For Header Table:
        int[] newRow2 = new int[1];
        newRow2[0] = ROW_SIZE;
        headerModel.addRow();


    }

    private void createHeaderTable() {
        headerModel = new HeaderModel(ROW_SIZE, 0);
        headerTable = new JTable(headerModel);
        headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        headerTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        headerTable.setPreferredScrollableViewportSize(new Dimension(30, 0));
        headerTable.setBackground(table1.getTableHeader().getBackground());
        headerTable.setFocusable(false);
        headerTable.setRowSelectionAllowed(false);
        headerTable.setColumnSelectionAllowed(false);
        model.fireTableRowsUpdated(model.getRowCount() - 1, model.getRowCount() - 1);
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
