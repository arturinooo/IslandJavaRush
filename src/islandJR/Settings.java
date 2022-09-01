package islandJR;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

// Настройки
public class Settings {
    private static Settings instance;                           // ссылка на объект
    private static final String animals_config = "animals.cfg"; // файл с параметрами животных
    private static final String params_config = "params.cfg";   // параметры эмуляции

    // приватный конструктор чтобы настройки были доступны только через getInstance()
    private Settings(){
        init();
    }

    // получение настроек
    synchronized public static Settings getInstance(){
        if(instance==null) instance = new Settings();
        return instance;
    }

    // список информации о животных
    private HashMap<String, Info> hmInfo = new HashMap<>();
    private int iterations = 10;    // количество итераций
    private int duration   = 2000;  // длительность одной итерации
    private int cellPoolSize = 10;  // размер пула жизненного цикла животных на клетке


    // список информации о животных
    public HashMap<String, Info> getInfoMap(){
        return hmInfo;
    }

    // количество итераций
    public int getIterations() {
        return iterations;
    }

    // задать количество итераций
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    // длительность одной итерации
    public int getDuration() {
        return duration;
    }

    // задать длительность одной итерации
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // размер пула
    public int getCellPoolSize() {
        return cellPoolSize;
    }

    // задать размер пула
    public void setCellPoolSize(int cellPoolSize) {
        this.cellPoolSize = cellPoolSize;
    }

    // инициализация данных
    private void init(){

        // загружаем значения по умолчанию
        hmInfo.put("Волк",new Info(50,30,3,8,2,5,10));
        hmInfo.put("Удав",new Info(15,30,1,3,2,4,10));
        hmInfo.put("Лиса",new Info(8,30,2,2,2,8,10));
        hmInfo.put("Медведь",new Info(500,5,2,80,1,2,10));
        hmInfo.put("Орел",new Info(6,20,3,1,2,5,10));
        hmInfo.put("Лошадь",new Info(400,20,4,60,1,20,10));
        hmInfo.put("Олень",new Info(300,20,4,50,1,15,10));
        hmInfo.put("Кролик",new Info(2,150,2,0.45,2,10,10));
        hmInfo.put("Мышь",new Info(0.05,500,1,0.01,10,20,10));
        hmInfo.put("Коза",new Info(60,140,3,10,2,10,10));
        hmInfo.put("Овца",new Info(70,140,3,15,2,10,10));
        hmInfo.put("Кабан",new Info(400,50,2,50,3,10,10));
        hmInfo.put("Буйвол",new Info(700,10,3,100,2,10,10));
        hmInfo.put("Утка",new Info(1,200,4,0.15,3,20,10));
        hmInfo.put("Гусеница",new Info(0.01,1000,0,0.01,4,100,10));
        hmInfo.put("Растения",new Info(1,200,0,0,1,200,0));

        // загружаем значения из файла animals.cfg
        loadAnimals();
        // загружаем значения из файла params.cfg
        loadParams();
    }

    // геттеры парамтеров животных по названию

    public int getMaxCount(String name){
        if(!hmInfo.containsKey(name)) return 0;
        return hmInfo.get(name).getMaxCount();
    }

    public double getWeight(String name){
        if(!hmInfo.containsKey(name)) return 0;
        return hmInfo.get(name).getWeight();
    }

    public int getSpeed(String name){
        if(!hmInfo.containsKey(name)) return 0;
        return hmInfo.get(name).getSpeed();
    }

    public double getMaxFood(String name){
        if(!hmInfo.containsKey(name)) return 0;
        return hmInfo.get(name).getMaxFood();
    }

    public Info getInfo(String name){
        if(!hmInfo.containsKey(name)) return new Info();
        return hmInfo.get(name);
    }

    public int getInitCount(String name){
        if(!hmInfo.containsKey(name)) return 0;
        return hmInfo.get(name).getInitCount();
    }

    // загрузка описания животных из файла
    public void loadAnimals(){
        try {
            File file = new File(animals_config);
            if(!file.exists()) {
                System.out.println("Нет файла animals.cfg, используем значения по умолчанию.");
                storeAnimals();
                return;
            }
            List<String> lines = Files.readAllLines(file.toPath());

            for(String line: lines){
                line = line.trim();
                if(line.length()==0 || line.startsWith("#")) continue;
                try{
                    String[] items = line.split("\\s*;\\s*");
                    if(items.length<8) {
                        System.err.println("Неправильный формат: "+ line);
                        continue;
                    }
                    Info info = new Info();
                    info.setWeight(Double.parseDouble(items[1]));
                    info.setMaxCount(Integer.parseInt(items[2]));
                    info.setSpeed(Integer.parseInt(items[3]));
                    info.setMaxFood(Double.parseDouble(items[4]));
                    info.setChilds(Integer.parseInt(items[5]));
                    info.setInitCount(Integer.parseInt(items[6]));
                    info.setProbMove(Integer.parseInt(items[7]));
                    hmInfo.put(items[0], info);
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // запись описания животных в файл
    public void storeAnimals(){
        try(FileWriter fw = new FileWriter(animals_config)){
            fw.write("#name     ; weight;count;speed;maxfood;childs;initcount;move probability\n");
            for(String key: hmInfo.keySet()){
                Info info = hmInfo.get(key);
                // используем Locale.US чтобы для десятичного знака использовалась '.'
                // так как Double.parseDouble использует '.'
                String line = String.format(Locale.US,"%-10s;\t%6.2f;\t%4d;\t%2d;\t%6.2f;\t%2d;\t%3d;\t%2d\n",key,
                        info.getWeight(),
                        info.getMaxCount(),
                        info.getSpeed(),
                        info.getMaxFood(),
                        info.getChilds(),
                        info.getInitCount(),
                        info.getProbMove()
                        );
                fw.write(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // загрузка параметров
    public void loadParams(){
        Properties props = new Properties();
        File file = new File(params_config);
        if(!file.exists()) return;
        try(FileReader fr = new FileReader(file)) {
            props.load(fr);
            duration   = Integer.parseInt((String) props.getOrDefault("duration",""+duration), 10);
            iterations = Integer.parseInt((String) props.getOrDefault("iterations",""+iterations), 10);
            cellPoolSize = Integer.parseInt((String) props.getOrDefault("cellPoolSize",""+cellPoolSize), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // запись параметров в файл
    public void storeParams(){
        try(FileWriter fw = new FileWriter(params_config)){
            fw.write("duration="+duration+"\n");
            fw.write("iterations="+iterations+"\n");
            fw.write("cellPoolSize="+cellPoolSize+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
