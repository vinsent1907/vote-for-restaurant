package ru.diplom.bootjava.util;

import ru.diplom.bootjava.model.Vote;
import ru.diplom.bootjava.to.VoteTo;

public class VoteUtil {
    public static VoteTo asTo(Vote v) {
        return new VoteTo(v.getDates(), v.getRestaurant().getId(), v.getUser().getId());
    }
}
