public class Aeronave extends AgenteParticipativo {

    static int capacidadeMaxAgua = 15 ;
    static int capacidadeMaxCombustivel = 20;
    static int velocidade = 3;


    // allow tunable parameters
    static void changeParameters(int maxAgua, int maxCombustivel, int velocidade){
        Aeronave.capacidadeMaxAgua = maxAgua;
        Aeronave.capacidadeMaxCombustivel = maxCombustivel;
        Aeronave.velocidade = velocidade;
    }

    protected void setup(){
        super.initStatus(Aeronave.capacidadeMaxAgua, Aeronave.capacidadeMaxCombustivel, Aeronave.velocidade);
        super.setup();
    }


}