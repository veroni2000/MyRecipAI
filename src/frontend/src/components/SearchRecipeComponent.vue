<template>
  <div class="home-page">
    <h1 class="title">Search results for '{{ msg }}'</h1>
    <div v-for="recipe in recipes" :key="recipe.id">
      <RecipeComponent :recipe="recipe" />
    </div>
    <div ref="sentinel" class="sentinel"></div>
    <p v-if="loading" class="loading">Loading recipes...</p>
    <p v-if="noMoreData" class="no-more-data">All recipes found are loaded.</p>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";
import RecipeComponent from "@/components/RecipeComponent";

export default {
  name: "SearchRecipeComponent",
  components: {
    RecipeComponent,
  },
  data() {
    return {
      msg: '',
      recipes: [],
      page: 0,
      size: 10,
      loading: false,
      noMoreData: false,
    };
  },
  mounted() {
    this.msg = this.$route.params.msg;
    this.loadMoreRecipes();
    this.setupObserver();
  },
  methods: {
    loadMoreRecipes() {
      if (this.loading || this.noMoreData) return;
      this.loading = true;
      axiosInstance.get(`/public/search`, {
        params: {
          title: this.msg,
          page: this.page,
          size: this.size
        }
      })
          .then(response => {
            const newRecipes = response.data.content;
            if (newRecipes.length < this.size) {
              this.noMoreData = true;
            }
            this.recipes = [...this.recipes, ...newRecipes];
            this.page += 1;
            this.loading = false;
          })
          .catch(error => {
            console.error('Error fetching recipes:', error);
            this.loading = false;
          });
    },
    setupObserver() {
      const sentinel = this.$refs.sentinel;
      const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && !this.loading) {
          this.loadMoreRecipes();
        }
      });
      observer.observe(sentinel);
    }
  }
};
</script>

<style scoped>
.home-page {
  text-align: center;
  padding: 20px;
}

.title {
  font-size: 2em;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  margin-top: 20px;
}

.no-more-data {
  text-align: center;
  margin-top: 20px;
  color: gray;
}

.sentinel {
  height: 1px;
}
</style>
