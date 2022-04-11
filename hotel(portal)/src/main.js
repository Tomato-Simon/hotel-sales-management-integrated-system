import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'
import router from './router'
import store from './store'
import './assets/css/global.css'
import VueI18n from 'vue-i18n'
import znCH from './lang/zh-CN'
import enUS from './lang/en-US'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'

Vue.config.productionTip = false

Vue.use(VueI18n)

Vue.prototype.$axios = axios


const messages = {
  en: {
      ...enUS,
      ...enLocale
  },
  zh: {
      ...znCH,
      ...zhLocale
  }
}

const i18n = new VueI18n({
  locale: localStorage.getItem('lang'), // set locale
  fallbackLocale: 'zh',
  messages, // set locale messages
})

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
});


new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')
