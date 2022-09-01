package islandJR;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// класс для описания Острова, содержит массив клеток
public class Island {
    private Cell[] cells;   // клетки
    private Settings settings = Settings.getInstance();              // настройки
    private AtomicInteger counter = new AtomicInteger(0);   // счетчик шагоы эмуляции
    private int maxCounter = 10;                                     // останавливаем эмуляцию по достижении maxCounter шагов

    // конструктор
    public Island() {
        // загружаем карту
        Map map = Map.getInstance();
        if(!map.isReady()){
            System.err.println("Карта не готова");
            System.exit(-1);
        }
        cells = new Cell[map.getData().length];
        // создаем клетки острова
        for(int i=0; i< cells.length; i++){
            cells[i] = (map.isGround(i)) ? new Cell(i, this) : null;
        }
        maxCounter = settings.getIterations();
    }

    // метод для объединения статистики
    private void addStat(HashMap<String, Integer> dst, HashMap<String, Integer> src){
        for(String key: src.keySet()){
            if(dst.containsKey(key)) dst.put(key, dst.get(key)+src.get(key));
            else dst.put(key, src.get(key));
        }
    }

    // обновление данных о клетках и вывод статистики
    private void refreshStat(){
        HashMap<String, Integer> res = new HashMap<>();

        for(Cell cell: cells){
            if(cell==null) continue;
            cell.initNewLoop();
            addStat(res,cell.getStat());
        }
        // вывод статистики
        for(String key: res.keySet()){
            System.out.printf("%-10s %5d\n",key,res.get(key));
        }
    }

    // запуск шага итерации
    private void initLoop() {

        // обновляем счетчик шагов
        counter.incrementAndGet();

        System.out.println("\nШаг: "+counter.get());
        // обновляем и выводим статистику
        refreshStat();

        // запускаем клетки
        for(Cell cell: cells){
            if(cell==null) continue;
            cell.start();
        }
    }

    // пул для запуска шагов по расписанию
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    // периодическая задача для запуска задач для кадого шага
    private Runnable globalTask = new Runnable() {
        @Override
        public void run() {
            // шишциализация и запуск шага эмуляции
            initLoop();

            // если дошли до конечного шага, то останавливаем задачи
            if(counter.get()>=maxCounter) {
                // останавливаем расписание
                service.shutdown();
                for(Cell cell: cells){
                    if(cell==null) continue;
                    // останавливаем задачи клеток
                    cell.shutdown();
                }
                System.out.println("shutdown");
                // ожидаем завершения работы путов клеток
                for(Cell cell: cells){
                    if(cell==null) continue;
                    try {
                        cell.await(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // выводим итоговую статистику
                System.out.println("\nРезультат:");
                refreshStat();
            }
        }
    };

    // запускаем эмуляцию
    public void start(){
        service.scheduleAtFixedRate(globalTask, 1, settings.getDuration(), TimeUnit.MILLISECONDS);
    }

    // перемещение животного в другую клетку
    public boolean move(Animal animal, int toCell){
        if( cells[toCell]==null) return false;
        cells[toCell].putNewcomer(animal);
        return true;
    }

    // запуск программы
    public static void main(String[] args) {
        Island island = new Island();
        island.start();
    }
}
