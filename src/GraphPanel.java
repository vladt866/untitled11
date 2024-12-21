import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<Point> points; // Список точек для графика
    private double maxX; // Максимальное значение по X
    private double minX; // Минимальное значение по X
    private double maxY; // Максимальное значение по Y
    private double minY; // Минимальное значение по Y
    private boolean showGrid = false; // Флаг отображения сетки


    public GraphPanel() {
        points = new ArrayList<>(); // Инициализация списка точек
        setBackground(Color.WHITE); // Установка белого фона
    }

    // Метод для добавления точки на график
    public void addPoint(double x, double y) {
        points.add(new Point(x, y));
        // Обновление минимальных/максимальных значений
        if (points.size() == 1) {
            maxX = x;
            minX = x;
            maxY = y;
            minY = y;
        } else {
            maxX = Math.max(maxX, x);
            minX = Math.min(minX, x);
            maxY = Math.max(maxY, y);
            minY = Math.min(minY, y);
        }
        repaint(); // Перерисовка панели
    }


    // Метод для установки отображения сетки
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint(); // Перерисовка панели
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // Приведение к Graphics2D для более широких возможностей

        if (showGrid) {
            drawGrid(g2d); // Рисование сетки, если флаг установлен
        }

        if (!points.isEmpty()) {
            drawGraph(g2d); // Рисование графика, если есть точки
        }
    }

    // Метод для рисования сетки
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY); // Цвет сетки - светло-серый
        double gridX = (maxX - minX) / 10; // Шаг сетки по X
        double gridY = (maxY - minY) / 10; // Шаг сетки по Y

        // Вертикальные линии сетки
        for (double x = minX; x <= maxX + gridX / 2; x += gridX) {  // Добавлено gridX/2 для отображения последней линии
            int screenX = scaleX(x); // Масштабирование координаты X
            g2d.drawLine(screenX, 0, screenX, getHeight());
        }
        // Горизонтальные линии сетки
        for (double y = minY; y <= maxY + gridY / 2; y += gridY) { // Добавлено gridY/2 для отображения последней линии
            int screenY = scaleY(y); // Масштабирование координаты Y
            g2d.drawLine(0, screenY, getWidth(), screenY);
        }
    }


    // Метод для рисования графика
    private void drawGraph(Graphics2D g2d) {
        float[] dashPattern = {10, 5}; // Длина штриха и пробела
        g2d.setColor(Color.BLUE); // Цвет графика - синий
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0)); // Пунктирная линия

        // Рисование линий между точками
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g2d.drawLine(scaleX(p1.x), scaleY(p1.y), scaleX(p2.x), scaleY(p2.y));
        }

        // Рисование маркеров (круги 11x11)
        for (Point p : points) {
            if (isAscendingDigits(p.y)) {
                g2d.setColor(Color.RED); // Выделение точек с возрастающими цифрами красным
            } else {
                g2d.setColor(Color.BLUE); // Цвет маркера - синий
            }

            int x = scaleX(p.x) - 5; // Координата X маркера
            int y = scaleY(p.y) - 5; // Координата Y маркера
            g2d.fill(new Ellipse2D.Double(x, y, 11, 11)); // Рисование маркера как круга
        }
    }


    // Масштабирование координаты X
    private int scaleX(double x) {
        return (int) ((x - minX) / (maxX - minX) * getWidth());
    }

    // Масштабирование координаты Y
    private int scaleY(double y) {
        return (int) (getHeight() - (y - minY) / (maxY - minY) * getHeight());
    }


    // Проверка на возрастание цифр в числе
    private boolean isAscendingDigits(double y) {
        String yStr = String.valueOf(y).replaceAll("[^0-9]", "");  // Удаление нецифровых символов
        if (yStr.length() < 2) return true; // Одна цифра или пустая строка - всегда возрастает
        for (int i = 0; i < yStr.length() - 1; i++) {
            if (yStr.charAt(i) >= yStr.charAt(i + 1)) {
                return false; // Не возрастает
            }
        }
        return true; // Возрастает
    }


    // Внутренний класс для представления точки
    private static class Point {
        double x;
        double y;

        // Конструктор класса Point
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("График"); // Создание окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие окна при нажатии на крестик
        frame.setSize(1000, 800); // Размер окна

        GraphPanel graphPanel = new GraphPanel(); // Создание панели для графика
        frame.add(graphPanel); // Добавление панели в окно


        // Пример использования:
        graphPanel.addPoint(1, 123);
        graphPanel.addPoint(2, 246);
        graphPanel.addPoint(2, 381);
        graphPanel.addPoint(4, 56);
        graphPanel.addPoint(5, 43);

        // Пример отображения сетки:
        graphPanel.setShowGrid(true);


        frame.setVisible(true); // Отображение окна
    }
}