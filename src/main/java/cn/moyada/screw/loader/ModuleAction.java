package cn.moyada.screw.loader;

/**
 * @author xueyikang
 * @create 2018-04-27 14:12
 */
public interface ModuleAction {

    void load(String name, String url);

    void unload(String name);
}
