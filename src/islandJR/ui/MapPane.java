package islandJR.ui;

import islandJR.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// редактирование карты
public class MapPane extends JPanel {
    private Map map = Map.getInstance();    // карта
    private int cellSize = 10;              // размер клетки
    private Dimension size;                 // размер компоненты
    private Color colorSea = new Color(153,204,255); // цвет моря
    private Color colorGrn = new Color(0,204,51);    // цвет земли

    public MapPane() {
        init(); // инициализуем данные
        addMouseListener(mouse); // добавляем слушатель для мышки
    }

    // инициализуем данные
    private void init(){
        // если карта маленькая, используем размер клетки побольше
        cellSize = (map.getData().length<=100) ? 30 : 10;
        size = new Dimension(map.getWidth()*cellSize, map.getHeight()*cellSize);
    }

    // создаем новую карту с заданными размерами
    public void newMap(int width, int height){
        if(width<1 ) width=1;
        if(height<1) height=1;
        map.newMap(width, height);
        init();
        repaint();
    }

    // сохраняем карту в файл
    public void storeMap(){
        map.store();
    }

    // обработка кликов мышкой
    private MouseAdapter mouse = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int cellX = e.getX() / cellSize;
            int cellY = e.getY() / cellSize;

            if(cellX>=map.getWidth() || cellY>map.getHeight()) return;

            map.toggle(cellX, cellY);
            repaint();
        }
    };

    @Override
    // минимальные размеры компоненты
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    // максимальные размеры компоненты
    public Dimension getMaximumSize() {
        return size;
    }

    @Override
    // предпочтительный размер компоненты
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    // рисуем карту
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        int width  = map.getWidth() *cellSize;
        int height = map.getHeight()*cellSize;

        g2d.setColor(colorSea);
        g2d.fillRect(0,0,width,height);

        // остров
        g2d.setColor(colorGrn);
        char[] buf = map.getData();
        for(int i=0; i<buf.length; i++){
            if(buf[i]=='=') continue;
            int x = i % map.getWidth();
            int y = i / map.getWidth();

            g2d.fillRect(x*cellSize, y*cellSize, cellSize, cellSize);
        }

        // сетка
        g2d.setColor(Color.GRAY);
        for(int i=0; i<=map.getWidth(); i++){
            g2d.drawLine(i*cellSize,0,i*cellSize,height);
        }
        for(int i=0; i<=map.getHeight(); i++){
            g2d.drawLine(0,i*cellSize,width,i*cellSize);
        }


    }

    // пробный запуск
    public static void main(String[] args) {
        MapPane pane = new MapPane();
        JPanel  content = new JPanel(new BorderLayout(3,3));
        content.add(new JScrollPane(pane));
        JPanel  pButtons = new JPanel();
        JFormattedTextField tfWidth  = new JFormattedTextField(new Integer(0));
        tfWidth.setColumns(5);
        JFormattedTextField tfHeight = new JFormattedTextField(new Integer(0));
        tfHeight.setColumns(5);
        JButton btnNew = new JButton("New map");
        btnNew.addActionListener(e->pane.newMap((Integer) (tfWidth.getValue()), (Integer)(tfHeight.getValue())));
        pButtons.add(new JLabel("width: "));
        pButtons.add(tfWidth);
        pButtons.add(new JLabel(" height: "));
        pButtons.add(tfHeight);
        pButtons.add(Box.createHorizontalStrut(20));
        pButtons.add(btnNew);

        JButton btnStore = new JButton("Store");
        btnStore.addActionListener(e->pane.storeMap());
        pButtons.add(Box.createHorizontalStrut(20));
        pButtons.add(btnStore);

        content.add(pButtons, BorderLayout.SOUTH);

        JFrame frame = new JFrame("Island");

        frame.setSize(500,300);
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }

}
