package islandJR;

import islandJR.animals.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

// Базовый класс всех животных
abstract public class Animal {
    private String name;            // название животного
    private boolean dead = false;   // отметка если животное умерло
    //private boolean moved = false;  // отметка что животное перенесено в другую клетку
    private ReentrantLock lock = new ReentrantLock();   // Lock - для контроля доступа к животному
    private double satiety;         // сытость
    //Info
    private Info info;              // информация о животном

    // блокируем доступ к животному
    public void lock(){
        lock.lock();
    }

    // пытаемся получить доступ если он не заблокирован
    public boolean tryLock(){
        boolean res = lock.tryLock();
        return res;
    }

    // разблокируем доступ к животному
    public void unlock(){
        lock.unlock();
    }

    public Animal(String name) {
        this.name = name;
        info = Settings.getInstance().getInfo(name);
        satiety = info.getMaxFood();
    }

    // название животного
    public String getName() {
        return name;
    }

    // вес животного
    public double getWeight(){
        return info.getWeight();
    }

    // скорость передвижения животного
    public int getSpeed(){
        return info.getSpeed();
    }

    // максимальный вес пищи
    public double getMaxFood(){
        return info.getMaxFood();
    }

    // сытость
    public double getSatiety() {
        return satiety;
    }

    // каждый ход сытость животного уменьшается на 10% от максимального весе пищи.
    // если сытость снижается до 0 то животное гибнет от голода
    public boolean hungry(){
        double value = getMaxFood() * 0.1;
        setSatiety(getSatiety()-value);
        boolean liveFlag = (getSatiety()>0);
        return liveFlag; // false - означает что животное умерло от голода
    }

    // задать значение сытости
    public void setSatiety(double satiety) {
        this.satiety = satiety;
    }

    // если животное мертво
    public boolean isDead() {
        return dead;
    }

    // установить флаг что животное мертво
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    // herbivore predator
    // методы жизненного цикла

    // выбор действия на новом шаге: двигаться, размножаться или питаться
    public void live(Cell cell){
        // выбор действия
        int probMove = info.getProbMove();
        int probEat, probPair;
        if(probMove<0 || probMove>60) {
            probMove = 10;
        }
        probPair = (int)((100 - probMove) * 0.2);
        if(probPair<10) probPair=10;

        probEat = 100 - probMove - probPair;

        int selector = ThreadLocalRandom.current().nextInt(100);
        if (selector < probMove) {
            move(cell);
        } else if (selector < probMove+probPair) {
            pair(cell);
        } else {
            eat(cell);
        }

    }

    // алгоритм питания разный у разных животных
    abstract public void eat(Cell cell);

    // размножение животных
    public void pair(Cell cell){
        // пытаемся найти пару
       Animal p = null;
       try {
           for (int i = 0; i < 10 && p==null; i++) {
               p = cell.getRandom(getName());
           }
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           if(p!=null) p.unlock();
       }

       // пара не нашлась
       if(p==null) return;

       // количество приплода
       int cnt = ThreadLocalRandom.current().nextInt(info.getChilds())+1;
       for(int i=0; i<cnt; i++){
           cell.putNewborn(create(getName()));
       }
    }

    // передвижение животного на соседние клетки
    public void move(Cell cell){
        int len = getSpeed();
        int new_pos = Map.getInstance().path(cell.getPos(), len);
        cell.move(this, new_pos);
    }

    // создание животных
    public static Animal create(String name){
        if("Лошадь".equals(name)) return new Horse();
        if("Олень".equals(name)) return new Deer();
        if("Кролик".equals(name)) return new Rabbit();
        if("Мышь".equals(name)) return new Mouse();
        if("Коза".equals(name)) return new Goat();
        if("Овца".equals(name)) return new Sheep();
        if("Кабан".equals(name)) return new Boar();
        if("Буйвол".equals(name)) return new Buffalo();
        if("Утка".equals(name)) return new Duck();
        if("Гусеница".equals(name)) return new Caterpillar();
        if("Волк".equals(name)) return new Wolf();
        if("Удав".equals(name)) return new Boa();
        if("Лиса".equals(name)) return new Fox();
        if("Медведь".equals(name)) return new Bear();
        if("Орел".equals(name)) return new Eagle();
        return null;
    }
}
