<template>
  <div class="big-body">
    <!-- 导航栏 -->
    <top-nav></top-nav>
    <div class="hotel-list-bg d-flex align-items-center justify-content-center position-relative">
      <img src="../assets/images/hotel-list-bg.png" alt="">
      <!--      <h3>{{ $t('hotelList.title') }}</h3>-->
    </div>
    <div class="hotel-list-main">


      <div class="big-box">
        <!--        <p>酒店id：{{ hotelDetails.hotleId }}</p>-->
        <div class="hotel-title">
          {{ hotelDetails.name }}
        </div>

        <div class="hotel-detail">
          <div class="detail-left">
            <div class="detail-detail">
              <div class="flex flex-row flex-n-wrap">
                <div class="flex-column address-detail">
                  <p>{{ hotelDetails.province + " " + hotelDetails.city }}</p>
                  <p>{{ hotelDetails.address }}</p>
                </div>
                <div class="flex justify-content-end align-items-center" style="width: 50%">
                  <i class="el-icon-map-location location" @click="toMap()"></i>
                </div>
              </div>
              <el-divider content-position="left">酒店介绍</el-divider>
              <div class="detail-introduce" v-html="hotelDetails.introduce"></div>
            </div>

            <div class="detail-block">
              <h3 class="sub-title">
                酒店图片
                <span class="en">picture</span>
              </h3>

              <div class="detail-content" style="margin: 0;padding: 0">
                <el-image
                    style="width: 100%; height: 100%"
                    :src="hotelDetails.cover"
                    fit="fit"></el-image>
              </div>
            </div>

            <div class="detail-block">
              <h3 class="sub-title">
                设施服务
                <span class="en">AMENITIES</span>
              </h3>

              <div class="detail-content">
                <div v-html="hotelDetails.facilities"></div>
              </div>
            </div>


            <div class="detail-block">
              <h3 class="sub-title">
                其他政策
                <span class="en">NOTICE</span>
              </h3>

              <div class="detail-content">
                <div v-html="hotelDetails.otherPolicy"></div>
              </div>
            </div>
          </div>
          <div class="detail-right" >
            <div class="detail-right-price" id="detailRight" :style="isFixed ? 'position:fixed;top:90px;width: 440px;' : ''">
              <div class="book-box">
                <span class="price-sum">
                  <span>
                    <span class="unit">¥</span>
                    <span class="zg-price">
                      <strong>
                        <span v-if="!bookDay || !totalFee">{{ minFee + '~' + maxFee }}</span>
                        <span v-else>{{ totalFee }}</span>
                      </strong>
                    </span>
                  </span>
                  <!--                  <small class="text-weak">/晚</small>-->
                </span>
                <div class="book-date flex flex-row align-items-center">
                  <el-date-picker
                      v-model="dateValue"
                      type="daterange"
                      value-format="yyyy-MM-dd"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      :picker-options="pickerOptions">
                  </el-date-picker>
                  <p v-if="bookDay" style="margin: 2px">共{{ bookDay }}晚</p>
                </div>
                <div class="book-room">
                  <el-select v-model="currentRoomType" :placeholder="$t('hotelList.selectRoom')">
                    <el-option
                        v-for="item in roomTypeList"
                        :label="item.name"
                        :value="item.id">
                      <span style="float: left">{{ item.name }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">￥{{ item.fee }}</span>
                    </el-option>
                  </el-select>
                  <div class="room-detail" v-if="currentRoomType">
                    <i class="el-icon-files"> 床位数:{{ roomTypeMap[currentRoomType].bed }}</i><br>
                    <i class="el-icon-user"> 最大容纳:{{ roomTypeMap[currentRoomType].maxLoad }}</i><br>
                    <i class="el-icon-picture-outline"> 是否有窗:{{
                        roomTypeMap[currentRoomType].haveWindow == 1 ? "是" : "否"
                      }}</i><br>
                    <i class="el-icon-fork-spoon"> 是否含早餐:{{
                        roomTypeMap[currentRoomType].haveBreakfast == 1 ? "是" : "否"
                      }}</i><br>
                    <i class="el-icon-magic-stick"> 是否有网络:{{
                        roomTypeMap[currentRoomType].network == 1 ? "是" : "否"
                      }}</i>
                  </div>
                  <div class="mt-10">
                    <el-select v-model="provinceVal" :placeholder="$t('hotelList.province')">
                      <el-option
                          v-for="item in options"
                          :label="item.label"
                          :value="item.value">
                      </el-option>
                    </el-select>
                  </div>
                  <div>
                    <el-button @click="confirm()" type="primary" style="width: 100%;margin: 10px 0">预订</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
      <Footer></Footer>

    </div>


  </div>
</template>

<script>

import TopNav from '../components/TopNav'
import Footer from '../components/Footer.vue';
import {get, post} from "../utils/request";

export default {
  name: "HotelDetails",
  components: {
    TopNav,
    Footer
  },
  data() {
    return {
      hotelId: '',
      hotelDetails: '',
      roomTypeList: [],
      roomTypeMap: {},
      currentRoomType: '',
      dateValue: '',
      bookDay: 0,
      maxFee: 0,
      minFee: 0,
      totalFee: 0,
      provinceVal: '',
      options: [
        {
          value: this.$t('hotelList.beijing'),
          label: this.$t('hotelList.beijing')
        }, {
          value: this.$t('hotelList.shanghai'),
          label: this.$t('hotelList.shanghai')
        }, {
          value: this.$t('hotelList.tianjin'),
          label: this.$t('hotelList.tianjin')
        }, {
          value: this.$t('hotelList.chongqing'),
          label: this.$t('hotelList.chongqing')
        }, {
          value: this.$t('hotelList.hebei'),
          label: this.$t('hotelList.hebei')
        }, {
          value: this.$t('hotelList.shanxi'),
          label: this.$t('hotelList.shanxi')
        }, {
          value: this.$t('hotelList.neimenggu'),
          label: this.$t('hotelList.neimenggu')
        }, {
          value: this.$t('hotelList.liaoning'),
          label: this.$t('hotelList.liaoning')
        }, {
          value: this.$t('hotelList.jilin'),
          label: this.$t('hotelList.jilin')
        }, {
          value: this.$t('hotelList.heilongjiang'),
          label: this.$t('hotelList.heilongjiang')
        }, {
          value: this.$t('hotelList.jiangsu'),
          label: this.$t('hotelList.jiangsu')
        }, {
          value: this.$t('hotelList.zhejiang'),
          label: this.$t('hotelList.zhejiang')
        }, {
          value: this.$t('hotelList.anhui'),
          label: this.$t('hotelList.anhui')
        }, {
          value: this.$t('hotelList.fujian'),
          label: this.$t('hotelList.fujian')
        }, {
          value: this.$t('hotelList.jiangxi'),
          label: this.$t('hotelList.jiangxi')
        }, {
          value: this.$t('hotelList.shandong'),
          label: this.$t('hotelList.shandong')
        }, {
          value: this.$t('hotelList.henan'),
          label: this.$t('hotelList.henan')
        }, {
          value: this.$t('hotelList.hubei'),
          label: this.$t('hotelList.hubei')
        }, {
          value: this.$t('hotelList.hunan'),
          label: this.$t('hotelList.hunan')
        }, {
          value: this.$t('hotelList.guangdong'),
          label: this.$t('hotelList.guangdong')
        }, {
          value: this.$t('hotelList.guangxi'),
          label: this.$t('hotelList.guangxi')
        }, {
          value: this.$t('hotelList.hainan'),
          label: this.$t('hotelList.hainan')
        }, {
          value: this.$t('hotelList.sichuan'),
          label: this.$t('hotelList.sichuan')
        }, {
          value: this.$t('hotelList.guizhou'),
          label: this.$t('hotelList.guizhou')
        }, {
          value: this.$t('hotelList.yunnan'),
          label: this.$t('hotelList.yunnan')
        }, {
          value: this.$t('hotelList.xizang'),
          label: this.$t('hotelList.xizang')
        }, {
          value: this.$t('hotelList.shanxi'),
          label: this.$t('hotelList.shanxi')
        }, {
          value: this.$t('hotelList.gansu'),
          label: this.$t('hotelList.gansu')
        }, {
          value: this.$t('hotelList.ningxia'),
          label: this.$t('hotelList.ningxia')
        }, {
          value: this.$t('hotelList.qinghai'),
          label: this.$t('hotelList.qinghai')
        }, {
          value: this.$t('hotelList.xinjiang'),
          label: this.$t('hotelList.xinjiang')
        }, {
          value: this.$t('hotelList.taiwan'),
          label: this.$t('hotelList.taiwan')
        }, {
          value: this.$t('hotelList.xianggang'),
          label: this.$t('hotelList.xianggang')
        }, {
          value: this.$t('hotelList.aomen'),
          label: this.$t('hotelList.aomen')
        }],
      isFixed: false,
      offsetTop: 0,
      pickerOptions: {
        disabledDate(v) {
          return v.getTime() < new Date().getTime() - 86400000;
        }
      }

    }
  },
  watch: {
    "dateValue"(val, oldVal) {//普通的watch监听
      // console.log("a: "+val, oldVal);
      this.bookDay = 0
      this.totalFee = 0
      if (val) {
        this.updateFee()
      }
    },
    "currentRoomType"(val, oldVal) {//普通的watch监听
      // console.log("a: "+val, oldVal);
      this.bookDay = 0
      this.totalFee = 0
      if (val) {
        this.updateFee()
      }
    },
  },
  mounted() {
    this.$nextTick(() => {
      //获取对象相对于版面或由 offsetTop 属性指定的父坐标的计算顶端位置
      this.offsetTop = document.querySelector('#detailRight').offsetTop;
    })
    window.addEventListener("scroll", this.handleScroll, true);
    this.hotelId = this.$route.params.hotelId
    this.getHotelDetails()
    this.getRoomType();
  },
  methods: {
    updateFee() {
      this.countTime()
      this.totalFee = this.bookDay * this.roomTypeMap[this.currentRoomType].fee
    },
    countTime() {
      let estimatedCheckIn = new Date(this.dateValue[0])
      let estimatedCheckOut = new Date(this.dateValue[1])
      this.bookDay = parseInt(Math.abs(estimatedCheckIn - estimatedCheckOut) / 1000 / 60 / 60 / 24)
      // console.log("dateDiff-----------",this.bookDay)
    },

    handleScroll() {
      let scrollTop =
          document.documentElement.scrollTop || document.body.scrollTop;
      this.isFixed = scrollTop > this.offsetTop*1.15 ? true : false;
      console.log(this.offsetTop)
      // console.log(this.isFixed)
      // console.log(scrollTop);
    },


    toMap() {
      let that = this;
      window.open("https://ditu.amap.com/search?query=" + that.hotelDetails.address)
    },

    getRoomType() {
      //this.hotelId = this.$route.params.hotelId
      get('/api/roomType/currentRoomTypeList/' + this.hotelId)
          .then(res => {
            console.log(res);
            //this.dialogVisible = true
            this.roomTypeList = res.data.data
            var map = {};
            let min = 0;
            let max = 0;
            for (var index in this.roomTypeList) {
              let fee = this.roomTypeList[index].fee;
              if (fee < min) {
                min = fee
              } else if (fee > max) {
                max = fee
              }
              map[this.roomTypeList[index].id] = this.roomTypeList[index];
            }
            this.roomTypeMap = map
            this.maxFee = max
            this.minFee = min
            console.log(this.maxFee)
          })
          .catch(err => {
            console.log(err);
          })
    },

    confirm() {
      let data = {
        customerId: localStorage.getItem('userId'),
        hotelId: this.hotelId,
        roomType: this.currentRoomType,
        estimatedCheckIn: this.dateValue[0],
        estimatedCheckOut: this.dateValue[1],
        province: this.provinceVal,
      }
      // console.log("data=========", data)
      post('/api/order/create',data)
          .then(res => {
            console.log(res);
            if (res.data.code == 200) {
              this.$message({
                message: this.$t('hotelList.success'),
                type: 'success'
              });
              this.dialogVisible = false
              console.log("订单id：",res.data.data.id)
            } else {
              this.$message({
                message: res.data.msg,
                type: 'warning',
                duration: 4000
              });
            }

          })
          .catch(err => {
            console.log(err);
          })

    },

    getHotelDetails() {
      //let id = this.$route.params.hotelId
      let that = this
      // console.log(id)
      get("/api/hotel/get/" + this.hotelId).then(res => {
        that.hotelDetails = res.data.data;
        console.log(that.hotelDetails)
      })
    }
  }
}


</script>

<style scoped>
.hotel-list-main {
  /*height: calc(100vh - 220px);*/
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.big-body {
  background-color: #ecf5ff;
}

.hotel-list-bg {
  width: 100%;
  height: 220px;
}

.hotel-list-bg img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 220px;
}

.big-box {
  margin: 0 auto;
  /*border: 1px solid red;*/
  width: 1200px;

}

.hotel-title {
  font-size: 30px;
  color: #252626;
  font-family: inherit;
  font-weight: 600;
  line-height: 1.1;
  margin: 30px 0;
}

.hotel-detail {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  /*border: 1px solid blue;*/
}

.detail-left {
  left: 0;
  flex: 1;
  max-width: 60%;
  overflow: hidden;
  /*border: 1px solid blueviolet;*/
}

/*@media (min-width: 1000px)*/
.detail-right {
  /*position:fixed;*/
  /*right: 0;*/
  /*top:100%;*/
  width: 440px;
  /*border: 1px solid saddlebrown;*/
}

.detail-detail {
  line-height: 1.1;
  position: relative;
  background-color: #fff;
  overflow: hidden;
  padding: 10px;
  box-shadow: 0 12px 5px -10px rgba(0, 0, 0, 0.1), 0 0 4px 0 rgba(0, 0, 0, 0.1);

}

.sub-title {
  display: block;
  font-size: 24px;
  margin-block-start: 1.6em;
  margin-block-end: 1em;
  margin-inline-start: 0px;
  margin-inline-end: 0px;
  font-weight: bold;
  position: relative;
  /*margin-top: 10px;*/
}

h3.sub-title .en {
  position: absolute;
  top: 0;
  font-size: 10px;
  color: #898a8a;
  font-weight: 500;
  font-style: italic;
  margin-left: 4px;
  text-transform: uppercase;
}

.detail-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 10px;
  line-height: 2;
  /*position: relative;*/
  background-color: #fff;
  overflow: hidden;
  overflow-x: hidden;
  overflow-y: hidden;
  margin-bottom: 30px;
  box-shadow: 0 12px 5px -10px rgba(0, 0, 0, 0.1), 0 0 4px 0 rgba(0, 0, 0, 0.1);
}

.detail-right-price {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.text-weak {
  color: #585a5a;
  font-size: 12px;
}

.price-sum {
  color: #ff4d6a;
  font-size: 32px;
  margin: 10px;
}

.unit {
  font-size: 16px;
}

.book-box {
  line-height: 1.1;
  position: relative;
  background-color: #fff;
  overflow: hidden;
  padding-top: 8px;
  padding-bottom: 12px;
  width: 100%;
  padding: 8px;
  box-shadow: 0 12px 5px -10px rgba(0, 0, 0, 0.1), 0 0 4px 0 rgba(0, 0, 0, 0.1);

}

.book-date {
  margin: 10px;
}

.book-room {
  margin: 10px;
}

.room-detail {
  font-size: 14px;
  line-height: 20px;
  margin: 8px 0;
}

.detail-introduce {
  line-height: 26px;
  margin: 0px 6px 8px 6px;
  font-size: 14px;
}

.location {
  color: #0af;
  font-size: 40px;
  cursor: pointer;
  margin: 6px 12px;
}

.address-detail {
  width: 50%;
  font-size: 16px;
  line-height: 30px;
  padding: 6px;
}


</style>