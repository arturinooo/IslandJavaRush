package islandJR;

import islandJR.ui.Editor;

public class Main {

    // ������ ��������� �������� ��� �������� �������
    public static void main(String[] args) {
	    if(args.length>0 && "edit".equalsIgnoreCase(args[0])){
	        new Editor().editor();
        } else {
	        new Island().start();
        }
    }
}
