package islandJR.ui;

import islandJR.Map;

import javax.swing.*;
import java.awt.*;

// Редактор настроек
public class Editor {

    // вкладки с разделами
    private JTabbedPane tpane;

    public void editor(){
        tpane = new JTabbedPane();

        // редактирование параметров животных
        JPanel pInfo = new JPanel(new BorderLayout(3,3));
            InfoTableModel tableModel = new InfoTableModel();
            JTable table = new JTable(tableModel);
        pInfo.add(table, BorderLayout.CENTER);
            JPanel pInfoButtons = new JPanel();
            JButton btnInfoSave = new JButton("Save");
        pInfoButtons.add(btnInfoSave);
        pInfo.add(pInfoButtons, BorderLayout.SOUTH);

        tpane.add(pInfo,"Animals");

        // редактирование острова
        JPanel pMap = new JPanel(new BorderLayout(3,3));
            MapPane mapPane = new MapPane();
        pMap.add(mapPane, BorderLayout.CENTER);
            Map map = Map.getInstance();
            JFormattedTextField tfWidth  = new JFormattedTextField(new Integer(map.getWidth()));
            tfWidth.setColumns(5);
            JFormattedTextField tfHeight = new JFormattedTextField(new Integer(map.getHeight()));
            tfHeight.setColumns(5);
            JPanel pMapEdits = new JPanel();
                pMapEdits.add(new JLabel("width: "));
                pMapEdits.add(tfWidth);
                pMapEdits.add(Box.createHorizontalStrut(20));
                pMapEdits.add(new JLabel("height: "));
                pMapEdits.add(tfHeight);
        pMap.add(pMapEdits, BorderLayout.NORTH);
            JPanel pMapButtons = new JPanel();
            JButton bMapNew = new JButton("New map");
            bMapNew.addActionListener(e->mapPane.newMap((Integer) (tfWidth.getValue()), (Integer)(tfHeight.getValue())));
            JButton bMapStore  = new JButton("Store");
                bMapStore.addActionListener(e->mapPane.storeMap());
                pMapButtons.add(bMapNew);
                pMapButtons.add(Box.createHorizontalStrut(20));
                pMapButtons.add(bMapStore);
        pMap.add(pMapButtons, BorderLayout.SOUTH);

        tpane.add(pMap, "Map");

        // параметры моделирования
        ParamsPane paramsPane = new ParamsPane();

        tpane.add(paramsPane, "Params");

        // запускаем фрейм
        JFrame frame = new JFrame("Editor");
        frame.setSize(700, 400);

        frame.setContentPane(tpane);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new Editor().editor();
    }
}
