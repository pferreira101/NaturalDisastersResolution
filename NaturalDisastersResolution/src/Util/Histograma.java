import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.*;

public class Histograma extends JFrame {

    Mapa mapa;

    public Histograma(Mapa m) {
        this.mapa=m;
        initUI();
    }

    private void initUI() {

        CategoryDataset dataset = createDataset();

        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        chartPanel.setPreferredSize(new Dimension(550,280));
        chartPanel.setMaximumDrawWidth(550);
        chartPanel.setMaximumDrawHeight(280);
        add(chartPanel);

        pack();
        setTitle("Células Ardidas por Tipo");

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private CategoryDataset createDataset() {

        var dataset = new DefaultCategoryDataset();
        dataset.setValue(contaFlorestaArdidas(), "Células Ardidas", "Ponto Florestal");
        dataset.setValue(contaHabArdidas(), "Células Ardidas", "Habitação");
        dataset.setValue(contaPostosCombArdidas(), "Células Ardidas", "Postos Combustíveis");
        dataset.setValue(contaOutrasArdidas(),"Células Ardidas", "Vazias");
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart barChart = ChartFactory.createBarChart(
                "Células Ardidas por Tipo",
                "",
                "Células Ardidas",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        return barChart;
    }

    public int contaOutrasArdidas () {
        int cont = 0;
        if (this.mapa.areaArdida  !=  null) {
            for (Posicao p : this.mapa.areaArdida) {
                if (this.mapa.hab(p) == false && this.mapa.arvore(p) == false && this.mapa.postoC(p) == false) cont++;
            }
            return cont;
        }
        return 0;
    }

    public int contaHabArdidas () {
        int cont = 0;
        if (this.mapa.areaArdida  !=  null) {
            for (Posicao p : this.mapa.areaArdida) {
                if (this.mapa.hab(p)) cont++;
            }
            return cont;
        }
        return 0;
    }

    public int contaFlorestaArdidas () {
        int cont = 0;
        if ( this.mapa.areaArdida  != null) {
            for (Posicao p : this.mapa.areaArdida) {
                if (this.mapa.arvore(p)) cont++;
            }
            return cont;
        }
        return 0;
    }

    public int contaPostosCombArdidas () {
        int cont = 0;
        if ( this.mapa.areaArdida.isEmpty() == false) {
            for (Posicao p : this.mapa.areaArdida) {
                if (this.mapa.postoC(p)) cont++;
            }
        }
        return cont;
    }

}