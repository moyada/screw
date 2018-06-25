package cn.moyada.screw.model;

import java.sql.Timestamp;

/**
 * Created by xueyikang on 2017/12/11.
 */
public class BaseDO {
    
    private Long id;

    private Timestamp dateCreate;

    private Timestamp dateUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Timestamp dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Timestamp getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Timestamp dateUpdate) {
        this.dateUpdate = dateUpdate;
    }
}
