package ru.diplom.bootjava.util;

import lombok.experimental.UtilityClass;
import ru.diplom.bootjava.error.IllegalRequestDataException;
import ru.diplom.bootjava.error.NotFoundException;
import ru.diplom.bootjava.error.TimeException;
import ru.diplom.bootjava.model.BaseEntity;

import java.time.LocalTime;
import java.util.Objects;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(BaseEntity entity) {
        if (!entity.isNew()) {
            throw new IllegalRequestDataException(entity.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(BaseEntity entity, int id) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.id() != id) {
            throw new IllegalRequestDataException(entity.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkingPossibilityVoting(LocalTime time) {
        if (LocalTime.now().isAfter(time))
            throw new TimeException("You can change your voice up to "
                    + time +  " o'clock." + " Today the voting result cannot be changed");
    }

    public static int checkNull(Integer id) {
        if (Objects.isNull(id))
            throw new NotFoundException("id must be not null");
        else
            return id;
    }

}