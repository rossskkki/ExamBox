<template>
  <div class="home-container">
    <van-nav-bar title="主页" />
    <div class="button-container">
      <!-- 第一行：两个按钮 -->
      <div class="button-row">
        <van-button type="default" @click="goToSearch">搜索试卷</van-button>
        <van-button type="primary" @click="gotoSearchQ">文字搜题</van-button>
      </div>
      
      <!-- 第二行：一个按钮 -->
      <van-button type="default" block @click="goToUploadQ">上传题目</van-button>
    </div>
  </div>
</template>


<script>
export default {
  name: 'HomePage',
  methods: {
    goToSearch() {
      this.$router.push('/examSearch');
    },
    goToUploadQ() {
      this.$router.push('/uploadQ');
    },
    gotoSearchQ(){
      this.$router.push('/searchQ')
    }
  },
};
</script>

<style scoped>
.home-container {
  padding: 16px;
}
/* 
.button-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
} */

.van-button {
  width: 48%;
}

.button-container {
  padding: 16px;
}

.button-row {
  display: flex;
  gap: 8px; /* 两个按钮之间的间距 */
  margin-bottom: 8px; /* 与下一行按钮的间距 */
}

.button-row .van-button {
  flex: 1; /* 两个按钮平分宽度 */
}

/* 确保第二行按钮的宽度等于第一行两个按钮的总和 */
.button-container .van-button:last-child {
  width: calc(100% - 8px); /* 减去第一行的gap，确保总宽度匹配 */
  margin-left: 0;
}
</style>