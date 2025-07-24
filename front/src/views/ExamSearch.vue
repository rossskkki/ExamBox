<template>
  <div class="search-container">
    <van-nav-bar
      title="试卷搜索"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />
    <van-search
      v-model="searchKeyword"
      placeholder="请输入试卷名称"
      show-action
      @search="onSearch"
    >
      <template #action>
        <div @click="onSearch">搜索</div>
      </template>
    </van-search>

    <van-list
      v-model:loading="loading" :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
      :immediate-check="false">
      <van-cell
        v-for="item in searchResults"
        :key="item.id"
        :title="item.name"
        :label="`分类：${item.categoryName}`"
        is-link @click="openPdf(item.path)"
      />
    </van-list>
  </div>
</template>

<script>
export default {
  name: 'ExamSearch',
  data() {
    return {
      searchKeyword: '',
      searchResults: [],
      loading: false,
      finished: false
    };
  },
  methods: {
    onSearch() {
      console.log("search!!!!!!");
      this.searchResults = [];
      this.finished = false;
      this.onLoad();
    },
    async onLoad() {
      if (this.searchKeyword.trim()) {
        console.log("load!!!!!!");
        try {
          this.loading = true; // 开始加载
          const res = await this.$api.searchExam(this.searchKeyword);
          this.searchResults = res.data.data;
          this.finished = true;
        } catch (error) {
          console.error('搜索失败:', error);
          this.$toast.fail('搜索失败');
        } finally {
          this.loading = false; // 结束加载
        }
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  },
  setup() {
    const openPdf = (path) => {
      const url = `/upload-files/${path}`;
      window.open(url, '_blank');
    };
    return{
      openPdf
    };
  }
};
</script>

<style scoped>
.search-container {
  padding: 16px;
}
</style>