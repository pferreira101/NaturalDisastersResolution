import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Histograma3Barras extends ApplicationFrame {

    public Histograma3Barras() {
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
    }

    private CategoryDataset createDataset( ) {
        final String apagar = "APAGAR";
        final String abastecer = "ABASTECER";
        final String prevenir = "PREVENIR";
        final String aeronave = "AERONAVE";
        final String drone = "DRONE";
        final String camiao = " CAMIAO";

        final DefaultCategoryDataset dataset =
                new DefaultCategoryDataset( );

        dataset.addValue( 1.0 , apagar , aeronave );
        dataset.addValue( 3.0 , apagar , drone );
        dataset.addValue( 5.0 , apagar , camiao );

        dataset.addValue( 5.0 , abastecer , aeronave );
        dataset.addValue( 6.0 , abastecer , drone );
        dataset.addValue( 10.0 , abastecer , camiao );

        dataset.addValue( 4.0 , prevenir , aeronave );
        dataset.addValue( 2.0 , prevenir , drone );
        dataset.addValue( 3.0 , prevenir , camiao );


        return dataset;
    }

}