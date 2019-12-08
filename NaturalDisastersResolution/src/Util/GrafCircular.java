import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafCircular extends JFrame {

    Map<String,List<Tarefa>> tarefasRealizadas;

    public GrafCircular( Map<String,List<Tarefa>> ts ) {
        this.tarefasRealizadas =ts;
        initUI();
    }

    private  PieDataset createDataset( ) {
        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue( "Abastecimento C" , contagemTotal().get(1));
        dataset.setValue( "Apagar" , contagemTotal().get(2) );
        dataset.setValue( "Prevenir" , contagemTotal().get(3) );
        dataset.setValue( "Abastecimento A" , contagemTotal().get(4) );
        return dataset;
    }

    private static JFreeChart createChart( PieDataset dataset ) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Total de Tarefas",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        return chart;
    }

    public  JPanel initUI( ) {
        JFreeChart chart = createChart(createDataset( ) );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        chartPanel.setPreferredSize(new Dimension(550,280));
        chartPanel.setMaximumDrawWidth(550);
        chartPanel.setMaximumDrawHeight(280);
        add(chartPanel);

        pack();

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        return new ChartPanel( chart );
    }

    public Map<Integer,Integer> contagemTotal () {
        Map<Integer,Integer> contas = new HashMap<Integer,Integer>();
        int comb=0;
        int apagar=0;
        int prevenir =0;
        int agua=0;
        if (this.tarefasRealizadas!= null) {
            for (Map.Entry<String,List<Tarefa>> entry : this.tarefasRealizadas.entrySet()) {
                    for ( Tarefa t : entry.getValue()) {
                        if (t.tipo == 1) comb++;
                        else if (t.tipo == 2) apagar++;
                        else if (t.tipo == 3) prevenir++;
                        else if (t.tipo == 4) agua++;
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