public class PostosDecision{
    int tempo;
    Posicao postoComb;
    Posicao postoAgua;
    int option;
    // 0 -> postoComb é o primeiro destino;
    // 1 -> postoAgua é o primeiro destino;
    // 2 -> só postoComb;
    // 3 -> só postoAgua;
    // 4 -> direto (não precisa de nada);

    PostosDecision(int tempo){
        this.tempo = tempo;
        this.option = 4;
    }

    PostosDecision(int tempo, Posicao posto, int option){
        if(option==2){
            this.postoComb = posto;
            this.option = 2;
        }
        if(option==3){
            this.postoAgua = posto;
            this.option = 3;
        }
        this.tempo = tempo;
    }

    PostosDecision(int tempo, Posicao postoComb, Posicao postoAgua, int option){
        this.tempo = tempo;
        this.postoComb = postoComb;
        this.postoAgua = postoAgua;
        this.option = option;
    }

}