import java.io.Serializable;

public class Tarefa implements Serializable {

    final static int ABASTECER = 1;
    final static int APAGAR = 2;

    int taskId;
    int tipo;
    int fireId;
    Posicao posicao;
    int tempoDeslocacao;

    Tarefa(int id, int op, Posicao p){
        this.taskId = id;
        this.tipo = op;
        this.posicao = p;
    }

    Tarefa(int id, int op, int fireId, Posicao p, int t){
        this.taskId = id;
        this.fireId = fireId;
        this.tipo = op;
        this.posicao = p;
        this.tempoDeslocacao = t;
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
