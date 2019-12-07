public class SimulationConfig {

    public static int NUM_MAX_DRONES = 2;
    public static int NUM_MAX_CAMIOES = 2;
    public static int NUM_MAX_AERONAVES = 2;
    public static int TAMANHO_MAPA = 10;
    public static int NUM_POSTOS_AGUA = 1;
    public static int NUM_POSTOS_COMB= 5;
    public static int NUM_HABITACOES= 10;
    public static int NUM_PONTOS_FLORESTAIS = 30;

    static void changeNumVehicles(int nDrones, int nCamioes, int nAeronaves){
        NUM_MAX_DRONES = nDrones;
        NUM_MAX_AERONAVES = nAeronaves;
        NUM_MAX_CAMIOES = nCamioes;
    }

    static void changeMapSpecs(int size, int nPostosAgua, int nPostosComb, int nHabitacoes, int nFloresta){
        TAMANHO_MAPA = size;
        NUM_POSTOS_AGUA = nPostosAgua;
        NUM_POSTOS_COMB = nPostosComb;
        NUM_HABITACOES = nHabitacoes;
        NUM_PONTOS_FLORESTAIS = nFloresta;
    }
}
