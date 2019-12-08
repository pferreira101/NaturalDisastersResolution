import jade.core.Agent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Histograma3Barras extends JFrame {

    Map<String,List<Tarefa>> tarefasRealizadas;

    public Histograma3Barras(Map<String, List<Tarefa>> ts){
        this.tarefasRealizadas = ts;
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
        setTitle("Quantidade de Tarefas por Tipo de Agente");

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    }

    private JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart barChart = ChartFactory.createBarChart(
                "Quantidade de Tarefas por Tipo de Agente",
                "Tarefa",
                "Quantidade",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);


        return barChart;
    }


    private CategoryDataset createDataset( ) {
        final String apagar = "APAGAR";
        final String abastecer_comb = "ABASTECER_COMB";
        final String abastecer_agua = "ABASTECER_AGUA";
        final String prevenir = "PREVENIR";
        final String aeronave = "AERONAVE";
        final String drone = "DRONE";
        final String camiao = " CAMIAO";

        final DefaultCategoryDataset dataset =
                new DefaultCategoryDataset( );


        dataset.addValue( contagemPorAgente("Plane").get(1), abastecer_comb , aeronave );
        dataset.addValue( contagemPorAgente("Drone").get(1), abastecer_comb , drone );
        dataset.addValue(  contagemPorAgente("Firetruck").get(1), abastecer_comb , camiao );

        dataset.addValue( contagemPorAgente("Plane").get(2) , apagar , aeronave );
        dataset.addValue( contagemPorAgente("Drone").get(2), apagar , drone );
        dataset.addValue( contagemPorAgente("Firetruck").get(2) , apagar , camiao );

        dataset.addValue( contagemPorAgente("Plane").get(3) , prevenir , aeronave );
        dataset.addValue( contagemPorAgente("Drone").get(3) , prevenir , drone );
        dataset.addValue( contagemPorAgente("Firetruck").get(3), prevenir , camiao );


        dataset.addValue( contagemPorAgente("Plane").get(4), abastecer_agua , aeronave );
        dataset.addValue( contagemPorAgente("Drone").get(4), abastecer_agua , drone );
        dataset.addValue( contagemPorAgente("Firetruck").get(4), abastecer_agua , camiao );



        return dataset;
    }

    public Map<Integer,Integer> contagemPorAgente (String s) {
            Map<Integer,Integer> contas = new HashMap<Integer,Integer>();
            int comb=0;
            int apagar=0;
            int prevenir =0;
            int agua=0;
            if (this.tarefasRealizadas!= null) {
            for (Map.Entry<String,List<Tarefa>> entry : this.tarefasRealizadas.entrySet()) {
                if ( entry.getKey().equals(s)){
                    for ( Tarefa t : entry.getValue()) {
                        if (t.tipo == 1) comb++;
                        else if (t.tipo == 2) apagar++;
                        else if (t.tipo == 3) prevenir++;
                        else if (t.tipo == 4) agua++;
                    }
                }
            }
            }
            contas.put(1,comb);
            contas.put(2,apagar);
            contas.put(3,prevenir);
            contas.put(4,agua);
            return contas;
    }

}