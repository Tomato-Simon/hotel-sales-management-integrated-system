package group.oneonetwo.hotelintelligencesystem.modules.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import group.oneonetwo.hotelintelligencesystem.components.security.utils.AuthUtils;
import group.oneonetwo.hotelintelligencesystem.components.websocket.WebSocketServer;
import group.oneonetwo.hotelintelligencesystem.exception.CommonException;
import group.oneonetwo.hotelintelligencesystem.exception.SavaException;
import group.oneonetwo.hotelintelligencesystem.modules.discounts.service.IDiscountsService;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.po.OrderPO;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.vo.OrderVO;
import group.oneonetwo.hotelintelligencesystem.modules.order.service.IOrderService;
import group.oneonetwo.hotelintelligencesystem.modules.room.dao.RoomMapper;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.po.RoomPO;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.CheckInVO;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.DetailVO;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.RoomVO;
import group.oneonetwo.hotelintelligencesystem.modules.room.service.IRoomService;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.model.vo.RoomTypeVO;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.service.IRoomTypeServeice;
import group.oneonetwo.hotelintelligencesystem.modules.sys_logs.service.impl.LogsService;
import group.oneonetwo.hotelintelligencesystem.tools.ConvertUtils;
import group.oneonetwo.hotelintelligencesystem.tools.TimeUtils;
import group.oneonetwo.hotelintelligencesystem.tools.WStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class RoomServiceImpl implements IRoomService {

    Logger logger = LoggerFactory.getLogger(Object.class);

    @Autowired
    RoomMapper roomMapper;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    IOrderService orderService;

    @Autowired
    IRoomTypeServeice roomTypeServeice;

    @Autowired
    IDiscountsService discountsService;

    @Autowired
    LogsService logsService;

    @Override
    public RoomVO add(RoomVO roomVO) {
        if (roomVO == null) {
            throw new SavaException("??????????????????,??????????????????");
        }
        RoomPO roomPO = new RoomPO();
        BeanUtils.copyProperties(roomVO, roomPO);
        int insert = roomMapper.insert(roomPO);
        if (insert > 0) {
            return roomVO;
        }
        throw new SavaException("??????????????????");
    }

    @Override
    public RoomVO selectOneByIdReturnVO(String id) {
        if (id == null) {
            throw new CommonException(501, "????????????");
        }
        RoomPO roomPO = roomMapper.selectById(id);
        RoomVO roomVO = new RoomVO();
        if (roomVO != null) {
            BeanUtils.copyProperties(roomPO, roomVO);
        }
        return roomVO;
    }

    @Override
    public RoomPO save(RoomVO roomVO){
        if(roomVO==null){
            throw new CommonException(501,"??????????????????");
        }
        RoomVO check=selectOneByIdReturnVO(roomVO.getId());
        if(check==null){
            throw new CommonException(4004,"?????????id???'"+roomVO.getId()+"'?????????");
        }
        RoomPO roomPO=new RoomPO();
        BeanUtils.copyProperties(roomVO,roomPO);
        int save=roomMapper.updateById(roomPO);
        RoomPO thisRoom = selectOneById(roomPO.getId());
        BeanUtils.copyProperties(thisRoom,roomVO);
        if(save>0){
            if (!WStringUtils.isBlank(thisRoom.getId()) && !WStringUtils.isBlank(thisRoom.getHotelId())) {
                sendUpdateInfo(roomVO);
            }
            return roomMapper.selectById(roomPO.getId());
        }
        throw new SavaException("??????????????????");
    }
    @Override
    public Integer deleteById(String id){
        RoomVO check=selectOneByIdReturnVO(id);
        if(check==null){
            throw new CommonException(4004,"?????????id???'"+id+"'?????????");
        }
        Gson gson = new Gson();
        logsService.createLog("????????????????????????",gson.toJson(check),1,1);
        int i= roomMapper.deleteById(id);
        return i;
    }
    @Override
    public RoomPO selectOneById(String id){
        RoomPO roomPO=roomMapper.selectById(id);
        return roomPO;
    }

    @Override
    public RoomVO saveone(RoomVO roomVO){
        RoomVO before = selectOneByIdReturnVO(roomVO.getId());
        RoomPO save=save(roomVO);
        BeanUtils.copyProperties(save,roomVO);
        Gson gson = new Gson();
        logsService.createLog("????????????????????????",gson.toJson(before) + "@*@" + gson.toJson(save),1,1);
        return roomVO;
    }

    @Override
    public Page<RoomVO> getPage(RoomVO roomVO){
        QueryWrapper<RoomPO> wrapper=new QueryWrapper<>();
        Page<RoomPO> page=new Page<>(roomVO.getPage().getPage(),roomVO.getPage().getSize());
        Page<RoomPO> poiPage=(Page<RoomPO>) roomMapper.selectPage(page,wrapper);
        return ConvertUtils.transferPage(poiPage,RoomVO.class);
    }

    @Override
    public List<RoomVO> getAllList(RoomVO roomVO){
        roomVO.setHotelId(authUtils.getUserHotelId());
        return roomMapper.getAllList(roomVO);
    }

    @Override
    public DetailVO getDetail(String id){
        return roomMapper.getDetail(id);
    }

    @Override
    public  List<RoomVO> getRoomTypeList( RoomVO roomVO){
        return roomMapper.getRoomTypeList(roomVO);
    }

    /**
     * ??????
     * @param checkInVO id,customerId(?????????),orderId(?????????),province
     * @return
     */
    @Override
    public OrderVO checkIn(CheckInVO checkInVO) {

        Date now = new Date();
        // ??????id????????????,???????????????
        if (!WStringUtils.isBlank(checkInVO.getOrderId())) {
            //???????????????????????????
            OrderVO thisOrder = orderService.selectOneByIdReturnVO(checkInVO.getOrderId());
            if (!now.after(thisOrder.getEstimatedCheckIn())) {
                throw new CommonException("??????????????????????????????");
            }
        }

        int[] pays = new int[2];
        RoomVO thisRoom = selectOneByIdReturnVO(checkInVO.getId());
        //?????????????????????????????????id?????????,??????????????????
        if (thisRoom.getStatus() == 2 && (WStringUtils.isBlank(checkInVO.getOrderId()) || !thisRoom.getOrderId().equals(checkInVO.getOrderId()))) {
            assignRoom(thisRoom);
        }
        RoomTypeVO roomTypeVO = roomTypeServeice.selectOneByIdReturnVO(thisRoom.getType());
        OrderVO updateOrder = new OrderVO();
        OrderVO orderVO;
        RoomVO updateRoom = new RoomVO();
        //???????????????????????????
        updateRoom.setStatus(1);
        updateRoom.setId(checkInVO.getId());

        //???????????????
        updateOrder.setCheckInTime(now);
        updateOrder.setProvince(checkInVO.getProvince());
        if (WStringUtils.isBlank(checkInVO.getOrderId())) {
            updateOrder.setEstimatedCheckIn(TimeUtils.setSplitTime(now));
        } else {
            orderVO = orderService.selectOneByIdReturnVO(checkInVO.getOrderId());
            updateOrder.setEstimatedCheckIn(orderVO.getEstimatedCheckIn());
        }
        updateOrder.setEstimatedCheckOut(TimeUtils.setSplitTime(checkInVO.getEstimatedCheckOut()));
        updateOrder.setDays(TimeUtils.daysBetween(updateOrder.getEstimatedCheckIn(),updateOrder.getEstimatedCheckOut(),"ceil"));
        pays = discountsService.countPay(TimeUtils.daysBetween(updateOrder.getEstimatedCheckIn(),updateOrder.getEstimatedCheckOut(),"ceil"),roomTypeVO.getFee());
        updateOrder.setPay(String.valueOf(pays[0]));
        updateOrder.setLastPay(String.valueOf(pays[1]));

        //??????????????????????????????
        updateOrder.setRoomType(thisRoom.getType());
        updateOrder.setRoomName(thisRoom.getName());
        updateOrder.setHotelId(thisRoom.getHotelId());

        //?????????id?????????,??????????????????
        if (!WStringUtils.isBlank(checkInVO.getCustomerId())) {
            updateOrder.setCustomerId(checkInVO.getCustomerId());
        }

        OrderPO orderSave;
        updateOrder.setStatus("3");
        if (!WStringUtils.isBlank(checkInVO.getOrderId())) {
            updateOrder.setId(checkInVO.getOrderId());
            orderSave = orderService.save(updateOrder);
        } else {
            orderSave = orderService.add(updateOrder);
        }

        //?????????????????????
        updateRoom.setOrderId(orderSave.getId());

        RoomPO roomSave = save(updateRoom);

        //???????????????
        OrderVO res = new OrderVO();
        BeanUtils.copyProperties(orderSave,res);
        return res;
    }

    /**
     * ??????
     * @param id
     * @return
     */
    @Override
    public String checkOut(String id) {
        Date now = new Date();
        RoomPO thisRoom = selectOneById(id);
        OrderVO thisOrder = orderService.selectOneByIdReturnVO(thisRoom.getOrderId());
        Integer btTime = 0;
        int[] pays = new int[2];
        int extraFee = 0;
        RoomTypeVO roomTypeVO = roomTypeServeice.selectOneByIdReturnVO(thisRoom.getType());
        //??????????????????
        pays[0] = Integer.parseInt(thisOrder.getPay());
        pays[1] = Integer.parseInt(thisOrder.getLastPay());
        OrderVO updateOrder = new OrderVO();

        //???????????????
        if (now.after(thisOrder.getEstimatedCheckOut())) {
            //??????
            btTime = TimeUtils.daysBetween(TimeUtils.setSplitTime(thisOrder.getEstimatedCheckOut()), now, "ceil");
            pays[0] += btTime * roomTypeVO.getFee();
            pays[1] += btTime * roomTypeVO.getFee();
            extraFee = btTime * roomTypeVO.getFee();
            updateOrder.setDays(btTime);

        }
        //??????????????????
        updateOrder.setCheckOutTime(now);
        updateOrder.setId(thisOrder.getId());
        //??????????????????,????????????
        updateOrder.setPay(String.valueOf(pays[0]));
        updateOrder.setLastPay(String.valueOf(pays[1]));

        //????????????
        updateOrder.setStatus("4");

        orderService.save(updateOrder);

        Integer integer = unlockRoom(thisRoom.getId());

        if (extraFee == 0) {
            return "????????????!";
        } else {
            return "?????????" + extraFee + "???!";
        }
    }

    public Integer unlockRoom(String id) {
        Integer integer = roomMapper.unlockRoom(id);
        if (integer > 0) {
            sendUpdateInfo(selectOneByIdReturnVO(id));
        }
        return integer;
    }

    private void sendUpdateInfo(RoomVO vo) {
        List<String> hotelAllUser = authUtils.getHotelAllUser(vo.getHotelId());
        Iterator<String> allUserIter = hotelAllUser.iterator();
//        RoomVO roomVO = new RoomVO();
//        roomVO.setId(vo.getId());
//        List<RoomVO> allList = getAllList(roomVO);
        QueryWrapper<RoomPO> wrapper = new QueryWrapper<>();
        wrapper.eq("id",vo.getId());
        List<RoomPO> allList = roomMapper.selectList(wrapper);
        Gson gson = new Gson();
        String hotelInfo = gson.toJson(allList.get(0));
        while(allUserIter.hasNext()) {
            try {
                WebSocketServer.sendInfo(hotelInfo, allUserIter.next());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    }

    @Override
    public void cancelRoom(RoomVO roomVO) {
        QueryWrapper<RoomPO> wrapper = new QueryWrapper<RoomPO>();
        wrapper.eq("order_id",roomVO.getOrderId()).eq("status",2);
        List<RoomPO> roomPOS = roomMapper.selectList(wrapper);
        if (roomPOS.size() > 0) {
            unlockRoom(roomPOS.get(0).getId());
        }
    }

    @Override
    public List<String> getFloor() {
        QueryWrapper<RoomPO> wrapper = new QueryWrapper<RoomPO>();
        wrapper.eq("hotel_id",authUtils.getUserHotelId()).select("floor").groupBy("floor");
        List<RoomPO> roomPOS = roomMapper.selectList(wrapper);
        Iterator<RoomPO> iterator = roomPOS.iterator();
        List<String> floors = new ArrayList<>();
        while (iterator.hasNext()) {
            floors.add(iterator.next().getFloor());
        }
        return floors;
    }


    @Override
    public void assignRoom(RoomVO roomVO) {
        QueryWrapper<RoomPO> wrapper = new QueryWrapper<RoomPO>();
        //???????????????????????????
        if (!WStringUtils.isBlank(roomVO.getOrderId())) {
            cancelRoom(roomVO);
            if (roomVO.getId() != null) {
                wrapper.ne("id",roomVO.getId());
            }
        }


        wrapper.eq("type",roomVO.getType()).eq("status",0);
        List<RoomPO> roomPOS = roomMapper.selectList(wrapper);
        if (roomPOS.size() > 1) {
            RoomVO vo = new RoomVO();
            vo.setId(roomPOS.get(0).getId());
            vo.setStatus(2);
            vo.setOrderId(roomVO.getOrderId());
            save(vo);
        } else {
            throw new CommonException("?????????????????????,???????????????????????????????????????");
        }
    }


}
