package islandJR;

import islandJR.ui.Editor;

public class Main {

    // запуск редактора настроек или эмул€ции острова
    public static void main(String[] args) {
	    if(args.length>0 && "edit".equalsIgnoreCase(args[0])){
	        new Editor().editor();
        } else {
	        new Island().start();
        }
    }
}
