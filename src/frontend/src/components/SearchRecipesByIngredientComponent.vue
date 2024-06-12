<template>
  <div class="home-page">
    <h1 class="title">All recipes with {{ ingredient }}</h1>
    <div v-for="recipe in recipes" :key="recipe.id">
      <RecipeComponent :recipe="recipe" />
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/services/apiClient";
import RecipeComponent from "@/components/RecipeComponent"; // Import the new component

export default {
  name: "SearchRecipesByIngredientComponent",
  components: {
    RecipeComponent, // Register the component
  },
  data() {
    return {
      ingredientId: '',
      ingredient: '',
      recipes: []
    };
  },
  mounted() {
    this.getRecipes();
  },
  methods: {
    getRecipes() {
      this.ingredientId = this.$route.params.ingId;
      this.ingredient = this.$route.params.ingName;
      axiosInstance.get(`/public/recipesByIngredient?ingredientId=${this.ingredientId}`)
          .then(response => {
            this.recipes = response.data;
          })
          .catch(error => {
            console.error('Error fetching recipes:', error);
          });
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
</style>
