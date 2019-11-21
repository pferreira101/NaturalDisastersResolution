import java.io.Serializable;

public class Tarefa implements Serializable {

    final static int ABASTECER = 1;
    final static int APAGAR = 2;

    int id;
    int tipo;
    Posicao posicao;

    Tarefa(int id, int op, Posicao p){
        this.id = id;
        this.tipo = op;
        this.posicao = p;
    }

    @Override
    public String toString() {
        return tipo == ABASTECER? "abastecer tanques":"apagar fogo" + " em célula " + posicao.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Tarefa){
            Tarefa t = (Tarefa) o;
            return this.id == t.id;
        }
        else return false;
    }
}
