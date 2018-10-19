package cn.moyada.screw.exception;

/**
 * @author xueyikang
 * @create 2018-04-27 01:27
 */
public class ClassAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -1436809736502315016L;

    public ClassAlreadyExistsException(String className) {
        super(className + " is exists in system classloader.");
    }
}
