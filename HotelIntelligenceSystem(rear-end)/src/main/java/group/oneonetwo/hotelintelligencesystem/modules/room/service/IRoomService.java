package group.oneonetwo.hotelintelligencesystem.modules.room.service;

import group.oneonetwo.hotelintelligencesystem.modules.room.model.po.RoomPO;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.RoomVO;

public interface IRoomService {
    RoomPO selectOneById(String id);
    RoomVO add(RoomVO roomVO);
    RoomVO selectOneByIdReturnVO(String id);
    RoomPO save(RoomVO roomVO);
    Integer deleteById(String id);
    RoomVO saveone(RoomVO roomVO);
}
