import {createStore} from 'vuex'

const store = createStore({
    // 声明变量
    state: {
        user: {}
    },
    // 同步操作变量
    mutations: {
        setUser(state, user) {
            state.user = user;
        }
    },
    // 异步操作变量
    actions: {},

    modules: {}
})

export default store;
