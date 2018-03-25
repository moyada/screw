package cn.moyada.screw.io;

/**
 * @author xueyikang
 * @create 2018-03-24 22:25
 */
public abstract class AbstractFileReader extends AbstractFile {

    protected int readPos;
    protected int limit;

    protected boolean remain() {
        return readPos < limit;
    }

    public abstract boolean hasNext();

    public abstract String nextLine();
}
