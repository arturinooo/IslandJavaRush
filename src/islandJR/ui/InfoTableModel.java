package islandJR.ui;

import islandJR.Info;
import islandJR.Settings;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

// табличная модель данных для информации о животных
public class InfoTableModel extends AbstractTableModel {
    private Settings settings = Settings.getInstance();             // настройки
    private HashMap<String, Info> hmInfo = settings.getInfoMap();   // информация о животных
    private String[] names; // список названий животных

    // конструктор
    public InfoTableModel() {
        names = hmInfo.keySet().toArray(new String[0]);
        Arrays.sort(names);
    }

    @Override
    // количество строк
    public int getRowCount() {
        return hmInfo.size();
    }

    @Override
    // количество колонок
    public int getColumnCount() {
        return 8;
    }

    @Override
    // возвращает занчение ячейки таблицы
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex<0 || rowIndex>=getRowCount() || columnIndex<0 || columnIndex>=getColumnCount()) return null;
        String key = names[rowIndex];
        Info row = hmInfo.get(key);
        switch (columnIndex){
            case 0: return key;
            case 1: return row.getWeight();
            case 2: return row.getMaxCount();
            case 3: return row.getSpeed();
            case 4: return row.getMaxFood();
            case 5: return row.getChilds();
            case 6: return row.getInitCount();
            case 7: return row.getProbMove();
        }
        return null;
    }

    // названия колонок
    private String[] titles = {"Name","Weight","MaxCount","Speed","MaxFood","Childs","InitCount","Probability Move"};

    @Override
    // возвращает название колонки
    public String getColumnName(int column) {
        if(column<0 || column>=getColumnCount()) return null;
        return titles[column];
    }

    @Override
    // возвращает тип данных в ячейке
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex<0 || columnIndex>=getColumnCount()) return null;
        if(columnIndex==0) return String.class;
        else if(columnIndex==1 || columnIndex==4) return Double.class;
        else return Integer.class;
    }

    @Override
    // делаем редактируемыми все колонки кроме первой
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex>0);
    }

    @Override
    // задать новое значение в ячейку таблицы
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex<0 || rowIndex>=getRowCount() || columnIndex<0 || columnIndex>=getColumnCount()) return;
        String key = names[rowIndex];
        Info row = hmInfo.get(key);
        switch ((columnIndex)){
            case 1: row.setWeight((Double)aValue); break;
            case 2: row.setMaxCount((Integer)aValue); break;
            case 3: row.setSpeed((Integer)aValue); break;
            case 4: row.setMaxFood((Double)aValue); break;
            case 5: row.setChilds((Integer)aValue); break;
            case 6: row.setInitCount((Integer)aValue); break;
            case 7: row.setProbMove((Integer)aValue); break;
        }

    }

    // пробный запуск таблицы
    public static void main(String[] args) {
        JTable table = new JTable(new InfoTableModel());
        Settings settings = Settings.getInstance();

        JFrame frame = new JFrame("Table");
        frame.setSize(500, 300);

        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton save = new JButton("Save");
        panel.add(save, BorderLayout.SOUTH);
        save.addActionListener(e->settings.storeAnimals());
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
