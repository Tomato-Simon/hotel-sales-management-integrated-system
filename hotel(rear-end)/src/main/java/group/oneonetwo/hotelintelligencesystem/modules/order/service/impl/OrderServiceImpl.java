package group.oneonetwo.hotelintelligencesystem.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group.oneonetwo.hotelintelligencesystem.components.security.utils.AuthUtils;
import group.oneonetwo.hotelintelligencesystem.exception.CommonException;
import group.oneonetwo.hotelintelligencesystem.exception.SavaException;
import group.oneonetwo.hotelintelligencesystem.modules.discounts.service.IDiscountsService;
import group.oneonetwo.hotelintelligencesystem.modules.hotel.model.vo.HotelVO;
import group.oneonetwo.hotelintelligencesystem.modules.hotel.service.IHotelService;
import group.oneonetwo.hotelintelligencesystem.modules.order.dao.OrderMapper;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.po.OrderPO;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.vo.OrderVO;
import group.oneonetwo.hotelintelligencesystem.modules.order.service.IOrderService;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.RoomVO;
import group.oneonetwo.hotelintelligencesystem.modules.room.service.IRoomService;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.model.vo.RoomTypeVO;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.service.IRoomTypeServeice;
import group.oneonetwo.hotelintelligencesystem.modules.user.model.vo.UserVO;
import group.oneonetwo.hotelintelligencesystem.modules.user.service.IUserService;
import group.oneonetwo.hotelintelligencesystem.tools.ConvertUtils;
import group.oneonetwo.hotelintelligencesystem.tools.TimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class OrderServiceImpl implements IOrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    IUserService userService;

    @Autowired
    IHotelService hotelService;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    IDiscountsService discountsService;

    @Autowired
    IRoomService roomService;

    @Override
    public OrderPO add(OrderVO orderVO){
        if(orderVO==null){
            throw new SavaException("??????????????????:??????????????????");
        }
        OrderPO orderPO = new OrderPO();
        BeanUtils.copyProperties(orderVO,orderPO);
        int insert=orderMapper.insert(orderPO);
        if(insert>0){
            return orderPO;
        }
        throw new SavaException("??????????????????");
    }

    @Override
    public OrderVO selectOneByIdReturnVO(String id){
        if(id==null){
            throw new CommonException(501,"????????????");
        }
        OrderPO orderPO=orderMapper.selectById(id);
        OrderVO orderVO=new OrderVO();
        if(orderVO!=null){
            BeanUtils.copyProperties(orderPO,orderVO);
        }
        return orderVO;
    }

    @Override
    public OrderPO save(OrderVO orderVO){
        if (orderVO==null){
            throw new CommonException(501,"??????????????????");
        }
        OrderVO check=selectOneByIdReturnVO(orderVO.getId());
        if(check==null){
            throw new CommonException(4004,"?????????id???:"+orderVO.getId()+"?????????");
        }
        OrderPO orderPO = new OrderPO();
        BeanUtils.copyProperties(orderVO,orderPO);
        int save=orderMapper.updateById(orderPO);
        if(save>0){
            return orderPO;
        }
        throw new SavaException("??????????????????");
    }
    @Override
    public Integer deleteById(String id){
        OrderVO check=selectOneByIdReturnVO(id);
        if(check==null){
            throw  new CommonException(4004,"?????????id???"+id+"?????????");
        }
        int i=orderMapper.deleteById(id);
        return i;
   }

    @Override
    public OrderPO selectOneById(String id){
        OrderPO orderPO=orderMapper.selectById(id);
        return orderPO;
    }

    @Override
    public OrderVO saveone(OrderVO orderVO){
        OrderPO save=save(orderVO);
        BeanUtils.copyProperties(save,orderVO);
        return orderVO;
    }

    @Override
    public OrderVO addOne(OrderVO orderVO){
        OrderPO st=add(orderVO);
        BeanUtils.copyProperties(st,orderVO);
        return orderVO;
    }

    @Override
    public Page<OrderVO> getPage(OrderVO orderVO){
        Page<OrderPO> page=new Page<>(orderVO.getPage().getPage(),orderVO.getPage().getSize());
        return orderMapper.getPage(page,orderVO);
    }

    @Override
    public List<OrderVO> getAllList(OrderVO orderVO) {
        String authority = authUtils.getRole();
        switch (authority) {
            case "admin":break;
            case "hotel_admin":
                UserVO userVO = userService.selectOneByIdReturnVO(authUtils.getUid());
                if (userVO == null) {
                    throw new CommonException(401,"?????????");
                }
                HotelVO hotelVO = new HotelVO();
                hotelVO.setDeptId(userVO.getDept());
                hotelVO = hotelService.selectOneByDeptId(userVO.getDept());
                if (hotelVO == null) {
                    throw new CommonException(500,"????????????????????????");
                }
                orderVO.setHotelId(hotelVO.getId());
                break;
            default:
                throw new CommonException(401,"?????????");
        }
        return orderMapper.getAllList(orderVO);
    }

    @Override
    public OrderVO createNewOrder(OrderVO orderVO) {

        orderVO = addOne(orderVO);

        orderVO.setWay(2);

        //????????????
        RoomVO roomVO = new RoomVO();
        roomVO.setType(orderVO.getRoomType());
        roomVO.setOrderId(orderVO.getId());
        roomService.assignRoom(roomVO);

        //???????????????
        orderVO.setEstimatedCheckIn(TimeUtils.setSplitTime(orderVO.getEstimatedCheckIn()));
        orderVO.setEstimatedCheckOut(TimeUtils.setSplitTime(orderVO.getEstimatedCheckOut()));

        //????????????
        orderVO.setDays(TimeUtils.daysBetween(orderVO.getEstimatedCheckIn(),orderVO.getEstimatedCheckOut(),"ceil"));

        //????????????????????????
        RoomTypeVO roomTypeVO = roomTypeServeice.selectOneByIdReturnVO(orderVO.getRoomType());

        //????????????
        int[] pays = discountsService.countPay(orderVO.getDays(), roomTypeVO.getFee());
        orderVO.setPay(String.valueOf(pays[0]));
        orderVO.setLastPay(String.valueOf(pays[1]));

        OrderPO save = save(orderVO);
        BeanUtils.copyProperties(save,orderVO);


        return orderVO;
    }

    @Override
    public String cancelOrder(String id) {
        RoomVO roomVO = new RoomVO();
        roomVO.setOrderId(id);
        roomService.cancelRoom(roomVO);
        OrderVO orderVO = new OrderVO();
        orderVO.setStatus("2");
        orderVO.setId(id);
        OrderPO save = save(orderVO);
        return "??????????????????,??????" + save.getLastPay() + "?????????0-3??????????????????????????????";
    }

    @Override
    public  Page<OrderVO> my(OrderVO orderVO){
        String role = authUtils.getRole();
        switch (role) {
            case "admin":break;
            case "hotel_admin":orderVO.setHotelId(authUtils.getUserHotelId());break;
            default:
                orderVO.setCustomerId(authUtils.getUid());
                break;
        }
        Page<OrderVO> page = getPage(orderVO);
        return page;
    }

    @Override
    public  List<OrderVO> count(OrderVO orderVO){
        orderVO.setCustomerId(authUtils.getUid());
        return orderMapper.getcount(orderVO);
    }

    @Autowired
    IRoomTypeServeice roomTypeServeice;

    @Override
    public void randomOrder() {
        String[] provinces = {"?????????","?????????","?????????","?????????","????????????","?????????",
                "?????????","?????????","?????????","?????????","?????????","?????????","?????????","?????????",
                "?????????","?????????","?????????","?????????","?????????","?????????","?????????","?????????",
                "?????????","??????????????????","?????????????????????","???????????????","?????????????????????",
                "????????????????????????","?????????","?????????","?????????","?????????","?????????????????????","?????????????????????"};
        String[] members = {"3","4","5","6","7","1506647494363246593"};
        String hotel = "1";
        Integer[] ways = {1,2};
        String[] roomTypes = {"1463764265293885441", "1463890996549947394", "1463891142348148737","1470613302300102658","1470613446756126722"};
        for (int i = 0; i < 1500; i++) {
            OrderVO orderVO = new OrderVO();
            Random random = new Random();
            orderVO.setHotelId(hotel);
            orderVO.setProvince(provinces[random.nextInt(provinces.length)]);
            orderVO.setCustomerId(members[random.nextInt(members.length)]);
            orderVO.setRoomType(roomTypes[random.nextInt(roomTypes.length)]);
            orderVO.setStatus("1");
            orderVO.setDays(random.nextInt(5) + 1);
            orderVO.setWay(ways[random.nextInt(2)]);
            RoomTypeVO roomTypeVO = roomTypeServeice.selectOneByIdReturnVO(orderVO.getRoomType());
            orderVO.setPay(String.valueOf(roomTypeVO.getFee() * orderVO.getDays()));
            orderVO.setLastPay(orderVO.getPay());
            add(orderVO);
        }
    }
}
