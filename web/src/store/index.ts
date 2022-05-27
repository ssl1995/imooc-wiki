import {createStore} from 'vuex'

declare let SessionStorage: any;
const USER = 'USER';

const store = createStore({
    // 声明变量
    state: {
        user: SessionStorage.get(USER) || {}
    },
    // 同步操作变量
    mutations: {
        setUser(state, user) {
            state.user = user;
            SessionStorage.set(USER,user);
        }
    },
    // 异步操作变量
    actions: {},

    modules: {}
})

export default store;
