package ru.diplom.bootjava.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Access(AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseTo implements Serializable {
    protected Integer id;

    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }
}
