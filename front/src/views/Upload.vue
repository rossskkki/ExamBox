<template>
  <div class="page-container">
    <van-nav-bar
      title="上传试卷"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="formData.name"
          name="name"
          label="试卷名称"
          placeholder="请输入试卷名称"
          :rules="[{ required: true, message: '请输入试卷名称' }]"
        />

        <van-field
          v-model="formData.categoryId"
          name="categoryId"
          label="所属分类"
          :rules="[{ required: true, message: '请选择所属分类' }]"
        >
          <template #input>
            <van-cell-group inset style="margin: 0; padding: 0">
              <div style="display: flex; align-items: center">
                <van-cell
                  :title="
                    selectedCategory ? selectedCategory.name : '请选择分类'
                  "
                  is-link
                  @click="showCategoryPicker = true"
                />
                <van-icon
                  name="plus"
                  size="20"
                  @click.stop="openAddDialog"
                  style="margin-left: 8px; cursor: pointer"
                />
              </div>
            </van-cell-group>
          </template>
        </van-field>

        <van-field name="file" label="试卷文件">
          <template #input>
            <van-uploader
              v-model="fileList"
              :max-count="1"
              :before-read="beforeRead"
              @delete="onFileDelete"
              accept=".pdf,application/pdf"
            >
              <van-button icon="plus" type="primary">上传文件</van-button>
            </van-uploader>
          </template>
        </van-field>
      </van-cell-group>

      <div style="margin: 16px">
        <van-button
          round
          block
          type="primary"
          native-type="submit"
          :disabled="uploading"
        >
          {{ uploading ? "上传中..." : "提交" }}
        </van-button>
      </div>
    </van-form>


  <!-- 分类选择弹窗 -->
  <van-dialog
    v-model:show="showCategoryPicker"
    title="选择分类"
    show-cancel-button
    @confirm="showCategoryPicker = false"
  >
    <div class="category-picker">
      <van-cell
        v-for="category in categories"
        :key="category.id"
        :title="category.name"
        clickable
        @click="selectCategory(category)"
      >
        <template #right-icon>
          <van-icon
            v-if="selectedCategory && selectedCategory.id === category.id"
            name="success"
            color="#1989fa"
          />
        </template>
      </van-cell>
    </div>
  </van-dialog>

  <!-- 添加分类弹窗 -->
  <van-dialog
    v-model:show="showAddCategoryDialog"
    title="添加分类"
    show-cancel-button
    @confirm="addCategory"
  >
    <van-field
      v-model="newCategoryName"
      label="分类名称"
      placeholder="请输入分类名称"
    />
  </van-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import {
  getCategoryList,
  uploadPaper,
  addCategory as addCategoryApi,
} from "../api/exambase";

export default {
  name: "UploadPage",
  setup() {
    const router = useRouter();
    const fileList = ref([]);
    const categories = ref([]);
    const selectedCategory = ref(null);
    const showCategoryPicker = ref(false);
    const showAddCategoryDialog = ref(false);
    const newCategoryName = ref("");
    const uploading = ref(false);

    const formData = reactive({
      name: "",
      categoryId: null,
    });

    // 获取分类列表
    const fetchCategories = async () => {
      try {
        const res = await getCategoryList();
        categories.value = res.data.data || [];
      } catch (error) {
        console.error("获取分类列表失败:", error);
        // Toast.fail('获取分类列表失败')
        showToast({ type: "fail", message: "获取分类列表失败" });
      }
    };

    // 选择分类
    const selectCategory = (category) => {
      selectedCategory.value = category;
      formData.categoryId = category.id;
      showCategoryPicker.value = false;
    };

    // 文件上传前的校验
    const beforeRead = (file) => {
      // 这里可以添加文件类型和大小的校验
      const isPDF = file.type === "application/pdf";
      if (!isPDF) {
        // Toast('请上传 PDF 格式的文件')
        showToast({ type: "fail", message: "请上传 PDF 格式的文件" });
        return false;
      }
      return true;
    };

    // 删除文件
    const onFileDelete = () => {
      fileList.value = [];
    };

    // 提交表单
    const onSubmit = async () => {
      if (!selectedCategory.value) {
        // Toast('请选择所属分类11')
        showToast({ type: "fail", message: "请选择所属分类" });
        return;
      }

      if (fileList.value.length === 0) {
        // Toast('请上传试卷文件')
        showToast({ type: "fail", message: "请上传试卷文件" });
        return;
      }

      try {
        uploading.value = true;

        // 创建FormData对象
        const data = new FormData();
        data.append("file", fileList.value[0].file);
        data.append("name", formData.name);
        data.append("categoryId", formData.categoryId);

        // 调用上传接口
        const res = await uploadPaper(data);

        if (res.data.code === 200) {
          // Toast.success('上传成功')
          showToast({ type: "success", message: "上传成功" });
          router.push("/");
        } else {
          // Toast.fail(res.data.msg || '上传失败')
          showToast({ type: "fail", message: "上传失败" });
        }
      } catch (error) {
        console.error("上传试卷失败:", error);
        // Toast.fail('上传试卷失败')
        showToast({ type: "fail", message: "上传试卷失败" });
      } finally {
        uploading.value = false;
      }
    };

    // 返回上一页
    const goBack = () => {
      router.back();
    };

    onMounted(() => {
      fetchCategories();
    });

    // 添加分类
    const addCategory = async () => {
      if (!newCategoryName.value) {
        showToast({ type: "fail", message: "请输入分类名称" });
        return;
      }

      try {
        const res = await addCategoryApi({ name: newCategoryName.value });
        if (res.data.code === 200) {
          showToast({ type: "success", message: "添加分类成功" });
          fetchCategories(); // 刷新分类列表
          newCategoryName.value = "";
          showAddCategoryDialog.value = false;
        } else {
          showToast({ type: "fail", message: res.data.msg || "添加分类失败" });
        }
      } catch (error) {
        console.error("添加分类失败:", error);
        showToast({ type: "fail", message: "添加分类失败" });
      }
    };

    const openAddDialog = () => {
      console.log("Add category button clicked");
      showAddCategoryDialog.value = true;
    };

    return {
      formData,
      fileList,
      categories,
      selectedCategory,
      showCategoryPicker,
      showAddCategoryDialog,
      newCategoryName,
      uploading,
      beforeRead,
      onFileDelete,
      onSubmit,
      selectCategory,
      addCategory,
      goBack,
      openAddDialog,
    };
  },
};
</script>

<style scoped>
.category-picker {
  max-height: 300px;
  overflow-y: auto;
}

.van-field {
  display: flex;
  align-items: center;
}

.van-field__label {
  width: 100px;
  text-align: right;
  padding-right: 12px;
}

.van-field__control {
  flex: 1;
}

.upload-container {
  padding-left: 100px;
}
</style>
