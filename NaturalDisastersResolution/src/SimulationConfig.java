class SimulationConfig {

    static int SIMULATION_NUMBER = 0;
    static int NUM_MAX_DRONES = 2;
    static int NUM_MAX_CAMIOES = 2;
    static int NUM_MAX_AERONAVES = 2;
    static int TAMANHO_MAPA = 6;
    static int NUM_POSTOS_AGUA = 1;
    static int NUM_POSTOS_COMB= 1;
    static int NUM_HABITACOES= 1;
    static int NUM_PONTOS_FLORESTAIS = 1;
    static int FREQ_CRIACAO_INCENDIO = 6000;
    static int FREQ_EXPANSAO_INCENDIO = 8000;
    static int TEMPO_QUEIMAR_CELULA = 0;

    static void incSimulationNumber(){
        SIMULATION_NUMBER++;
    }

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
