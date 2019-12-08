public class Camiao extends AgenteParticipativo {

    static int capacidadeMaxAgua = 10;
    static int capacidadeMaxCombustivel = 10;
    static int velocidade = 2;

    // allow tunable parameters
    static void changeParameters(int maxAgua, int maxCombustivel, int velocidade){
        Camiao.capacidadeMaxAgua = maxAgua;
        Camiao.capacidadeMaxCombustivel = maxCombustivel;
        Camiao.velocidade = velocidade;
    }

    protected void setup(){
        super.initStatus(Camiao.capacidadeMaxAgua, Camiao.capacidadeMaxCombustivel, Camiao.velocidade);
        super.setup();
    }

}
