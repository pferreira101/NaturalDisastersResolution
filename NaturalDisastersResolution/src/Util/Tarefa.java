import java.io.Serializable;

public class Tarefa implements Serializable {

    final static int ABASTECER = 1;
    final static int APAGAR = 2;
    final static int PREVENIR = 3;

    int taskId;
    int tipo;
    int fireId;
    Posicao posicao;
    int minTempo;

    Tarefa(int id, int op, Posicao p){
        this.taskId = id;
        this.tipo = op;
        this.posicao = p;
    }

    Tarefa(int id, int op, int fireId, Posicao p){
        this.taskId = id;
        this.fireId = fireId;
        this.tipo = op;
        this.posicao = p;
    }

    Tarefa(int id, int op, int fireId, Posicao p, int minTempo){
        this.taskId = id;
        this.fireId = fireId;
        this.tipo = op;
        this.posicao = p;
        this.minTempo = minTempo;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public String toString() {
        return tipo == ABASTECER? "abastecer tanques":"apagar fogo" + " em célula " + posicao.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Tarefa){
            Tarefa t = (Tarefa) o;
            return this.taskId == t.taskId;
        }
        else return false;
    }
}
