package islandJR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// описание карты территории
public class Map {
    private int width, height;  // количество клеток по ширине и высоте
    private char[] data;        // массив длиной width * height
    private static final String fileName = "map.cfg";   // файл с описание карты

    public static final int LEFT=0, UP=1, RIGHT=2, DOWN=3;  // константы направлений движения

    private static Map instance; // карта - синглтон, instance - ссылка на созданный объек Map

    // приватный конструктор чтобы объект можно было получить только через метов getInstance()
    private Map(){
        width = 0;
        height= 0;
        data  = new char[0];
        load();
    }

    // зарузка карты из файла
    private void load(){
        try{
            List<String> lines = Files.readAllLines(new File(fileName).toPath());
            if(lines.size()<2) {
                System.err.println("Нет карты");
                return;
            }
            // в первой строке ширина и высота карты разделенный пробелом, запятой или '*'
            String[] sizes = lines.get(0).trim().split("(\\s|,|\\*)+");
            if(sizes.length<2) {
                System.err.println("В первой строке карты нужно указать width height");
                return;
            }
            width = Integer.parseInt(sizes[0],10);
            height= Integer.parseInt(sizes[1],10);
            data = new char[width * height];

            Arrays.fill(data, '='); // = - море # - остров
            StringBuilder sb = new StringBuilder();
            for(int i=1; i<lines.size(); i++){
                sb.append(lines.get(i).trim());
            }
            char[] map = sb.toString().toCharArray();
            for(int i=0; i<data.length && i<map.length; i++){
                data[i] = map[i];
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // сохраняем карту в файл
    public void store(){
        try(FileWriter fw = new FileWriter(fileName)) {
            fw.write(""+width+" "+height+"\n");
            for(int i=0; i<height; i++){
                fw.write(new String(data, i*width, width));
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // создаем новую карту заданных размеров
    public void newMap(int width, int height){
        this.width = width;
        this.height= height;
        data = new char[width*height];
        Arrays.fill(data,'=');
    }

    // метод для получения карты
    synchronized public static Map getInstance(){
        if(instance == null) {
            instance = new Map();
        }
        return instance;
    }

    // проверка корректности параметров карты
    public boolean isReady(){
        return (width>0 && height>0 && data!=null);
    }

    // вывод карты на экран
    public void print(){
        if(!isReady()) {
            System.err.println("Нет карты");
            return;
        }
        for(int i=0; i<height; i++){
            String line = new String(data, i*width, width);
            System.out.println(line);
        }
    }

    // получения номера в массиве по координатам
    public int position(int x, int y){
        return y * width + x;
    }

    // получение номера ячейки в массиве после передвижения в указанном направлении
    public int shift(int pos, int direction){
        int x = pos % width;
        int y = pos / width;
        switch (direction){
            case LEFT:  x--; break;
            case UP:    y--; break;
            case RIGHT: x++; break;
            case DOWN:  y++; break;
        }
        int new_pos = position(x,y);
        return new_pos;
    }

    // проверка возможно ли движение в указанном направлении
    // движение возможно только по земле не пересекая края карты
    public boolean isMoveApplicable(int pos, int direction){
        int x = pos % width;
        int y = pos / width;
        switch (direction){
            case LEFT:  if(x==0) return false;
                        x--;
                        break;
            case UP:    if(y==0) return false;
                        y--;
                        break;
            case RIGHT: if(x==width-1) return false;
                        x++;
                        break;
            case DOWN:  if(y==height-1) return false;
                        y++;
                        break;
        }
        int new_pos = position(x,y);
        return data[new_pos]=='#';
    }

    // ширина карты
    public int getWidth() {
        return width;
    }

    // высота карты
    public int getHeight() {
        return height;
    }

    // массив клеток карты
    public char[] getData() {
        return data;
    }

    // проверяет что клетка содержит землю
    public boolean isGround(int pos){
        if(!isReady()) return false;
        if(pos<0 || pos>=data.length) return false;
        return data[pos]=='#';
    }

    // проверяет возможно ли движение из данной клетки в любом направлении
    public boolean isBlocked(int pos){
        int x = pos % width;
        int y = pos / width;
        //System.out.println("x="+x+", y="+y);
        if(x>0 && isGround(position(x-1,y))) return false;
        if(y>0 && isGround(position(x,y-1))) return false;
        if(x<width-1 && isGround(position(x+1,y))) return false;
        if(y<height-1 && isGround(position(x,y+1))) return false;
        return true;
    }

    // двигаемся на count клеток в случайном направлении
    // если дальше двигаться в выбранном направлении не получается, меняем направление
    // возвращает номер клетки куда пришли
    public int path(int fromCell, int count){
        int pos = fromCell;
        if(isBlocked(fromCell)) return fromCell;
        while(count>0){
            // выбираем случайное направление
            int direction = ThreadLocalRandom.current().nextInt(4);
            if(!isMoveApplicable(pos,direction)) continue;
            // двигаемся пока движение возможно
            do{
                pos = shift(pos, direction);
                count--;
            }while(count>0 && isMoveApplicable(pos,direction));
        }
        return pos;
    }

    // заменяет клетку море на землю и наоборот
    public void toggle(int x, int y){
        int pos = position(x,y);
        data[pos] = (data[pos]=='=') ? '#' : '=';
    }

    // пробный запуск карты
    public static void main(String[] args) {
        Map map = Map.getInstance();
        map.print();

        System.out.println("false ="+map.isMoveApplicable(7, LEFT));
        System.out.println("false ="+map.isMoveApplicable(5, LEFT));
        System.out.println("false ="+map.isMoveApplicable(7, UP));
        System.out.println("false ="+map.isMoveApplicable(4, UP));
        System.out.println("false ="+map.isMoveApplicable(7, RIGHT));
        System.out.println("false ="+map.isMoveApplicable(4, RIGHT));
        System.out.println("false ="+map.isMoveApplicable(17, DOWN));
        System.out.println("false ="+map.isMoveApplicable(20, DOWN));

        System.out.println("true ="+map.isMoveApplicable(7, DOWN));

        System.out.println("true = "+map.isBlocked(0));
        System.out.println("false = "+map.isBlocked(7));

    }
}
