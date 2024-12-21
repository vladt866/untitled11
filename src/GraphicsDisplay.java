import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.*;

import static java.lang.Math.abs;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {

    // Список координат точек для построения графика
    private Double[][] graphicsData;



    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showNullMarkers = false;

    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    // Используемый масштаб отображения
    private double scale;

    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;

    private DecimalFormat formatter =
            (DecimalFormat) NumberFormat.getInstance();

    // Различные шрифты отображения надписей
    private Font axisFont;

    public GraphicsDisplay() {
        // Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
        // Сконструировать необходимые объекты, используемые в рисовании
        // Перо для рисования графика
//        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
//                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[] {40, 20, 20, 40, 10}, 0.0f);
        // Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    // Данный метод вызывается из обработчика элемента меню "Открыть файл с графиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
        // Сохранение границ по умолчанию

        repaint();
    }


    // Методы-модификаторы для изменения параметров отображения графика
    // Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    public void setShowNullMarkers(boolean showNullMarkers) {
        this.showNullMarkers = showNullMarkers;
        repaint();
    }


    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (graphicsData==null || graphicsData.length==0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX) {

            double yIncrement = (getSize().getHeight() / scale - (maxY -
                    minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            // Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth() / scale - (maxX -
                    minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);

        paintGraphics(canvas);

        if (showMarkers) paintMarkers(canvas);
        if (showNullMarkers) paintNullMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    // Отрисовка графика по прочитанным координатам
    protected void paintGraphics(Graphics2D canvas) {
        // Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
        // Выбрать цвет линии
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            // Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i > 0) {
                // Не первая итерация цикла - вести линию в точку point
                graphics.lineTo(point.getX(), point.getY());
            } else {
                // Первая итерация цикла - установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        // Отобразить график
        canvas.draw(graphics);
    }


    protected void paintMarkers(Graphics2D canvas) {

        canvas.setStroke(markerStroke);


        for (Double[] point : graphicsData) {

            double pd = (double)point[1];
            int p = (int)pd;
            if(p % 2 == 0){
                canvas.setColor(Color.BLACK);
            }
            else{
                canvas.setColor(Color.RED);
            }
            // Инициализировать эллипс как объект для представления маркера
            GeneralPath marker = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0],
                    point[1]);

            marker.moveTo(center.getX() + 5.5, center.getY());
            marker.lineTo(center.getX() - 5.5, center.getY());
            marker.moveTo(center.getX(), center.getY() + 5.5);
            marker.lineTo(center.getX(), center.getY() - 5.5);
            marker.moveTo(center.getX() + 5.5, center.getY()+4);
            marker.lineTo(center.getX() + 5.5, center.getY()-4);
            marker.moveTo(center.getX() - 5.5, center.getY()+4);
            marker.lineTo(center.getX() - 5.5, center.getY()-4);
            marker.moveTo(center.getX() + 4, center.getY() + 5.5);
            marker.lineTo(center.getX() - 4, center.getY() + 5.5);
            marker.moveTo(center.getX() + 4, center.getY() - 5.5);
            marker.lineTo(center.getX() - 4, center.getY() - 5.5);
//

            canvas.draw(marker); // Начертить контур маркера
//            canvas.fill(marker); // Залить внутреннюю область маркера
        }
    }


    protected void paintNullMarkers(Graphics2D canvas) {

        canvas.setStroke(markerStroke);


        canvas.setColor(Color.BLUE);

        for (int i = 1; i < graphicsData.length; i++) {
            if(Math.signum(graphicsData[i-1][1]) != Math.signum(graphicsData[i][1])) {
                // Преобразовать значения (x,y) в точку на экране point
                Double mod_y1 = abs(graphicsData[i-1][1]);
                Double mod_y2 = abs(graphicsData[i][1]);
                Double x1 = graphicsData[i-1][0];
                Double x2 = graphicsData[i][0];
                Double x0 = (mod_y1*x2+mod_y2*x1)/(mod_y2+mod_y1);
                Point2D.Double center = xyToPoint(x0,
                        0);


                GeneralPath marker = new GeneralPath();


                marker.moveTo(center.getX() + 5.5, center.getY());
                marker.lineTo(center.getX() - 5.5, center.getY());
                marker.moveTo(center.getX(), center.getY() + 5.5);
                marker.lineTo(center.getX(), center.getY() - 5.5);
                marker.moveTo(center.getX() + 5.5, center.getY() + 4);
                marker.lineTo(center.getX() + 5.5, center.getY() - 4);
                marker.moveTo(center.getX() - 5.5, center.getY() + 4);
                marker.lineTo(center.getX() - 5.5, center.getY() - 4);
                marker.moveTo(center.getX() + 4, center.getY() + 5.5);
                marker.lineTo(center.getX() - 4, center.getY() + 5.5);
                marker.moveTo(center.getX() + 4, center.getY() - 5.5);
                marker.lineTo(center.getX() - 4, center.getY() - 5.5);
//

                canvas.draw(marker); // Начертить контур маркера
//            canvas.fill(marker); // Залить внутреннюю область маркера

            }

        }
    }

    
    protected void paintAxis(Graphics2D canvas) {
        // Установить особое начертание для осей
        canvas.setStroke(axisStroke);
        // Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0) {


            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                    xyToPoint(0, minY)));
            // Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести левый "скат" стрелки в точку с относительными координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5,
                    arrow.getCurrentPoint().getY() + 20);
            // Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10,
                    arrow.getCurrentPoint().getY());
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));
        }
        // Определить, должна ли быть видна ось X на графике
        if (minY <= 0.0 && maxY >= 0.0) {

            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                    xyToPoint(maxX, 0)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() - 20,
                    arrow.getCurrentPoint().getY() - 5);

            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY() + 10);

            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            // Вывести надпись в точке с вычисленными координатами

            canvas.drawString("x", (float) (labelPos.getX() -
                    bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

        }
    }


    protected Point2D.Double xyToPoint(double x, double y) {
        // Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
        // Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }


    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {

        Point2D.Double dest = new Point2D.Double();

        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }


}