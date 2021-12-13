package swing;

import java.awt.*;

//Swing은 단일 Thread 기반이므로 Queue를 통해 순서대로 실행됨. 이 때 긴 작업이 걸리면 UI가 멈추기 때문에 해당 작업을 별도 Thread로
//분리해야함. 그 역할을 하는 Class
public class WorkerThread{

    public static void main(String[] args) {

//      EventQueue : Event 처리 Class. Swing의 모든 Event는 이곳에 등록되어 처리된다.
//      invoke : CallBack, Later : 비동기 -> Event가 비면 처리 실행
        EventQueue.invokeLater(() -> {
            SwingProgressBar progressBar = new SwingProgressBar();
            progressBar.setVisible(true);
        });
    }
}
