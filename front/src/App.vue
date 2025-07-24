<template>
  <router-view />
  <van-tabbar route>
    <van-tabbar-item replace to="/" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item replace to="/question-bank" icon="search">题库</van-tabbar-item>
    <van-tabbar-item replace to="/exam-bank" icon="description-o">试卷</van-tabbar-item>
  </van-tabbar>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
  background-color: #f7f8fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Segoe UI, Arial, Roboto, 'PingFang SC', 'miui', 'Hiragino Sans GB', 'Microsoft Yahei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
  width: 100%;
}

.app-container {
  height: 100%;
  width: 100%;
  max-width: 750px;
  margin: 0 auto;
  position: relative;
}

.page-container {
  padding: 16px;
  padding-bottom: 70px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}
</style>
