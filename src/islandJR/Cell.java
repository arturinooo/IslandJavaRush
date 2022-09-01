package islandJR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

// описание одной клетки карты
public class Cell {
    private int pos;        // номер клетки
    private double grass;   // сколько на клетке растительности. Доступ к grass регулируется lock.
    private ReentrantLock lock = new ReentrantLock(); // блокировка доступа к ресурсам клетки
    private HashMap<String, List<Animal>> hmAnimals = new HashMap<>();  // список животных
    private HashMap<String, List<Animal>> hmNewcomers = new HashMap<>();// список пришедших на клетку животных
    private HashMap<String, List<Animal>> hmNewborns = new HashMap<>(); // список родившихся животных

    private Island island;  // данные об острове

    // настройки
    private Settings settings = Settings.getInstance();

    // конструктор
    public Cell(int pos, Island island) {
        this.pos = pos;
        this.island = island;
        randomFill();
    }

    // выдает случайное животное по названию
    // животное выдается уже заблокированным, поэтому по завершению работы с ним нужно снять блокировку
    public Animal getRandom(String name){
        Animal result = null;
        lock.lock();
        try {
            List<Animal> list = hmAnimals.get(name);
            if (list==null || list.size() == 0) return null;
            result = list.get(ThreadLocalRandom.current().nextInt(list.size()));
            if(result==null || !result.tryLock()){
                result = null;
            }
            if(result!=null && result.isDead()){
                result.unlock();
                result = null;
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    // возвращает список имен животных
    public List<String> getNames(){
        ArrayList<String> result = new ArrayList<>();
        lock.lock();
        try {
            for (String key : hmAnimals.keySet()) {
                if (hmAnimals.get(key).size() > 0) {
                    result.add(key);
                }
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    // возвращает случайное животное с именем не совпадающим с name
    // животное выдается уже заблокированным, поэтому по завершению работы с ним нужно снять блокировку
    public Animal getRandomOther(String name){
        List<String> keys = getNames();
        keys.remove(name);
        if(keys.size()==0) return null;
        Animal result = null;
        int cnt = 0;
        do {
            String key = keys.get(ThreadLocalRandom.current().nextInt(keys.size()));

            result = getRandom(key);
            cnt++;
        } while(result==null && cnt<=10);
        return result;
    }

    // возвращает случайное животное с именем из заданного списка
    // животное выдается уже заблокированным, поэтому по завершению работы с ним нужно снять блокировку
    public Animal getRandom(String[] list){
        List<String> names = getNames();
        List<String> keys = new ArrayList<>();
        for(String name: list){
            if(names.contains(name)) keys.add(name);
        }

        if(keys.size()==0) return null;
        Animal result = null;
        int cnt = 0;
        do {
            String key = keys.get(ThreadLocalRandom.current().nextInt(keys.size()));

            result = getRandom(key);
            cnt++;
        } while(result==null && cnt<=10);
        return result;
    }

    // добавляет count животных с имененм name
    private void fill(String name, int count){
        lock.lock();
        try {
            for (int i = 0; i < count; i++) {
                putToMap(hmAnimals, Animal.create(name));
            }
        }finally {
            lock.unlock();
        }
    }

    // заполняет клетку животными случайным образом
    private void randomFill(){
        grass = (ThreadLocalRandom.current().nextInt(settings.getInitCount("Растения")));
        for(String key: settings.getInfoMap().keySet()){
            if("Растения".equals(key)) continue;
            fill(key, ThreadLocalRandom.current().nextInt(settings.getInitCount(key)));
        }
    }

    // подготовка к запуску новога шага эмеляции
    public void initNewLoop(){
        lock.lock();
        try {
            if (grass > 0) {
                // трава удваивается но не больше максимума
                grass = (Math.min(grass * 2, settings.getMaxCount("Растения")));
            } else {
                // не даем растениям полностью погибнуть (из семян и корней вырастают новые)
                grass = 10;
            }
            // удаляем из списка погибших животных
            for (String key : hmAnimals.keySet()) {
                List<Animal> src = hmAnimals.get(key);
                List<Animal> listRemove = new ArrayList<>();
                // готовим список на удаление
                for (Animal animal : src) {
                    if (animal.isDead()) {
                        listRemove.add(animal);
                    }
                }
                // удаляем по списку
                for(Animal animal: listRemove){
                    src.remove(animal);
                }
                listRemove.clear();
            }
            // добавляем в список пришедших и родившихся животных
            addAnimals(hmNewcomers);    hmNewcomers.clear();
            addAnimals(hmNewborns);     hmNewborns.clear();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    // метод для добавление списков к hmAnimals
    private void addAnimals(HashMap<String, List<Animal>> src){
        lock.lock();
        try {
            for (String name : src.keySet()) {
                int maxCount = settings.getMaxCount(name);
                if (maxCount == 0) continue;
                List<Animal> list = src.get(name);
                List<Animal> dst = hmAnimals.get(name);
                if(dst==null || dst.size()==0) continue;
                int size= dst.size();
                if (size >= maxCount) {
                    // добавляем не больше чем максимальное количество на клетке
                    continue;
                }

                int cnt = maxCount - size;

                for (int i = 0; i < cnt && i < list.size(); i++) {
                    dst.add(list.get(i));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    // собираем статистику по количеству животных и растений
    public HashMap<String, Integer> getStat(){
        HashMap<String, Integer> res = new HashMap<>();
        for(String name: hmAnimals.keySet()){
            res.put(name, hmAnimals.get(name).size());
        }
        res.put("Растения", (int)Math.round(grass));
        return res;
    }

    // метод для добавления животного в список
    private void putToMap(HashMap<String, List<Animal>> map, Animal animal){
        if(animal==null) return;
        lock.lock();
        try {
            List<Animal> list = map.get(animal.getName());
            if (list == null) {
                list = new ArrayList<>();
                map.put(animal.getName(), list);
            }
            list.add(animal);
        }finally {
            lock.unlock();
        }
    }

    // добавляем животное в список пришедших, в начале следующего цикла он попадет в общий список
    public void putNewcomer(Animal animal){
        putToMap(hmNewcomers, animal);
    }

    // добавляем животное в список родившихся, в начале следующего цикла он попадет в общий список
    public void putNewborn(Animal animal){
        putToMap(hmNewborns, animal);
    }

    // переводим животное в другую клетку
    public void move(Animal animal, int toCell){
        if(island.move(animal, toCell)) {
            lock.lock();
            try{
                hmAnimals.get(animal.getName()).remove(animal);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    // возвращает номер клетки
    public int getPos() {
        return pos;
    }

    // пул потоков жизненного цикла животных
    private ExecutorService pool = Executors.newFixedThreadPool(settings.getCellPoolSize());

    // добавление в пул задач для жизненного зикла животных на этой клетке
    public void start() {
        lock.lock();
        try {
            for (String name : hmAnimals.keySet()) {
                for (Animal animal : hmAnimals.get(name)) {
                    pool.submit(new AnimalTask(animal, Cell.this));
                }
            }
        }finally {
            lock.unlock();
        }
    }

    // ожидание завершения выполнения пула
    public void await(int milliseconds) throws InterruptedException {
            pool.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);
    }

    // поедание растительности, возвращает сколько удалось съесть
    public double eatGrass(double value){
        double result = 0;
        lock.lock();
        try{
            if(value<0) value = 0;
            if(grass>value) {
                result = value;
                grass-=value;
            } else {
                result = grass;
                grass = 0;
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    // остановка пула
    public void shutdown(){
        pool.shutdown();
    }
    
}
