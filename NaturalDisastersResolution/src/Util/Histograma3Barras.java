import jade.core.Agent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.HashMap;
import java.util.Map;

public class Histograma3Barras extends ApplicationFrame {

    Map<AgentStatus,Tarefa> tarefasRealizadas;

    public Histograma3Barras(Map<AgentStatus,Tarefa> ts) {

        super("Quantidade de Tarefas por Tipo de Agente");
        JFreeChart barChart = ChartFactory.createBarChart(
                "Quantidade de Tarefas por Tipo de Agente",
                "Tarefa",
                "Quantidade",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel( barChart );
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
        this.tarefasRealizadas = ts;
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


        dataset.addValue( contagem("Plane").get(1), abastecer_comb , aeronave );
        dataset.addValue( contagem("Drone").get(1), abastecer_comb , drone );
        dataset.addValue(  contagem("Firetruck").get(1), abastecer_comb , camiao );

        dataset.addValue( contagem("Plane").get(2) , apagar , aeronave );
        dataset.addValue( contagem("Drone").get(2), apagar , drone );
        dataset.addValue( contagem("Firetruck").get(2) , apagar , camiao );

        dataset.addValue( contagem("Plane").get(3) , prevenir , aeronave );
        dataset.addValue( contagem("Drone").get(3) , prevenir , drone );
        dataset.addValue( contagem("Firetruck").get(3), prevenir , camiao );


        dataset.addValue( contagem("Plane").get(4), abastecer_agua , aeronave );
        dataset.addValue( contagem("Drone").get(4), abastecer_agua , drone );
        dataset.addValue( contagem("Firetruck").get(4), abastecer_agua , camiao );



        return dataset;
    }

    public Map<Integer,Integer> contagem (String s) {
            Map<Integer,Integer> contas = new HashMap<Integer,Integer>();
            int comb=0;
            int apagar=0;
            int prevenir =0;
            int agua=0;
            if (this.tarefasRealizadas!= null) {
            for (Map.Entry<AgentStatus,Tarefa> entry : this.tarefasRealizadas.entrySet()) {
                if ( entry.getKey().equals(s)){
                    if (entry.getValue().equals(1)) comb++;
                    else if (entry.getValue().equals(2)) apagar++;
                    else if (entry.getValue().equals(3)) prevenir++;
                    else if (entry.getValue().equals(4)) agua++;
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